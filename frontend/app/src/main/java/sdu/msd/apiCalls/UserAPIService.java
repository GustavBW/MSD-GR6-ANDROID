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
import sdu.msd.dtos.UpdateUserDTO;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;

public interface UserAPIService {

    @POST("create")
    Call<UserDTO> createUser(@Body CreateUserDTO createUserDTO);
    @POST("login")
    Call<UserDTO> checkCredentials(@Body UserCredentialsDTO userCredentialsDTO);
    @POST("{userId}")
    Call<UserDTO> updateUser(@Path("userId") int userId, @Body UpdateUserDTO updateUserDTO);

    @GET("users")
    Call<List<UserDTO>> checkUser(@Query("usernames") String usernames);

    @GET("{userId}")
    Call<List<UserDTO>> getUserFromId(@Query("userId") List<Integer> userId);

}
