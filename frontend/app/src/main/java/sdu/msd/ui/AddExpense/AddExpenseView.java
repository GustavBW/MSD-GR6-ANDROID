package sdu.msd.ui.AddExpense;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.camera.CameraView;

public class AddExpenseView extends AppCompatActivity {
    // User Api
    private static final String BASEURLUser =  getApi() + "users/";
    private UserAPIService userApiService;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferencesUsers;

    // User Information:
    int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragement_add_expense);

        createProfileView();

        // User Api service
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLUser)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userApiService = retrofit.create(UserAPIService.class);
        sharedPreferencesUsers = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferencesUsers.getInt("userId", -1);
    }

    private void createProfileView(){
        // Load and display username:
        username = sharedPreferencesUsers.getString("username", "Value not found!");
        TextView textViewUsername = findViewById(R.id.user);
        textViewUsername.setText(username);
        scaleUsernameText(username);

        // Load users in group:


        // Buttons:
        Button closeButton = findViewById(R.id.buttonClose); // Go to group view:
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseView.this, GroupView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });

        Button cancelButton = findViewById(R.id.cancel); // Go to group view:
        cancelButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseView.this, GroupView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });

        Button confirmButton = findViewById(R.id.confirm); // Go to group view:
        confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseView.this, GroupView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
            // TODO: add logic that adds expense to group
        });

        LinearLayout attachmentButton = findViewById(R.id.attachment); // Go to take camera view:
        attachmentButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddExpenseView.this, CameraView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });

    }

    private void scaleUsernameText(String username) {
        TextView usernameTextView = findViewById(R.id.user);
        usernameTextView.setText(username);

        // float textSize = (float)(75.0 / (1.0 + (float)Math.exp(0.4055 * ((float)(username.length()) - 1.0))));
        float textSize = (float)(100.0 / (1.0 + (float)Math.exp(-(-0.11564339880716944) * ((float)(username.length() - 10.5)))));

        usernameTextView.setTextSize(textSize);
    }
}
