package gbw.sdu.msd.backend.models;

import java.util.List;
import java.util.Objects;

public final class Group {
    private final int id;
    private final User admin;
    private final List<User> users;

    private String name;
    private String description;
    private int groupColor;

    public Group(int id, String name, String description, int groupColor, User admin, List<User> users) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupColor = groupColor;
        this.admin = admin;
        this.users = users;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int groupColor() {
        return groupColor;
    }

    public User admin() {
        return admin;
    }

    public List<User> users() {
        return users;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setGroupColor(int color){
        this.groupColor = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Group) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.description, that.description) &&
                this.groupColor == that.groupColor &&
                Objects.equals(this.admin, that.admin) &&
                Objects.equals(this.users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, groupColor, admin, users);
    }

    @Override
    public String toString() {
        return "Group[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "description=" + description + ", " +
                "groupColor=" + groupColor + ", " +
                "admin=" + admin + ", " +
                "users=" + users + ']';
    }


}
