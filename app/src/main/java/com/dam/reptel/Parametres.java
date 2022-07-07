package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * ecran parametres pour gerer les directions de l'application:
 *
 */
public class Parametres extends AppCompatActivity {
    private static final String TAG = "Parametres";

    /**
     * variables globales
     */
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

        /**
         * direction Annonce pour entregistrer l'annonce
         */

        btnAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parametres.this, Annonce.class);
                intent.putExtra("Titre", btnAnnonce.getText().toString());
                startActivity(intent);
            }
        });

        /**
         * direction Messages recus pour afficher le premier RV des contacts ayant laissé un message.
         */

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

        /**
         * direction Simulation des messages dans la mesure ou Google nous interdit d'enregistrer des appels et donc
         * d'avoir de vrais messages.
         */

        btnSimuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parametres.this, EnregistrementMessages.class);
                intent.putExtra("Titre", btnSimuler.getText().toString());
                intent.putExtra("numTel", numTel);
                startActivity(intent);
            }
        });

        /**
         * direction Signout pour se deconnecter de l'application.
         */

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