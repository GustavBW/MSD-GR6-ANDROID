package sdu.msd.ui.editGroup;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UpdateGroupDTO;
import sdu.msd.dtos.UserCredentialsDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createUser.CreateUserView;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.login.LoginView;

public class EditGroup extends AppCompatActivity {

    int userId, groupId;
    private Button saveChangesButton, deleteGroupButton, closeButton;
    private EditText groupNameEditText, groupDescriptionEditText;
    private GroupAPIService groupApiService;
    private UserAPIService userAPIService;
    private  Retrofit retrofit;
    private SharedPreferences sharedPreferences;


    private static final String BASEURLGROUP =  getApi() + "groups/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getIntExtra("groupId",-1);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",-1);
        setContentView(R.layout.fragment_edit_group);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLGROUP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        groupApiService = retrofit.create(GroupAPIService.class);
        groupDescriptionEditText = findViewById(R.id.groupDescEditText);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        saveChangesButton = findViewById(R.id.saveButton);
        deleteGroupButton = findViewById(R.id.deleteGroup);
        cancelCreation();
        getGroup(groupId);
    }

    private void cancelCreation(){
        closeButton = findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditGroup.this, HomeView.class);
            startActivity(intent);
        });
    }

    private void checkIfAdmin(int userId) {
        Call<Boolean> call = groupApiService.getUserIsAdmin(groupId,userId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()){
                    deleteGroup();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    private void getGroup(int groupId){
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
            checkIfAdmin(userId);
        });
    }

    public void deleteGroup(){
        String username = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(username,password);
        Call<GroupDTO> call = groupApiService.deleteGroup(groupId,userCredentialsDTO);

        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(EditGroup.this,"works",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditGroup.this, HomeView.class);
                    intent.putExtra("groupId",groupId);
                    startActivity(intent);
                    finish();
                } else {Toast.makeText(EditGroup.this,"fail",Toast.LENGTH_LONG).show();}
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                //Toast.makeText(EditGroup.this,t.toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditGroup.this, HomeView.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
                finish();
            }
        });

    }
    public void updateData(){
        UpdateGroupDTO updateGroupDTO = new UpdateGroupDTO(userId,groupNameEditText.getText().toString(),groupDescriptionEditText.getText().toString(),0);
        Call<GroupDTO> call = groupApiService.updateGroup(groupId,updateGroupDTO);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(EditGroup.this, "Group info has been updated", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditGroup.this, GroupView.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                    finish();
                } else {Toast.makeText(EditGroup.this, "false", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(EditGroup.this, t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
