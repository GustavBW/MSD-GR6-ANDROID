package sdu.msd.ui.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import sdu.msd.R;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.createGroup.CreateGroupView;
import sdu.msd.ui.notifications.NotificationsView;
import sdu.msd.ui.profile.profile;

public class HomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);
        Button groupBtn = findViewById(R.id.group);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnNotifications = findViewById(R.id.btnNotifications);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeView.this, profile.class);
                startActivity(intent);
            }
        });

        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeView.this, NotificationsView.class);
                startActivity(intent);
            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeView.this, CreateGroupView.class);
                startActivity(intent);
            }
        });

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeView.this,GroupView.class);
                startActivity(intent);
            }
        });
    }
}
