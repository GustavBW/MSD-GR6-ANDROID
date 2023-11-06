package gbw.sdu.msd.backend.services;

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
    public Map<User, Double> whoDoesThisUserOweMoney(User entity) {
        return graph.whoDoesThisUserOweMoney(entity);
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
    public double totalDeptToGroup(User debtor, List<User> creditors) {
        return graph.totalDeptToGroup(debtor,creditors);
    }

    @Override
    public Map<User, Double> whoOwesMoneyToThisUser(User entity) {
        return graph.whoOwesMoneyToThisUser(entity);
    }
}
