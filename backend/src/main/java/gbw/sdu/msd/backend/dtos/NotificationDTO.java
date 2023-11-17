package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.Notification;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record NotificationDTO(String title, String message) {

    public static NotificationDTO of(Notification notification){
        return new NotificationDTO(notification.title(), notification.message());
    }
    public static Stream<NotificationDTO> of(Collection<Notification> list){
        return list.stream().map(NotificationDTO::of);
    }
}
