package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.CreateUserDTO;
import gbw.sdu.msd.backend.dtos.UpdateUserDTO;
import gbw.sdu.msd.backend.dtos.UserCredentialsDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserRegistry implements IUserRegistry {
    private final Map<Integer,User> usersById = new HashMap<>();
    private final Map<String,User> usersByUsername = new HashMap<>();



    /**
     * @return null on not found
     */
    @Override
    public User get(int id) {
        return usersById.get(id);
    }
    /**
     * @return null on not found
     */
    @Override
    public User get(UserCredentialsDTO credentials) {
        for(User user : usersById.values()){
            if(credentials.compareTo(user) == 0){
                return user;
            }
        }
        return null;
    }

    @Override
    public User get(String username){
        return usersByUsername.get(username);
    }

    @Override
    public User create(CreateUserDTO dto) {
        User user = new User(
                usersById.size() + 1,
                dto.username(),
                dto.passwordHash(),
                dto.email(),
                dto.phoneNumber(),
                dto.name()
        );
        usersByUsername.put(user.username(), user);
        usersById.put(user.id(), user);
        return user;
    }
    /**
     * @return null on not found, else, same user
     */
    @Override
    public User update(int id, UpdateUserDTO dto) {
        User found = usersById.get(id);
        if(found == null){
            return null;
        }
        found.setUsername(dto.username());
        found.setName(dto.name());
        found.setPhoneNumber(dto.phoneNumber());
        found.setEmail(dto.email());
        return found;
    }
    /**
     * @return false on not found
     */
    @Override
    public boolean changePasswordOf(int id, String newPassword) {
        User found = usersById.get(id);
        if(found == null) {
            return false;
        }
        found.setPassword(newPassword);
        return true;
    }
    /**
     * @return null on not found
     */
    @Override
    public User get(UserDTO userDTO) {
        for(User user : usersById.values()){
            if(userDTO.compareTo(user) == 0){
                return user;
            }
        }
        return null;
    }
}
