package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.CreateUserDTO;
import gbw.sdu.msd.backend.dtos.UpdateUserDTO;
import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.User;

public interface IUserRegistry {
    User get(int id);
    User get(String username, String password);
    User create(CreateUserDTO dto);
    User update(int id, UpdateUserDTO dto);
    boolean changePasswordOf(int id, String newPassword);
    User get(UserDTO userDTO);
}
