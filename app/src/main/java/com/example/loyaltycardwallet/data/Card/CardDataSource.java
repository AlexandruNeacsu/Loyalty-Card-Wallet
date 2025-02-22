package com.example.loyaltycardwallet.data.Card;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.loyaltycardwallet.data.Database.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CardDataSource {

    private static double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; // to km
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

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

                DatabaseReference store = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("magazine")
                        .child(card.name);


                store.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer count = mutableData.getValue(Integer.class);
                        if (count == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(count + 1);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });

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

                DatabaseReference store = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("magazine")
                        .child(card.name);

                store.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer count = mutableData.getValue(Integer.class);
                        if (count == null || count == 0) {
                            return Transaction.success(mutableData);
                        } else {
                            mutableData.setValue(count - 1);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });

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

    public static class getClosest<T extends Activity & CardDbActivity> extends AsyncTask<Void, Void, List<Card>> {
        private WeakReference<T> activityWeakReference;
        private double lat;
        private double lng;


        public getClosest(T activity, double lat, double lng) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected List<Card> doInBackground(Void... voids) {
            T activity = activityWeakReference.get();

            CardDao dao = Database.getInstance(activity.getApplicationContext()).getCardDao();

            List<Card> list = dao.getDistances();
            list.sort((card1, card2) -> {

                double card1Distance = distance(lat, lng, card1.lat, card1.lng);
                double card2Distance = distance(lat, lng, card2.lat, card2.lng);

                return (int) (card1Distance - card2Distance);
            });

            List<Card> filteredList = new ArrayList<>();

            // remove duplicate stores
            for (Card card : list) {
                if (filteredList.stream().noneMatch(card1 -> card1.name.equals(card.name))) {
                    filteredList.add(card);
                }
            }

            int[] ids = filteredList.stream().limit(10).mapToInt(card -> card.id).toArray();

            return dao.find5(ids);
        }

        @Override
        protected void onPostExecute(List<Card> list) {
            T activity = activityWeakReference.get();

            activity.getClosestItemsResponse(list);
        }
    }
}
