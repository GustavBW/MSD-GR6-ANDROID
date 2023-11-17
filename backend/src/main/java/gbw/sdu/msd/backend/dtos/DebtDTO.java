package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.Debt;

import java.util.Collection;
import java.util.List;

public record DebtDTO(int idOfCreditor, double amount) {
    public static DebtDTO of(Debt debt){
        return new DebtDTO(debt.creditor().id(), debt.amount());
    }
    public static List<DebtDTO> of(Collection<Debt> debts){
        return debts.stream().map(DebtDTO::of).toList();
    }
}
