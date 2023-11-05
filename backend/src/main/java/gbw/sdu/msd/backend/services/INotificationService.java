package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.NotificationDTO;
import gbw.sdu.msd.backend.models.Notification;
import gbw.sdu.msd.backend.models.User;

import java.util.List;

public interface INotificationService {

    /**
     * @return Get all notifications for user
     */
    List<Notification> getFor(int userId);

    /**
     * @return The amount of notifications for said user
     */
    int amountAvailableFor(int userId);

    /**
     * Removes the notifications so that the user is no longer pinged.
     */
    void userHasViewed(Integer userId, List<Integer> notifications);

    void pushTo(Notification nota, User user);

    /**
     * Adds their id to the notification registry and pushes a "Welcome" to them
     */
    void onNewUser(User user);

    /**
     * Creates a new notification with the given parameters
     * @return a new notification
     */
    Notification create(String title, String message);
    Notification create(NotificationDTO dto);

}
