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

            new LogoProvider(this).execute(
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

    private static class LogoProvider extends AsyncTask<CardProvider, Integer, String> {
        private WeakReference<AddActivity> activityWeakReference;

        LogoProvider(AddActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(CardProvider... providers) {

            for (int i = 0; i < providers.length; i++) {
                CardProvider provider = providers[i];

                if (provider.getLogo() == null) {
                    try {

                        URLConnection urlConnection = new URL(provider.urlString).openConnection();

                        provider.setLogo(BitmapFactory.decodeStream(urlConnection.getInputStream()));

                        publishProgress(i);

                        // Escape early if cancel() is called
                        if (isCancelled()) break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            AddActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            ProgressBar progressBar = activity.findViewById(R.id.progressBar_add);
            progressBar.setProgress(values[values.length - 1]);
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
