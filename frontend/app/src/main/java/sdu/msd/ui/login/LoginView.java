package sdu.msd.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import sdu.msd.R;
import sdu.msd.ui.createUser.CreateUserView;

public class LoginView extends AppCompatActivity {
    private EditText username, password;
    private Button register, login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        register.setOnClickListener(view -> {
            Intent intet = new Intent(LoginView.this, CreateUserView.class);
            startActivity(intet);
        });
        // For the user login there is an interface is created user that can be used to check credetnials and then direct user to home view.

    }
}
