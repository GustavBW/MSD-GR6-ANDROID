package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.GroupActivityDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.Debt;
import gbw.sdu.msd.backend.models.DebtGraph;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeathAndTaxes implements IDeptService {

    private final DebtGraph graph = new DebtGraph();
    private final IInvoiceRegistry invoiceRegistry;
    private final IGroupRegistry groupRegistry;

    @Autowired
    public DeathAndTaxes(IInvoiceRegistry invoiceRegistry, IGroupRegistry groupRegistry){
        this.invoiceRegistry = invoiceRegistry;
        this.groupRegistry = groupRegistry;
    }

    @Override
    public void distributeDebt(User debtor, List<User> creditors, double amount) {
        double amountPerCreditor = amount / creditors.size();
        for(User creditor : creditors){
            addDebt(debtor, creditor, amountPerCreditor);
        }
    }

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
        double remaining = graph.processPayment(userA, userB, amount);
        double actualPayedAmount = amount - remaining;
        invoiceRegistry.add(userA, userB, actualPayedAmount);
        return remaining;
    }

    @Override
    public double processPayment(User user, Group group, double amount) {
        double remaining = amount;
        for(User creditor : group.users()){
            double userSpecificDebt = getAmountOwedBy(user, creditor);
            if(userSpecificDebt <= 0) continue;
            if(remaining <= 0) break;
            remaining = processPayment(user, creditor, remaining);
        }
        groupRegistry.addActivity(group.id(),
                new GroupActivityDTO(
                        UserDTO.of(user),
                        amount - remaining,
                        UserDTO.of(group.users()),
                        false
                )
        );
        return remaining;
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
