package com.example.loyaltycardwallet.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.Card.CardDao;
import com.example.loyaltycardwallet.data.Card.CardDataSource;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.data.Database.Database;
import com.example.loyaltycardwallet.data.Card.CardDbActivity;
import com.example.loyaltycardwallet.ui.Reports.DistanceReportActivity;
import com.example.loyaltycardwallet.ui.Reports.NumbersReportActivity;
import com.example.loyaltycardwallet.ui.add.AddActivityCardProvider;
import com.example.loyaltycardwallet.ui.add.ScanActivity;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownStackAnimatorAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener, CardDbActivity {
    private static final int ADD_CARD = 0;
    private static final int UPDATE_CARD = 1;

    public CustomStackAdapter stackAdapter;

    public Menu menu;
    public Card selectedCard;

    @Override
    public void getItemsResponse(List<Card> cards) {
        new Handler().postDelayed(() -> {
                    stackAdapter.updateData(cards);

                    // update the cards data(location, etc...)
                    new LocationAndLogoUpdater(this).execute(cards.toArray(new Card[0]));
                },
                200
        );

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
        menu.findItem(R.id.action_add).setVisible(true);
        menu.findItem(R.id.reports_list).setVisible(true);
        menu.findItem(R.id.card_edit).setVisible(false);

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


        Toolbar myToolbar = findViewById(R.id.toolbarMain);
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
            case R.id.reports_list_distance: {
                Intent intent = new Intent(this, DistanceReportActivity.class);

                startActivity(intent);

                return true;
            }
            case R.id.reports_list_number: {
                Intent intent = new Intent(this, NumbersReportActivity.class);

                startActivity(intent);

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

        switch (requestCode) {
            case ADD_CARD: {
                if (resultCode == RESULT_OK && data != null) {
                    CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                    if (provider != null) {
                        Card card = new Card(provider);

                        new CardDataSource.insert<>(this, getApplicationContext(), card).execute();
                    }

                    break;
                }
            }
            case UPDATE_CARD: {
                if (resultCode == RESULT_OK && data != null) {
                    CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                    if (selectedCard != null && provider != null && !selectedCard.barcode.equals(provider.barcode)) {
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
        menu.findItem(R.id.reports_list).setVisible(!expand);
        menu.findItem(R.id.card_edit).setVisible(expand);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        new CardDataSource.getAll<>(this, getApplicationContext()).execute();
    }

    private static class LocationAndLogoUpdater extends AsyncTask<Card, Void, String> {
        private WeakReference<MainActivity> activityWeakReference;
        private String placesUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=KEY-PLACEHOLDER&" +
                "language=ro&inputtype=textquery&fields=formatted_address,geometry,name,place_id,opening_hours&input="; // TODO remove API KEY

        LocationAndLogoUpdater(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);

            SharedPreferences sharedPreferences = activity.getSharedPreferences("loyaltyCarda-keys", Context.MODE_PRIVATE);

            String key = sharedPreferences.getString("places", null);

            placesUrl = placesUrl.replace("KEY-PLACEHOLDER", key);
        }

        @Override
        protected String doInBackground(Card... cards) {
            StringBuilder builder = new StringBuilder();


            Activity activity = activityWeakReference.get();
            CardDao dao = Database.getInstance(activity.getApplicationContext())
                    .getCardDao();

            for (int i = 0; i < cards.length; i++) {
                Card card = cards[i];

                if (card.logo == null) {
                    try {
                        // get the logo
                        URLConnection logoUrlConnection = new URL(card.logoUrlString).openConnection();

                        card.logo = BitmapFactory.decodeStream(logoUrlConnection.getInputStream());

                        dao.update(card);

                        // Escape early if cancel() is called
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "AddProviderError", "Failed to get logo for " + card.name);
                        e.printStackTrace();
                    }
                }

                if (card.address == null) {
                    try {
                        // get the closesest location data
                        URLConnection locationUrlConnection = new URL(placesUrl + card.name).openConnection();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(locationUrlConnection.getInputStream()));

                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            builder.append(line);
                        }

                        String jsonString = builder.toString();


                        builder.setLength(0);

                        JSONObject object = new JSONObject(jsonString);

                        JSONObject result = object.getJSONArray("candidates").getJSONObject(0);


                        Log.println(Log.DEBUG, "Places result", result.toString());


                        card.formated_name = result.getString("name");
                        card.address = result.getString("formatted_address");
                        card.isOpen = result.getJSONObject("opening_hours").getBoolean("open_now");

                        JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                        card.lat = location.getDouble("lat");
                        card.lng = location.getDouble("lng");

                        dao.update(card);
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "AddProviderError", "Failed to get data for " + card.name);
                        e.printStackTrace();
                    }
                }


                if (isCancelled()) break;
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            MainActivity activity = activityWeakReference.get();


            if (activity == null || activity.isFinishing()) return;

            activity.stackAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void getClosestItemsResponse(List<Card> list) {

    }
}
