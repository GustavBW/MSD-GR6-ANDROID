package sdu.msd.ui.notifications;

import static sdu.msd.ui.home.HomeView.getApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import sdu.msd.apiCalls.NotificationAPIService;
import sdu.msd.dtos.NotificationDTO;
import sdu.msd.ui.home.HomeView;

public class NotificationsView extends AppCompatActivity {

    private int userId;
    private NotificationAPIService notificationAPIService;
    private Retrofit retrofit;
    private SharedPreferences sharedPreferences, notifcationSharedPrefences;
    private static final String BASENOTIFICATIONURL = getApi() + "notifications/";
    private List<NotificationDTO> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        // Get user id:
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        notifcationSharedPrefences = getSharedPreferences("notifications", MODE_PRIVATE);
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
        notificationsAreChecked();
    }

    private void notificationsAreChecked() {
        SharedPreferences.Editor editor = notifcationSharedPrefences.edit();
        editor.putBoolean("areChecked", true);
        editor.apply();
    }

    private void addNotificationsToView(List<NotificationDTO> notifications) {
        LinearLayout notificationsContainer = findViewById(R.id.notificationContainer);

        // Append scroll view:
        for (int i = notifications.size() - 1; i >= 0; i--) {
            NotificationDTO notification = notifications.get(i);
            // Create container for each notification
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.WHITE);
            gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));

            LinearLayout notificationLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 20);
            notificationLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams marginHorizontal = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            marginHorizontal.setMargins(20,0,20,0);

            // Create notification title:
            TextView titleTextView = new Button(this);
            titleTextView.setText(notification.title());
            titleTextView.setTextColor(Color.BLACK);
            titleTextView.setTextSize(20);  // Adjusted size
            //titleTextView.setBackground(gradientDrawable);
            titleTextView.setBackgroundColor(0);
            titleTextView.setLayoutParams(marginHorizontal);
            // Create notification message:
            TextView messageTextView = new Button(this);
            messageTextView.setText(notification.message());
            messageTextView.setTextColor(Color.BLACK);
            messageTextView.setTextSize(15);
           // messageTextView.setBackground(gradientDrawable);
            messageTextView.setBackgroundColor(0);
            messageTextView.setLayoutParams(marginHorizontal);
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.corner_radius))); // Set your divider height here
            separator.setBackground(getResources().getDrawable(R.drawable.divider_line)); // Set your divider color here
            // Add title and message to the container
            notificationLayout.addView(titleTextView);
            notificationLayout.addView(messageTextView);
            notificationLayout.addView(separator);
            notificationLayout.setBackground(gradientDrawable);
            notificationLayout.setLayoutParams(layoutParams);

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
            startActivity(intent);
        });

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationsView.this, HomeView.class);
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
