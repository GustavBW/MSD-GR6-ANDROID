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
import sdu.msd.dtos.UserCredentialsDTO;

public interface DebtAPIService {
    @POST("{userA}/distribute/{amount}")
    Call<Boolean> addDebtToMember(@Path("userA") int userA, @Path("amount") double amount, @Query("creditors") List<Integer> creditors, @Query("groupId") Integer groupI);

    @POST("{userA}/reverse-distribute/{amount}")
    Call<Boolean> addDebtToMembers(@Path("userA") int userA, @Path("amount") double amount, @Query("debtees") List<Integer> debtees, @Query("groupId") Integer groupI);

    @GET("{userId}/to-group/{groupId}")
    Call<Double> getHowMuchUserOwesGroup(@Path("userId") int userId, @Path("groupId") int groupId);

    @GET("{userA}")
    Call<Double> getHowMuchUser(@Path("userA") int userA);

}
