package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.InvoiceDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceRegistry implements IInvoiceRegistry {

    private final Map<User,List<InvoiceDTO>> invoicesPerUser = new HashMap<>();

    @Override
    public List<InvoiceDTO> getInvoices(User user, int listLength) {
        List<InvoiceDTO> invoices = invoicesPerUser.computeIfAbsent(user, k -> new ArrayList<>());
        if(invoices.size() < listLength || listLength == -1){
            return invoices;
        }
        return invoices.subList(invoices.size() - (listLength + 1), invoices.size() - 1);
    }

    @Override
    public void addInvoice(User debtee, User creditor, double amount) {
        InvoiceDTO invoice = new InvoiceDTO(UserDTO.of(debtee), UserDTO.of(creditor), amount);
        invoicesPerUser.computeIfAbsent(debtee, k -> new ArrayList<>()).add(invoice);
        invoicesPerUser.computeIfAbsent(creditor, k -> new ArrayList<>()).add(invoice);
    }
}
