package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.CreateGroupDTO;
import gbw.sdu.msd.backend.dtos.GroupDTO;
import gbw.sdu.msd.backend.dtos.UserCredentialsDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
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
     * Example: /api/v1/groups/join?groupId=1&userId=1
     * @param groupId
     * @param userId
     * @return Information about the group joined.
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

    @PostMapping(path= "/{groupId}/remove-user/{idOfUserToBeRemoved}")
    public @ResponseBody ResponseEntity<Boolean> removeUser(@PathVariable Integer idOfUserToBeRemoved, @PathVariable Integer groupId, @RequestBody UserCredentialsDTO actingUser){
        if(actingUser == null || groupId == null){
            return ResponseEntity.notFound().build();
        }
        User maybeAdmin = userRegistry.get(actingUser.username(), actingUser.password());
        if(maybeAdmin == null || !auth.mayDeleteUsersFrom(maybeAdmin.id(),groupId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(idOfUserToBeRemoved == null){
            return ResponseEntity.notFound().build();
        }
        User user = userRegistry.get(idOfUserToBeRemoved);
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

    @PostMapping(path="/{groupId}/delete")
    public @ResponseBody ResponseEntity<Boolean> deleteGroup(@PathVariable Integer groupId, @RequestBody UserCredentialsDTO credentials){
        User user = userRegistry.get(credentials.username(), credentials.password());
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }
        if(!auth.mayDeleteGroup(user.id(),groupId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(groupRegistry.delete(groupId));
    }
}
