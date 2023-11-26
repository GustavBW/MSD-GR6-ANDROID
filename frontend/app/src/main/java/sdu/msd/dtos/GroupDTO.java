package sdu.msd.dtos;


import java.util.List;
import java.util.Objects;

public final class GroupDTO {
    private final int id;
    private final int adminId;
    private final String name;
    private final String description;
    private final int groupColor;
    public final List<Integer> users;


    public GroupDTO(int id, int adminId, String name, String description, int groupColor, List<Integer> users  ){
        this.id = id;
        this.adminId = adminId;
        this.name = name;
        this.description = description;
        this.groupColor = groupColor;
        this.users = users;
    }

    public int id() {
        return id;
    }

    public int adminId() {
        return adminId;
    }

    public String name() {
        return name;
    }

    public String descriptions() {
        return description;
    }

    public int getGroupColor() {
        return groupColor;
    }

    public List<Integer> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "GroupDTO[" +
                "id=" + id + ", " +
                "adminId=" + adminId + ", " +
                "name=" + name + ", " +
                "descriptions=" + description + ", " +
                "groupColor=" + groupColor + "," +
        ']';
    }
}
