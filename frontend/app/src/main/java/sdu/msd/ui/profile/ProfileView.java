package sdu.msd.ui.profile;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
import sdu.msd.dtos.UpdateUserDTO;
import sdu.msd.ui.groupInfo.GroupInfoView;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.login.LoginView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createGroup.CreateGroupView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.ProfileView;

public class ProfileView extends AppCompatActivity {
    private static final String BASEURL =  getApi() + "users/";
    private UserAPIService apiService;
    private UserDTO userDTO;
    int userId;
    private SharedPreferences sharedPreferences;
    private EditText currentPassword;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextFullName;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        createProfileView();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(UserAPIService.class);
    }

    private void scaleUsernameText(String username) {
        TextView usernameTextView = findViewById(R.id.username);
        usernameTextView.setText(username);

        // float textSize = (float)(75.0 / (1.0 + (float)Math.exp(0.4055 * ((float)(username.length()) - 1.0))));
        float textSize = (float)(100.0 / (1.0 + (float)Math.exp(-(-0.11564339880716944) * ((float)(username.length() - 10.5)))));

        usernameTextView.setTextSize(textSize);
    }

    private void createProfileView(){
        username = sharedPreferences.getString("username", "Value not found!");
        TextView textViewUsername = findViewById(R.id.username);
        textViewUsername.setText(username);
        scaleUsernameText(username);

        editTextEmail = findViewById(R.id.textEmailAddress);
        editTextEmail.setText(sharedPreferences.getString("email", "Value not found!"));

        editTextPhone = findViewById(R.id.textPhone);
        editTextPhone.setText(sharedPreferences.getString("phoneNumber", "Value not found!"));

        editTextFullName = findViewById(R.id.textFullName);
        editTextFullName.setText(sharedPreferences.getString("name", "Value not found!"));

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, HomeView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });

        Button logoutButton = findViewById(R.id.buttonLogout); // Go to login
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, LoginView.class);
            startActivity(intent);
        });

        Button invoicesButton = findViewById(R.id.buttonInvoices); // Go to invoices
        invoicesButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, HomeView.class); // TODO: change to InvoiceView.class when possible.
            startActivity(intent);
        });

        Button updateButton = findViewById(R.id.buttonUpdate);
        currentPassword = findViewById(R.id.textPassword);
        updateButton.setOnClickListener(view -> {
            updateData();
        });
    }

    private void updateData() {
        if (currentPassword.getText().toString().equals(sharedPreferences.getString("password", ""))) {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(username, editTextFullName.getText().toString(), editTextPhone.getText().toString(), editTextEmail.getText().toString());
            Call<UserDTO> call = apiService.updateUser(userId, updateUserDTO);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    UserDTO userDTOUpdatedInformation = response.body();
                    if (userDTOUpdatedInformation != null) {
                        editTextFullName.setText(userDTOUpdatedInformation.name());
                        editTextPhone.setText(userDTOUpdatedInformation.phoneNumber());
                        editTextEmail.setText(userDTOUpdatedInformation.email());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", userDTOUpdatedInformation.name());
                        editor.putString("email", userDTOUpdatedInformation.email());
                        editor.putString("phoneNumber", userDTOUpdatedInformation.phoneNumber());
                        editor.apply();
                    }

                    Toast.makeText(ProfileView.this, "Information updated!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Toast.makeText(ProfileView.this, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProfileView.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
        }
    }
}