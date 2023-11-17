package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.*;
import gbw.sdu.msd.backend.models.User;

public interface IUserRegistry {
    User get(int id);
    User get(UserCredentialsDTO credentials);
    User get(String username);
    User create(CreateUserDTO dto);
    User update(int id, UpdateUserDTO dto);
    boolean changePasswordOf(int id, String newPassword);
    User get(UserDTO userDTO);
    boolean isUnique(String username);

    void updatePreferences(Integer userId, UpdateUserPreferencesDTO dto);
}
