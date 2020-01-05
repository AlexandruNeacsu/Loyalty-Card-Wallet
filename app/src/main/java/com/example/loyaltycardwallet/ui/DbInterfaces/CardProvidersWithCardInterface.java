package com.example.loyaltycardwallet.ui.DbInterfaces;

import com.example.loyaltycardwallet.data.CardProvidersWithCards.CardProvidersWithCards;

import java.util.List;

public interface CardProvidersWithCardInterface {
    void getItemsByNameResponse(List<CardProvidersWithCards> list);

}
