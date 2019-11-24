package com.example.loyaltycardwallet.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.ui.AddActivity;
import com.loopeer.cardstack.CardStackView;

import java.util.Arrays;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.loopeer.cardstack.UpDownStackAnimatorAdapter;

public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener {
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
            R.color.color_8,
            R.color.color_9,
            R.color.color_10,
            R.color.color_11,
            R.color.color_12,
            R.color.color_13,
            R.color.color_14,
            R.color.color_15,
            R.color.color_16,
            R.color.color_17,
            R.color.color_18,
            R.color.color_19,
            R.color.color_20,
            R.color.color_21,
            R.color.color_22,
            R.color.color_23,
            R.color.color_24,
            R.color.color_25,
            R.color.color_26
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardStackView stackView = findViewById(R.id.stackview_main);

        stackView.setItemExpendListener(this);
        stackView.setAnimatorAdapter(new UpDownStackAnimatorAdapter(stackView));

        final CustomStackAdapter stackAdapter = new CustomStackAdapter(this);
        stackView.setAdapter(stackAdapter);


        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        stackAdapter.updateData(Arrays.asList(TEST_DATAS));
                    }
                },
                200
        );

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(myToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onItemExpend(boolean expand) {
    }
}
