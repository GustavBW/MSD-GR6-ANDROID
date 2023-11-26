package sdu.msd.ui.displayPicture;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import sdu.msd.R;
import sdu.msd.ui.expense.AddExpenseView;
import sdu.msd.ui.camera.CameraView;

public class DisplayPictureView extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String imageUri;
    private int groupId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_picture);
        ImageView imageView = findViewById(R.id.viewPhoto);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button retake = findViewById(R.id.retake);
        sharedPreferences = getSharedPreferences("image_path", MODE_PRIVATE);
        // Get the URI of the captured image from the intent
        imageUri =sharedPreferences.getString("IMAGEPATH",null);
        groupId = getIntent().getIntExtra("groupId", -1);

        // Load and display the captured image using Glide
        Glide.with(this)
                .load(Uri.parse(imageUri))
                .into(imageView);

        retake.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayPictureView.this, CameraView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            finish();
        });

        btnCancel.setOnClickListener(view -> {
            deleteImageLocally();
            // Handle cancel button click (e.g., delete the captured image)
            Intent intent = new Intent(DisplayPictureView.this, AddExpenseView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            finish();
        });

        btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayPictureView.this, AddExpenseView.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
            finish();
        });
    }
    private void deleteImageLocally() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        String imageUri = sharedPreferences.getString("IMAGEPATH", null);
        if (imageUri != null) {
            Uri uri = Uri.parse(imageUri);
            deleteImageFromMediaStore(uri);
        }
    }
    private void deleteImageFromMediaStore(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        int rowsDeleted = contentResolver.delete(uri, null, null);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "File deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show();
        }
    }
}
