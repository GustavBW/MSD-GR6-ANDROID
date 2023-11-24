package sdu.msd.dtos;


import java.util.List;
import java.util.Objects;

public final class GroupDTO {
    private final int id;
    private final int adminId;
    private final String name;
    private final String descriptions;
    private final int groupColor;
    public static GroupDTO groupDTO;
    public final List<Integer> users;


    public GroupDTO(int id, int adminId, String name, String descriptions, int groupColor, List<Integer> users  ){
        this.id = id;
        this.adminId = adminId;
        this.name = name;
        this.descriptions = descriptions;
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
        return descriptions;
    }

    public int getGroupColor() {
        return groupColor;
    }

    public List<Integer> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        GroupDTO that = (GroupDTO) obj;
        return this.id == that.id &&
                this.adminId == that.adminId &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.descriptions, that.descriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, adminId, name, descriptions,groupColor);
    }

    @Override
    public String toString() {
        return "GroupDTO[" +
                "id=" + id + ", " +
                "adminId=" + adminId + ", " +
                "name=" + name + ", " +
                "descriptions=" + descriptions + ", " +
                "groupColor=" + groupColor + "," +
        ']';
    }
}
