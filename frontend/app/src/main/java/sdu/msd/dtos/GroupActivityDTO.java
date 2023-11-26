package sdu.msd.dtos;

import java.util.List;

public class GroupActivityDTO {
    private UserDTO creditor;
    private double amount;
    private List<UserDTO> debtees;
    private boolean isExpense;

    public GroupActivityDTO(UserDTO creditor, double amount, List<UserDTO> debtees, boolean isExpense) {
        this.creditor = creditor;
        this.amount = amount;
        this.debtees = debtees;
        this.isExpense = isExpense;
    }

    public UserDTO getCreditor() {
        return creditor;
    }

    public double getAmount() {
        return amount;
    }

    public List<UserDTO> getDebtees() {
        return debtees;
    }

    public boolean isExpense() {
        return isExpense;
    }

}
