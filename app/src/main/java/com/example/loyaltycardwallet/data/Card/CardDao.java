package com.example.loyaltycardwallet.data.Card;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDao {
    @Query("SELECT * FROM cards")
    List<Card> getAll();

    @Insert
    void insert(Card card);

    @Insert
    void insert(List<Card> cards);

    @Delete
    void delete(Card card);

    @Update
    void update(Card card);
}
