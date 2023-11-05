package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;

import java.util.List;

public record GroupDTO(int id, int adminId, String name, String descriptions, List<Integer> users) {

    public static GroupDTO of(Group group){
        return new GroupDTO(group.id(), group.admin().id(), group.name(), group.description(), group.users().stream().map(User::id).toList());
    }
    public static List<GroupDTO> of(List<Group> groups) {
        return groups.stream().map(GroupDTO::of).toList();
    }
}
