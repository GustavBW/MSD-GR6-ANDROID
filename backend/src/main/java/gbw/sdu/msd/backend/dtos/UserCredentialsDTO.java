package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.dtos.UserDTO;
import gbw.sdu.msd.backend.models.User;

public record UserCredentialsDTO(String username, String password) implements Comparable<User> {

    /**
     * @return 0 on same, 1 on different, -1 on compared to null
     */
    @Override
    public int compareTo(User user) {
        if(user == null) return -1;
        if(user.username().equals(this.username()) && user.password().equals(this.password())){
            return 0;
        }
        return 1;
    }
}
