package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record UserPreferencesDTO(boolean showNotifications) {
    public static UserPreferencesDTO of(User user){
        return new UserPreferencesDTO(user.isShowNotifications());
    }
    public static Stream<UserPreferencesDTO> of(Collection<User> users){
        return users.stream().map(UserPreferencesDTO::of);
    }
}
