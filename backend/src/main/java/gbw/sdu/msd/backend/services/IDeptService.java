package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.models.Debt;
import gbw.sdu.msd.backend.models.User;

import java.util.List;
import java.util.Map;

public interface IDeptService {



    void addDebt(User debtor, User creditor, double amount);
    List<Debt> whoDoesThisUserOweMoney(User entity);
    double totalOwedByUser(User entity);
    double totalOwedToUser(User user);
    double totalDeptToGroup(User debtor, List<User> creditors);
    List<Debt> whoOwesMoneyToThisUser(User entity);
}
