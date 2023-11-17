package sdu.msd.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.ui.createUser.CreateUserView;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.home.HomeView;

public class LoginView extends AppCompatActivity {
    private EditText username, password;
    private Button register, login;

    private static final String BASEURL =  HomeView.getApi() + "users/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        register();
        login();
    }

    private void register() {
        register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            Intent intet = new Intent(LoginView.this, CreateUserView.class);
            startActivity(intet);

        });
    }

    private void login() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    postLoginData(username.getText().toString(),password.getText().toString());
            }
        });
    }

    private void postLoginData(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserAPIService userAPIService = retrofit.create(UserAPIService.class);
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(username, password);
        Call<UserDTO> call = userAPIService.checkCredentials(userCredentialsDTO);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                UserDTO userDTO = response.body();
                if (userDTO != null) {
                    Intent intent = new Intent(LoginView.this, HomeView.class);
                    // Pass other user information if needed
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(LoginView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
