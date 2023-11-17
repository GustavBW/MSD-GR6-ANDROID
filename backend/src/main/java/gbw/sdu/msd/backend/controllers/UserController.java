package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.*;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
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
     * Users of listed ids URI Example: /api/v1/users?ids=1,7,32,45
     * Request parameters are mutually exclusive
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid or missing id list"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping
    public @ResponseBody ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = false) List<Integer> ids, @RequestParam(required = false) List<String> usernames){
        if((ids == null || ids.isEmpty()) && (usernames == null || usernames.isEmpty())){
            return ResponseEntity.badRequest().build();
        }
        if(ids != null && !ids.isEmpty()){
            return ResponseEntity.ok(
                    UserDTO.of(
                            ids.stream().map(userRegistry::get).toList()
                    )
            );
        }
        if(usernames != null && !usernames.isEmpty()){
            return ResponseEntity.ok(
                    UserDTO.of(
                            usernames.stream().map(userRegistry::get).toList()
                    )
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    /**
     * All groups the user is part of. An empty list if none.
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
            @ApiResponse(responseCode = "401", description = "Unauthorized, this Username is already taken."),
            @ApiResponse(responseCode = "400", description = "Missing required data for user creation"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/create")
    public @ResponseBody ResponseEntity<UserDTO> create(@RequestBody CreateUserDTO dto){
        //Check if username is unique
        if(dto.username() == null || dto.username().isBlank()){
            return ResponseEntity.badRequest().build();
        }
        if(!userRegistry.isUnique(dto.username())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRegistry.create(dto);
        return ResponseEntity.ok(UserDTO.of(user));
    }

    /**
     * Retrieves the user information for the user with matching username and password
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

    /**
     * Returns the preferences of a given user
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path = "/{userId}/preferences")
    public @ResponseBody ResponseEntity<UserPreferencesDTO> getUserPreferences(@PathVariable Integer userId){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserPreferencesDTO.of(user));
    }

    /**
     * Updates user preferences
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path = "/{userId}/preferences")
    public @ResponseBody ResponseEntity<UserPreferencesDTO> postUserPreferences(@PathVariable Integer userId, @RequestBody UpdateUserPreferencesDTO dto){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        userRegistry.updatePreferences(userId, dto);
        return ResponseEntity.ok(UserPreferencesDTO.of(user));
    }

    /**
     * Update user information
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{userId}")
    public @ResponseBody ResponseEntity<UserDTO> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserDTO dto){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserDTO.of(userRegistry.update(userId, dto)));
    }
}
