package com.example.loyaltycardwallet.data.CardProvider;

import android.graphics.Bitmap;
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

    // TODO make all fields private!

    // logo related
    private static final String logoProviderURL = "https://logo.clearbit.com/";
    public final String name;
    public final String logoUrlString;
    public String barcode;
    public Bitmap barcodeBitmap;
    public int colorIndex = -1;
    private Bitmap logo;

    // location details
    private String formated_name;
    private Boolean isOpen;
    private String address;

    // other
    private String placeId;



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

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        if (logo != null) {
            this.logo = logo;
        }
    }

    public String getFormated_name() {
        return formated_name;
    }

    public void setFormated_name(String formated_name) {
        this.formated_name = formated_name;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
