package com.example.loyaltycardwallet.data.CardProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.loyaltycardwallet.data.CardProvider.CardProvider;

import java.util.List;

public interface CardProviderDbActivity {

    void getItemsResponse(List<CardProvider> providers);

    void insertItemResponse(Boolean response);
}
