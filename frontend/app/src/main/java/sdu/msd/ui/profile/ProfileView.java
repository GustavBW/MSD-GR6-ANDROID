package sdu.msd.ui.profile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;

public class ProfileView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

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