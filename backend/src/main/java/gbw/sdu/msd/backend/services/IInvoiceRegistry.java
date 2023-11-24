package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.InvoiceDTO;
import gbw.sdu.msd.backend.models.User;

import java.util.List;

public interface IInvoiceRegistry {
    /**
     * @param user
     * @param amount -1 for all
     * @return
     */
    List<InvoiceDTO> getInvoices(User user, int listLength);
    void addInvoice(User debtee, User creditor, double amount);
}
