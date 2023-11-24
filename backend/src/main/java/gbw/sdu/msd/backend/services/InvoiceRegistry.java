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
    private final Map<Integer, InvoiceDTO> invoicePerId = new HashMap<>();
    private static int idOfNext = 0;

    @Override
    public List<InvoiceDTO> get(User user, int listLength) {
        List<InvoiceDTO> invoices = invoicesPerUser.computeIfAbsent(user, k -> new ArrayList<>());
        if(invoices.size() < listLength || listLength == -1){
            return invoices;
        }
        return invoices.subList(invoices.size() - listLength, invoices.size());
    }

    @Override
    public void add(User debtee, User creditor, double amount) {
        InvoiceDTO invoice = new InvoiceDTO(idOfNext++, UserDTO.of(debtee), UserDTO.of(creditor), amount);
        invoicePerId.put(invoice.id(), invoice);
        invoicesPerUser.computeIfAbsent(debtee, k -> new ArrayList<>()).add(invoice);
        invoicesPerUser.computeIfAbsent(creditor, k -> new ArrayList<>()).add(invoice);
    }

    @Override
    public InvoiceDTO get(int id) {
        return invoicePerId.get(id);
    }
}
