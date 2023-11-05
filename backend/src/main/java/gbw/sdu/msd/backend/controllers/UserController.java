package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.CreateUserDTO;
import gbw.sdu.msd.backend.dtos.GroupDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.IGroupRegistry;
import gbw.sdu.msd.backend.services.IUserRegistry;
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

    @GetMapping(path="/{userId}")
    public @ResponseBody ResponseEntity<UserDTO> getUser(@PathVariable Integer userId){
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserDTO.of(user));
    }

    /**
     * Example: /api/v1/users?ids=1,7,32,45
     * @return Users of listed ids.
     */
    @GetMapping()
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

    @GetMapping(path="/{userId}/groups")
    public @ResponseBody ResponseEntity<List<GroupDTO>> getUserGroups(@PathVariable Integer userId){
        return ResponseEntity.ok(
                GroupDTO.of(
                        groupRegistry.ofUser(userId)
                )
        );
    }

    @PostMapping(path="/create")
    public @ResponseBody ResponseEntity<UserDTO> create(@RequestBody CreateUserDTO dto){
        User user = userRegistry.create(dto);
        return ResponseEntity.ok(UserDTO.of(user));
    }



    @PostMapping(path="/login")
    public @ResponseBody ResponseEntity<UserDTO> login(@RequestParam String username, @RequestParam String password){
        User user = userRegistry.get(username, password);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(UserDTO.of(user));
    }
}
