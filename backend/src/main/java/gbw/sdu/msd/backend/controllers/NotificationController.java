package gbw.sdu.msd.backend.controllers;

import gbw.sdu.msd.backend.dtos.NotificationDTO;
import gbw.sdu.msd.backend.models.Group;
import gbw.sdu.msd.backend.models.Notification;
import gbw.sdu.msd.backend.models.User;
import gbw.sdu.msd.backend.services.IGroupRegistry;
import gbw.sdu.msd.backend.services.INotificationService;
import gbw.sdu.msd.backend.services.IUserRegistry;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/notifications")
public class NotificationController {

    private final IUserRegistry userRegistry;
    private final INotificationService notifications;
    private final IGroupRegistry groupRegistry;
    @Autowired
    public NotificationController(IUserRegistry userRegistry, INotificationService notifications, IGroupRegistry groupRegistry){
        this.userRegistry = userRegistry;
        this.notifications = notifications;
        this.groupRegistry = groupRegistry;
    }

    /**
     * Get all notifications for the user.
     * @param userId id of user
     * @return Get all notifications for the user.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userId}")
    public @ResponseBody ResponseEntity<List<NotificationDTO>> getAllNotifications(@PathVariable Integer userId){
        if(userRegistry.get(userId) == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(NotificationDTO.of(notifications.getFor(userId)));
    }

    /**
     * Get the amount of notifications currently available for a given user.
     * @return Get the amount of notifications currently available for a given user.
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(path="/{userId}/amount")
    public @ResponseBody ResponseEntity<Integer> getAmountFor(@PathVariable Integer userId){
        if(userId == null){
            return ResponseEntity.badRequest().build();
        }
        if(userRegistry.get(userId) == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notifications.amountAvailableFor(userId));
    }

    /**
     * Voids the notifications listed so the user won't be pinged about them anymore
     * @return Voids the notifications listed so the user won't be pinged about them anymore. URI example: /api/v1/notifications/1/remove?ids=1,2,3,4
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id or notification ids"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/{userId}/remove")
    public @ResponseBody ResponseEntity<Boolean> removeNotificationsFor(@PathVariable Integer userId, @RequestParam List<Integer> ids){
        if(userId == null || ids == null){
            return ResponseEntity.badRequest().build();
        }
        if(userRegistry.get(userId) == null){
            return ResponseEntity.notFound().build();
        }
        notifications.userHasViewed(userId, ids);
        return ResponseEntity.ok(true);
    }

    /**
     * Pushes the provided notification to a given user
     * @return Pushes the provided notification to a given user
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such user"),
            @ApiResponse(responseCode = "400", description = "Missing user id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/push-to-user/{userId}")
    public @ResponseBody ResponseEntity<Boolean> pushToUser(@PathVariable Integer userId, @RequestBody NotificationDTO dto){
        if(userId == null ){
            return ResponseEntity.badRequest().build();
        }
        User user = userRegistry.get(userId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        Notification nota = notifications.create(dto);
        notifications.pushTo(nota, user);
        return ResponseEntity.ok(true);
    }

    /**
     * Pushes a notification to all members of a group
     * @param groupId id of group
     * @return pushes notification to all members of a group
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "No such group"),
            @ApiResponse(responseCode = "400", description = "Missing group id"),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(path="/push-to-group/{groupId}")
    public @ResponseBody ResponseEntity<Boolean> pushToGroup(@PathVariable Integer groupId, @RequestBody NotificationDTO dto){
        if(groupId == null ){
            return ResponseEntity.badRequest().build();
        }
        Group group = groupRegistry.get(groupId);
        if(group == null){
            return ResponseEntity.notFound().build();
        }
        Notification nota = notifications.create(dto);
        group.users().forEach(user -> notifications.pushTo(nota, user));
        return ResponseEntity.ok(true);
    }
}
