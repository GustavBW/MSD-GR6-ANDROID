package sdu.msd.apiCalls;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sdu.msd.dtos.CreateUserDTO;
import sdu.msd.dtos.UserCredentialsDTO;

public interface UserAPIService {

    @POST("create")
    Call<CreateUserDTO> createUser(@Body CreateUserDTO createUserDTO);
    @POST("login")
    Call<UserCredentialsDTO> checkCredentials(@Body UserCredentialsDTO userCredentialsDTO);


}
