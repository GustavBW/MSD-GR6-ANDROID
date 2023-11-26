package sdu.msd.dtos;

import java.util.List;

public class GroupActivityDTO {
    private UserDTO creditor;
    private double amount;
    private List<UserDTO> debtes;
    private boolean isExpense;

    public GroupActivityDTO(UserDTO creditor, double amount, List<UserDTO> debtes, boolean isExpense) {
        this.creditor = creditor;
        this.amount = amount;
        this.debtes = debtes;
        this.isExpense = isExpense;
    }

    public UserDTO getCreditor() {
        return creditor;
    }

    public double getAmount() {
        return amount;
    }

    public List<UserDTO> getDebtes() {
        return debtes;
    }

    public boolean isExpense() {
        return isExpense;
    }

}
