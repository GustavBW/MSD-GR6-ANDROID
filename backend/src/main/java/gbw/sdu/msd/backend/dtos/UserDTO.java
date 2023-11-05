package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.User;

import java.util.List;

public record UserDTO(int id, String username, String name, String email, String phoneNumber) implements Comparable<User> {
    public static UserDTO of(User user) {
        return new UserDTO(user.id(), user.username(), user.name(), user.email(), user.phoneNumber());
    }
    public static List<UserDTO> of(List<User> users){
        return users.stream().map(UserDTO::of).toList();
    }

    /**
     * @param user the object to be compared.
     * @return 0 on same, 1 on different
     */
    @Override
    public int compareTo(User user) {
        if(user.name().equals(this.name())
                && user.id() == this.id()
                && user.username().equals(this.username())
                && user.email().equals(this.email())
                && user.phoneNumber().equals(this.phoneNumber())){
            return 0;
        }
        return 1;
    }
}
