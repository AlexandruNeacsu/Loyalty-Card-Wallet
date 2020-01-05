package com.example.loyaltycardwallet.data.Card;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.loyaltycardwallet.data.Database.Database;
import com.example.loyaltycardwallet.ui.DbInterfaces.CardDbActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class CardDataSource {

    public static class getAll<T extends CardDbActivity> extends AsyncTask<Void, Void, List<Card>> {
        private WeakReference<T> activityWeakReference;
        private WeakReference<Context> contextWeakReference;


        public getAll(T activity, Context context) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected List<Card> doInBackground(Void... voids) {
            Context context = contextWeakReference.get();

            return Database.getInstance(context).getCardDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Card> cards) {
            T activity = activityWeakReference.get();

            activity.getItemsResponse(cards);
        }
    }

    public static class insert<T extends CardDbActivity> extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<T> activityWeakReference;
        private WeakReference<Context> contextWeakReference;
        private Card card;


        public insert(T activity, Context context, Card card) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.contextWeakReference = new WeakReference<>(context);

            this.card = card;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Context context = contextWeakReference.get();

            if (this.card != null) {
                Database.getInstance(context)
                        .getCardDao()
                        .insert(card);

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            T activity = activityWeakReference.get();

            activity.insertItemResponse(res);
        }
    }

    public static class update<T extends CardDbActivity> extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<T> activityWeakReference;
        private WeakReference<Context> contextWeakReference;
        private Card card;


        public update(T activity, Context context, Card card) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.contextWeakReference = new WeakReference<>(context);

            this.card = card;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Context context = contextWeakReference.get();

            if (this.card != null) {
                Database.getInstance(context)
                        .getCardDao()
                        .update(card);

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            T activity = activityWeakReference.get();

            activity.updateItemResponse(res);
        }
    }


    public static class delete<T extends CardDbActivity> extends AsyncTask<Card, Void, Boolean> {
        private WeakReference<T> activityWeakReference;
        private WeakReference<Context> contextWeakReference;
        private Card card;


        public delete(T activity, Context context, Card card) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.contextWeakReference = new WeakReference<>(context);

            this.card = card;
        }

        @Override
        protected Boolean doInBackground(Card... cardsx) {
            Context context = contextWeakReference.get();

            if (this.card != null) {
                Database.getInstance(context)
                        .getCardDao()
                        .delete(card);

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            T activity = activityWeakReference.get();

            activity.deleteItemResponse(res);
        }
    }
}
