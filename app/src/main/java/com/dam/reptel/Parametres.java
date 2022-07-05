package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Parametres extends AppCompatActivity {
    private static final String TAG = "Parametres";

    private Button btnAnnonce, btnMessages, btnSimuler, btnSignout;
    String numTel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        btnAnnonce = findViewById(R.id.btnAnnonce);
        btnMessages = findViewById(R.id.btnMessages);
        btnSimuler = findViewById(R.id.btn_simuler);
        btnSignout = findViewById(R.id.btn_signout);

        Intent intent = getIntent();
        numTel = intent.getStringExtra("numTel");
        Log.i(TAG, "onCreate: numTel = " + numTel);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


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
                intent.putExtra("ColKeyPhoneNumber", numTel);
                //intent.putExtra("ColKeyPhoneNumber", "0777392997");
                Log.i(TAG, "onClick: numTel = " + numTel);
                startActivity(intent);
            }
        });

        btnSimuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parametres.this, EnregistrementMessages.class);
                intent.putExtra("Titre", btnSimuler.getText().toString());
                intent.putExtra("numTel", numTel);
                startActivity(intent);
            }
        });

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(Parametres.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }
}