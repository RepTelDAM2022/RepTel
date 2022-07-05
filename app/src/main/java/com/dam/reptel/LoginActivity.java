package com.dam.reptel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.reptel.commons.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    /** variables globales **/

    private TextInputEditText et_email, etMotPass;
    private String email, password;

    public void initUI(){
        et_email = findViewById(R.id.et_email);
        etMotPass = findViewById(R.id.etMotPass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

        etMotPass.setOnEditorActionListener(editorActionListener);
    }

    public void btnLoginClick(View v) {
        email = et_email.getText().toString().trim();
        password = etMotPass.getText().toString().trim();

        // Vérification du remplissage des champs email et password
        if (email.equals("")) {
            et_email.setError("Entrez votre Email");
        } else if (password.equals("")) {
            etMotPass.setError("Entrez votre mot de passe");
        } else {
            // 9 Ajout de la vérification de la connection internet
            if (Util.connectionAvailable(this)) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(LoginActivity.this, Parametres.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Login failed" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, Parametres.class));
            finish();
        }

    }

    /** 12 Ajout des boutons next et send à la place du retour chariot du keyboard **/
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // Utilisation de actionId qui correspond à l'action ajouter dans le xml
            switch (actionId){
                case EditorInfo.IME_ACTION_DONE:
                    btnLoginClick(v);
            }
            return false; // On laisse le return à false pour empêcher le comportement normal du clavier
        }
    };
}