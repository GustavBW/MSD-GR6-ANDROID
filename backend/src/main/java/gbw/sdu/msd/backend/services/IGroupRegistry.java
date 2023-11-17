package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.CreateGroupDTO;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IGroupRegistry {
    Group get(int id);
    Group create(CreateGroupDTO dto, User admin);
    Boolean removeUser(int idOfGroup, User user);
    Boolean addUser(int idOfGroup, User user);
    List<Group> ofUser(int userId);

    Boolean delete(Integer groupId);
}
