package com.example.loyaltycardwallet.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.ui.add.AddActivity;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownStackAnimatorAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener {
    private static int ADD_PROVIDER = 0;
    public static List<CardProvider> TEST_DATA = new ArrayList<>(); // TODO
    private CustomStackAdapter stackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardStackView stackView = findViewById(R.id.stackview_main);

        stackView.setItemExpendListener(this);
        stackView.setAnimatorAdapter(new UpDownStackAnimatorAdapter(stackView));

        stackAdapter = new CustomStackAdapter(this);
        stackView.setAdapter(stackAdapter);


        new Handler().postDelayed(
                () -> stackAdapter.updateData(TEST_DATA),
                200
        );

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(myToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivityForResult(intent, ADD_PROVIDER);

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PROVIDER) {
            if (resultCode == RESULT_OK && data != null) {
                CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                Log.println(Log.DEBUG, "testAlex", Boolean.toString(provider.getLogo() == null) );

                TEST_DATA.add(provider);

                stackAdapter.updateData(TEST_DATA);
            }
        }
    }

    @Override
    public void onItemExpend(boolean expand) {
    }
}
