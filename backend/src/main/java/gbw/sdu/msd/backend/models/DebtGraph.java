package gbw.sdu.msd.backend.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebtGraph {
    private final Map<User, Map<User,Double>> whoDoesThisUserOweMoney;
    private final Map<User, Map<User,Double>> whoOwesThisUserMoney; // Reverse graph for creditors.

    public DebtGraph() {
        this.whoDoesThisUserOweMoney = new HashMap<>();
        this.whoOwesThisUserMoney = new HashMap<>();
    }

    public void addDebt(User debtor, User creditor, double amount) {
        // Check if debtor already owes creditor.
        if (whoDoesThisUserOweMoney.computeIfAbsent(debtor, k -> new HashMap<>()).containsKey(creditor)) {
            double currentDebt = whoDoesThisUserOweMoney.get(debtor).get(creditor);

            // Update the debt amount and swap roles if necessary.
            double newDebt = currentDebt - amount;
            if (newDebt > 0) {
                whoDoesThisUserOweMoney.get(debtor).put(creditor, newDebt);
                whoOwesThisUserMoney.get(creditor).put(debtor, newDebt);
            } else if (newDebt < 0) {
                whoDoesThisUserOweMoney.get(debtor).remove(creditor);
                whoOwesThisUserMoney.get(creditor).remove(debtor);
                whoDoesThisUserOweMoney
                        .computeIfAbsent(creditor, user -> new HashMap<>())
                        .merge(debtor, -newDebt, Double::sum);
                whoOwesThisUserMoney
                        .computeIfAbsent(debtor, user -> new HashMap<>())
                        .merge(creditor, -newDebt, Double::sum);
            } else {
                whoDoesThisUserOweMoney.get(debtor).remove(creditor);
                whoOwesThisUserMoney.get(creditor).remove(debtor);
            }
        } else {
            // No existing debt, simply add the new debt.
            whoDoesThisUserOweMoney
                    .computeIfAbsent(debtor, user -> new HashMap<>())
                    .merge(creditor, amount, Double::sum);
            whoOwesThisUserMoney
                    .computeIfAbsent(creditor, user -> new HashMap<>())
                    .merge(debtor, amount, Double::sum);
        }
    }

    /**
     * Debts of user
     */
    public Map<User,Double> whoDoesThisUserOweMoney(User entity) {
        return whoDoesThisUserOweMoney.get(entity);
    }
    public double totalOwedByUser(User entity){
        double total = 0;
        for(Map.Entry<User,Double> debt : whoDoesThisUserOweMoney(entity).entrySet()){
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

    /**
     * Who owes this user money
     */
    public Map<User,Double> whoOwesMoneyToThisUser(User entity) {
        return whoOwesThisUserMoney.get(entity);
    }

}
