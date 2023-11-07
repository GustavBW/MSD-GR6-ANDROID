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

        double whatAowesB = amount;
        double whatBMayBeOwingA = getAmountOwedBy(userB, userA);

        if(whatBMayBeOwingA != 0){
            if (whatBMayBeOwingA > whatAowesB){
                // B --50--> A --10--> B, resolves to:
                // B --40--> A ---0--> B
                whatBMayBeOwingA -= whatAowesB;
                whoDoesThisUserOweMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userA, whatBMayBeOwingA);
                whoOwesThisUserMoney.computeIfAbsent(userA, k -> new HashMap<>()).put(userB, whatBMayBeOwingA);
                //Clear what A might have been owing B
                whoDoesThisUserOweMoney.computeIfAbsent(userA, k -> new HashMap<>()).put(userB, 0.0);
                whoOwesThisUserMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userA, 0.0);
                return;
            }else{ //If A owes more to B than B to A
                // B --10--> A --50--> B, resolves to:
                // B ---0--> A --40--> B
                //Reduce remaining amount
                whatAowesB -= whatBMayBeOwingA;
                //Clear what B might have been owing A
                whoDoesThisUserOweMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userA, 0.0);
                whoOwesThisUserMoney.computeIfAbsent(userA, k -> new HashMap<>()).put(userB, 0.0);
                //However, we havn't checked B's creditors yet, so we can't set anything just yet
            }
        }

        // Then check if somebody owes money to A (which could've been B, but that is resolved above and wont be a part of this list)
        for (Map.Entry<User, Double> debtOfCToA : getDebtsTo(userA).toList()) {
            if(whatAowesB <= 0) break;

            double amountCOwesA = debtOfCToA.getValue();
            User userC = debtOfCToA.getKey();
            if(userB == userC) continue;

            if (amountCOwesA < whatAowesB) {
                // C --10--> A --50--> B, resolves to:
                // C --------10------> B
                whoDoesThisUserOweMoney.computeIfAbsent(userC, k -> new HashMap<>()).merge(userB, amountCOwesA, Double::sum);
                whoOwesThisUserMoney.computeIfAbsent(userB, k -> new HashMap<>()).merge(userC, amountCOwesA, Double::sum);
                // A --------40------> B is resolved below as long as "whatAowesB" is modified correctly
                whatAowesB -= amountCOwesA;
            } else {
                // C --50--> A --10--> B, resolves to:
                // C --------10------> B
                whoDoesThisUserOweMoney.computeIfAbsent(userC, k -> new HashMap<>()).merge(userB, whatAowesB, Double::sum);
                whoOwesThisUserMoney.computeIfAbsent(userB, k -> new HashMap<>()).merge(userC, whatAowesB, Double::sum);
                // C --40--> A
                whoDoesThisUserOweMoney.computeIfAbsent(userC, k -> new HashMap<>()).put(userA, amountCOwesA - whatAowesB);
                whoOwesThisUserMoney.computeIfAbsent(userA, k -> new HashMap<>()).put(userC, amountCOwesA - whatAowesB);
                return;
            }
        }

        // Transfer debt to third-party creditors of B
        for (Map.Entry<User, Double> debtOfBToC : getDebtsOf(userB).toList()) {
            if(whatAowesB <= 0) break; //We could return here

            User userC = debtOfBToC.getKey();
            double amountBOwesC = debtOfBToC.getValue();
            if(userA == userC) continue;

            if (amountBOwesC < whatAowesB) {
                // A --50--> B --10--> C, resolves to:
                // A --------10------> C
                whoDoesThisUserOweMoney.computeIfAbsent(userA, k -> new HashMap<>()).merge(userC, amountBOwesC, Double::sum);
                whoOwesThisUserMoney.computeIfAbsent(userC, k -> new HashMap<>()).merge(userA, amountBOwesC, Double::sum);
                // A --40--> B is resolved down below as long as "whatAowesB" is modified correctly
                whatAowesB -= amountBOwesC;
            } else {
                // A --10--> B --50--> C, resolves to:
                // A --------10------> C
                whoDoesThisUserOweMoney.computeIfAbsent(userA, k -> new HashMap<>()).merge(userC, whatAowesB, Double::sum);
                whoOwesThisUserMoney.computeIfAbsent(userC, k -> new HashMap<>()).merge(userA, whatAowesB, Double::sum);
                // B --40--> C
                whoDoesThisUserOweMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userC, amountBOwesC - whatAowesB);
                whoOwesThisUserMoney.computeIfAbsent(userC, k -> new HashMap<>()).put(userB, amountBOwesC - whatAowesB);
                return;
            }
        }

        //We should have resolved all complexity at this point so
        if(whatAowesB > 0.0){
            //If A still owes B money at this point
            whoDoesThisUserOweMoney.computeIfAbsent(userA, k -> new HashMap<>()).merge(userB, whatAowesB, Double::sum);
            whoOwesThisUserMoney.computeIfAbsent(userB, k -> new HashMap<>()).merge(userA, whatAowesB, Double::sum);
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
