package com.dam.reptel;

import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNAME;
import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNAMELOWERCASE;
import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNUM;
import static com.dam.reptel.commons.NodesNames.KEY_FIRSTMESSAGE;
import static com.dam.reptel.commons.NodesNames.KEY_FLAG;
import static com.dam.reptel.commons.NodesNames.KEY_MESSAGE;
import static com.dam.reptel.commons.NodesNames.KEY_MESSAGE_LOCAL;
import static com.dam.reptel.commons.NodesNames.KEY_MYNUM;
import static com.dam.reptel.commons.NodesNames.KEY_TIMESTAMP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupEmail extends AppCompatActivity {

    private static final String TAG = "SignupEmail";

    /** Variables Globales **/

    private TextInputEditText etPrenomNom, etNumTel, etMotPass, etConfMotPass, etEmail;
    private String nom, numTel, motPasse, confMotPass, email;
    private Button btnSenregistrer;
    private String userID;

    /** ajout de FirebaseAuth pour enregistrer l'utilisateur
     * et ajout de la firebase**/

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore dbc = FirebaseFirestore.getInstance();

    private DocumentReference dbmInfo = dbc.document("");


    /** initialisation **/
    private void initUI(){
        etPrenomNom = findViewById(R.id.etPrenomNom);
        etNumTel = findViewById(R.id.et_email);
        etEmail= findViewById(R.id.etEmail);
        etMotPass = findViewById(R.id.etMotPass);
        etConfMotPass = findViewById(R.id.etConfMotPass);
        btnSenregistrer = findViewById(R.id.btnSenregistrer);

        firebaseAuth= FirebaseAuth.getInstance();
        //Log.i(TAG, "initUI: instantiation de la firebase");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);

        /** recuperation de la valeur de l'intent **/
        Intent intent = getIntent();
        String titre = intent.getStringExtra("TitrePage");
        setTitle(titre);

        initUI();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        //Log.i(TAG, "onCreate: firebase current user " + currentUser);

//        /**
//         * ici test ci le currentUser est null, on s'inscrit sinon on va vers l'ecran suivant.
//         *
//         */
//
//        if (currentUser != null) {
//            Intent intent1 = new Intent(SignupEmail.this, Parametres.class);
//            startActivity(intent1);
//        }

        // Set onClickListener pour le bouton d'enregistrement
        btnSenregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = etPrenomNom.getText().toString().trim();
                numTel = etNumTel.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                motPasse = etMotPass.getText().toString().trim();
                confMotPass = etConfMotPass.getText().toString().trim();

                //verifications si les cases ne sont pas vides
                if (nom.equals("")) {
                    etPrenomNom.setError(getString(R.string.erreurnom));
                } else if (numTel.equals("")) {
                    etNumTel.setError("Entrez votre numéro de téléphone");
                } else if (numTel.length() < 10) {
                    etNumTel.setError("Entrez un numero de tel correct");
                } else if (email.equals("")) {
                    etEmail.setError("Entrez une adresse Email");
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etEmail.setError("Entrez une adresse Email valide SVP");
                } else if (motPasse.equals("")) {
                    etMotPass.setError("Entrez un mot de passe");
                } else if (motPasse.length()<6) {
                    etMotPass.setError("Le mot de passe doit etre supérieur à 6 caractères");
                } else if (confMotPass.equals("")) {
                    etConfMotPass.setError("Confirmez le mot de passe");
                } else if (!motPasse.equals(confMotPass)) {
                    etConfMotPass.setError("Mots de passe ne correspondent pas");
                }
                // Si tout est bon on s'enregistre.
                else {
                    firebaseAuth.createUserWithEmailAndPassword(email, motPasse)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.i(TAG, "onComplete: task : " + task);
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.i(TAG, "onSuccess: userId = " + firebaseAuth.getCurrentUser().getUid());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "onFailure: pas pu enregistrer ex = " + e);

                                }
                            });

                    userID = firebaseAuth.getCurrentUser().getUid();
                    //enregistrerDansLaBDD();

                    /**
                     *
                     *
                     * ici on continue vers les ecrans suivants
                     *
                     * il faudra creer la FireStore Collection et y mettre les infos comme
                     * le nom et le num de tel
                     *
                     * **/

                    Intent intent = new Intent(SignupEmail.this, Parametres.class);
                    intent.putExtra("numTel", numTel);
                    startActivity(intent);
                }
            }
        });
    }


//    private void enregistrerDansLaBDD() {
//
//        String myPhoneNumber = etNumTel.getText().toString().trim();
//        long time = System.currentTimeMillis();
//
//        CollectionReference productsRef = FirebaseFirestore.getInstance().collection(userID);
//
//        // on prepare les donnees pour les envoyer dans la bdd
//        Map<String, Object> datas = new HashMap<>();
//        datas.put(KEY_MYNUM, myPhoneNumber);
//        datas.put(KEY_CALLERSNUM, "00000000");
//        datas.put(KEY_CALLERSNAME, "Nom Appelant");
//        datas.put(KEY_MESSAGE, null);
//        datas.put(KEY_MESSAGE_LOCAL, null);
//        datas.put(KEY_TIMESTAMP, time);
//        datas.put(KEY_FLAG, "false");
//        datas.put(KEY_FIRSTMESSAGE, "true");
//        datas.put(KEY_CALLERSNAMELOWERCASE, null);
//
//        productsRef.add(datas)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.i(TAG, "onSuccess: DocumentSnapshot added with ID = " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i(TAG, "onFailure: error adding document to db " + e);
//                    }
//                });
//    }
//

}