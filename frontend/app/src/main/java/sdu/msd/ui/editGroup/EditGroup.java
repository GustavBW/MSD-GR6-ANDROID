package sdu.msd.ui.editGroup;

import static sdu.msd.ui.home.HomeView.getApi;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import sdu.msd.apiCalls.NotificationAPIService;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.NotificationDTO;
import sdu.msd.dtos.UpdateGroupDTO;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createUser.CreateUserView;
import sdu.msd.ui.expense.AddExpenseView;
import sdu.msd.ui.groupInfo.GroupInfoView;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.login.LoginView;

public class EditGroup extends AppCompatActivity {

    int userId, groupId;
    private Button saveChangesButton, deleteGroupButton, closeButton;
    private EditText groupNameEditText, groupDescriptionEditText;
    private GroupAPIService groupApiService;
    private UserAPIService userAPIService;
    private NotificationAPIService notificationAPIService;
    private  Retrofit retrofit;
    private SharedPreferences sharedPreferences;
    private List<UserDTO> members;
    private String username;
    private String password;
    private String groupName;


    private static final String BASEURLUser =  getApi();
    private static final String BASEURLGROUP =  getApi() + "groups/";
    private static final String BASENOTIFICATIONURL = getApi() + "notifications/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getIntExtra("groupId",-1);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",-1);
        groupName = getIntent().getStringExtra("groupName");
        username = sharedPreferences.getString("username","");
        password = sharedPreferences.getString("password","");
        setContentView(R.layout.fragment_edit_group);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLGROUP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        groupApiService = retrofit.create(GroupAPIService.class);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLUser)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userAPIService = retrofit.create(UserAPIService.class);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASENOTIFICATIONURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        notificationAPIService = retrofit.create(NotificationAPIService.class);
        groupDescriptionEditText = findViewById(R.id.groupDescEditText);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        saveChangesButton = findViewById(R.id.saveButton);
        deleteGroupButton = findViewById(R.id.deleteGroup);
        closeButton = findViewById(R.id.buttonClose);
        members = new ArrayList<>();
        cancelCreation();
        getGroup();
        getGroupInformation();
    }

    private void cancelCreation(){
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditGroup.this, GroupView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    private void checkIfAdmin(int userId) {
        Call<Boolean> call = groupApiService.getUserIsAdmin(groupId,userId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() !=null){
                    if(!response.body()){
                        Toast.makeText(EditGroup.this, "You are not allowed to make change on this group", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    deleteGroup();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(EditGroup.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getGroup(){
        Call<GroupDTO> call = groupApiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDTO groupDTO = response.body();
                    createGroupEditView(groupDTO);
                }
            }
            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(EditGroup.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Log the exception for debugging purposes
            }
        });
    }
    private void createGroupEditView(GroupDTO groupDTO){
        groupNameEditText.setText(groupDTO.name());
        groupDescriptionEditText.setText(groupDTO.descriptions());
        saveChangesButton.setOnClickListener(view -> {
            updateData();
        });
        deleteGroupButton.setOnClickListener(view -> {
            new AlertDialog.Builder(EditGroup.this)
                    .setTitle("Leave Group")
                    .setMessage("Are you sure you want to remove group " + groupName + "?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        checkIfAdmin(userId);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

        });
    }

    private void getGroupInformation() {
        Call<GroupDTO> call = groupApiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> memberIds = response.body().getUsers();
                    getMembers(memberIds);

                }
            }
            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(EditGroup.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Log the exception for debugging purposes
            }
        });
    }
    private void getMembers(List<Integer> userIds) {
        userIds.removeIf(userId -> userId == this.userId);
        Call<List<UserDTO>> call = userAPIService.getUsersFromId(userIds);
        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    members = response.body();
                    addMembersToView();
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                Toast.makeText(EditGroup.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void addMembersToView() {
        LinearLayout memberContainer = findViewById(R.id.membersContainer);
        memberContainer.removeAllViews();
        for (UserDTO member : members) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(16);
            RelativeLayout relativeLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.activity_vertical_margin));
            relativeLayout.setLayoutParams(layoutParams);
            TextView textView = new TextView(this);
            textView.setText(member.name());
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(25);
            textView.setPadding(10,0,0,0);
            textView.setGravity(Gravity.CENTER);
            gradientDrawable.setColor(Color.WHITE);
            Button remove = new Button(this);
            GradientDrawable buttonDrawable = new GradientDrawable();
            buttonDrawable.setCornerRadius(16);
            buttonDrawable.setColor(Color.rgb(244, 67, 54)); // Set the background color
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            relativeLayout.setBackground(layerDrawable);
            // Add items to container
            remove.setText("-");
            remove.setTextSize(40);
            remove.setTextColor(Color.WHITE);
            remove.setBackground(buttonDrawable);
            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            remove.setPadding(0, 0, 0, 0);   // Remove any existing padding
            RelativeLayout.LayoutParams removeButtonParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            removeButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
            removeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            remove.setLayoutParams(removeButtonParams);
            textParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            textView.setLayoutParams(textParams);
            relativeLayout.addView(textView);
            relativeLayout.addView(remove);
            remove.setOnClickListener(view -> {
                new AlertDialog.Builder(EditGroup.this)
                        .setTitle("Leave Group")
                        .setMessage("Are you sure you want to remove " + member.username() + " from the group?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            removeUserFromGroup(member);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            });
            memberContainer.addView(relativeLayout);
        }
    }

    private void removeUserFromGroup(UserDTO member) {
        Call<Boolean> call = groupApiService.leaveGroup(groupId,member.id(),new UserCredentialsDTO(username,password));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful() && response.body() !=null){
                    Iterator<UserDTO> iterator = members.iterator();
                    while (iterator.hasNext()) {
                        UserDTO user = iterator.next();
                        if (user.id() == member.id()) {
                            iterator.remove();
                            pushNotification(user.id());
                            break;
                        }
                    }
                    Toast.makeText(EditGroup.this, member.username() + " had been removed from the group", Toast.LENGTH_SHORT).show();
                    addMembersToView();
                }
                else{
                    Toast.makeText(EditGroup.this, "You are not allowed to make change on this group", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(EditGroup.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void pushNotification(int id) {
        String username = sharedPreferences.getString("username", null);
        String invitationMessage = username + " removed you from group " + groupName;
        NotificationDTO notificationDTO = new NotificationDTO(username + " removed you from the group: " + groupName, invitationMessage);
        Call<Boolean> call = notificationAPIService.pushToUser(id, notificationDTO);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(EditGroup.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteGroup(){
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(username,password);
        Call<Boolean> call = groupApiService.deleteGroup(groupId,userCredentialsDTO);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(!response.body()){
                        Toast.makeText(EditGroup.this, "You are not allowed to make change on this group", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(EditGroup.this, HomeView.class);
                    intent.putExtra("groupId",groupId);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(EditGroup.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public void updateData(){
        UpdateGroupDTO updateGroupDTO = new UpdateGroupDTO(groupId,userId,groupNameEditText.getText().toString(),groupDescriptionEditText.getText().toString(),0);
        Call<GroupDTO> call = groupApiService.updateGroup(groupId,updateGroupDTO);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    GroupDTO groupDTO = response.body();
                    createGroupEditView(groupDTO);
                    addMembersToView();
                    Toast.makeText(EditGroup.this, "Group info has been updated", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditGroup.this, GroupView.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EditGroup.this, "You are not allowed to make change on this group", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(EditGroup.this, t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
