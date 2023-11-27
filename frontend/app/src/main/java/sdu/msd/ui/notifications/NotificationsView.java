package sdu.msd.ui.notifications;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.GroupAPIService;
import sdu.msd.apiCalls.NotificationAPIService;
import sdu.msd.dtos.NotificationDTO;
import sdu.msd.dtos.UserDTO;
import sdu.msd.ui.expense.AddExpenseView;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.profile.ProfileView;

public class NotificationsView extends AppCompatActivity {

    private int userId;
    private NotificationAPIService notificationAPIService;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences;
    private static final String BASENOTIFICATIONURL = getApi() + "notifications/";
    private List<NotificationDTO> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        // Get user id:
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        notifications = new ArrayList<>();

        // Build API:
        retrofit = new Retrofit.Builder()
                .baseUrl(BASENOTIFICATIONURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        notificationAPIService = retrofit.create(NotificationAPIService.class);

        // Create view:
        createNotificationsView();
    }

    private void addNotificationsToView(List<NotificationDTO> notifications) {
        // Find container
        LinearLayout notificationsContainer = findViewById(R.id.notificationContainer);

        // Append scroll view:
        for (NotificationDTO notification : notifications) {
            // Create container for each notification
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.WHITE);
            LinearLayout notificationLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.activity_vertical_margin));
            notificationLayout.setLayoutParams(layoutParams);
            notificationLayout.setOrientation(LinearLayout.VERTICAL);

            // Create notification title:
            TextView titleTextView = new TextView(this);
            titleTextView.setText(notification.title());
            titleTextView.setTextColor(Color.BLACK);
            titleTextView.setTextSize(25);
            titleTextView.setPadding(10,0,0,0);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            titleTextView.setBackground(layerDrawable);

            // Create notification message:
            TextView messageTextView = new TextView(this);
            messageTextView.setText(notification.message());
            messageTextView.setTextColor(Color.BLACK);
            messageTextView.setTextSize(15);
            messageTextView.setPadding(10,0,0,0);
            layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            messageTextView.setBackground(layerDrawable);

            // Add title and message to the container
            notificationLayout.addView(titleTextView);
            notificationLayout.addView(messageTextView);

            // Add the container to the main container
            notificationsContainer.addView(notificationLayout);
        }
    }



    private void getNotifications() {
        Call<List<NotificationDTO>> call = notificationAPIService.getUserNotifications(userId);
        call.enqueue(new Callback<List<NotificationDTO>>() {

            @Override
            public void onResponse(Call<List<NotificationDTO>> call, Response<List<NotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications = response.body();
                    addNotificationsToView(notifications);
                }
            }

            @Override
            public void onFailure(Call<List<NotificationDTO>> call, Throwable t) {
                Toast.makeText(NotificationsView.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createNotificationsView() {

        // Buttons:
        Button btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationsView.this, HomeView.class);
            intent.putExtra("AreChecked",true);
            startActivity(intent);
        });

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationsView.this, HomeView.class);
            intent.putExtra("AreChecked",true);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.slide_in_down);
        });

        getNotifications();
       // removeNotificationsFor(userId);
    }

    /*
    private void removeNotificationsFor(int userId){
        List<Integer> notificationsIds = new ArrayList<>();
        for(NotificationDTO notificationDTO: notifications){
            notificationsIds.add(notificationDTO)

        }
        Call<Boolean> call = notificationAPIService.removeNotificationsFor(userId, notifications);

    }

     */
}
