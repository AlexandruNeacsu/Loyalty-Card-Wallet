package com.example.loyaltycardwallet.ui.Reports;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loyaltycardwallet.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class NumbersReportActivity extends AppCompatActivity {
    private String report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers_report);

        Toolbar myToolbar = findViewById(R.id.report_numbers_toolbar);
        setSupportActionBar(myToolbar);

        LinearLayout layout = findViewById(R.id.numbers_report);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(5, 15,  5, 5);

        NumbersReportActivity self = this;

        FirebaseDatabase.getInstance().getReference().child("magazine").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                layout.removeViews(2, layout.getChildCount() - 2);

                StringBuilder builder = new StringBuilder();
                builder.append(getResources().getString(R.string.numbers_report_title));
                builder.append("\n");

                int i = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    int count = snapshot.getValue(Integer.class);

                    // skip empty stores
                    if (count == 0) continue;

                    String text = String.format("%s: %s", name, count);


                    TextView view = new TextView(self);
                    view.setLayoutParams(params);
                    view.setText(text);

                    builder.append(text);
                    builder.append("\n");

                    layout.addView(view);

                    // max 10 cards
                    i++;
                    if(i > 10) break;
                }

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
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
