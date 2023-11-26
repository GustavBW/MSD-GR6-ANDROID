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


    public int idOfAdmin() {
        return idOfAdmin;
    }

    public String desc() {
        return desc;
    }


    public int groupColor() {
        return groupColor;
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
