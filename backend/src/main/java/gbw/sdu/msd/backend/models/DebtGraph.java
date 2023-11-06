package gbw.sdu.msd.backend.models;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class DebtGraph {
    private final Map<User, Map<User,Double>> whoDoesThisUserOweMoney;
    private final Map<User, Map<User,Double>> whoOwesThisUserMoney; // Reverse graph for creditors.

    public DebtGraph() {
        this.whoDoesThisUserOweMoney = new HashMap<>();
        this.whoOwesThisUserMoney = new HashMap<>();
    }

    /**
     * @param userA - the user that owes money
     * @param userB - who A owes money to
     * @param amount - how much
     */
    public void recordDebt(User userA, User userB, double amount) {
        if(userA == userB) return;

        double remainingAmount = amount + getAmountOwedBy(userA,userB);
        List<Map.Entry<User,Double>> thirdPartyCreditors = new ArrayList<>(getNonZeroDebts(userB).toList());

        //If B owes A, A is already a creditor and is placed as the first creditor evaluated
        if(getAmountOwedBy(userB, userA) > 0){
            thirdPartyCreditors.sort((entry1, entry2) -> {
                if(entry1.getKey() == userA) return -1;
                if(entry2.getKey() == userA) return 1;
                return 0;
            });
        }

        //Check thirdparty outstandings for userB.
        for(Map.Entry<User,Double> debtByTo : thirdPartyCreditors){
            if(remainingAmount <= 0){ //It should never be less than 0, but... for safety
                //All creditors of the creditor have been paid as much as possible
                return;
            }
            User userC = debtByTo.getKey();
            double amountBOwesC = debtByTo.getValue();
            if(amountBOwesC < remainingAmount){
                //clear what B owes C
                whoDoesThisUserOweMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userC, 0.0);
                whoOwesThisUserMoney.computeIfAbsent(userC, k -> new HashMap<>()).put(userB, 0.0);
                //transfer as much debt from A->B to A->C
                recordDebt(userA, userC, amountBOwesC);
                remainingAmount -= amountBOwesC;
            }else if(amountBOwesC == remainingAmount){
                recordDebt(userA, userC, amountBOwesC);
                return;
            }else if(amountBOwesC > remainingAmount){ //If B owes C more than A owes B
                //reduce the amount B owes C by the amount A owes B
                whoDoesThisUserOweMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userC, amountBOwesC - remainingAmount);
                whoOwesThisUserMoney.computeIfAbsent(userC, k -> new HashMap<>()).put(userB, amountBOwesC - remainingAmount);
                //set A to owe C said amount
                recordDebt(userA, userC, remainingAmount);
                return;
            }
        }

        //This is only reached if what A owes B is greater than what B owes to anyone else combined
        whoDoesThisUserOweMoney.computeIfAbsent(userA, k -> new HashMap<>()).put(userB, Math.max(remainingAmount, 0.0));
        whoOwesThisUserMoney.computeIfAbsent(userB, k -> new HashMap<>()).put(userA, Math.max(remainingAmount, 0.0));
    }

    private void balanceDebts(User from, User to) {
        Map<User, Double> debtorDebts = whoDoesThisUserOweMoney.get(from);
        Map<User, Double> creditorDebts = whoDoesThisUserOweMoney.get(to);

        if (debtorDebts != null && creditorDebts != null) {
            for (User intermediary : creditorDebts.keySet()) {
                double debtToCreditor = creditorDebts.get(intermediary);
                double debtFromDebtor = debtorDebts.getOrDefault(intermediary, 0.0);

                if (debtToCreditor > debtFromDebtor) {
                    double transferAmount = Math.min(debtToCreditor - debtFromDebtor, debtorDebts.get(to));
                    debtorDebts.compute(to, (k, v) -> v - transferAmount);
                    creditorDebts.compute(intermediary, (k, v) -> v - transferAmount);
                    //Recursion. lets go
                    recordDebt(from, intermediary, transferAmount);
                }
            }
        }
    }

    /**
     * @param userA - from
     * @param userB - to
     * @param amount - amount
     * @return the amount of money "left over" from settling all debt between the users.
     */
    public double processPayment2(User userA, User userB, double amount) {
        //First check if userA even owes userB anything.
        double totalOwedByAToB = getAmountOwedBy(userA, userB);
        if(totalOwedByAToB <= 0){
            return amount;
        }

        double remainingAmount = amount;
        //Check thirdparty outstandings by userB.
        for(Map.Entry<User,Double> debtByTo : getNonZeroDebts(userB).toList()){
            if(remainingAmount <= 0){ //It should never be less than 0, but... for safety
                //All creditors of the creditor have been paid as much as possible
                break;
            }
            //Recursion here we go
            remainingAmount = processPayment2(userB, debtByTo.getKey(), remainingAmount);
        }

        double delta = totalOwedByAToB - amount;
        whoDoesThisUserOweMoney.get(userA).put(userB, Math.max(delta, 0.0));
        whoOwesThisUserMoney.get(userB).put(userA, Math.max(delta, 0.0));

        //If negative, more money was paid that was owed
        return Math.abs(delta);
    }

    private static final Predicate<Map.Entry<User,Double>> NOT_ZERO_OR_NULL = entry -> entry.getValue() != null && entry.getValue() > 0.0;

    private Stream<Map.Entry<User,Double>> getNonZeroDebts(User user){
        Map<User,Double> thirdPartyOutstandingsByUser = whoDoesThisUserOweMoney.get(user);
        if(thirdPartyOutstandingsByUser == null){
            return Stream.of();
        }
        return thirdPartyOutstandingsByUser.entrySet().stream().filter(NOT_ZERO_OR_NULL);
    }

    private Stream<Map.Entry<User,Double>> getNonZeroOwedAmountsTo(User user){
        Map<User,Double> pplWhoOweMoneyToThisUser = whoOwesThisUserMoney.get(user);
        if(pplWhoOweMoneyToThisUser == null){
            return Stream.of();
        }
        return pplWhoOweMoneyToThisUser.entrySet().stream().filter(NOT_ZERO_OR_NULL);
    }

    public void processPayment(User from, User to, double amount) {
        // Check if payer owes money to payee
        double debt = whoDoesThisUserOweMoney.getOrDefault(from, Collections.emptyMap())
                .getOrDefault(to, 0.0);

        if (debt >= amount) {
            // Payer fully pays payee
            whoDoesThisUserOweMoney.get(from).put(to, debt - amount);
            whoOwesThisUserMoney.get(to).put(from, debt - amount);

            // Clear the debt from B to C as well
            whoDoesThisUserOweMoney.get(to).put(from, 0.0);
            whoOwesThisUserMoney.get(from).put(to, 0.0);

            // Balance debts after processing the payment
            balanceDebts(from, to);
            return;
        }

        if (debt > 0) {
            // Payer partially pays payee
            whoDoesThisUserOweMoney.get(from).put(to, 0.0);
            whoOwesThisUserMoney.get(to).put(from, 0.0);
            amount -= debt;
        }

        // Handle money redirects
        Map<User, Double> payeeDebts = whoDoesThisUserOweMoney.get(to);
        if (payeeDebts != null) {
            for (User creditor : payeeDebts.keySet()) {
                double debtToCreditor = payeeDebts.get(creditor);
                if (amount >= debtToCreditor) {
                    whoDoesThisUserOweMoney.get(to).put(creditor, 0.0);
                    whoOwesThisUserMoney.get(creditor).put(to, 0.0);
                    amount -= debtToCreditor;
                } else {
                    whoDoesThisUserOweMoney.get(to).put(creditor, debtToCreditor - amount);
                    whoOwesThisUserMoney.get(creditor).put(to, debtToCreditor - amount);
                    amount = 0;
                    break;
                }
            }
        }

        // Update remaining amount as debt from payer to payee
        whoDoesThisUserOweMoney.computeIfAbsent(from, k -> new HashMap<>())
                .merge(to, amount, Double::sum);
        whoOwesThisUserMoney.computeIfAbsent(to, k -> new HashMap<>())
                .merge(from, amount, Double::sum);

        // Balance debts after processing the payment
        balanceDebts(from, to);
    }

    /**
     * @param by - user the amount is owed by
     * @param to - user the amount is owed to
     * @return 0 on nothing owed to "to"
     */
    public double getAmountOwedBy(User by, User to){
        Map<User, Double> debtorMap = whoDoesThisUserOweMoney.get(by);
        if (debtorMap == null) {
            return 0;
        }

        Double amount = debtorMap.get(to);
        return amount != null ? amount : 0;
    }



    /**
     * Who owes this user money
     */
    public Map<User,Double> whoOwesMoneyToThisUser(User entity) {
        return whoOwesThisUserMoney.computeIfAbsent(entity, k -> new HashMap<>());
    }
    /**
     * Debts of user
     */
    public Map<User,Double> whoDoesThisUserOweMoney(User entity) {
        return whoDoesThisUserOweMoney.computeIfAbsent(entity, k -> new HashMap<>());
    }
    public double totalOwedByUser(User user){
        double total = 0;
        for(Map.Entry<User,Double> debt : whoDoesThisUserOweMoney(user).entrySet()){
            total += debt.getValue() == null ? 0 : debt.getValue();
        }
        return total;
    }
    public double totalOwedToUser(User user){
        double total = 0;
        for(Map.Entry<User,Double> debt : whoOwesMoneyToThisUser(user).entrySet()){
            total += debt.getValue() == null ? 0 : debt.getValue();
        }
        return total;
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
    public String toString(){
        StringBuilder sb = new StringBuilder("DebtGraph").append("\n");
        for(Map.Entry<User, Map<User,Double>> debtEntries : whoDoesThisUserOweMoney.entrySet()){
            sb.append("\tUser ").append(debtEntries.getKey().name()).append(" owes:").append("\n");
            for(Map.Entry<User,Double> userDebtEntries : debtEntries.getValue().entrySet()){
                sb.append("\t\t").append(userDebtEntries.getKey().name()).append(" ").append(userDebtEntries.getValue()).append(" bucks\n");
            }
        }
        return sb.toString();
    }


}
