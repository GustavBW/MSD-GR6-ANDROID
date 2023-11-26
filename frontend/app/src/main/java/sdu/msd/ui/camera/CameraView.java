package sdu.msd.ui.camera;

// import your generated binding class
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import sdu.msd.databinding.FragmentCameraBinding;
import sdu.msd.ui.displayPicture.DisplayPictureView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCaptureException;
import androidx.core.content.ContextCompat;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class CameraView extends AppCompatActivity {

    private FragmentCameraBinding viewBinding;
    private LifecycleCameraController cameraController;
    private int groupId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        viewBinding = FragmentCameraBinding.inflate(getLayoutInflater());
        groupId = getIntent().getIntExtra("groupId", -1);
        setContentView(viewBinding.getRoot());
        if(!hasPermissions(getBaseContext())){


        }
        else{
            startCamera();
        }
        viewBinding.btnTake.setOnClickListener(view -> takePhoto());

    }

    private void takePhoto() {
        // Create time-stamped name and MediaStore entry.
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        .build();

        // Set up image capture listener,
        // which is triggered after the photo has been taken
        cameraController.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        String msg = "Photo capture succeeded: " + output.getSavedUri();
                        // Start DisplayPictureActivity and pass the image URI
                        Intent intent = new Intent(CameraView.this, DisplayPictureView.class);
                        intent.putExtra("groupId", groupId);
                        saveImageLocally(Objects.requireNonNull(output.getSavedUri()));
                        startActivity(intent);
                        Log.d(TAG, msg);
                    }


                });
    }


    private void saveImageLocally(Uri savedUri) {
        SharedPreferences sharedPreferences = getSharedPreferences("image_path", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IMAGEPATH", savedUri.toString());
        editor.apply();
    }
    private void startCamera() {
        PreviewView previewView = viewBinding.viewFinder;
        cameraController = new LifecycleCameraController(getBaseContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);
    }
    private boolean hasPermissions(Context context) {
        // Check for camera-related permissions
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


}
