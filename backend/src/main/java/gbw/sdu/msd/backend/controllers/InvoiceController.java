package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.InvoiceDTO;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.IDeptService;
import gbw.sdu.msd.backend.services.IGroupRegistry;
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
    @Autowired
    public InvoiceController(IUserRegistry userRegistry, IGroupRegistry groupRegistry,IDeptService deptService){
        this.userRegistry = userRegistry;
        this.groupRegistry = groupRegistry;
        this.deptService = deptService;
    }

    /**
     * Gives the invoices for the given user.
     * Optionally use query param "amount" to limit length
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/{userId}")
    public @ResponseBody ResponseEntity<List<InvoiceDTO>> getInvoicesForUser(Integer userId, @RequestParam(required = false) Integer amount){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(List.of());
    }


}
