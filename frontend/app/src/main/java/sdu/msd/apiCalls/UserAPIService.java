package sdu.msd.apiCalls;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import sdu.msd.dtos.CreateUserDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;

public interface UserAPIService {

    @POST("create")
    Call<UserDTO> createUser(@Body CreateUserDTO createUserDTO);
    @POST("login")
    Call<UserDTO> checkCredentials(@Body UserCredentialsDTO userCredentialsDTO);


}
