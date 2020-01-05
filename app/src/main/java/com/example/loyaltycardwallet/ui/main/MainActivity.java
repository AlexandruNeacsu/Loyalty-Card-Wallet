package com.example.loyaltycardwallet.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.Card.CardDataSource;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.ui.DbInterfaces.CardDbActivity;
import com.example.loyaltycardwallet.ui.add.AddActivityCardProvider;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownStackAnimatorAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener, CardDbActivity {
    private static int ADD_PROVIDER = 0;
    public CustomStackAdapter stackAdapter;

    public Menu menu;

    @Override
    public void getItemsResponse(List<Card> cards) {
        new Handler().postDelayed(
                () -> stackAdapter.updateData(cards),
                200
        );
    }

    @Override
    public void insertItemResponse(Boolean response) {
        new CardDataSource.getAll<>(this, getApplicationContext()).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardStackView stackView = findViewById(R.id.stackview_main);

        stackView.setItemExpendListener(this);
        stackView.setAnimatorAdapter(new UpDownStackAnimatorAdapter(stackView));

        stackAdapter = new CustomStackAdapter(this);
        stackView.setAdapter(stackAdapter);

        new CardDataSource.getAll<>(this, getApplicationContext()).execute();


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(myToolbar);


        // set the API keys
        SharedPreferences sharedPreferences = getSharedPreferences("loyaltyCarda-keys", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("places", null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("places", "AIzaSyCgMhZFEdwuzEJ030exD9vf9HPl5A0WqdE");

            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, AddActivityCardProvider.class);
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

                Card card = new Card(provider);

                new CardDataSource.insert<>(this, getApplicationContext(), card).execute();
            }
        }
    }

    @Override
    public void onItemExpend(boolean expand) {
    }
}
