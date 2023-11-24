package sdu.msd.ui.addExpense;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.DebtAPIService;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.apiCalls.UserAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.camera.CameraView;

public class AddExpenseView extends AppCompatActivity {
    // Debt Api

    private final String BASEURLDebt = getApi() + "debt/";

    private DebtAPIService debtAPIService;

    private HashMap<UserDTO, CheckBox> userCheckBoxes;
    // User Api
    private static final String BASEURLUser =  getApi() + "users/";
    private UserAPIService userApiService;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferencesUsers;

    // User Information:
    int userId;
    private String username;

    // Group Api:
    private static final String BASEURLGroup = getApi() + "groups/";
    private GroupAPIService groupApiService;

    // Group Information:
    int groupId;

    // Payment information:
    private ArrayList<Integer> selectedMembers;
    private Button confirmButton;
    private EditText amountEditText;
    private double amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragement_add_expense);

        createProfileView();

        // User Api service:
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLUser)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userApiService = retrofit.create(UserAPIService.class);
        sharedPreferencesUsers = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferencesUsers.getInt("userId", -1);
        confirmButton = findViewById(R.id.confirm);

        // Group Api service:
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLGroup)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        groupApiService = retrofit.create(GroupAPIService.class);
        groupId = getIntent().getIntExtra("groupId", -1);
        userCheckBoxes = new HashMap<>();
        selectedMembers = new ArrayList<>();
        getGroupInformation(groupId);

        // GDebt Api service:
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURLDebt)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        debtAPIService = retrofit.create(DebtAPIService.class);

        // Define checkbox listeners:
        checkIfMemberIsSelected();

        confirm();
    }

    private void getGroupInformation(int groupId) {
        Call<GroupDTO> call = groupApiService.getGroup(groupId);
        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> memberIds = response.body().getUsers();
                    getMembers(memberIds);

                }
            }
            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Toast.makeText(AddExpenseView.this, Log.getStackTraceString(t).substring(150), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // Log the exception for debugging purposes

            }
        });
    }

    private void getMembers(List<Integer> userIds) {
        Call<List<UserDTO>> call = userApiService.getUserFromId(userIds);
        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserDTO> members = response.body();
                    addMembersToView(members);
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                Toast.makeText(AddExpenseView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMembersToView(List<UserDTO> members) {
        LinearLayout memberContainer = findViewById(R.id.groupButtonContainer);
        for (UserDTO member : members) {
            GradientDrawable gradientDrawable = new GradientDrawable();

            // Layout
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.activity_vertical_margin));

            TextView textView = new TextView(this);
            textView.setText(member.name());
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(20);

            gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));
            CheckBox checkBox = new CheckBox(this);

            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            linearLayout.setBackground(layerDrawable);
            // TODO: Set check design

            // Add items to container
            memberContainer.addView(textView);
            memberContainer.addView(checkBox);

            // Map user to checkbox
            userCheckBoxes.put(member, checkBox);
        }
    }

    private void createProfileView(){
        // Load and display username:
        username = sharedPreferencesUsers.getString("username", "Value not found!");
        TextView textViewUsername = findViewById(R.id.user);
        textViewUsername.setText(username);
        scaleUsernameText(username);

        // EditText:
        amountEditText = findViewById(R.id.amount);

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

    private void checkIfMemberIsSelected() {
        for(Map.Entry<UserDTO,CheckBox> pair : userCheckBoxes.entrySet()){
            pair.getValue().setOnClickListener(view -> {
                if (pair.getValue().isChecked()) {
                    selectedMembers.add(pair.getKey().id());
                } else {
                    selectedMembers.remove(pair.getKey().id());
                }
            });
        }
    }

    private void confirm() {
        amount = Double.parseDouble(amountEditText.getText().toString()) / (double)selectedMembers.size();
        confirmButton.setOnClickListener(view -> {
        Call<Boolean> call = debtAPIService.addDebtToMembers(userId, amount, selectedMembers, groupId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(AddExpenseView.this, GroupView.class);
                    Toast.makeText(AddExpenseView.this, "The expense was added!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(AddExpenseView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
        });
    }
}

