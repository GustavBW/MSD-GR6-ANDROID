package sdu.msd.ui.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import sdu.msd.R;
import sdu.msd.ui.createGroup.CreateGroupView;

public class HomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeView.this, CreateGroupView.class);
                startActivity(intent);
            }
        });
    }
}
