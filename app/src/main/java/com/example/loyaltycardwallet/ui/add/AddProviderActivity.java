package com.example.loyaltycardwallet.ui.add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.loyaltycardwallet.R;

public class AddProviderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_provider);

        Button button = findViewById(R.id.add_provider_button);
        button.setOnClickListener(v -> {
            EditText text = findViewById(R.id.add_provider_store_name);
            String storeName = text.getText().toString();

            Intent returnIntent = new Intent();
            returnIntent.putExtra("storeName", storeName);

            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }
}
