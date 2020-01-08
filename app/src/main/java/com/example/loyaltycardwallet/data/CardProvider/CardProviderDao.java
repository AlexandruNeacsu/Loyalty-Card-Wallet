package com.example.loyaltycardwallet.data.CardProvider;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardProviderDao {
    @Query("SELECT * FROM providers")
    List<CardProvider> getAll();

    @Insert
    void insert(CardProvider CardProvider);

    @Insert
    void insert(List<CardProvider> providers);

    @Delete
    void delete(CardProvider CardProvider);

    @Update
    void update(CardProvider provider);
}
