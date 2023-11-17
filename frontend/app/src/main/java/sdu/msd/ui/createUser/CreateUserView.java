package sdu.msd.ui.createUser;

import android.content.Intent;
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
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.CreateUserDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.home.HomeView;

public class CreateUserView extends AppCompatActivity {
    private Button cancelBtn,confirmationBtn;
    private EditText name,username,password,confirmPassword,email,phone;
    private static final String BASEURL =  HomeView.getApi() + "users/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);
        cancelCreation();
        createUser();
    }

    private void cancelCreation(){
        cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CreateUserView.this, HomeView.class);
            startActivity(intent);
        });
    }
    private void createUser(){
        confirmationBtn = findViewById(R.id.confirm);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confrmPassword);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        confirmationBtn.setOnClickListener(view -> {
            if(!name.getText().toString().isEmpty() && !username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !email.getText().toString().isEmpty()){
                if(password.getText().toString().equals(confirmPassword.getText().toString())){
                    postData(name.getText().toString(),username.getText().toString(),password.getText().toString(),email.getText().toString(),phone.getText().toString());
                }
                else{
                    Toast.makeText(CreateUserView.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void postData(String name, String username, String password, String email, String phone) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserAPIService userAPIService = retrofit.create(UserAPIService.class);
        CreateUserDTO createUserDTO = new CreateUserDTO(username,password,name,phone,email);
        Call<UserDTO> call = userAPIService.createUser(createUserDTO);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                UserDTO userDTO = response.body();
                if (userDTO != null) {
                    Intent intent = new Intent(CreateUserView.this, HomeView.class);
                    intent.putExtra("userId", userDTO.id());
                    // Pass other user information if needed
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(CreateUserView.this, t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
