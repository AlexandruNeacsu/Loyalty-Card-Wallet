package com.example.loyaltycardwallet.data.CardProvidersWithCards;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CardProvidersWithCardsDao {
    @Transaction
    @Query("SELECT * FROM providers")
    public List<CardProvidersWithCards> getProvidersWithCards();

    @Transaction
    @Query("SELECT * FROM providers WHERE name = :name")
    public List<CardProvidersWithCards> getProvidersWithCards(String name);

}
