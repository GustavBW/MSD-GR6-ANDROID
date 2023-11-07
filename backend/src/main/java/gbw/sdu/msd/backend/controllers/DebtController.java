package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.models.Debt;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.IDeptService;
import gbw.sdu.msd.backend.services.IGroupRegistry;
import gbw.sdu.msd.backend.services.INotificationService;
import gbw.sdu.msd.backend.services.IUserRegistry;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/debt")
public class DebtController {

    private final IUserRegistry userRegistry;
    private final IGroupRegistry groupRegistry;
    private final IDeptService deptService;
    @Autowired
    public DebtController(IUserRegistry userRegistry, IGroupRegistry groupRegistry,IDeptService deptService){
        this.userRegistry = userRegistry;
        this.groupRegistry = groupRegistry;
        this.deptService = deptService;
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
    @GetMapping(path="/of-user/{userId}/to-group/{groupId}")
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
    @GetMapping(path="/of-user/{userA}/to-user/{userB}")
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
     * Add debt between two users
     * @param userA id of user who owes money
     * @param userB id of user who is receiving this money
     * @return Add debt between two users
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id or missing amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/of-user/{userA}/add/{userB}/amount/{amount}")
    public @ResponseBody ResponseEntity<Boolean> addDebt(@PathVariable Integer userA, @PathVariable Integer userB, @PathVariable Double amount){
        if(userA == null || userB == null || amount == null){
            return ResponseEntity.badRequest().build();
        }
        User userBFound = userRegistry.get(userB);
        User userAFound = userRegistry.get(userA);
        if(userAFound == null || userBFound == null){
            return ResponseEntity.notFound().build();
        }
        deptService.addDebt(userAFound, userBFound, amount);
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
    @GetMapping(path="/creditors-of/{userA}")
    public @ResponseBody ResponseEntity<List<Debt>> getWhoOwesThisUserMoney(@PathVariable Integer userA){
        if(userA == null ){
            return ResponseEntity.badRequest().build();
        }
        User userAFound = userRegistry.get(userA);
        if(userAFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deptService.whoDoesThisUserOweMoney(userAFound));
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
    public @ResponseBody ResponseEntity<List<Debt>> whoOwesMoneyToThisUser(@PathVariable Integer userA){
        if(userA == null ){
            return ResponseEntity.badRequest().build();
        }
        User userAFound = userRegistry.get(userA);
        if(userAFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deptService.whoOwesMoneyToThisUser(userAFound));
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
    @GetMapping(path="/of-user/{userA}/total")
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
    @GetMapping(path="/to-user/{userA}/total")
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
     * Pay debt between two users
     * @param userA id of user who pays
     * @param userB id of user to receive payment
     * @param amount how much
     * @return Pay debt between two users
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id or missing amount"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{userA}/pay/{userB}/amount/{amount}")
    public @ResponseBody ResponseEntity<Double> totalDebtOwedToUser(@PathVariable Integer userA, @PathVariable Integer userB, @PathVariable Double amount){
        if(userA == null || userB == null || amount == null){
            return ResponseEntity.badRequest().build();
        }
        User userBFound = userRegistry.get(userB);
        User userAFound = userRegistry.get(userA);
        if(userAFound == null || userBFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deptService.processPayment(userAFound, userBFound, amount));
    }
}
