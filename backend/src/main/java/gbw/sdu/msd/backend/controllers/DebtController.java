package gbw.sdu.msd.backend.controllers;

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
}
