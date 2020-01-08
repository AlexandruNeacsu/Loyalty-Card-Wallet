package com.example.loyaltycardwallet.data.Card;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.loyaltycardwallet.data.CardProvider.CardProvider;

import static androidx.room.ForeignKey.CASCADE;


@Entity(
        tableName = "Cards",
        foreignKeys = @ForeignKey(
                entity = CardProvider.class,
                parentColumns = "id",
                childColumns = "cardProviderId",
                onDelete = CASCADE
        )
)
public class Card {
    private static final String logoProviderURL = "https://logo.clearbit.com/";


    @PrimaryKey(autoGenerate = true)
    public int id;

    public int cardProviderId;

    public String name;
    public String barcode;
    public Bitmap barcodeBitmap;
    public int colorIndex = -1;
    public Bitmap logo;
    public String logoUrlString;


    // location details
    public String formated_name;
    public Boolean isOpen;
    public String address;

    public String placeId;

    public double lat;
    public double lng;

    public Card() {
    }

    public Card(CardProvider provider) {
        this.name = provider.name;
        this.barcode = provider.barcode;
        this.barcodeBitmap = provider.barcodeBitmap;
        this.colorIndex = provider.colorIndex;
        this.logo = provider.logo;
        this.formated_name = provider.formated_name;
        this.isOpen = provider.isOpen;
        this.address = provider.address;
        this.placeId = provider.placeId;
        this.cardProviderId = provider.id;
        this.logoUrlString = logoProviderURL.concat(name).concat(".ro");
    }
}
