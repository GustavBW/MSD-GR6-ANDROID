package sdu.msd.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
import sdu.msd.ui.home.HomeView;
import sdu.msd.ui.login.LoginView;

public class ProfileView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        scaleUsernameText();

        Button closeButton = findViewById(R.id.buttonClose); // Go to home
        closeButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, HomeView.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.buttonLogout); // Go to login
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, LoginView.class);
            startActivity(intent);
        });

        Button invoicesButton = findViewById(R.id.buttonInvoices); // Go to invoices
        invoicesButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileView.this, HomeView.class); // TODO: change to InvoiceView.class when possible.
            startActivity(intent);
        });
    }

    private void scaleUsernameText() {
        TextView usernameTextView = findViewById(R.id.username);
        String username = ""; // Replace with the actual username value
        usernameTextView.setText(username);

        // float textSize = (float)(75.0 / (1.0 + (float)Math.exp(0.4055 * ((float)(username.length()) - 1.0))));
        float textSize = (float)(100.0 / (1.0 + (float)Math.exp(-(-0.11564339880716944) * ((float)(username.length() - 10.5)))));

        usernameTextView.setTextSize(textSize);
    }

    private void fetchData() {

    }
}