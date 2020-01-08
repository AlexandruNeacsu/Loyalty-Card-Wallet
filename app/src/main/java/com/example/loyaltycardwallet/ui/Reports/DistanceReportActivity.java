package com.example.loyaltycardwallet.ui.Reports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.Card.CardDataSource;
import com.example.loyaltycardwallet.data.Card.CardDbActivity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class DistanceReportActivity extends AppCompatActivity implements CardDbActivity, LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private final int REQUEST_CODE_PERMISSIONS = 11;

    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    private String report;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_report);

        Toolbar myToolbar = findViewById(R.id.report_distance_toolbar);
        setSupportActionBar(myToolbar);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
            );
        }

        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.println(Log.DEBUG, "Raport", "before gps");

            if (isGPSEnabled) {
                Log.println(Log.DEBUG, "Raport", "after gps");

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.println(Log.DEBUG, "Raport", "location");

                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    Log.println(Log.DEBUG, "Raport", "before execute");

                    new CardDataSource.getClosest<>(this, lat, lng).execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getClosestItemsResponse(List<Card> list) {
        LinearLayout layout = findViewById(R.id.distance_report);

        StringBuilder builder = new StringBuilder();
        builder.append(getResources().getString(R.string.distance_report_title));
        builder.append("\n");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(5, 15,  5, 5);

        list.forEach(card -> {
            String text = String.format("%s: %s", card.name, card.address);

            TextView view = new TextView(this);
            view.setLayoutParams(params);
            view.setText(text);

            builder.append(text);
            builder.append("\n");

            layout.addView(view);
        });

        this.report = builder.toString();

        builder.setLength(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        getApplicationContext().openFileOutput("report_distance.txt", Context.MODE_PRIVATE)
                );

                outputStreamWriter.write(this.report);
                outputStreamWriter.close();

                Toast.makeText(this, R.string.report_save_succes, Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void getItemsResponse(List<Card> cards) {

    }

    @Override
    public void insertItemResponse(Boolean response) {

    }

    @Override
    public void updateItemResponse(Boolean response) {

    }

    @Override
    public void deleteItemResponse(Boolean response) {

    }
}
