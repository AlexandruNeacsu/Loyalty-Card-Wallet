package com.example.loyaltycardwallet.ui.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loyaltycardwallet.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Spinner spinner = findViewById(R.id.register_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.language_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        final Button button = findViewById(R.id.registe_button);

        button.setOnClickListener(v -> {
            final EditText emailEditText = findViewById(R.id.register_email_edit_text);
            final EditText passEditText = findViewById(R.id.register_password_edit_text);

            String email = emailEditText.getText().toString();
            String password = passEditText.getText().toString();

            if (email.isEmpty()) {
                emailEditText.setError(getString(R.string.error_empty_email));
            } else if (password.isEmpty()) {
                passEditText.setError(getString(R.string.error_empty_password));
            } else {
                Intent returnIntent = new Intent();

                returnIntent.putExtra("email", email);
                returnIntent.putExtra("password", password);
                returnIntent.putExtra("language", spinner.getSelectedItem().toString());

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
