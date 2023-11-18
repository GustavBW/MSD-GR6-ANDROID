package sdu.msd.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        UserAPIService userAPIService;

        scaleUsernameText();

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, HomeView.class);
            startActivity(intent);
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
    }

    private void scaleUsernameText() {
        TextView usernameTextView = findViewById(R.id.username);
        String username = ""; // Replace with the actual username value
        usernameTextView.setText(username);

        // float textSize = (float)(75.0 / (1.0 + (float)Math.exp(0.4055 * ((float)(username.length()) - 1.0))));
        float textSize = (float)(100.0 / (1.0 + (float)Math.exp(-(-0.11564339880716944) * ((float)(username.length() - 10.5)))));

        usernameTextView.setTextSize(textSize);
    }

    private void fetchData() {

    }

    private void getGroupsOfUser(int userId){
        Call<List<GroupDTO>> call = apiService.getGroupsOfUser(userId);
        call.enqueue(new Callback<List<GroupDTO>>() {
            @Override
            public void onResponse(Call<List<GroupDTO>> call, Response<List<GroupDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GroupDTO> userGroups = response.body();
                    createGroupViews(userGroups);
                }
            }
            @Override
            public void onFailure(Call<List<GroupDTO>> call, Throwable t) {
                LinearLayout groupButtonContainer = findViewById(R.id.groupButtonContainer);
                Toast.makeText(HomeView.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Log the exception for debugging purposes
            }
        });
    }
}