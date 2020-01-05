package com.example.loyaltycardwallet.data.CardProvider;

import android.content.Context;
import android.os.AsyncTask;

import com.example.loyaltycardwallet.data.Database.Database;
import com.example.loyaltycardwallet.ui.DbInterfaces.CardProviderDbActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CardProviderDataSource {
    private static final List<CardProvider> originalItems = new ArrayList<>();
    private static final List<CardProvider> items = Collections.synchronizedList(new ArrayList<>());

    private static List<String> DEFAULT_ITEMS = Arrays.asList(
            "Kaufland",
            "Lidl",
            "Ikea",
            "Profi"
    );


    public static List<CardProvider> getItems() {
        return items;
    }

    public static List<CardProvider> getOriginalItems() {
        return originalItems;
    }

    public static void resetItems() {
        items.clear();
        items.addAll(originalItems);
    }

    public static class getAll<T extends CardProviderDbActivity> extends AsyncTask<Void, Void, List<CardProvider>> {
        private WeakReference<T> activityWeakReference;
        private WeakReference<Context> contextWeakReference;


        public getAll(T activity, Context context) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected List<CardProvider> doInBackground(Void... voids) {
            Context context = contextWeakReference.get();

            CardProviderDao dao = Database.getInstance(context)
                    .getCardProviderDao();

            List<CardProvider> providers =  dao.getAll();

            if (providers.size() == 0) {
                DEFAULT_ITEMS.forEach(s -> providers.add(new CardProvider(null, s)));

                dao.insert(providers);
            }

            return providers;
        }

        @Override
        protected void onPostExecute(List<CardProvider> providers) {
            T activity = activityWeakReference.get();

            items.clear();
            originalItems.clear();

            items.addAll(providers);
            originalItems.addAll(providers);

            activity.getItemsResponse(providers);
        }
    }

    public static class insert<T extends CardProviderDbActivity> extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<T> activityWeakReference;
        private WeakReference<Context> contextWeakReference;
        private CardProvider provider;


        public insert(T activity, Context context, CardProvider provider) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.contextWeakReference = new WeakReference<>(context);

            this.provider = provider;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Context context = contextWeakReference.get();

            if (this.provider != null) {
                Database.getInstance(context)
                        .getCardProviderDao()
                        .insert(provider);

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            T activity = activityWeakReference.get();

            if (res) {
                items.add(this.provider);
                originalItems.add(provider);
            }

            activity.insertItemResponse(res);
        }
    }
}
