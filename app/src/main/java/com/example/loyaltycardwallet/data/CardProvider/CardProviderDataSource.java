package com.example.loyaltycardwallet.data.CardProvider;

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


    static {
        ITEMS = Collections.synchronizedList(new ArrayList<>(ORIGINAL_ITEMS));
    }

}
