// DisplayPictureActivity.java

package sdu.msd.ui.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import sdu.msd.R; // Replace with your actual package name
import sdu.msd.ui.addExpense.AddExpenseView;

public class DisplayPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_picture);

        ImageView imageView = findViewById(R.id.viewPhoto);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button retake = findViewById(R.id.retake);

        // Get the path of the captured image from the intent
        String imagePath = getIntent().getStringExtra("IMAGE_PATH");

        // Display the captured image
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        retake.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayPictureActivity.this, CameraView.class);
            startActivity(intent);
            finish();
        });

        btnCancel.setOnClickListener(view -> {
            // Handle cancel button click (e.g., delete the captured image)
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
            Intent intent = new Intent(DisplayPictureActivity.this, AddExpenseView.class);
            startActivity(intent);
            finish(); // Close the activity
        });

        btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayPictureActivity.this, AddExpenseView.class);
            startActivity(intent);
            finish(); // Close the activity
        });
    }
}
