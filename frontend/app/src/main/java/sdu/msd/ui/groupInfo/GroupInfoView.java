package sdu.msd.ui.groupInfo;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
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
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.home.HomeView;

public class GroupInfoView extends AppCompatActivity {
    int userId, groupId;
    private GroupAPIService apiService;
    private UserAPIService userAPIService;
    private static final String BASEURL = getApi() + "groups/";
    private SharedPreferences sharedPreferences;

    private TextView groupNameTextView, descriptionTextView;
    private Button closeBtn, leaveGroup, addGroupMembers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_info);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        groupId = getIntent().getIntExtra("groupId", -1);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        groupNameTextView = findViewById(R.id.groupNameText);
        descriptionTextView = findViewById(R.id.groupDescText);
        leaveGroup = findViewById(R.id.leaveGroup);
        addGroupMembers = findViewById(R.id.addGroupMembers);
        closeBtn = findViewById(R.id.buttonClose);
        createView();
        closeBtn.setOnClickListener(view -> {
            closeView();
        });

        addGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserPopup();
            }
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

    private void createView() {
        String groupName = getIntent().getStringExtra("groupNme");
        String groupDescription = getIntent().getStringExtra("groupDescription");
        groupNameTextView.setText(groupName);
        descriptionTextView.setText(groupDescription);

    }

    private void closeView() {
        Intent intent = new Intent(GroupInfoView.this, GroupView.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
        overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        finish();
    }

    private void leaveGroup(int userId, int groupId) {
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
        Call<Boolean> call = apiService.leaveGroup(groupId, userId, new UserCredentialsDTO(username, password));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(GroupInfoView.this, HomeView.class);
                    intent.putExtra("UserLeftGroup", true);
                    deleteGroupLocally(groupId);
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

    private void deleteGroupLocally(int targetedGroupId) {
        SharedPreferences sharedPreferences = getSharedPreferences("group_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("groups", "");
        Type type = new TypeToken<List<GroupDTO>>() {
        }.getType();
        List<GroupDTO> groupDTOList = gson.fromJson(json, type);
        // Check if the group exists in the list and remove it
        if (groupDTOList != null) {
            Iterator<GroupDTO> iterator = groupDTOList.iterator();
            while (iterator.hasNext()) {
                GroupDTO existingGroup = iterator.next();
                if (existingGroup.id() == targetedGroupId) {
                    iterator.remove();
                    break;
                }
            }
            json = gson.toJson(groupDTOList);
            editor.putString("groups", json);
            editor.apply();
        }
    }

    private void addUserPopup() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getApi())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userAPIService = retrofit.create(UserAPIService.class);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.fragment_add_group_members, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        EditText editTextUsername = (EditText) view.findViewById(R.id.addUsernameInput);
        EditText editTextLink = (EditText) view.findViewById(R.id.inviteLink);
        Button copyLinkButton = (Button) view.findViewById(R.id.copyInviteLink);
        Button addGroupMemberButton = (Button) view.findViewById(R.id.addUsernameButton);

        copyLinkButton.setOnClickListener(popupView -> alertD.dismiss());
        addGroupMemberButton.setOnClickListener(popupView -> {
            addUserToGroup(editTextUsername.getText().toString());
        });
        alertD.setView(view);
        alertD.show();
    }

    private void addUserToGroup(String username) {
        Call<List<UserDTO>> call2 = userAPIService.checkUser(username);
        call2.enqueue(new Callback<List<UserDTO>>() {

            @Override
            public void onResponse(Call<List<UserDTO>>call, Response<List<UserDTO>> response) {
                if(response.isSuccessful()) {
                    List<UserDTO> rs = response.body();
                   addUserToGroupHelper(rs.get(0).id());
                } else {
                    Toast.makeText(GroupInfoView.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToGroupHelper(int id) {
        Call<Boolean> call = apiService.addUserToGroup(groupId, id);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GroupInfoView.this, "User added to group.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

