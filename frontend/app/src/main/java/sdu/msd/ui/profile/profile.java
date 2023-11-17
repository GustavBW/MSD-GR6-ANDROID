package sdu.msd.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
import sdu.msd.ui.home.HomeView;

public class profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile); // Load the XML layout for the second activity

        scaleUsernameText();


    }

    private void scaleUsernameText() {
        // Choose the appropriate dimension based on the length of the username
        TextView usernameTextView = findViewById(R.id.username);
        String username = usernameTextView.getText().toString();

        float textSize;

        textSize = (float)((2.0 / username.length()) * (username.length() / 20.0));

        usernameTextView.setTextSize(textSize);
    }
}


