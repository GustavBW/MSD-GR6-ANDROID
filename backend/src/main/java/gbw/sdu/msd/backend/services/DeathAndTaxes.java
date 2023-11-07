package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.models.Debt;
import gbw.sdu.msd.backend.models.DebtGraph;
import gbw.sdu.msd.backend.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeathAndTaxes implements IDeptService {

    private final DebtGraph graph = new DebtGraph();

    @Override
    public void addDebt(User debtor, User creditor, double amount) {
        graph.recordDebt(debtor, creditor, amount);
    }

    @Override
    public List<Debt> whoDoesThisUserOweMoney(User entity) {
        return graph.getDebtsOf(entity)
                .map(debt -> new Debt(debt.getKey(), debt.getValue()))
                .toList();
    }

    @Override
    public double totalOwedByUser(User entity) {
        return graph.totalOwedByUser(entity);
    }

    @Override
    public double totalOwedToUser(User user) {
        return graph.totalOwedToUser(user);
    }

    @Override
    public double processPayment(User userA, User userB, double amount) {
        return graph.processPayment(userA, userB, amount);
    }

    @Override
    public double getAmountOwedBy(User userA, User userB) {
        return graph.getAmountOwedBy(userA, userB);
    }

    @Override
    public double totalDeptToGroup(User debtor, List<User> creditors) {
        return graph.totalDeptToGroup(debtor,creditors);
    }

    @Override
    public List<Debt> whoOwesMoneyToThisUser(User entity) {
        return graph.getDebtsTo(entity)
                .map(debt -> new Debt(debt.getKey(), debt.getValue()))
                .toList();
    }
}
