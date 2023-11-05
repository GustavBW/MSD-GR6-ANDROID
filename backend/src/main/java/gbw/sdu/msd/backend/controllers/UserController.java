package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.CreateUserDTO;
import gbw.sdu.msd.backend.dtos.GroupDTO;
import gbw.sdu.msd.backend.dtos.UserCredentialsDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.IGroupRegistry;
import gbw.sdu.msd.backend.services.IUserRegistry;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserRegistry userRegistry;
    private final IGroupRegistry groupRegistry;

    @Autowired
    public UserController(IUserRegistry users, IGroupRegistry groups){
        this.userRegistry = users;
        this.groupRegistry = groups;
    }

    /**
     * Get user information.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userId}")
    public @ResponseBody ResponseEntity<UserDTO> getUser(@PathVariable Integer userId){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserDTO.of(user));
    }


    /**
     * Users of listed ids. URI Example: /api/v1/users?ids=1,7,32,45
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid id list"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping
    public @ResponseBody ResponseEntity<List<UserDTO>> getUsers(@RequestParam List<Integer> ids){
        if(ids == null || ids.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                UserDTO.of(
                        ids.stream().map(userRegistry::get).toList()
                )
        );
    }

    /**
     * All groups the user is part of. An empty list of none.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userId}/groups")
    public @ResponseBody ResponseEntity<List<GroupDTO>> getUserGroups(@PathVariable Integer userId){
        return ResponseEntity.ok(
                GroupDTO.of(
                        groupRegistry.ofUser(userId)
                )
        );
    }

    /**
     * Creates a new user from the information given
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/create")
    public @ResponseBody ResponseEntity<UserDTO> create(@RequestBody CreateUserDTO dto){
        //Check if username is unique

        User user = userRegistry.create(dto);
        return ResponseEntity.ok(UserDTO.of(user));
    }

    /**
     * The user information for the user with matching username and password
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/login")
    public @ResponseBody ResponseEntity<UserDTO> login(@RequestBody UserCredentialsDTO credentials){
        User user = userRegistry.get(credentials);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(UserDTO.of(user));
    }
}
