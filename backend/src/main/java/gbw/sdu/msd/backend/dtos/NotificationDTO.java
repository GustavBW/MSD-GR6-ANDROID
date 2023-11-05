package gbw.sdu.msd.backend.dtos;

import gbw.sdu.msd.backend.models.Notification;

import java.util.Collection;
import java.util.List;

public record NotificationDTO(String title, String message) {

    public static NotificationDTO of(Notification notification){
        return new NotificationDTO(notification.title(), notification.message());
    }
    public static List<NotificationDTO> of(Collection<Notification> list){
        return list.stream().map(NotificationDTO::of).toList();
    }
}
