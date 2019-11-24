package com.example.loyaltycardwallet.ui.add;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.loyaltycardwallet.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddActivity extends AppCompatActivity implements LifecycleOwner {
    private final int REQUEST_CODE_PERMISSIONS = 10;

    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextureView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        viewFinder = findViewById(R.id.viewFinder);

        // Request camera permissions
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            viewFinder.post(this::startCamera);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
            );
        }


        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> updateTransform());
    }


    private void startCamera() {
        PreviewConfig.Builder builder = new PreviewConfig.Builder();
        builder.setTargetResolution(new Size(640, 480));

        PreviewConfig previewConfig = builder.build();

        // Build the viewfinder use case
        Preview preview = new Preview(previewConfig);

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener(output -> {
            // To update the SurfaceTexture, we have to remove it and re-add it
            ViewGroup parent = (ViewGroup) viewFinder.getParent();

            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);

            viewFinder.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform();
        });

        // add analyzer
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .build();

        if (!detector.isOperational()) {
            // TODO
//            txtView.setText("Could not set up the detector!");
            return;
        }


        ImageAnalysisConfig config =
                new ImageAnalysisConfig.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                        .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(config);

        imageAnalysis.setAnalyzer(
                AsyncTask.THREAD_POOL_EXECUTOR,
                (image, rotationDegrees) -> {
                    Bitmap bitmap = viewFinder.getBitmap();
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                    SparseArray<Barcode> barcodes = detector.detect(frame);

                    if (barcodes.size() > 0) {
                        Barcode barcode = barcodes.valueAt(0);
                        Log.println(Log.ERROR, "test", barcode.rawValue);

                        Intent intent = new Intent(this, AddActivity.class);
                        startActivity(intent);
                    }

                });


        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, imageAnalysis, preview);
    }

    public void updateTransform() {
        Matrix matrix = new Matrix();

        // Compute the center of the view finder
        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;

        // Correct preview output to account for display rotation
        int rotationDegrees = 0;

        switch (viewFinder.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
            default:
                break;
        }

        matrix.postRotate((float) -rotationDegrees, centerX, centerY);

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post(this::startCamera);
            } else {
                Toast.makeText(
                        this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        }
    }


    /**
     * Check if all permission specified in the manifest have been granted
     */
    private boolean allPermissionsGranted() {
        return Arrays.stream(REQUIRED_PERMISSIONS).allMatch((String it) -> ContextCompat.checkSelfPermission(getBaseContext(), it) == PackageManager.PERMISSION_GRANTED);
    }
}
