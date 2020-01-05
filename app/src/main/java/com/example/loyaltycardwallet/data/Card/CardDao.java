package com.example.loyaltycardwallet.data.Card;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.loyaltycardwallet.data.CardProvidersWithCards.CardProvidersWithCards;

import java.util.List;

@Dao
public interface CardDao {
    @Query("SELECT * FROM cards")
    List<Card> getAll();

    @Insert
    void insert(Card card);

    @Delete
    void delete(Card card);

    @Update
    void update(Card card);

    @Query("SELECT id, lat, lng, cardProviderId, colorIndex  FROM cards")
    List<Card> getDistances();

    @Query("SELECT * FROM cards WHERE id IN (:ids) LIMIT 5")
    List<Card> find5(int[] ids);

}
