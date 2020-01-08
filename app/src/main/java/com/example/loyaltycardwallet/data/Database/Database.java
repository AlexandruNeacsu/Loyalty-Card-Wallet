package com.example.loyaltycardwallet.data.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.Card.CardDao;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.data.CardProvider.CardProviderDao;

@androidx.room.Database(entities = {CardProvider.class, Card.class}, version = 8)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {
    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, Database.class, "loyaltyCard.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract CardProviderDao getCardProviderDao();

    public abstract CardDao getCardDao();
}
