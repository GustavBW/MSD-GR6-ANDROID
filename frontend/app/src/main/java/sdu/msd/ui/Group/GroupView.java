package sdu.msd.ui.Group;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.ProfileView;

public class GroupView extends AppCompatActivity {
    int userId, groupId;
    private Button payBtn;
    private GroupAPIService apiService;

    private ImageView notification, profile;

    private static final String BASEURL =  getApi() + "groups/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("userId",-1);
        groupId = getIntent().getIntExtra("groupId",-1);
        setContentView(R.layout.fragment_group);
        payBtn = findViewById(R.id.pay);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        getGroup(groupId);
        doPay();



    }

    private void doPay(){
        // TODO: 17-11-2023 This will also be done soon 
        payBtn.setOnClickListener(v -> {
            Intent intent = new Intent(GroupView.this, HomeView.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }

    private void getGroup(int groupId){
        Call<GroupDTO> call = apiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDTO groupDTO = response.body();
                    createGroupView(groupDTO);
                }
                }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(GroupView.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Log the exception for debugging purposes

            }
        });
    }

    private void createGroupView(GroupDTO groupDTO){
        TextView textView = findViewById(R.id.groupName);
        textView.setText(groupDTO.name());
        notification = findViewById(R.id.btnNotifications);
        profile = findViewById(R.id.btnProfile);
        // TODO: 17-11-2023
        /*
        The rest will be added soon when other features are done
         */
        notification.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, NotificationsView.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        profile.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, ProfileView.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}
