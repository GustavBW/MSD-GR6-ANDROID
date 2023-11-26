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
import sdu.msd.ui.addExpense.AddExpenseView;
import sdu.msd.ui.groupInfo.GroupInfoView;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.ProfileView;

public class GroupView extends AppCompatActivity {
    int userId, groupId;
    private Button payBtn, addExpense;
    private GroupAPIService apiService;

    private ImageView groupInfo, editGroup;

    private static final String BASEURL =  getApi() + "groups/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        groupInfo = findViewById(R.id.groupInfo);
        editGroup = findViewById(R.id.editGroupBtn);
        addExpense = findViewById(R.id.addExpense);
        // TODO: 17-11-2023
        /*
        The rest will be added soon when other features are done.
         */
        editGroup.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, NotificationsView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        addExpense.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, AddExpenseView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        });

        groupInfo.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, GroupInfoView.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("groupNme", groupDTO.name());
            intent.putExtra("groupDescription", groupDTO.descriptions());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        });

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, HomeView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });
    }
}
