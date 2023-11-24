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
    private EditText usernameEditText, passwordEditText;
    private Button register, login;

    private static final String BASEURL =  HomeView.getApi() + "users/";
    private  SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        setContentView(R.layout.fragment_login);
        usernameEditText = findViewById(R.id.username_login);
        passwordEditText = findViewById(R.id.password_login);
        if (isLoggedIn()) {
            postLoginData(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""));
            Intent intent = new Intent(LoginView.this, HomeView.class);
            startActivity(intent);
            finish();
        } else {
            register();
            login();
        }
    }

    private void register() {
        register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginView.this, CreateUserView.class);
            startActivity(intent);

        });
    }

    private void login() {
        login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            if(!usernameEditText.getText().toString().trim().isEmpty() && !passwordEditText.getText().toString().trim().isEmpty()) {
                postLoginData(usernameEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            } else {
                Toast.makeText(LoginView.this, "Enter username and password", Toast.LENGTH_LONG).show();
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
                    saveUserDataLocally(userDTO);
                    startActivity(intent);
                    finish();


                }
                else{
                    if(!usernameEditText.getText().toString().trim().isEmpty() && !passwordEditText.getText().toString().trim().isEmpty()) {
                        Toast.makeText(LoginView.this, "Wrong credentials", Toast.LENGTH_LONG).show();
                    }
                    if(!sharedPreferences.getAll().isEmpty()){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                        startActivity(new Intent(LoginView.this, LoginView.class));  // Start a new instance of LoginView
                    }


                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(LoginView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserDataLocally(UserDTO userDTO) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userDTO.id());
        editor.putString("username", userDTO.username());
        editor.putString("name", userDTO.name());
        editor.putString("email", userDTO.email());
        if(!passwordEditText.getText().toString().trim().isEmpty()){
            editor.putString("password", passwordEditText.getText().toString());
        }
        editor.putString("phoneNumber", userDTO.phoneNumber());
        editor.apply();
    }
    private boolean isLoggedIn() {
        return sharedPreferences.contains("userId") && sharedPreferences.contains("username") && sharedPreferences.contains("password");
    }
}
