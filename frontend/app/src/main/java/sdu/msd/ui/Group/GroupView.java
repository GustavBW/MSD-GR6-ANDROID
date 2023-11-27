package sdu.msd.ui.Group;

import static sdu.msd.ui.home.HomeView.getApi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
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
import sdu.msd.ui.editGroup.EditGroup;
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
    private double amount;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getIntExtra("groupId",-1);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        payBtn = new Button(this);
        decimalFormat = new DecimalFormat("#,##0.00");
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEUSERURL)
            .addConverterFactory(GsonConverterFactory.create())
                    .build();

        setContentView(R.layout.fragment_group);
        userAPIService = retrofit.create(UserAPIService.class);
        this.amount = 0;
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
        getHowMuchMoneyUserOwes(userId);


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
        editGroup.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, EditGroup.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("groupName", groupDTO.name());
            startActivity(intent);
        });

        addExpense.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, AddExpenseView.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("groupName", groupDTO.name());
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

        ImageView closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(GroupView.this, HomeView.class);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });
    }
    private void getHowMuchMoneyUserOwes(int userId) {
        this.userId = userId;
         Call<Double> call = debtAPIService.getHowMuchUserOwesGroup(userId, groupId);

        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() !=null){
                    amount = response.body();
                    updateHowMuchToPayInView(amount);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                Toast.makeText(GroupView.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    @SuppressLint("SetTextI18n")
    private void updateHowMuchToPayInView(double amount) {
        FrameLayout parentLayout = findViewById(R.id.paymentPopUp);
        if(amount != 0){
            LinearLayout layout = new LinearLayout(GroupView.this);
            layout.setBackgroundColor(Color.rgb(31, 35, 40));
            layout.setOrientation(LinearLayout.VERTICAL); // Set orientation to vertical
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            TextView textView = new TextView(GroupView.this);
            textView.setText("You owe:");
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            TextView howMuch = new TextView(GroupView.this);
            howMuch.setTextColor(Color.WHITE);
            howMuch.setGravity(Gravity.CENTER);
            String formattedAmount = decimalFormat.format(amount);
            howMuch.setText(formattedAmount + " DKK");
            howMuch.setTextSize(20);
            payBtn = new Button(GroupView.this);
            payBtn.setText("Pay");
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
            payBtn.setLayoutParams(layoutParams);
            payBtn.setTextColor(Color.WHITE);
            payBtn.setBackgroundResource(R.drawable.buttoncolors); // Set the background drawable
            layout.addView(textView);
            layout.addView(howMuch);
            layout.addView(payBtn);


            // Find the parent layout in your XML file

            // Add the dynamic view to the parent layout
            parentLayout.addView(layout);
            
            doPay();

        } else{
            parentLayout.removeAllViews();
        }

        getGroupActivities();

    }



    private void doPay(){
        payBtn.setOnClickListener(v -> {
            payGroupDept();
        });

    }

    private void payGroupDept() {
        String formattedAmount = decimalFormat.format(amount);
        Call<Double> call = debtAPIService.payGroupDept(userId, groupId, amount);
        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if(response.isSuccessful() && response.body() !=null){
                    updateHowMuchToPayInView(response.body());
                    Toast.makeText(GroupView.this, "you have successfully paid" + formattedAmount, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                Toast.makeText(GroupView.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getGroupActivities(){
        Call<List<GroupActivityDTO>> call = apiService.getActivities(groupId);
        call.enqueue(new Callback<List<GroupActivityDTO>>() {
            @Override
            public void onResponse(Call<List<GroupActivityDTO>> call, Response<List<GroupActivityDTO>> response) {
                if(response.isSuccessful() && response.body() != null){
                    createGroupActivitiesView(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<GroupActivityDTO>> call, Throwable t) {
            }
        });

    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void createGroupActivitiesView(List<GroupActivityDTO> groupActivityDTOS){
        LinearLayout activitiesContainer = findViewById(R.id.activitiesContainer);
        activitiesContainer.removeAllViews();
        for (int i = groupActivityDTOS.size() - 1; i >= 0; i--) {
            GroupActivityDTO groupActivityDTO = groupActivityDTOS.get(i);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));
            gradientDrawable.setColor(Color.WHITE);
            TextView textView = new TextView(this);
            String formattedAmount = decimalFormat.format(groupActivityDTO.getAmount());
            String text = groupActivityDTO.getCreditor().name() + " added an expense with amount =" + groupActivityDTO.getAmount() + "DKK";
            if(!groupActivityDTO.isExpense()){
                for(UserDTO userDTO: groupActivityDTO.getDebtees()){
                    if(userDTO.id() == userId){
                        text = userDTO.name()+
                                " had paid " +
                                formattedAmount +
                                " DKK";
                        break;
                    }
                }
            }
            textView.setText(text);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(20);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            textView.setBackground(layerDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 10, 0, (int) 0);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(30,30,2,1);
            activitiesContainer.addView(textView);
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.corner_radius))); // Set your divider height here
            separator.setBackground(getResources().getDrawable(R.drawable.divider_line)); // Set your divider color here
            activitiesContainer.addView(separator);
        }
    }

}
