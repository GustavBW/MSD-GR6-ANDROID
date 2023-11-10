package sdu.msd.ui.createGroup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.dtos.CreateGroupDTO;
import sdu.msd.ui.home.HomeView;

public class CreateGroupView extends AppCompatActivity {
    private Random random;
    private Button cancelBtn;
    private Button confirmationBtn;
    private Button uploadBtn;
    private EditText groupName;
    private EditText groupDescription;
    private ImageView groupImg;
    private static final String BASEURL =  "http://10.126.121.41:8080/api/v1/groups/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_group); // Load the XML layout for the second activity
        cancelCreation();
        createGroup();

    }

    private void cancelCreation(){
        cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroupView.this, HomeView.class);
                startActivity(intent);
            }
        });

        createGroup();
    }

    private void createGroup(){
        random = new Random();
        confirmationBtn = findViewById(R.id.confirm);
        uploadBtn = findViewById(R.id.uploadImage);
        int adminID = random.nextInt();
        groupName = findViewById(R.id.nameEditText);
        groupDescription = findViewById(R.id.descriptionEditText);
        groupImg = findViewById(R.id.groupImage);
        confirmationBtn.setOnClickListener(view -> {
            if(groupName.getText().toString().isEmpty() &&  groupDescription.getText().toString().isEmpty()){
                Toast.makeText(CreateGroupView.this, "Please enter both the values", Toast.LENGTH_SHORT).show();

                return;
            }
            postData(adminID,groupName.getText().toString(),groupDescription.getText().toString());
            //Intent intent = new Intent(CreateGroupView.this, HomeView.class);
            // startActivity(intent);




        });



    }

    private void postData(int id, String name, String description) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GroupAPIService apiService = retrofit.create(GroupAPIService.class);
        CreateGroupDTO createGroupDTO = new CreateGroupDTO(id,name,description);
        Call<CreateGroupDTO> call = apiService.createGroup(createGroupDTO);
        call.enqueue(new Callback<CreateGroupDTO>() {
            @Override
            public void onResponse(Call<CreateGroupDTO> call, Response<CreateGroupDTO> response) {
                Toast.makeText(CreateGroupView.this, "Data added to API"+id, Toast.LENGTH_SHORT).show();
                groupName.setText("");
                groupDescription.setText("");
            }

            @Override
            public void onFailure(Call<CreateGroupDTO> call, Throwable t) {
                Toast.makeText(CreateGroupView.this, t.toString(), Toast.LENGTH_LONG).show();


            }
        });
    }
}


