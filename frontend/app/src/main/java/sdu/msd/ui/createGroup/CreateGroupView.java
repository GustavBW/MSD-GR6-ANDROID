package sdu.msd.ui.createGroup;

import android.content.Context;
import  android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.dtos.CreateGroupDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.ui.home.HomeView;

public class CreateGroupView extends AppCompatActivity {
    private Random random;
    private Button cancelBtn,confirmationBtn, changeColor;
    private EditText groupName,groupDescription;
    private View groupViewColor;
    private int color;
    private SharedPreferences sharedPreferences;
    private static List<GroupDTO> groupDTOList = new LinkedList<>();
    private static final String BASEURL =  HomeView.getApi() + "groups/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_group);
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
        confirmationBtn = findViewById(R.id.confirm);
        groupName = findViewById(R.id.nameEditText);
        groupDescription = findViewById(R.id.descriptionEditText);
        confirmationBtn.setOnClickListener(view -> {
            if(groupName.getText().toString().isEmpty() &&  groupDescription.getText().toString().isEmpty()){
                Toast.makeText(CreateGroupView.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                return;
            }
            sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
            postData(sharedPreferences.getInt("userId", -1),groupName.getText().toString(),groupDescription.getText().toString(),this.color);
        });
    }
    private void postData(int userId, String name, String description, int groupColor) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GroupAPIService apiService = retrofit.create(GroupAPIService.class);
        CreateGroupDTO createGroupDTO = new CreateGroupDTO(userId,name,description, groupColor);
        Call<GroupDTO> call = apiService.createGroup(createGroupDTO);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful()) {
                    GroupDTO groupDTO = response.body();
                    if (groupDTO != null) {
                        saveGroupsLocally(groupDTO);
                        Intent intent = new Intent(CreateGroupView.this, HomeView.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(CreateGroupView.this, "error in" + response.code(), Toast.LENGTH_SHORT).show();
                    // Handle the error, you can check the error body using response.errorBody()
                }
            }
            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(CreateGroupView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void saveGroupsLocally(GroupDTO groupDTO) {
        SharedPreferences sharedPreferences = getSharedPreferences("group_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        groupDTOList.add(groupDTO);
        String json = gson.toJson(groupDTOList);
        editor.putString("groups", json);
        editor.apply();
    }



}


