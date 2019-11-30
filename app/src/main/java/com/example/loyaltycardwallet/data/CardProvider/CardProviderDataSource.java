package com.example.loyaltycardwallet.data.CardProvider;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Replace all uses of this class
 */
public class CardProviderDataSource {

    public static final List<CardProvider> ITEMS;
    public static final List<CardProvider> ORIGINAL_ITEMS = new ArrayList<CardProvider>() {
        {
            add(new CardProvider(null, "Kaufland"));
            add(new CardProvider(null, "Lidl"));
            add(new CardProvider(null, "Ikea"));
            add(new CardProvider(null, "Profi"));

        }
    };

    private static final String logoProviderURL = "https://logo.clearbit.com/";


    static {
        ITEMS = Collections.synchronizedList(new ArrayList<>(ORIGINAL_ITEMS));
    }


    public static class CardProvider {
        public final String name;
        public final String urlString;
        public Bitmap logo;


        public CardProvider(Bitmap logo, String name) {
            this.logo = logo;
            this.name = name;
            this.urlString = logoProviderURL.concat(name).concat(".ro");
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
