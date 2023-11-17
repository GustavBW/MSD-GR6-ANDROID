package gbw.sdu.msd.backend.models;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This class represents a graph model between users (nodes) and the amount they owe each other (edges). <br>
 * The majority of the calculations in terms of simplifying the graph happen on insert ({@link DebtGraph#recordDebt(User, User, double)}) making lookups
 * and payments cheap in terms of compute at scale. <br>
 * Furthermore, it automatically simplifies relationships, i.e. if A owes B who owes C some amount, this is simplified to potentially just A owes C.<br>
 * Again, this is computed at insert at an insignificant cost. <br>
 * Atomic. This cannot be parallelized.
 * @author GBW
 */
public class DebtGraph {
    private final Map<User, Map<User, Double>> whoDoesThisUserOweMoney;
    private final Map<User, Map<User, Double>> whoOwesThisUserMoney; // Reverse graph for creditors.

    public DebtGraph() {
        this.whoDoesThisUserOweMoney = new HashMap<>();
        this.whoOwesThisUserMoney = new HashMap<>();
    }

    /**
     * @param userA  - the user that owes money
     * @param userB  - who A owes money to
     * @param amount - how much
     */
    public void recordDebt(User userA, User userB, double amount) {
        if (userA == userB) return;
        if (amount <= 0) return;

        //doing it manually avoids 1 if statement per insert per potential .computeIfAbsent, but is actually not significantly faster, even at scale
        whoDoesThisUserOweMoney.computeIfAbsent(userA, k -> new HashMap<>());
        whoDoesThisUserOweMoney.computeIfAbsent(userB, k -> new HashMap<>());
        whoOwesThisUserMoney.computeIfAbsent(userA, k -> new HashMap<>());
        whoOwesThisUserMoney.computeIfAbsent(userB, k -> new HashMap<>());


        double whatAowesB = amount;
        double whatBMayBeOwingA = getAmountOwedBy(userB, userA);

        if(whatBMayBeOwingA != 0){
            whatAowesB = resolveOneToOne(userA, userB, whatAowesB, whatBMayBeOwingA);
        }

        if(whatAowesB <= 0.0) return;

        // Then check if somebody owes money to A (which could've been B, but that is resolved above and wont be a part of this list)
        for (Map.Entry<User, Double> debtOfCToA : whoOwesThisUserMoney.get(userA).entrySet()) {
            if(whatAowesB <= 0) return;
            User userC = debtOfCToA.getKey();
            double amountCOwesA = debtOfCToA.getValue();
            if(userB == userC || amountCOwesA <= 0) continue;
            whatAowesB = resolveDebteeOfA(userA, userB, userC, amountCOwesA, whatAowesB);
        }

        if(whatAowesB <= 0.0) return;

        // Transfer debt to third-party creditors of B
        for (Map.Entry<User, Double> debtOfBToC : whoDoesThisUserOweMoney.get(userB).entrySet()) {
            double amountBOwesC = debtOfBToC.getValue();
            if(whatAowesB <= 0) return;
            User userC = debtOfBToC.getKey();
            if(userA == userC || amountBOwesC <= 0) continue;
            whatAowesB = resolveCreditorOfB(userA, userB, userC, amountBOwesC, whatAowesB);
        }

        //We should have resolved all complexity at this point so
        if(whatAowesB > 0.0){
            //If A still owes B money at this point
            whoDoesThisUserOweMoney.get(userA).merge(userB, whatAowesB, Double::sum);
            whoOwesThisUserMoney.get(userB).merge(userA, whatAowesB, Double::sum);
        }
    }

    private double resolveOneToOne(User userA, User userB, double whatAowesB, double whatBMayBeOwingA){
        if (whatBMayBeOwingA > whatAowesB){
            // B --50--> A --10--> B, resolves to:
            // B --40--> A ---0--> B
            whatBMayBeOwingA -= whatAowesB;
            whoDoesThisUserOweMoney.get(userB).put(userA, whatBMayBeOwingA);
            whoOwesThisUserMoney.get(userA).put(userB, whatBMayBeOwingA);
            //Clear what A might have been owing B
            whoDoesThisUserOweMoney.get(userA).put(userB, 0.0);
            whoOwesThisUserMoney.get(userB).put(userA, 0.0);
            return 0;
        }else{ //If A owes more to B than B to A
            // B --10--> A --50--> B, resolves to:
            // B ---0--> A --40--> B
            //Clear what B might have been owing A
            whoDoesThisUserOweMoney.get(userB).put(userA, 0.0);
            whoOwesThisUserMoney.get(userA).put(userB, 0.0);
            //However, we havn't checked B's creditors yet, so we can't set anything just yet
            //Reduce remaining amount
            return whatAowesB - whatBMayBeOwingA;
        }
    }

    private double resolveDebteeOfA(User userA, User userB, User userC, double amountCOwesA, double whatAowesB){
        if (amountCOwesA < whatAowesB) {
            // C --10--> A --50--> B, resolves to:
            // C --------10------> B
            whoDoesThisUserOweMoney.get(userC).merge(userB, amountCOwesA, Double::sum);
            whoOwesThisUserMoney.get(userB).merge(userC, amountCOwesA, Double::sum);
            // A --------40------> B is resolved below as long as "whatAowesB" is modified correctly
            return whatAowesB - amountCOwesA;
        } else {
            // C --50--> A --10--> B, resolves to:
            // C --------10------> B
            whoDoesThisUserOweMoney.get(userC).merge(userB, whatAowesB, Double::sum);
            whoOwesThisUserMoney.get(userB).merge(userC, whatAowesB, Double::sum);
            // C --40--> A
            whoDoesThisUserOweMoney.get(userC).put(userA, amountCOwesA - whatAowesB);
            whoOwesThisUserMoney.get(userA).put(userC, amountCOwesA - whatAowesB);
            return 0;
        }
    }

    //Zero on stop or nothing left
    private double resolveCreditorOfB(User userA, User userB, User userC, double amountBOwesC, double whatAowesB){
        if (amountBOwesC < whatAowesB) {
            // A --50--> B --10--> C, resolves to:
            // A --------10------> C
            whoDoesThisUserOweMoney.get(userA).merge(userC, amountBOwesC, Double::sum);
            whoOwesThisUserMoney.get(userC).merge(userA, amountBOwesC, Double::sum);
            // A --40--> B is resolved down below as long as "whatAowesB" is modified correctly
            return whatAowesB - amountBOwesC;
        } else {
            // A --10--> B --50--> C, resolves to:
            // A --------10------> C
            whoDoesThisUserOweMoney.get(userA).merge(userC, whatAowesB, Double::sum);
            whoOwesThisUserMoney.get(userC).merge(userA, whatAowesB, Double::sum);
            // B --40--> C
            whoDoesThisUserOweMoney.get(userB).put(userC, amountBOwesC - whatAowesB);
            whoOwesThisUserMoney.get(userC).put(userB, amountBOwesC - whatAowesB);
            return 0;
        }
    }

    /**
     * @param userA  - from
     * @param userB  - to
     * @param amount - amount
     * @return the amount of money "left over" from settling all debt between the users.
     */
    public double processPayment(User userA, User userB, double amount) {
        //the insertion should handle everything, so this is simply
        double totalOwedByAToB = getAmountOwedBy(userA, userB);
        if (totalOwedByAToB <= 0) {
            return amount;
        }

        double delta = totalOwedByAToB - amount;
        whoDoesThisUserOweMoney.get(userA).put(userB, Math.max(delta, 0.0));
        whoOwesThisUserMoney.get(userB).put(userA, Math.max(delta, 0.0));

        //If negative, more money was paid that was owed, else either the exact amount or less money was paid than owed
        return delta < 0 ? 0 : delta;
    }

    private static final Predicate<Map.Entry<User, Double>> NOT_ZERO_OR_NULL = entry -> entry.getValue() != null && entry.getValue() > 0.0;

    /**
     * @return A stream of those the user owe money to
     */
    public Stream<Map.Entry<User, Double>> getDebtsOf(User user) {
        Map<User, Double> thirdPartyOutstandingsByUser = whoDoesThisUserOweMoney.get(user);
        if (thirdPartyOutstandingsByUser == null) {
            return Stream.of();
        }
        return thirdPartyOutstandingsByUser.entrySet().stream().filter(NOT_ZERO_OR_NULL);
    }

    /**
     * @return A stream of those who owe money to the user
     */
    public Stream<Map.Entry<User, Double>> getDebtsTo(User user) {
        Map<User, Double> pplWhoOweMoneyToThisUser = whoOwesThisUserMoney.get(user);
        if (pplWhoOweMoneyToThisUser == null) {
            return Stream.of();
        }
        return pplWhoOweMoneyToThisUser.entrySet().stream().filter(NOT_ZERO_OR_NULL);
    }

    /**
     * @param by - user the amount is owed by
     * @param to - user the amount is owed to
     * @return 0 on nothing owed to "to"
     */
    public double getAmountOwedBy(User by, User to) {
        Map<User, Double> debtorMap = whoDoesThisUserOweMoney.get(by);
        if (debtorMap == null) {
            return 0;
        }

        Double amount = debtorMap.get(to);
        return amount != null ? amount : 0;
    }

    public double totalOwedByUser(User user) {
        return getDebtsOf(user)
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    public double totalOwedToUser(User user) {
        return getDebtsTo(user)
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    public double totalDeptToGroup(User debtor, List<User> creditors) {
        double totalDebt = 0.0;

        if (whoDoesThisUserOweMoney.containsKey(debtor)) {
            for (User creditor : creditors) {
                if (whoDoesThisUserOweMoney.get(debtor).containsKey(creditor)) {
                    totalDebt += whoDoesThisUserOweMoney.get(debtor).get(creditor);
                }
            }
        }

        return totalDebt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DebtGraph").append("\n");
        for (Map.Entry<User, Map<User, Double>> debtEntries : whoDoesThisUserOweMoney.entrySet()) {
            sb.append("\tUser ").append(debtEntries.getKey().name()).append(" owes:").append("\n");
            for (Map.Entry<User, Double> userDebtEntries : debtEntries.getValue().entrySet()) {
                if(userDebtEntries.getValue() <= 0) continue;
                sb.append("\t\t").append(userDebtEntries.getKey().name()).append(" ").append(userDebtEntries.getValue()).append(" bucks\n");
            }
        }
        return sb.toString();
    }


}
