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

    @GET("of-user/{userId}")
    Call<List<GroupDTO>> getGroupsOfUser(@Path("userId") String email);
    @POST("create")
    Call<CreateGroupDTO> createGroup(@Body CreateGroupDTO createGroupDTO);
}
