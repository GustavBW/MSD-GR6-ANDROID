package sdu.msd.ui.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
import sdu.msd.ui.createGroup.CreateGroupView;
import sdu.msd.ui.home.HomeView;

public class GroupView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group);

        Button payBtn = findViewById(R.id.pay);


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupView.this, HomeView.class);
                startActivity(intent);
            }
        });


    }
}
