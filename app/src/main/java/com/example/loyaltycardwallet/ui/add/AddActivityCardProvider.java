package com.example.loyaltycardwallet.ui.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.data.CardProvider.CardProviderDao;
import com.example.loyaltycardwallet.data.CardProvider.CardProviderDataSource;
import com.example.loyaltycardwallet.data.Database.Database;
import com.example.loyaltycardwallet.ui.CardProviderList.CardProviderAdapter;
import com.example.loyaltycardwallet.ui.CardProviderList.CardProviderFragment;
import com.example.loyaltycardwallet.ui.DbInterfaces.CardProviderDbActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class AddActivityCardProvider extends AppCompatActivity implements CardProviderFragment.OnListFragmentInteractionListener, CardProviderDbActivity {
    private static int BARCODE_REQUEST = 0;
    private static int NEW_PROVIDER_REQUEST = 1;


    private CardProviderAdapter fragmentAdapter;

    @Override
    public void getItemsResponse(List<CardProvider> providers) {
        // called when CardProviderDataSource.getAll is executed
        new LocationAndLogoProvider(this).execute(
                providers.toArray(new CardProvider[0])
        );
    }

    @Override
    public void insertItemResponse(Boolean response) {
        new CardProviderDataSource.getAll<>(this, getApplicationContext()).execute();
    }

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

            new CardProviderDataSource.getAll<>(this, getApplicationContext()).execute();
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
                    CardProviderDataSource.getItems().clear();

                    if (newText.isEmpty()) {
                        CardProviderDataSource.getItems().addAll(CardProviderDataSource.getOriginalItems());
                    } else {
                        for (CardProvider provider : CardProviderDataSource.getOriginalItems()) {
                            if (provider.name.toLowerCase().startsWith(newText.toLowerCase())) {
                                CardProviderDataSource.getItems().add(provider);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_add_provider) {
            Intent intent = new Intent(this, AddProviderActivity.class);

            startActivityForResult(intent, NEW_PROVIDER_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        CardProviderDataSource.resetItems();
    }

    @Override
    public void onListFragmentInteraction(CardProvider item) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("cardProvider", item);

        startActivityForResult(intent, BARCODE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == BARCODE_REQUEST) {
                Intent returnIntent = new Intent();

                CardProvider provider = data.getExtras().getParcelable("cardProviderInitialized");

                returnIntent.putExtra("cardProviderInitialized", provider);

                setResult(RESULT_OK, returnIntent);
                finish();
            } else if (requestCode == NEW_PROVIDER_REQUEST) {
                String provider = data.getStringExtra("storeName");

                CardProviderFragment fragment = (CardProviderFragment) getSupportFragmentManager().findFragmentById(R.id.providerList);

                // TODO move to method
                if (fragment != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .hide(fragment)
                            .commit();
                }

                new CardProviderDataSource.insert<>(this, getApplicationContext(), new CardProvider(null, provider)).execute();

            }
        }
    }

    private static class LocationAndLogoProvider extends AsyncTask<CardProvider, Integer, String> {
        private WeakReference<AddActivityCardProvider> activityWeakReference;
        private String placesUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=KEY-PLACEHOLDER&" +
                "language=ro&inputtype=textquery&fields=formatted_address,name,place_id,opening_hours&input="; // TODO remove API KEY

        LocationAndLogoProvider(AddActivityCardProvider activity) {
            this.activityWeakReference = new WeakReference<>(activity);

            SharedPreferences sharedPreferences = activity.getSharedPreferences("loyaltyCarda-keys", Context.MODE_PRIVATE);

            String key = sharedPreferences.getString("places", null);

            placesUrl = placesUrl.replace("KEY-PLACEHOLDER", key);

        }

        @Override
        protected String doInBackground(CardProvider... providers) {
            StringBuilder builder = new StringBuilder();

            int progresPerProvider = 100 / (providers.length > 0 ? providers.length : 100);

            Activity activity = activityWeakReference.get();
            CardProviderDao dao = Database.getInstance(activity.getApplicationContext())
                    .getCardProviderDao();

            for (int i = 0; i < providers.length; i++) {
                CardProvider provider = providers[i];

                if (provider.logo == null) {
                    try {
                        // get the logo
                        URLConnection logoUrlConnection = new URL(provider.logoUrlString).openConnection();

                        provider.logo = BitmapFactory.decodeStream(logoUrlConnection.getInputStream());

                        dao.update(provider);

                        // Escape early if cancel() is called
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "AddProviderError", "Failed to get logo for " + provider.name);
                        e.printStackTrace();
                    }
                }

                if (provider.address == null) {
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


                        provider.formated_name = result.getString("name");
                        provider.address = result.getString("formatted_address");
                        provider.isOpen = result.getJSONObject("opening_hours").getBoolean("open_now");

                        dao.update(provider);
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
            AddActivityCardProvider activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            ProgressBar progressBar = activity.findViewById(R.id.progressBar_add);
            progressBar.setProgress(values[values.length - 1], true);
        }

        @Override
        protected void onPostExecute(String s) {
            AddActivityCardProvider activity = activityWeakReference.get();


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
