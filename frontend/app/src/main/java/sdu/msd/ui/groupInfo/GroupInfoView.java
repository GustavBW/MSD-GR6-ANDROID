package sdu.msd.ui.groupInfo;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.home.HomeView;

public class GroupInfoView extends AppCompatActivity {
    int userId, groupId;
    private GroupAPIService apiService;
    private static final String BASEURL =  getApi() + "groups/";
    private SharedPreferences sharedPreferences;

    private TextView groupNameTextView, descriptionTextView;
    private Button closeBtn, leaveGroup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_info);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",-1);
        groupId = getIntent().getIntExtra("groupId",-1);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        groupNameTextView = findViewById(R.id.groupNameText);
        descriptionTextView = findViewById(R.id.groupDescText);
        leaveGroup = findViewById(R.id.leaveGroup);
        closeBtn = findViewById(R.id.buttonClose);
        createView();
        closeBtn.setOnClickListener(view -> {
            closeView();
        });
        leaveGroup.setOnClickListener(view -> {
            new AlertDialog.Builder(GroupInfoView.this)
                    .setTitle("Leave Group")
                    .setMessage("Are you sure you want to leave this group?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        leaveGroup(userId, groupId);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
    }

    private void  createView(){
        String groupName = getIntent().getStringExtra("groupNme");
        String groupDescription = getIntent().getStringExtra("groupDescription");
        groupNameTextView.setText(groupName);
        descriptionTextView.setText(groupDescription);

    }
    private void closeView(){
        Intent intent = new Intent(GroupInfoView.this, GroupView.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
        finish();


    }
    private void leaveGroup(int userId, int groupId) {
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
        Call<Boolean> call = apiService.leaveGroup(groupId,userId,new UserCredentialsDTO(username,password));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(GroupInfoView.this, HomeView.class);
                    startActivity(intent);
                    Toast.makeText(GroupInfoView.this, "You have successfully leaved the group", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}

