package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.*;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.Auth;
import gbw.sdu.msd.backend.services.IGroupRegistry;
import gbw.sdu.msd.backend.services.IUserRegistry;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/groups")
public class GroupController {

    private final IUserRegistry userRegistry;
    private final IGroupRegistry groupRegistry;
    private final Auth auth;

    @Autowired
    public GroupController(IUserRegistry users, IGroupRegistry groups, Auth auth){
        this.userRegistry = users;
        this.groupRegistry = groups;
        this.auth = auth;
    }

    /**
     * Checks if the user is the admin of said group or not
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user or no such group"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/{groupId}/is-admin/{userId}")
    public @ResponseBody ResponseEntity<Boolean> checkAdmin(@PathVariable Integer groupId, @PathVariable Integer userId){
        User user = userRegistry.get(userId);
        Group group = groupRegistry.get(groupId);
        if(user == null || group == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.id() == group.admin().id());
    }

    /**
     * Adds a user to an existing group
     * @return Adds a user to an existing group
     */
    @PostMapping(path="{groupId}/add-user/{userId}")
    public @ResponseBody ResponseEntity<Boolean> joinGroup(@PathVariable Integer userId, @PathVariable int groupId){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }

        if(!groupRegistry.addUser(groupId,user)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Creates a new group
     * @return Creates a new group
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user - A user that doesn't exist can't be admin."),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/create")
    public @ResponseBody ResponseEntity<GroupDTO> create(@RequestBody CreateGroupDTO dto){
        User admin = userRegistry.get(dto.idOfAdmin());
        if(admin == null){
            return ResponseEntity.notFound().build();
        }

        Group group = groupRegistry.create(dto, admin);
        return ResponseEntity.ok(GroupDTO.of(group));
    }

    /**
     * Update the group information to be xxx, admin only.
     * Returns the full updated information of the group.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid values to be updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, acting user is not admin"),
            @ApiResponse(responseCode = "404", description = "No such group or no such acting user")
    })
    @PostMapping(path="/{groupId}/update")
    public @ResponseBody ResponseEntity<GroupDTO> updateGroup(@PathVariable Integer groupId, @RequestBody UpdateGroupDTO dto){
        User actingUser = userRegistry.get(dto.idOfActingUser());
        Group group = groupRegistry.get(groupId);
        if(actingUser == null || group == null){
            return ResponseEntity.notFound().build();
        }
        if(actingUser.id() != group.admin().id()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(dto.updatedColor() < 0 || dto.updatedDescription() == null
                || dto.updatedDescription().isBlank() || dto.updatedTitle() == null
                || dto.updatedTitle().isBlank()){
            return ResponseEntity.badRequest().build();
        }
        Group updatedGroup = groupRegistry.update(groupId, dto);
        return ResponseEntity.ok(GroupDTO.of(updatedGroup));
    }

    /**
     * Adds a user to a group. URI example: /api/v1/groups/join?groupId=1&userId=1
     * @param groupId id of group
     * @param userId id of user
     * @return Adds a user to a group. URI example: /api/v1/groups/join?groupId=1&userId=1
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user or no such group"),
            @ApiResponse(responseCode = "400", description = "Missing groupId or userId"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/join")
    public @ResponseBody ResponseEntity<GroupDTO> linkJoin(@RequestParam Integer groupId, @RequestParam Integer userId){
        if(groupId == null || userId == null){
            return ResponseEntity.badRequest().build();
        }
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        if(!groupRegistry.addUser(groupId, user)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GroupDTO.of(groupRegistry.get(groupId)));
    }

    /**
     * Get the activities for the group.
     * Optionally, you can use the query param "amount" to limit the... amount.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such group"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/{groupId}/activities")
    public @ResponseBody ResponseEntity<List<GroupActivityDTO>> getActivities(@PathVariable Integer groupId, @RequestParam(required = false) Integer amount){
        Group group = groupRegistry.get(groupId);
        if(group == null){
            return ResponseEntity.notFound().build();
        }
        if(amount == null){
            amount = -1;
        }
        return ResponseEntity.ok(groupRegistry.activitiesOf(groupId, amount));
    }

    /**
     * Removes the user from the group if the acting user is authorized to do so
     * @param userInQuestion id of user to be removed
     * @param groupId id of group
     * @param actingUser Credentials of the user performing this action
     * @return Removes the user from the group if the acting user is authorized to do so
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid acting user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path= "/{groupId}/remove-user/{userInQuestion}")
    public @ResponseBody ResponseEntity<Boolean> removeUser(@PathVariable Integer userInQuestion, @PathVariable Integer groupId, @RequestBody UserCredentialsDTO actingUser){
        if(actingUser == null || groupId == null){
            return ResponseEntity.notFound().build();
        }
        User maybeAdmin = userRegistry.get(actingUser);
        if(maybeAdmin == null){
            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else if(!auth.mayDeleteUsersFrom(maybeAdmin.id(), userInQuestion, groupId)){
            System.out.println("User not authorized: " + maybeAdmin);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(userInQuestion == null){
            return ResponseEntity.notFound().build();
        }
        User user = userRegistry.get(userInQuestion);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        if(!groupRegistry.removeUser(groupId, user)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     * The information about that group
     * @param groupId id of group
     * @return The information about that group
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such group"),
            @ApiResponse(responseCode = "400", description = "Missing group id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/{groupId}")
    public @ResponseBody ResponseEntity<GroupDTO> getGroup(@PathVariable Integer groupId){
        if(groupId == null){
            return ResponseEntity.badRequest().build();
        }
        Group group = groupRegistry.get(groupId);
        if(group == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GroupDTO.of(group));
    }

    /**
     * Deletes a given group if the acting user is authorized to do so
     * @param groupId group to delete
     * @param credentials of the user trying to delete said group
     * @return true on success
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid acting user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{groupId}/delete")
    public @ResponseBody ResponseEntity<Boolean> deleteGroup(@PathVariable Integer groupId, @RequestBody UserCredentialsDTO credentials){
        User user = userRegistry.get(credentials);
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }
        if(!auth.mayDeleteGroup(user.id(),groupId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(groupRegistry.delete(groupId));
    }
}
