package com.example.loyaltycardwallet.data.CardProvidersWithCards;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;

import java.util.List;

public class CardProvidersWithCards {
    @Embedded
    public CardProvider provider;

    @Relation(
            parentColumn = "id",
            entityColumn = "cardProviderId"
    )
    public List<Card> cards;
}
