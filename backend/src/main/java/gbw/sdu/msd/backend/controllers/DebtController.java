package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.DebtDTO;
import gbw.sdu.msd.backend.dtos.GroupActivityDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/api/v1/debt")
public class DebtController {

    private final IUserRegistry userRegistry;
    private final IGroupRegistry groupRegistry;
    private final IDeptService deptService;
    private final IInvoiceRegistry invoiceRegistry;
    @Autowired
    public DebtController(IUserRegistry userRegistry, IGroupRegistry groupRegistry, IDeptService deptService, IInvoiceRegistry invoiceRegistry){
        this.userRegistry = userRegistry;
        this.groupRegistry = groupRegistry;
        this.deptService = deptService;
        this.invoiceRegistry = invoiceRegistry;
    }

    /**
     * Distributes debt of UserA between all users listed. I.e. api/v1/debt/{UserA}/distribute/200?creditors=1,2,3,4.
     * Append the groupId if this is a group activity.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "UserA not found or any creditor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path = "/{userA}/distribute/{amount}")
    public @ResponseBody ResponseEntity<Boolean> distributeDebt(@PathVariable Integer userA, @PathVariable Double amount, @RequestParam(name = "creditors") List<Integer> creditorIds, @RequestParam(required = false) Integer groupId){
        User found = userRegistry.get(userA);
        if(found == null){
            return ResponseEntity.notFound().build();
        }
        List<User> creditors = new ArrayList<>();
        for(Integer i : creditorIds){
            User creditor = userRegistry.get(i);
            if(creditor == null){
                return ResponseEntity.notFound().build();
            }
            creditors.add(creditor);
        }
        if(amount == null || amount <= 0){
            return ResponseEntity.badRequest().build();
        }
        if(groupId != null && groupRegistry.get(groupId) != null){
            groupRegistry.addActivity(groupId,
                    new GroupActivityDTO(
                            UserDTO.of(found),
                            amount,
                            UserDTO.of(creditors),
                            true
                    ));
        }
        deptService.distributeDebt(found, creditors, amount);
        return ResponseEntity.ok(true);
    }

    /**
     * Distributes debt of the debtees to userA. I.e. api/v1/debt/{UserA}/distribute/200/reverse?debtees=1,2,3,4.
     * Append the groupId if this is a group activity.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "UserA not found or any creditor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path = "/{userA}/reverse-distribute/{amount}")
    public @ResponseBody ResponseEntity<Boolean> distributeDebtReverse(@PathVariable Integer userA, @PathVariable Double amount, @RequestParam(name = "debtees") List<Integer> debteeIds, @RequestParam(required = false) Integer groupId){
        User creditor = userRegistry.get(userA);
        if(creditor == null){
            return ResponseEntity.notFound().build();
        }
        List<User> debtees = new ArrayList<>();
        for(Integer i : debteeIds){
            User debtee = userRegistry.get(i);
            if(debtee == null){
                return ResponseEntity.notFound().build();
            }
            debtees.add(debtee);
        }
        if(amount == null || amount <= 0){
            return ResponseEntity.badRequest().build();
        }
        if(groupId != null && groupRegistry.get(groupId) != null){
            groupRegistry.addActivity(groupId,
                    new GroupActivityDTO(
                            UserDTO.of(creditor),
                            amount,
                            UserDTO.of(debtees),
                            true
                    ));
        }
        deptService.distributeDebtReverse(creditor, debtees, amount);
        return ResponseEntity.ok(true);
    }

    /**
     * How much money a user owes a certain group in total
     * @param groupId id of group
     * @param userId id of user
     * @return How much money a user owes a certain group in total
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such group or no such user"),
            @ApiResponse(responseCode = "400", description = "Missing group id or missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userId}/to-group/{groupId}")
    public @ResponseBody ResponseEntity<Double> getHowMuchUserOwesGroup(@PathVariable Integer userId, @PathVariable Integer groupId){
        if(userId == null || groupId == null){
            return ResponseEntity.badRequest().build();
        }
        Group group = groupRegistry.get(groupId);
        User user = userRegistry.get(userId);
        if(user == null || group == null){
            return ResponseEntity.notFound().build();
        }
        double sumOwed = deptService.totalDeptToGroup(user, group.users());
        return ResponseEntity.ok(sumOwed);
    }

    /**
     * How much money a user owes another user
     * @param userA id of userA
     * @param userB id of userB
     * @return How much money a user owes another user
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userA}/to-user/{userB}")
    public @ResponseBody ResponseEntity<Double> getHowMuchUserOwesUser(@PathVariable Integer userA, @PathVariable Integer userB){
        if(userA == null || userB == null){
            return ResponseEntity.badRequest().build();
        }
        User userBFound = userRegistry.get(userB);
        User userAFound = userRegistry.get(userA);
        if(userAFound == null || userBFound == null){
            return ResponseEntity.notFound().build();
        }
        double sumOwed = deptService.getAmountOwedBy(userAFound, userBFound);
        return ResponseEntity.ok(sumOwed);
    }

    /**
     * Add debt between two users.
     * If this is in the context of a group, a groupId can be provided as a query parameter to append the proper group activity.
     * Duly note that if the group does not exist, this is ignored.
     * @param userA id of user who owes money
     * @param userB id of user who is receiving this money
     * @return Add debt between two users
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id or missing amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{userA}/add/{userB}/amount/{amount}")
    public @ResponseBody ResponseEntity<Boolean> addDebt(@PathVariable Integer userA, @PathVariable Integer userB, @PathVariable Double amount, @RequestParam(required = false) Integer groupId){
        if(userA == null || userB == null || amount == null){
            return ResponseEntity.badRequest().build();
        }
        User userBFound = userRegistry.get(userB);
        User userAFound = userRegistry.get(userA);
        if(userAFound == null || userBFound == null){
            return ResponseEntity.notFound().build();
        }
        deptService.addDebt(userAFound, userBFound, amount);
        if(groupId != null && groupRegistry.get(groupId) != null){
            groupRegistry.addActivity(groupId,
                    new GroupActivityDTO(
                            UserDTO.of(userBFound),
                            amount,
                            UserDTO.of(List.of(userAFound)),
                            true
                    )
            );
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Who does a user owe money
     * @param userA id of user
     * @return Who does a user owe money
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userA}/creditors")
    public @ResponseBody ResponseEntity<List<DebtDTO>> getWhoOwesThisUserMoney(@PathVariable Integer userA){
        if(userA == null ){
            return ResponseEntity.badRequest().build();
        }
        User userAFound = userRegistry.get(userA);
        if(userAFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DebtDTO.of(deptService.whoDoesThisUserOweMoney(userAFound)));
    }

    /**
     * Who owes money to this user
     * @param userA id of user
     * @return Who owes money to this user
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/who-owes/{userA}")
    public @ResponseBody ResponseEntity<List<DebtDTO>> whoOwesMoneyToThisUser(@PathVariable Integer userA){
        if(userA == null ){
            return ResponseEntity.badRequest().build();
        }
        User userAFound = userRegistry.get(userA);
        if(userAFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DebtDTO.of(deptService.whoOwesMoneyToThisUser(userAFound)));
    }

    /**
     * Get total amount owed by user to anyone
     * @param userA id of user
     * @return Get total amount owed by user to anyone
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userA}")
    public @ResponseBody ResponseEntity<Double> totalDebtByUser(@PathVariable Integer userA){
        if(userA == null ){
            return ResponseEntity.badRequest().build();
        }
        User userAFound = userRegistry.get(userA);
        if(userAFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deptService.totalOwedByUser(userAFound));
    }

    /**
     * Get total amount owed to user by anyone
     * @param userA id of user
     * @return  Get total amount owed to user by anyone
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/who-owes/{userA}/total")
    public @ResponseBody ResponseEntity<Double> totalDebtOwedToUser(@PathVariable Integer userA){
        if(userA == null ){
            return ResponseEntity.badRequest().build();
        }
        User userAFound = userRegistry.get(userA);
        if(userAFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deptService.totalOwedToUser(userAFound));
    }

    /**
     * Pay debt between two users.
     * If the amount paid was too much, the remainder is returned.
     * Append the groupId if this was a group activity.
     * @param userA id of user who pays
     * @param userB id of user to receive payment
     * @param amount how much
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id or missing amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{userA}/pay/{userB}/amount/{amount}")
    public @ResponseBody ResponseEntity<Double> totalDebtOwedToUser(@PathVariable Integer userA, @PathVariable Integer userB, @PathVariable Double amount, @RequestParam(required = false) Integer groupId){
        if(userA == null || userB == null || amount == null){
            return ResponseEntity.badRequest().build();
        }
        User userBFound = userRegistry.get(userB);
        User userAFound = userRegistry.get(userA);
        if(userAFound == null || userBFound == null){
            return ResponseEntity.notFound().build();
        }
        double remainder = deptService.processPayment(userAFound, userBFound, amount);
        double actualPayedAmount = amount - remainder;
        if(groupId != null && groupRegistry.get(groupId) != null){
            groupRegistry.addActivity(groupId,
                    new GroupActivityDTO(
                            UserDTO.of(userAFound),
                            actualPayedAmount,
                            UserDTO.of(List.of(userBFound)),
                            false
                    ));
        }
        return ResponseEntity.ok(remainder);
    }

    /**
     * Pays the users dept to the group and returns the remaining dept to the group
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such users or no such group"),
            @ApiResponse(responseCode = "400", description = "Missing user id or missing amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{userId}/pay-group/{groupId}/amount/{amount}")
    public @ResponseBody ResponseEntity<Double> payGroupDept(@PathVariable Integer userId, @PathVariable Integer groupId, @PathVariable Double amount){
        if(userId == null || groupId == null || amount == null){
            return ResponseEntity.badRequest().build();
        }
        User user = userRegistry.get(userId);
        Group group = groupRegistry.get(groupId);
        if(user == null || group == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deptService.processPayment(user, group, amount));
    }
}
