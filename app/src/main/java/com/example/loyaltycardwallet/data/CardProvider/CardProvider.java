package com.example.loyaltycardwallet.data.CardProvider;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "providers")
public class CardProvider implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CardProvider createFromParcel(Parcel in) {
            return new CardProvider(in);
        }

        public CardProvider[] newArray(int size) {
            return new CardProvider[size];
        }
    };

    @PrimaryKey(autoGenerate = true)
    public int id;

    // logo related
    private static final String logoProviderURL = "https://logo.clearbit.com/";
    public String name;
    public String logoUrlString;
    public Bitmap logo;

    @Ignore
    public String barcode;

    @Ignore
    public Bitmap barcodeBitmap;

    @Ignore
    public int colorIndex = -1;

    // location details
    @Ignore
    public String formated_name;

    @Ignore
    public Boolean isOpen;

    @Ignore
    public String address;

    @Ignore
    public String placeId;



    private CardProvider(Parcel in) {
        name = in.readString();
        logoUrlString = in.readString();
        barcode = in.readString();
        barcodeBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        colorIndex = in.readInt();

        logo = in.readParcelable(Bitmap.class.getClassLoader());

        formated_name = in.readString();
        isOpen = in.readInt() != 0;
        address = in.readString();
    }

    public CardProvider(Bitmap logo, String name) {
        this.logo = logo;
        this.name = name;
        this.logoUrlString = logoProviderURL.concat(name).concat(".ro");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(logoUrlString);
        dest.writeString(barcode);
        dest.writeParcelable(barcodeBitmap, flags);
        dest.writeInt(colorIndex);

        dest.writeParcelable(logo, flags);

        dest.writeString(formated_name);
        dest.writeInt(isOpen != null && isOpen ? 1 : 0);
        dest.writeString(address);

    }

    @Override
    public String toString() {
        return name;
    }
}
