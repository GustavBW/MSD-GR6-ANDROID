package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.CreateGroupDTO;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GroupRegistry implements IGroupRegistry{
    /**
     * Key = Id of group, value = the group
     */
    private final Map<Integer, Group> groupsById = new HashMap<>();
    /**
     * Key = Id of user, value = their groups
     */
    private final Map<Integer, List<Group>> groupsOfUser = new HashMap<>();
    private final IUserRegistry userRegistry;

    @Autowired
    public GroupRegistry(IUserRegistry userRegistry){
        this.userRegistry = userRegistry;
    }

    @Override
    public Group get(int id) {
        return groupsById.get(id);
    }
    /**
     * @return null on missing admin user
     */
    @Override
    public Group create(CreateGroupDTO dto, User admin) {
        if(admin == null) return null;
        Group group = new Group(
                groupsById.keySet().size() + 1,
                dto.name(),
                dto.desc(),
                dto.groupColor(),
                admin,
                new ArrayList<>(List.of(admin))
            );
        groupsById.put(group.id(), group);
        groupsOfUser.computeIfAbsent(admin.id(), k -> new ArrayList<>()).add(group);
        return group;
    }
    /**
     * @return false on not found
     */
    @Override
    public Boolean removeUser(int idOfGroup, User user) {
        Group group = groupsById.get(idOfGroup);
        if(group == null) return false;
        group.users().remove(user);
        groupsOfUser.computeIfAbsent(user.id(), k -> new ArrayList<>()).remove(group);
        return true;
    }
    /**
     * @return false on not found
     */
    @Override
    public Boolean addUser(int idOfGroup, User user) {
        Group group = groupsById.get(idOfGroup);
        if(group == null) return false;
        group.users().add(user);
        groupsOfUser.computeIfAbsent(user.id(), k -> new ArrayList<>()).add(group);
        return true;
    }

    @Override
    public List<Group> ofUser(int userId) {
        List<Group> groups = groupsOfUser.get(userId);
        return groups == null ? Collections.emptyList() : groups;
    }

    /**
     * @return false on not found
     */
    @Override
    public Boolean delete(Integer groupId) {
        Group group = groupsById.get(groupId);
        if(group == null){
            return false;
        }
        groupsById.remove(groupId);
        groupsOfUser.values().forEach(list -> list.remove(group));
        return true;
    }
}
