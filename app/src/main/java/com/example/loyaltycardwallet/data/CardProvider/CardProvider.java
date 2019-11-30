package com.example.loyaltycardwallet.data.CardProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class CardProvider implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CardProvider createFromParcel(Parcel in) {
            return new CardProvider(in);
        }

        public CardProvider[] newArray(int size) {
            return new CardProvider[size];
        }
    };
    private static final String logoProviderURL = "https://logo.clearbit.com/";
    public final String name;
    public final String urlString;
    public String barcode;
    public int colorIndex = -1;
    private Bitmap logo;

    private CardProvider(Parcel in) {
        name = in.readString();
        urlString = in.readString();
        logo = in.readParcelable(Bitmap.class.getClassLoader());
        barcode = in.readString();
    }

    public CardProvider(Bitmap logo, String name) {
        this.logo = logo;
        this.name = name;
        this.urlString = logoProviderURL.concat(name).concat(".ro");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(urlString);
        dest.writeParcelable(logo, flags);
        dest.writeString(barcode);
    }

    @Override
    public String toString() {
        return name;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        if (logo != null) {
            this.logo = logo;
        }
    }
}
