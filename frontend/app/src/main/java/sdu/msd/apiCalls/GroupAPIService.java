package sdu.msd.apiCalls;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sdu.msd.dtos.CreateGroupDTO;

public interface GroupAPIService {
    @POST("create")
    Call<CreateGroupDTO> createGroup(@Body CreateGroupDTO createGroupDTO);
}
