package sdu.msd.apiCalls;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sdu.msd.dtos.CreateUserDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.NotificationDTO;
import sdu.msd.dtos.UpdateUserDTO;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;

public interface NotificationAPIService {

    @GET("{userId}")
    Call<List<NotificationDTO>> getUserNotifications(@Path("userId") int userId);

    @POST("push-to-user/{userId}")
    Call<Boolean> pushToUser(@Path("userId") int userId, @Body NotificationDTO notificationDTO);
    @GET("{userId}/amount")
    Call<Integer> getAmountFor(@Path("userId") int userId);

    @POST("{userId}/remove")
    Call<Boolean> removeNotificationsFor(@Path("userId") int userId, @Query("ids") List<Integer> ids);
}
