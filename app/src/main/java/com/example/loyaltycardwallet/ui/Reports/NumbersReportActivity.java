package com.example.loyaltycardwallet.ui.Reports;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.loyaltycardwallet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class NumbersReportActivity extends AppCompatActivity {
    private String report;

    PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers_report);

        Toolbar myToolbar = findViewById(R.id.report_numbers_toolbar);
        setSupportActionBar(myToolbar);

        pieChartView = findViewById(R.id.PieChartView);

        LinearLayout layout = findViewById(R.id.numbers_report);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(5, 15, 5, 5);

        NumbersReportActivity self = this;

        FirebaseDatabase.getInstance().getReference().child("magazine").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove all views except the title
                layout.removeViews(2, layout.getChildCount() - 2);

                StringBuilder builder = new StringBuilder();
                builder.append(getResources().getString(R.string.numbers_report_title));
                builder.append("\n");

                List<Pair<String, Integer>> allCounts = new ArrayList<>();

                int total = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    int count = snapshot.getValue(Integer.class);

                    total += count;
                    allCounts.add(new Pair<>(name, count));
                }

                allCounts.sort((o1, o2) -> o2.second - o1.second);

                int i = 0;

                float[] chartValues = new float[11];
                int otherTotal = 0;

                for (Pair<String, Integer> pair : allCounts) {
                    int count = pair.second;

                    // leave room for other
                    if (i < chartValues.length - 2 && count > 0) {
                        String text = String.format("%s: %d", pair.first, count);

                        TextView view = new TextView(self);
                        view.setLayoutParams(params);
                        view.setText(text);

                        builder.append(text);
                        builder.append("\n");

                        layout.addView(view);

                        // calculate the proportion of the "pie"
                        chartValues[i] = (360.0f / total) * count;

                        // max 9
                        i++;
                    } else if (count > 0) {
                        otherTotal += count;
                    }
                }

                if (!(i < chartValues.length - 3)) {
                    // other
                    String text = String.format("%s: %d", getResources().getString(R.string.Other), otherTotal);

                    TextView view = new TextView(self);
                    view.setLayoutParams(params);
                    view.setText(text);

                    builder.append(text);
                    builder.append("\n");

                    layout.addView(view);


                    chartValues[i + 1] = 360 * (otherTotal / total);

                }

                // draw / redraw the piechart
                pieChartView.setValues_degree(chartValues);

                report = builder.toString();

                builder.setLength(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        getApplicationContext().openFileOutput("report_numbers.txt", Context.MODE_PRIVATE)
                );

                outputStreamWriter.write(this.report);
                outputStreamWriter.close();

                Toast.makeText(this, R.string.report_save_succes, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
