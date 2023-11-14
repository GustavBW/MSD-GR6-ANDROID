package sdu.msd.ui.createGroup;

import  android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    private Button changeColor;
    private EditText groupName;
    private EditText groupDescription;
    private View groupViewColor;
    private String username;
    int color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_group); // Load the XML layout for the second activity
        username = getIntent().getStringExtra("username");
        changeColor = findViewById(R.id.changeColor);
        groupViewColor = findViewById(R.id.groupViewColor);

        changeColor.setOnClickListener(view -> changeViewColor());
        cancelCreation();
        createGroup();



    }

    private void changeViewColor(){
        //Generate Random Color
        Random randomColor = new Random();
        color = Color.argb(255, randomColor.nextInt(256), randomColor.nextInt(256), randomColor.nextInt(256));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));
        gradientDrawable.setColor(color);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
        groupViewColor.setBackground(layerDrawable);


    }

    private void cancelCreation(){
        cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CreateGroupView.this, HomeView.class);
            startActivity(intent);
        });
    }

    private void createGroup(){
        random = new Random();
        confirmationBtn = findViewById(R.id.confirm);
        int adminId = random.nextInt();
        groupName = findViewById(R.id.nameEditText);
        groupDescription = findViewById(R.id.descriptionEditText);
        confirmationBtn.setOnClickListener(view -> {
            if(groupName.getText().toString().isEmpty() &&  groupDescription.getText().toString().isEmpty()){
                Toast.makeText(CreateGroupView.this, "Please enter both the values", Toast.LENGTH_SHORT).show();

                return;
            }
            postData(adminId,groupName.getText().toString(),groupDescription.getText().toString(),this.color);





        });



    }

    private void postData(int id, String name, String description, int groupColor) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HomeView.getBASEURL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GroupAPIService apiService = retrofit.create(GroupAPIService.class);
        CreateGroupDTO createGroupDTO = new CreateGroupDTO(id,name,description, groupColor);
        Call<CreateGroupDTO> call = apiService.createGroup(createGroupDTO);
        call.enqueue(new Callback<CreateGroupDTO>() {
            @Override
            public void onResponse(Call<CreateGroupDTO> call, Response<CreateGroupDTO> response) {
                Intent intent = new Intent(CreateGroupView.this, HomeView.class);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<CreateGroupDTO> call, Throwable t) {
                Toast.makeText(CreateGroupView.this, t.toString(), Toast.LENGTH_LONG).show();


            }
        });
    }
}


