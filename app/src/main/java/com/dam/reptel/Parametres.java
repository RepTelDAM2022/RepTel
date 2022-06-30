package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Parametres extends AppCompatActivity {

    private Button btnAnnonce, btnMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        btnAnnonce = findViewById(R.id.btnAnnonce);
        btnMessages = findViewById(R.id.btnMessages);

        btnAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parametres.this, Annonce.class);
                intent.putExtra("Titre", btnAnnonce.getText().toString());
                startActivity(intent);
            }
        });

        btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parametres.this, ContactsRecyclerView.class);
                intent.putExtra("Titre", btnMessages.getText().toString());
                startActivity(intent);
            }
        });
    }
}