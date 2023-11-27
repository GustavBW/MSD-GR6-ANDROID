package sdu.msd.ui.groupInfo;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.expense.AddExpenseView;
import sdu.msd.ui.home.HomeView;

public class GroupInfoView extends AppCompatActivity {
    // Notification API
    private NotificationAPIService notificationAPIService;
    private static final String BASENOTIFICATIONURL = getApi() + "notifications/";
    private String groupName;

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
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(getApi())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userAPIService = retrofit2.create(UserAPIService.class);
        groupNameTextView = findViewById(R.id.groupNameText);
        descriptionTextView = findViewById(R.id.groupDescText);
        leaveGroup = findViewById(R.id.leaveGroup);
        addGroupMembers = findViewById(R.id.addGroupMembers);
        closeBtn = findViewById(R.id.buttonClose);
        createView();
        closeBtn.setOnClickListener(view -> {
            closeView();
        });

        // Notification Api service:
        retrofit = new Retrofit.Builder()
                .baseUrl(BASENOTIFICATIONURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        notificationAPIService = retrofit.create(NotificationAPIService.class);
        groupName = getIntent().getStringExtra("groupNme");

        addGroupMembers.setOnClickListener(view -> addUserPopup());

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
        getUsersOfGroup(groupId);
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
    private void addUserPopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.fragment_add_group_members_popup, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        EditText editTextUsername = view.findViewById(R.id.addUsernameInput);
        EditText editTextLink = view.findViewById(R.id.inviteLink);
        editTextLink.setEnabled(false);
        Button copyLinkButton = view.findViewById(R.id.copyInviteLink);
        Button addGroupMemberButton = view.findViewById(R.id.addUsernameButton);
        Button closePopup = view.findViewById(R.id.closePopup);

        closePopup.setOnClickListener(popupView -> alertD.dismiss());
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
                if(response.isSuccessful() && response.body() !=null) {
                    List<UserDTO> rs = response.body();
                   addUserToGroupHelper(rs.get(0).id());
                    createCardView(rs);
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
                    pushNotification(id);
                    Toast.makeText(GroupInfoView.this, "User added to group.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUsersOfGroup(int groupId) {
        Call<GroupDTO> call = apiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    getUsersFromId(response.body().getUsers());
                }
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void getUsersFromId(List<Integer> userIds) {
        Call<List<UserDTO>> call = userAPIService.getUsersFromId(userIds);
        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    createCardView(response.body());
                    }
                }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void createCardView(List<UserDTO> users) {
        LinearLayout userButtonContainer = findViewById(R.id.userButtonContainer);
        for (UserDTO user : users) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius((getResources().getDimension(R.dimen.corner_radius)));
            TextView textView = new TextView(this);
            textView.setText(user.name());
            textView.setGravity(1);
            textView.setTextColor(Color.BLACK);
            gradientDrawable.setColor(-1);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            textView.setBackground(layerDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.activity_vertical_margin));
            textView.setLayoutParams(layoutParams);
            userButtonContainer.addView(textView);
            textView.setTextSize(20);
        }
    }

    private void pushNotification(int id) {
        String username = sharedPreferences.getString("username", null);
        String invitationMessage = username + " Added you to group: " + groupName;
        NotificationDTO notificationDTO = new NotificationDTO(" Welcome you to group: " + groupName, invitationMessage);
        Call<Boolean> call = notificationAPIService.pushToUser(id, notificationDTO);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GroupInfoView.this, "A notification is sent to the user!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(GroupInfoView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

