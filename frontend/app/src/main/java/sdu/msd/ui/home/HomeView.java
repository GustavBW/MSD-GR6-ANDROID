package sdu.msd.ui.home;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.dtos.CreateGroupDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createGroup.CreateGroupView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.profile;

public class HomeView extends AppCompatActivity {
    private Context context;
    WifiManager wm;
    String ip;
    private GroupAPIService apiService;

    private static final String BASEURL =  "http://192.168.185.1:8080/api/v1/groups/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        String username = getIntent().getStringExtra("username");
        ImageView btnProfile = findViewById(R.id.btnProfile);
        ImageView btnNotifications = findViewById(R.id.btnNotifications);
        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeView.this, profile.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(view -> {
            Intent intent = new Intent(HomeView.this, NotificationsView.class);
            startActivity(intent);
        });


        btnCreateGroup.setOnClickListener(v -> {
            Intent intent = new Intent(HomeView.this, CreateGroupView.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        getGroupsOfUser(username);

    }




    private void getGroupsOfUser(String username){
        Call<List<GroupDTO>> call = apiService.getGroupsOfUser(username);
        call.enqueue(new Callback<List<GroupDTO>>() {
            @Override
            public void onResponse(Call<List<GroupDTO>> call, Response<List<GroupDTO>> response) {
                Toast.makeText(HomeView.this, response.toString(), Toast.LENGTH_SHORT).show();

                if (response.isSuccessful() && response.body() != null) {
                    List<GroupDTO> userGroups = response.body();
                    for (GroupDTO groupDTO : userGroups){
                        Toast.makeText(HomeView.this,groupDTO.name(),Toast.LENGTH_SHORT).show();
                    }
                    createGroupViews(userGroups);

                }
            }

            @Override
            public void onFailure(Call<List<GroupDTO>> call, Throwable t) {
                Toast.makeText(HomeView.this, "Network request failed", Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // Log the exception for debugging purposes

            }
        });

    }

    private void createGroupViews(List<GroupDTO> userGroups){
        LinearLayout groupButtonContainer = findViewById(R.id.groupButtonContainer);
        for (GroupDTO userGroup : userGroups) {
            Toast.makeText(HomeView.this,userGroup.name(),Toast.LENGTH_SHORT).show();
            Button groupButton = new Button(this);
            groupButton.setText(userGroup.name());
            groupButtonContainer.addView(groupButton);
        }

    }
    public static String getBASEURL() {
        return BASEURL;
    }
}
