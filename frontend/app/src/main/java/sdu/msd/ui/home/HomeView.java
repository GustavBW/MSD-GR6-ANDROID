package sdu.msd.ui.home;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.dtos.CreateGroupDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createGroup.CreateGroupView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.profile;

public class HomeView extends AppCompatActivity {
    private Context context;
    WifiManager wm;
    private static final String API = "http://192.168.185.1:8080/api/v1/";
    private GroupAPIService apiService;

    private static final String BASEURL =  API + "users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        int userId = getIntent().getIntExtra("userId",-1);
        ImageView btnProfile = findViewById(R.id.btnProfile);
        ImageView btnNotifications = findViewById(R.id.btnNotifications);
        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeView.this, profile.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(view -> {
            Intent intent = new Intent(HomeView.this, NotificationsView.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });


        btnCreateGroup.setOnClickListener(v -> {
            Intent intent = new Intent(HomeView.this, CreateGroupView.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        getGroupsOfUser(userId);

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

    private void createGroupViews(List<GroupDTO> userGroups){
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

            });

        }

    }

    public static String getApi() {
        return API;
    }
}
