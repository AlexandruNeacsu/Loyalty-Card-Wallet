package com.example.loyaltycardwallet.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.loyaltycardwallet.ui.add.ScanActivity;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownStackAnimatorAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener, CardDbActivity {
    private static final int ADD_CARD = 0;
    private static final int UPDATE_CARD = 1;

    public CustomStackAdapter stackAdapter;

    public Menu menu;
    public Card selectedCard;

    @Override
    public void getItemsResponse(List<Card> cards) {
        stackAdapter.updateData(cards);
    }

    @Override
    public void insertItemResponse(Boolean response) {
        new CardDataSource.getAll<>(this, getApplicationContext()).execute();
    }

    @Override
    public void updateItemResponse(Boolean response) {
        selectedCard = null;

        new CardDataSource.getAll<>(this, getApplicationContext()).execute();
    }

    @Override
    public void deleteItemResponse(Boolean response) {
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
        switch (item.getItemId()) {
            case R.id.action_add: {
                Intent intent = new Intent(this, AddActivityCardProvider.class);
                startActivityForResult(intent, ADD_CARD);

                return true;
            }
            case R.id.card_action_edit: {
                CardProvider provider = new CardProvider(selectedCard);

                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra("cardProvider", provider);

                startActivityForResult(intent, UPDATE_CARD);
                return true;
            }
            case R.id.card_action_delete: {
                new CardDataSource.delete<>(this, getApplicationContext(), selectedCard).execute();

                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.println(Log.DEBUG, "ScanDEBUG", Integer.toString(requestCode));


        switch (requestCode) {
            case ADD_CARD: {
                if (resultCode == RESULT_OK && data != null) {
                    CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                    Card card = new Card(provider);

                    new CardDataSource.insert<>(this, getApplicationContext(), card).execute();

                    break;
                }
            }
            case UPDATE_CARD: {
                if (resultCode == RESULT_OK && data != null) {
                    CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                    if (selectedCard != null && !selectedCard.barcode.equals(provider.barcode)) {
                        selectedCard.barcode = provider.barcode;


                        new CardDataSource.update<>(this, getApplicationContext(), selectedCard).execute();
                    }
                } else {
                    new CardDataSource.getAll<>(this, getApplicationContext()).execute();
                }

                break;
            }
        }
    }

    @Override
    public void onItemExpend(boolean expand) {
        menu.findItem(R.id.action_add).setVisible(!expand);
        menu.findItem(R.id.card_edit).setVisible(expand);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        new CardDataSource.getAll<>(this, getApplicationContext()).execute();
    }
}
