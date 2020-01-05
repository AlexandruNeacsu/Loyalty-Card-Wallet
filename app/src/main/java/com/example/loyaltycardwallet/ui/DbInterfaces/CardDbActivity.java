package com.example.loyaltycardwallet.ui.DbInterfaces;

import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;

import java.util.List;

public interface CardDbActivity {
    void getItemsResponse(List<Card> providers);

    void insertItemResponse(Boolean response);
}
