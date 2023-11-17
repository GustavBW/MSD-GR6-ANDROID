package gbw.sdu.msd.backend.models;

import java.util.List;

public record Group(int id, String name, String description, int groupColor, User admin, List<User> users) {


}
