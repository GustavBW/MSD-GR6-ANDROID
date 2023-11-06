package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.models.User;

import java.util.List;
import java.util.Map;

public interface IDeptService {



    void addDebt(User debtor, User creditor, double amount);
    Map<User,Double> whoDoesThisUserOweMoney(User entity);
    double totalOwedByUser(User entity);
    double totalOwedToUser(User user);
    double totalDeptToGroup(User debtor, List<User> creditors);
    Map<User,Double> whoOwesMoneyToThisUser(User entity);
}
