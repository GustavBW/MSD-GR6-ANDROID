package sdu.msd.ui.gallery;

import android.os.Bundle;
import android.view.Menu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SomeClass extends AppCompatActivity {

    public SomeClass(String message){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationView view = new NavigationView(savedInstanceState);
    }
}
