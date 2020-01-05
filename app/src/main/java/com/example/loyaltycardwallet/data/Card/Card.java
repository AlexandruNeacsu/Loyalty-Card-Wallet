package com.example.loyaltycardwallet.data.Card;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.loyaltycardwallet.data.CardProvider.CardProvider;


@Entity(tableName = "Cards")
public class Card {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String barcode;
    public Bitmap barcodeBitmap;
    public int colorIndex = -1;
    public Bitmap logo;

    // location details
    public String formated_name;
    public Boolean isOpen;
    public String address;

    public Card() {}

    public Card(CardProvider provider) {
        this.name = provider.name;
        this.barcode = provider.barcode;
        this.barcodeBitmap = provider.barcodeBitmap;
        this.colorIndex = provider.colorIndex;
        this.logo = provider.logo;
        this.formated_name = provider.formated_name;
        this.isOpen = provider.isOpen;
        this.address = provider.address;
    }
}
