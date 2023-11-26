package sdu.msd.apiCalls;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sdu.msd.dtos.CreateGroupDTO;
import sdu.msd.dtos.GroupActivityDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UpdateGroupDTO;
import sdu.msd.dtos.UserCredentialsDTO;

public interface GroupAPIService {

    @GET("{userId}/groups")
    Call<List<GroupDTO>> getGroupsOfUser(@Path("userId") int userId);
    @POST("create")
    Call<GroupDTO> createGroup(@Body CreateGroupDTO createGroupDTO);
    @GET("{groupId}")
    Call<GroupDTO> getGroup (@Path("groupId") int groupId);

    @POST("{groupId}/remove-user/{userInQuestion}")
    Call<Boolean> leaveGroup(@Path("groupId") int groupId, @Path("userInQuestion") int targetedUserId, @Body UserCredentialsDTO userCredentialsDTO);

    @POST("{groupId}/add-user/{userId}")
    Call<Boolean> addUserToGroup(@Path("groupId") int groupId, @Path("userId") int userId);

    @GET("{groupId}/is-admin/{userId}")
    Call<Boolean> getUserIsAdmin(@Path("groupId") int groupId, @Path("userId") int userId);

    @POST("{groupId}/delete")
    Call<GroupDTO> deleteGroup(@Path("groupId") int groupId);

    @GET("{groupId}/activites")
    Call<List<GroupActivityDTO>> getActivities(@Path("groupId") int groupId, @Query("amount") Integer amount);

    @POST("{groupId}/update")
    Call<GroupDTO> updateGroup(@Path("groupId") int groupId, @Body UpdateGroupDTO updateGroupDTO);


}
