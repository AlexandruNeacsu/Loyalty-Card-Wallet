package com.example.loyaltycardwallet.data.CardProvidersWithCards;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.loyaltycardwallet.data.Database.Database;
import com.example.loyaltycardwallet.ui.DbInterfaces.CardProvidersWithCardInterface;

import java.lang.ref.WeakReference;
import java.util.List;

public class CardProviderWithCardsDataStore {

    public static class getByName<T extends Activity & CardProvidersWithCardInterface> extends AsyncTask<Void, Void, List<CardProvidersWithCards>> {
        private WeakReference<T> activityWeakReference;
        private String name;


        public getByName(T activity, String name) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.name = name;
        }

        @Override
        protected List<CardProvidersWithCards> doInBackground(Void... voids) {
            T activity = activityWeakReference.get();

            return Database.getInstance(activity.getApplicationContext()).getCardProvidersWithCardsDao().getProvidersWithCards(name);
        }

        @Override
        protected void onPostExecute(List<CardProvidersWithCards> list) {
            T activity = activityWeakReference.get();

            activity.getItemsByNameResponse(list);
        }
    }

}
