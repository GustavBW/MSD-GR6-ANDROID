package sdu.msd.ui.home;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class HomeView extends AppCompatActivity {
    private static final String API = "http://10.0.2.2:8080/api/v1/";
    private GroupAPIService apiService;
    private static final String BASEURL = API + "users/";
    int userId;

    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = retrieveUserIdLocally();
        ImageView btnProfile = findViewById(R.id.btnProfile);
        ImageView btnNotifications = findViewById(R.id.btnNotifications);
        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);
        TextView userNameText = findViewById(R.id.userNameText);

        userNameText.setText(sharedPreferences.getString("username",null));

        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeView.this, ProfileView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        });

        btnNotifications.setOnClickListener(view -> {
            Intent intent = new Intent(HomeView.this, NotificationsView.class);
            startActivity(intent);
        });


        btnCreateGroup.setOnClickListener(v -> {
            Intent intent = new Intent(HomeView.this, CreateGroupView.class);
            startActivity(intent);
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        boolean groupIsCreated = getIntent().getBooleanExtra("groupIsCreated",false);
        boolean userLeftGroup = getIntent().getBooleanExtra("UserLeftGroup", false);
        if(groupIsCreated || userLeftGroup){
            getGroupsOfUser(userId);
        }
        else{
            List<GroupDTO> userGroups = getSavedGroupsById(userId);
            createGroupViews(userGroups);

        }

    }

    private int retrieveUserIdLocally() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.getInt("userId", -1);
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

    private void createGroupViews(List<GroupDTO> userGroups) {
        LinearLayout groupButtonContainer = findViewById(R.id.groupButtonContainer);
        for (GroupDTO userGroup : userGroups) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));
            Button groupButton = new Button(this);
            groupButton.setText(userGroup.name());
            if(userGroup.getGroupColor() !=0){
                groupButton.setTextColor(Color.WHITE);
                gradientDrawable.setColor(userGroup.getGroupColor());
            }
            else{
                groupButton.setTextColor(Color.BLACK);
                gradientDrawable.setColor(-1);
            }
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            groupButton.setBackground(layerDrawable);
            // Set marginBottom
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.activity_vertical_margin));
            groupButton.setLayoutParams(layoutParams);
            groupButtonContainer.addView(groupButton);
            groupButton.setTextSize(20);
            groupButton.setOnClickListener(view -> {
                Intent intent = new Intent(HomeView.this, GroupView.class);
                intent.putExtra("userId", userGroup.adminId());
                intent.putExtra("groupId", userGroup.id());
                startActivity(intent);
            });

        }

    }

    public static String getApi() {
        return API;
    }

    private List<GroupDTO>  getSavedGroups() {
        SharedPreferences sharedPreferences = getSharedPreferences("group_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String groupJson = sharedPreferences.getString("groups", null);
        Type listType = new TypeToken<ArrayList<GroupDTO>>(){}.getType();
        return gson.fromJson(groupJson, listType);

    }
    private List<GroupDTO> getSavedGroupsById(int adminId) {
        List<GroupDTO> allGroups = getSavedGroups();
        List<GroupDTO> filteredGroups = new ArrayList<>();

        // Mnadatory check as if no groups exists the application will crash.
        if (allGroups == null) {
            return filteredGroups;
        }

        for (GroupDTO group : allGroups) {
            if (group.adminId() == adminId) {
                filteredGroups.add(group);
            }
        }
        return filteredGroups;
    }
}
