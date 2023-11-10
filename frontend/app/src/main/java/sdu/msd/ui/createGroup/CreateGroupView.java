package sdu.msd.ui.createGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
import sdu.msd.ui.home.HomeView;

public class CreateGroupView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_group); // Load the XML layout for the second activity
        cancelCreation();
        createGroup();

    }

    private void cancelCreation(){
        Button cancelBtn = findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroupView.this, HomeView.class);
                startActivity(intent);
            }
        });
    }

    private void createGroup(){
        Button confirmationBtn = findViewById(R.id.confirm);
        Button uploadBtn = findViewById(R.id.uploadImage);
        EditText groupName = findViewById(R.id.nameEditText);
        EditText groupDescription = findViewById(R.id.descriptionEditText);
        ImageView groupImg = findViewById(R.id.groupImage);
        /*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("YOUR_API_BASE_URL") // Replace with your actual API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

         */

        // MyApiService apiService = retrofit.create(MyApiService.class);


        confirmationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CreateGroupView.this, HomeView.class);
                startActivity(intent);




            }
        });
    }
}


