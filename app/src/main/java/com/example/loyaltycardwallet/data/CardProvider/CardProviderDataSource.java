package com.example.loyaltycardwallet.data.CardProvider;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO: Replace all uses of this class with DB
 */
public class CardProviderDataSource {
    private static final List<CardProvider> originalItems = new ArrayList<>();
    private static final List<CardProvider> items = Collections.synchronizedList(new ArrayList<>());

    private static Set<String> DEFAULT_STORES = new HashSet<>(Arrays.asList(
            "Kaufland",
            "Lidl",
            "Ikea",
            "Profi"
    ));
    private WeakReference<Context> contextWeakReference;


    public CardProviderDataSource(Context context) {
        this.contextWeakReference = new WeakReference<>(context);

        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("cardProviders", Context.MODE_PRIVATE);

        Set<String> set = sharedPreferences.getStringSet("stores", DEFAULT_STORES);

        if (originalItems.size() == 0) {
            set.forEach(s -> {
                CardProvider provider = new CardProvider(null, s);

                items.add(provider);
                originalItems.add(provider);
            });
        }
    }

    public synchronized void addItem(String name) {
        Context context = contextWeakReference.get();

        SharedPreferences sharedPreferences = context.getSharedPreferences("stores", Context.MODE_PRIVATE);

        Set<String> set = sharedPreferences.getStringSet("stores", DEFAULT_STORES);
        set.add(name);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("stores", set);
        editor.apply();

        String[] words = name.split(" ");
        String formatedName = String.join("", words);


        CardProvider provider = new CardProvider(null, formatedName);

        items.add(provider);
        originalItems.add(provider);
    }

    public List<CardProvider> getItems() {
        return items;
    }

    public List<CardProvider> getOriginalItems() {
        return originalItems;
    }

    public void resetItems() {
        items.clear();
        items.addAll(originalItems);
    }
}
