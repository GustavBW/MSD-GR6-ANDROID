package sdu.msd.dtos;

import java.util.Objects;

public class CreateGroupDTO {
    private final int idOfAdmin;
    private final String name;
    private final String desc;
    private final int groupColor;

    public CreateGroupDTO(int idOfAdmin, String name, String desc, int groupColor) {
        this.idOfAdmin = idOfAdmin;
        this.name = name;
        this.desc = desc;
        this.groupColor = groupColor;
    }

    public int getIdOfAdmin() {
        return idOfAdmin;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getGroupColor() {
        return groupColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        CreateGroupDTO that = (CreateGroupDTO) obj;
        return this.idOfAdmin == that.idOfAdmin &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.desc, that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfAdmin, name, desc,groupColor);
    }

    @Override
    public String toString() {
        return "CreateGroupDTO{" +
                "idOfAdmin=" + idOfAdmin +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", groupColor=" + groupColor +
                '}';
    }
}
