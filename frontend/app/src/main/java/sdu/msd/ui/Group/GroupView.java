package sdu.msd.ui.Group;

import static sdu.msd.ui.home.HomeView.getApi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.DebtAPIService;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.GroupActivityDTO;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.expense.AddExpenseView;
import sdu.msd.ui.groupInfo.GroupInfoView;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.ProfileView;

public class GroupView extends AppCompatActivity {
    private int userId, groupId;
    private Button payBtn, addExpense;
    private GroupAPIService apiService;
    private DebtAPIService debtAPIService;
    private UserAPIService userAPIService;
    private Retrofit retrofit;

    private ImageView groupInfo, editGroup;
    private SharedPreferences sharedPreferences;

    private static final String BASEUSERURL = getApi() +  "users/";
    private static final String BASEGROUPURL =  getApi() + "groups/";
    private static final String BASEDEBTURL = getApi() + "debt/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getIntExtra("groupId",-1);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEUSERURL)
            .addConverterFactory(GsonConverterFactory.create())
                    .build();

        setContentView(R.layout.fragment_group);
        userAPIService = retrofit.create(UserAPIService.class);
        // payBtn = findViewById(R.id.pay);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEGROUPURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
        getGroup(groupId);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEDEBTURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        debtAPIService = retrofit.create(DebtAPIService.class);
        getGroupActivities();
        // getUser(userId);
        getHowMuchMoneyUserOwes(userId);
        //
        doPay();



    }

    private void getGroupActivities(){
        /*
        Call<List<GroupActivityDTO>> call = apiService.getActivities(groupId, 0, ,false);
        call.enqueue(new Callback<List<GroupActivityDTO>>() {
            @Override
            public void onResponse(Call<List<GroupActivityDTO>> call, Response<List<GroupActivityDTO>> response) {
                if(response.isSuccessful() && response.body() != null){
                    createGroupActivitieView();
                }
            }

            @Override
            public void onFailure(Call<List<GroupActivityDTO>> call, Throwable t) {

            }
        });

         */

    }

    private void createGroupActivitieView(){

    }

    private void getUser(int userId){
        Call<UserDTO> call = userAPIService.getUser(userId);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() !=null){
                    getHowMuchMoneyUserOwes(response.body().id());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {

            }
        });
    }
    private void getHowMuchMoneyUserOwes(int userId) {
        this.userId = userId;
         Call<Double> call = debtAPIService.getHowMuchUserOwesGroup(userId, groupId);
       // Call<Double> call = debtAPIService.getHowMuchUser(userId);

        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() !=null){
                    double amount = response.body();
                    Toast.makeText(GroupView.this, "amount" + amount, Toast.LENGTH_SHORT).show();
                    updateHowMuchToPayInView(amount);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {

            }
        });

    }
    private void updateHowMuchToPayInView(double amount) {
        // Create the dynamic views
        if(amount != 0){
            LinearLayout layout = new LinearLayout(GroupView.this);
            layout.setBackgroundColor(Color.rgb(31, 35, 40));
            layout.setOrientation(LinearLayout.VERTICAL); // Set orientation to vertical
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            TextView textView = new TextView(GroupView.this);
            textView.setText("You owe:");
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            TextView howMuch = new TextView(GroupView.this);
            howMuch.setTextColor(Color.WHITE);
            howMuch.setGravity(Gravity.CENTER);
            howMuch.setText(amount + " DKK");
            howMuch.setTextSize(20);
            Button button = new Button(GroupView.this);
            button.setText("Pay");
            // Set layout parameters to add horizontal margins
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int marginInDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    70, // replace with your desired margin in dp
                    getResources().getDisplayMetrics()
            );
            layoutParams.setMargins(marginInDp, 0, marginInDp, 0); // left, top, right, bottom
            button.setLayoutParams(layoutParams);
            button.setTextColor(Color.WHITE);
            button.setBackgroundResource(R.drawable.buttoncolors); // Set the background drawable
            layout.addView(textView);
            layout.addView(howMuch);
            layout.addView(button);


            // Find the parent layout in your XML file
            FrameLayout parentLayout = findViewById(R.id.paymentPopUp); // Change this to the actual ID of your parent layout

            // Add the dynamic view to the parent layout
            parentLayout.addView(layout);

        }

    }


    private void doPay(){
        // TODO: 17-11-2023 This will also be done soon
        /*
        payBtn.setOnClickListener(v -> {
            Intent intent = new Intent(GroupView.this, HomeView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

         */

    }

    private void getGroup(int groupId){
        Call<GroupDTO> call = apiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDTO groupDTO = response.body();
                    createGroupView(groupDTO);
                }
                }
            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(GroupView.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Log the exception for debugging purposes

            }
        });
    }

    private void createGroupView(GroupDTO groupDTO){
        TextView textView = findViewById(R.id.groupName);
        textView.setText(groupDTO.name());
        groupInfo = findViewById(R.id.groupInfo);
        editGroup = findViewById(R.id.editGroupBtn);
        addExpense = findViewById(R.id.addExpense);
        // TODO: 17-11-2023
        /*
        The rest will be added soon when other features are done.
         */
        editGroup.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, NotificationsView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        addExpense.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, AddExpenseView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        });

        groupInfo.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, GroupInfoView.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("groupNme", groupDTO.name());
            intent.putExtra("groupDescription", groupDTO.descriptions());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        });

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, HomeView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });
    }
}
