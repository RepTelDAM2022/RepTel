package com.dam.reptel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    /** variables globales **/

    private TextInputEditText etPrenomNom, etNumTel, etMotPass, etConfMotPass;
    private String nom, numTel, motPasse, confMotPass;
    private Button btnSenregistrer;

    /** ajout de FirebaseAuth pour enregistrer l'utilisateur **/
    private FirebaseAuth firebaseAuth;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;



    /** initialisation **/
    private void initUI(){
        etPrenomNom = findViewById(R.id.etPrenomNom);
        etNumTel = findViewById(R.id.etNumTel);
        etMotPass = findViewById(R.id.etMotPass);
        etConfMotPass = findViewById(R.id.etConfMotPass);
        btnSenregistrer = findViewById(R.id.btnSenregistrer);

        firebaseAuth= FirebaseAuth.getInstance();
        Log.i(TAG, "initUI: instantiation de la firebase");
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


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //updateUI(currentUser);

        Log.i(TAG, "onCreate: firebase current user " + currentUser);

        if (currentUser!=null){
            Intent intent1 = new Intent(SignupActivity.this, UnderConstruction.class);
            startActivity(intent1);

        }


        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                Log.i(TAG, "onVerificationCompleted: " + phoneAuthCredential);

                String verificationId = "";
                String code = "";
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                signInWithPhoneAuthCredential(credential);

                Log.i(TAG, "onVerificationCompleted: sIWPAC fait");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.i(TAG, "onVerificationFailed: invalid request");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.i(TAG, "onVerificationFailed: SMS quota for the project has been exceeded");
                }

                // Show a message and update the UI
                Log.i(TAG, "onVerificationFailed: ");

            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent: " + verificationId);
                Log.i(TAG, "onCodeSent: " + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Intent intent2 = new Intent(SignupActivity.this, VerificationNumTel.class);
                intent2.putExtra("verificationID", mVerificationId);
                intent2.putExtra("Token", mResendToken);
                intent2.putExtra("nom", nom);
                intent2.putExtra("numTel", numTel);
                startActivity(intent2);

            }
        };
        // [END phone_auth_callbacks]

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
            Log.i(TAG, "btnSignupClick: nom = " + nom + " tel = " + numTel );
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(numTel)                     // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS)  // Timeout and unit
                            .setActivity(this)                         // Activity (for callback binding)
                            .setCallbacks(mCallbacks)                  // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

        }

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }

}