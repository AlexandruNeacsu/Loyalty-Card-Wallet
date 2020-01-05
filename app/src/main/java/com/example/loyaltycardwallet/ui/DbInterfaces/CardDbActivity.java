package com.example.loyaltycardwallet.ui.DbInterfaces;

import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.data.CardProvidersWithCards.CardProvidersWithCards;

import java.util.List;

public interface CardDbActivity {
    void getItemsResponse(List<Card> cards);

    void insertItemResponse(Boolean response);

    void updateItemResponse(Boolean response);

    void deleteItemResponse(Boolean response);

    void getClosestItemsResponse(List<Card> list);

}
