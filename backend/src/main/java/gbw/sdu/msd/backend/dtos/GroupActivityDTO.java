package gbw.sdu.msd.backend.dtos;

import java.util.List;

/**
 * @param creditor / debtee
 * @param amount
 * @param debtees / creditors
 * @param isExpense if false, this is a payment. Much like an invoice. The creditor is the debtee, and the debtees are the creditors whom where payed.
 */
public record GroupActivityDTO(UserDTO creditor, double amount, List<UserDTO> debtees, boolean isExpense) {
}
