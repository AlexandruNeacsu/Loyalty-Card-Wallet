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

    @Query("SELECT * from providers WHERE name = :name")
    List<CardProvider> getByName(String name);

    @Query("SELECT * from providers WHERE id = :id")
    CardProvider getById(int id);

    @Insert
    void insert(CardProvider CardProvider);

    @Insert
    void insert(CardProvider... providers);

    @Insert
    void insert(List<CardProvider> providers);

    @Delete
    void delete(CardProvider CardProvider);

    @Query("DELETE FROM providers")
    void deleteAll();

    @Update
    void update(CardProvider provider);
}
