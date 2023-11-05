package gbw.sdu.msd.backend.services;

import gbw.sdu.msd.backend.dtos.NotificationDTO;
import gbw.sdu.msd.backend.models.Notification;
import gbw.sdu.msd.backend.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService implements INotificationService{

    private final Map<Integer, List<Notification>> notificationsByUserId = new HashMap<>();
    private int nextId = 0;

    @Override
    public List<Notification> getFor(int userId) {
        return notificationsByUserId.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    @Override
    public int amountAvailableFor(int userId) {
        return notificationsByUserId.computeIfAbsent(userId, k -> new ArrayList<>()).size();
    }

    @Override
    public void userHasViewed(Integer userId, List<Integer> notifications) {
        List<Notification> list = notificationsByUserId.computeIfAbsent(userId, k -> new ArrayList<>()).stream().filter(nota -> notifications.contains(nota.id())).toList();

    }
    @Override
    public void pushTo(Notification nota, User user) {
        notificationsByUserId.computeIfAbsent(user.id(), k -> new ArrayList<>()).add(nota);
    }

    @Override
    public void onNewUser(User user) {
        notificationsByUserId.put(user.id(), new ArrayList<>());
        pushTo(
                create("Welcome To <AppName>", "Time to collect them all."),
                user
        );
    }

    @Override
    public Notification create(String title, String message) {
        return new Notification(
                nextId++,
                title,
                message
        );
    }

    @Override
    public Notification create(NotificationDTO dto) {
        return create(dto.title(), dto.message());
    }
}
