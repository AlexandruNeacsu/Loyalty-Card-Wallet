package com.example.loyaltycardwallet.ui.add;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Arrays;

// TODO: check permissions, space, etc..
public class ScanActivity extends AppCompatActivity implements LifecycleOwner {
    private final int REQUEST_CODE_PERMISSIONS = 10;

    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private TextureView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        CardProvider provider = getIntent().getExtras().getParcelable("cardProvider");
        EditText editText = findViewById(R.id.barcode_manual_input);

        Log.println(Log.DEBUG, "ScanDEBUG", provider.barcode == null ? "null" : provider.barcode);

        if (provider.barcode != null) {
            editText.setText(provider.barcode);
        }

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


        Button button = findViewById(R.id.barcode_submit_button);
        button.setOnClickListener(v -> {
            String barcode = editText.getText().toString();

            if (barcode.isEmpty()) {
                editText.setError(getString(R.string.error_empty_barcode));
            } else {
                provider.barcode = barcode;


                Intent returnIntent = new Intent();
                returnIntent.putExtra("cardProviderInitialized", provider); // TODO

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

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

                        CardProvider provider = getIntent().getExtras().getParcelable("cardProvider");

                        provider.barcode = barcode.rawValue;

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("cardProviderInitialized", provider); // TODO

                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }

                });


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
