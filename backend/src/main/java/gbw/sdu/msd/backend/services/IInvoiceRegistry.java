package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.InvoiceDTO;
import gbw.sdu.msd.backend.models.User;

import java.util.List;

public interface IInvoiceRegistry {
    /**
     * @param user
     * @param listLength -1 for all
     * @return
     */
    List<InvoiceDTO> get(User user, int listLength);
    void add(User debtee, User creditor, double amount);
    InvoiceDTO get(int id);
}
