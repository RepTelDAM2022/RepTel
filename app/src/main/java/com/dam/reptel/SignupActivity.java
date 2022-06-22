package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;




public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    /** variables globales **/

    private TextInputEditText etPrenomNom, etNumTel, etMotPass, etConfMotPass;
    private String nom, numTel, motPasse, confMotPass;
    private Button btnSenregistrer;

    /** ajout de FirebaseAuth pour enregistrer l'utilisateur **/
    private FirebaseAuth firebaseAuth;

    /** initialisation **/
    private void initUI(){
        etPrenomNom = findViewById(R.id.etPrenomNom);
        etNumTel = findViewById(R.id.etNumTel);
        etMotPass = findViewById(R.id.etMotPass);
        etConfMotPass = findViewById(R.id.etConfMotPass);
        btnSenregistrer = findViewById(R.id.btnSenregistrer);

        firebaseAuth= FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /** recuperation de la valeur de l'intent **/
        Intent intent = getIntent();
        String titre = intent.getStringExtra("TitrePage");
        setTitle(titre);

        initUI();
    }

    public void btnSignupClick(View view) {
        nom=etPrenomNom.getText().toString().trim();
        numTel=etNumTel.getText().toString().trim();
        motPasse=etMotPass.getText().toString().trim();
        confMotPass=etConfMotPass.getText().toString().trim();

        //verifications si les cases ne sont pas vides
        if (nom.equals("")){
            etPrenomNom.setError("Entrez vos prénom et nom");
        } else if (numTel.equals("")){
            etNumTel.setError("Entrez votre numéro de téléphone");
        } else if (numTel.length()<10){
            etNumTel.setError("Entrez un numero de tel correct");
        } else if (motPasse.equals("")){
            etMotPass.setError("Entrez un mot de passe");
        } else if (confMotPass.equals("")){
            etConfMotPass.setError("Confirmez le mot de passe");
        } else if (!motPasse.equals(confMotPass)){
            etConfMotPass.setError("Mots de passe ne correspondent pas");
        }
        // Si tout est bon on s'enregistre.
        else {
            // on verifie la connection a internet
            if (Util.connectionAvailable(this))
        }


    }
}