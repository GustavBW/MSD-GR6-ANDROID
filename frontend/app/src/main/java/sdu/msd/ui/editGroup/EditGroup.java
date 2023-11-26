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
import sdu.msd.dtos.CreateGroupDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UpdateGroupDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createGroup.CreateGroupView;
import sdu.msd.ui.home.HomeView;

public class EditGroup extends AppCompatActivity {

    int userId, groupId;
    private Button saveChangesButton, deleteGroupButton, closeButton;
    private EditText groupNameEditText, groupDescriptionEditText;
    private GroupAPIService apiService;
    private SharedPreferences sharedPreferences;


    private static final String BASEURL =  getApi() + "groups/";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getIntExtra("groupId",-1);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",-1);
        setContentView(R.layout.fragment_edit_group);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);

        getGroup(groupId);

    }

    private void getGroup(int groupId){
        Call<GroupDTO> call = apiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDTO groupDTO = response.body();
                    Toast.makeText(EditGroup.this,groupDTO.descriptions(),Toast.LENGTH_LONG).show();
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
        groupDescriptionEditText = findViewById(R.id.groupDescEditText);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        saveChangesButton = findViewById(R.id.saveButton);
        deleteGroupButton = findViewById(R.id.deleteGroup);

        groupNameEditText.setText(groupDTO.name());
        groupDescriptionEditText.setText(groupDTO.descriptions());

        saveChangesButton.setOnClickListener(view -> {
            updateData();
        });
    }

    public void updateData(){
        UpdateGroupDTO updateGroupDTO = new UpdateGroupDTO(userId,groupNameEditText.getText().toString(),groupDescriptionEditText.getText().toString());
        Call<GroupDTO> call = apiService.updateGroup(updateGroupDTO.getIdOfActingUser(),updateGroupDTO);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    GroupDTO groupDTO = response.body();
                    Toast.makeText(EditGroup.this, "Group info has been updated", Toast.LENGTH_LONG).show();
                    updateView(groupDTO);
                }
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(EditGroup.this, t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void updateView(GroupDTO groupDTO){
        groupNameEditText.setText(groupDTO.name());
        groupDescriptionEditText.setText(groupDTO.descriptions());

    }

}
