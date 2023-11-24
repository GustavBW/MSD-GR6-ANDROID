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
    Call<Boolean> addDebtTiMembers(@Path("userA") int userA, @Path("amount") double amount, @Query("creditors") List<Integer> creditors, @Query("groupId") Integer groupI);

}
