package sdu.msd.dtos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DebtDTO {
    private final int idOfCreditor;
    private final double amount;

    public DebtDTO(int idOfCreditor, double amount) {
        this.idOfCreditor = idOfCreditor;
        this.amount = amount;
    }

    public int getIdOfCreditor() {
        return idOfCreditor;
    }

    public double getAmount() {
        return amount;
    }
}
