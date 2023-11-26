package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.CreateGroupDTO;
import gbw.sdu.msd.backend.dtos.GroupActivityDTO;
import gbw.sdu.msd.backend.dtos.UpdateGroupDTO;
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
    private final Map<Integer, List<GroupActivityDTO>> activitiesOfGroup = new HashMap<>();

    @Override
    public Group update(int groupId, UpdateGroupDTO dto) {
        Group groupToUpdate = groupsById.get(groupId);
        if(groupToUpdate == null) return null;

        groupToUpdate.setGroupColor(dto.updatedColor());
        groupToUpdate.setDescription(dto.updatedDescription());
        groupToUpdate.setName(dto.updatedTitle());

        return groupToUpdate;
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
        if(!group.users().contains(user)){
            group.users().add(user);
            groupsOfUser.computeIfAbsent(user.id(), k -> new ArrayList<>()).add(group);
        }
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

    @Override
    public List<GroupActivityDTO> activitiesOf(int groupId, int listLength) {
        List<GroupActivityDTO> activities = activitiesOfGroup.computeIfAbsent(groupId, k -> new ArrayList<>());
        if(activities.size() < listLength || listLength == -1){
            return activities;
        }
        return activities.subList(activities.size() - listLength, activities.size());
    }

    @Override
    public void addActivity(int groupId, GroupActivityDTO activity) {
        activitiesOfGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(activity);
    }
}
