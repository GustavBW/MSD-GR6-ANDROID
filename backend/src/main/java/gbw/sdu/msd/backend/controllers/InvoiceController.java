package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.InvoiceDTO;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.IDeptService;
import gbw.sdu.msd.backend.services.IGroupRegistry;
import gbw.sdu.msd.backend.services.IInvoiceRegistry;
import gbw.sdu.msd.backend.services.IUserRegistry;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/invoices")
public class InvoiceController {

    private final IUserRegistry userRegistry;
    private final IGroupRegistry groupRegistry;
    private final IDeptService deptService;
    private final IInvoiceRegistry invoiceRegistry;
    @Autowired
    public InvoiceController(IUserRegistry userRegistry, IGroupRegistry groupRegistry,IDeptService deptService,IInvoiceRegistry invoiceRegistry){
        this.userRegistry = userRegistry;
        this.groupRegistry = groupRegistry;
        this.deptService = deptService;
        this.invoiceRegistry = invoiceRegistry;
    }

    /**
     * Gets the invoices for the given user.
     * Optionally use query param "amount" to limit length
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/{userId}")
    public @ResponseBody ResponseEntity<List<InvoiceDTO>> getInvoicesForUser(@PathVariable Integer userId, @RequestParam(required = false) Integer amount){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        if(amount == null){
            amount = -1;
        }
        return ResponseEntity.ok(invoiceRegistry.get(user, amount));
    }

    /**
     * Gets a specific invoice.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such invoice"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/by-id/{invoiceId}")
    public @ResponseBody ResponseEntity<InvoiceDTO> getInvoicesForUser(@PathVariable Integer invoiceId){
        InvoiceDTO invoice = invoiceRegistry.get(invoiceId);
        if(invoice == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoice);
    }


}
