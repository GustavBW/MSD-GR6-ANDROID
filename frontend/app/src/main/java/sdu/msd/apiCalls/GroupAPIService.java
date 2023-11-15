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
import sdu.msd.dtos.GroupDTO;

public interface GroupAPIService {

    @GET("{userId}/groups")
    Call<List<GroupDTO>> getGroupsOfUser(@Path("userId") int userId);
    @POST("create")
    Call<GroupDTO> createGroup(@Body CreateGroupDTO createGroupDTO);
}
