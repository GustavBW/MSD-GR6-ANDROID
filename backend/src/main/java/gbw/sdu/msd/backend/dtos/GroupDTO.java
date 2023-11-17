package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.User;
import jakarta.annotation.Nullable;

import java.util.List;

public record GroupDTO(int id, int adminId, String name, String descriptions, int groupColor, List<Integer> users) {

    public static GroupDTO of(Group group){
        if (group == null) {
            // Handle the null case, perhaps by returning null or throwing an exception
            return null; // or throw new IllegalArgumentException("Group cannot be null");
        }
        return new GroupDTO(group.id(), group.admin().id(), group.name(), group.description(), group.groupColor(), group.users().stream().map(User::id).toList());
    }
    public static List<GroupDTO> of(List<Group> groups) {
        return groups.stream().map(GroupDTO::of).toList();
    }
}
