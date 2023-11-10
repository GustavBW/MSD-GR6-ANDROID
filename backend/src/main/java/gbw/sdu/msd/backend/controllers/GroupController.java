package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.CreateGroupDTO;
import gbw.sdu.msd.backend.dtos.GroupDTO;
import gbw.sdu.msd.backend.dtos.UserCredentialsDTO;
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
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/create")
    public @ResponseBody ResponseEntity<GroupDTO> create(@RequestBody CreateGroupDTO dto){
        Group group = groupRegistry.create(dto);
        return ResponseEntity.ok(GroupDTO.of(group));
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
        if(maybeAdmin == null || !auth.mayDeleteUsersFrom(maybeAdmin.id(),groupId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(userInQuestion == null){
            return ResponseEntity.notFound().build();
        }
        User user = userRegistry.get(userInQuestion);
        if(user == null){
            System.out.println("user not found");
            return ResponseEntity.notFound().build();
        }
        if(!groupRegistry.removeUser(groupId, user)){
            System.out.println("couldn't remove user");
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

    /**
     * Returns the Ids of all the groups the user is in.
     * @param userId, id of user
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid userId"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/of-user/{userId}")
    public @ResponseBody ResponseEntity<List<Integer>> getGroupsOfUser(@PathVariable Integer userId){
        if(userId == null || userId < 0){
            return ResponseEntity.badRequest().build();
        }
        User found = userRegistry.get(userId);
        if(found == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groupRegistry.ofUser(userId).stream().map(Group::id).toList());
    }
}
