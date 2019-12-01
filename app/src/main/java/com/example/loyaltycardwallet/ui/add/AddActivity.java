package com.example.loyaltycardwallet.ui.add;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.data.CardProvider.CardProviderDataSource;
import com.example.loyaltycardwallet.ui.CardProviderList.CardProviderAdapter;
import com.example.loyaltycardwallet.ui.CardProviderList.CardProviderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class AddActivity extends AppCompatActivity implements CardProviderFragment.OnListFragmentInteractionListener {
    private static int BARCODE_REQUEST = 0;

    private CardProviderAdapter fragmentAdapter;

    private CardProvider cardProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar myToolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(myToolbar);


        CardProviderFragment fragment = (CardProviderFragment) getSupportFragmentManager().findFragmentById(R.id.providerList);

        if (fragment != null) {
            fragmentAdapter = fragment.mAdapter;

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fragment)
                    .commit();

            new LocationAndLogoProvider(this).execute(
                    CardProviderDataSource.ITEMS.toArray(new CardProvider[0])
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (fragmentAdapter != null) {
                    CardProviderDataSource.ITEMS.clear();

                    if (newText.isEmpty()) {
                        Log.println(Log.DEBUG, "DEBUG", "here");

                        CardProviderDataSource.ITEMS.addAll(CardProviderDataSource.ORIGINAL_ITEMS);
                    } else {

                        for (CardProvider provider : CardProviderDataSource.ORIGINAL_ITEMS) {
                            if (provider.name.toLowerCase().startsWith(newText.toLowerCase())) {
                                CardProviderDataSource.ITEMS.add(provider);
                            }
                        }
                    }

                    fragmentAdapter.notifyDataSetChanged();

                    return true;
                }
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        CardProviderDataSource.ITEMS.clear();

        CardProviderDataSource.ITEMS.addAll(CardProviderDataSource.ORIGINAL_ITEMS);
    }

    @Override
    public void onListFragmentInteraction(CardProvider item) {
        cardProvider = item;

        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("cardProvider", item);

        startActivityForResult(intent, BARCODE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BARCODE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                Intent returnIntent = new Intent();

                CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                returnIntent.putExtra("cardProviderInitialized", provider); // TODO

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    private static class LocationAndLogoProvider extends AsyncTask<CardProvider, Integer, String> {
        private WeakReference<AddActivity> activityWeakReference;
        private String placesUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=AIzaSyCgMhZFEdwuzEJ030exD9vf9HPl5A0WqdE&" +
                "language=ro&inputtype=textquery&fields=formatted_address,name,place_id,opening_hours&input="; // TODO remove API KEY

        LocationAndLogoProvider(AddActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(CardProvider... providers) {
            StringBuilder builder = new StringBuilder();

            int progresPerProvider = 100 / providers.length;

            for (int i = 0; i < providers.length; i++) {
                CardProvider provider = providers[i];

                if (provider.getLogo() == null) {
                    try {
                        // get the logo
                        URLConnection logoUrlConnection = new URL(provider.logoUrlString).openConnection();

                        provider.setLogo(BitmapFactory.decodeStream(logoUrlConnection.getInputStream()));

                        // Escape early if cancel() is called
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "AddProviderError", "Failed to get logo for " + provider.name);
                        e.printStackTrace();
                    }
                }

                if (provider.getAddress() == null) {
                    try {
                        // get the closesest location data
                        URLConnection locationUrlConnection = new URL(placesUrl + provider.name).openConnection();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(locationUrlConnection.getInputStream()));

                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            builder.append(line);
                        }

                        String jsonString = builder.toString();


                        builder.setLength(0);

                        JSONObject object = new JSONObject(jsonString);

                        JSONObject result = object.getJSONArray("candidates").getJSONObject(0);


                        Log.println(Log.DEBUG, "Places result", result.toString());


                        provider.setFormated_name(result.getString("name"));
                        provider.setAddress(result.getString("formatted_address"));
                        provider.setOpen(result.getJSONObject("opening_hours").getBoolean("open_now"));
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "AddProviderError", "Failed to get data for " + provider.name);
                        e.printStackTrace();
                    }
                }

                publishProgress((i + 1) * progresPerProvider);

                if (isCancelled()) break;
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            AddActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            ProgressBar progressBar = activity.findViewById(R.id.progressBar_add);
            progressBar.setProgress(values[values.length - 1], true);
        }

        @Override
        protected void onPostExecute(String s) {
            AddActivity activity = activityWeakReference.get();


            if (activity == null || activity.isFinishing()) return;
            FragmentManager fm = activity.getSupportFragmentManager();

            CardProviderFragment fragment = (CardProviderFragment) fm.findFragmentById(R.id.providerList);


            if (fragment != null) {
                fm.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(fragment)
                        .commit();
            }

            ProgressBar progressBar = activity.findViewById(R.id.progressBar_add);
            progressBar.setVisibility(View.GONE);
        }
    }
}
