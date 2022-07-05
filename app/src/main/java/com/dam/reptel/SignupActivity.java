package com.dam.reptel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.concurrent.TimeUnit;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    /** variables globales **/

    private TextInputEditText etPrenomNom, etNumTel, etMotPass, etConfMotPass, etOTP;
    private String nom, numTel, motPasse, confMotPass, otp;
    private Button btnSenregistrer, btnContinuer;

    /** ajout de FirebaseAuth pour enregistrer l'utilisateur **/
    private FirebaseAuth firebaseAuth;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    /** initialisation **/
    private void initUI(){
        etPrenomNom = findViewById(R.id.etPrenomNom);
        etNumTel = findViewById(R.id.et_email);
        etMotPass = findViewById(R.id.etMotPass);
        etConfMotPass = findViewById(R.id.etConfMotPass);
        etOTP = findViewById(R.id.etOTP);
        btnSenregistrer = findViewById(R.id.btnSenregistrer);
        btnContinuer = findViewById(R.id.btnContinuer);

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

        /**
         * ici test ci le currentUser est null, on s'inscrit sinon on va vers l'ecran suivant.
         *
         * ici pour le test, j'ai mis en commentaires ce if donc on va directement vers l'ecran suivant.
         *
         * pour faire marcher le programme, il faut remettre ce if en marche.
         */



        if (currentUser != null) {
            Intent intent1 = new Intent(SignupActivity.this, Parametres.class);
            startActivity(intent1);

        }

        // Set OnClickListener pour le boutton s'enregistrer
        btnSenregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = etPrenomNom.getText().toString().trim();
                numTel = etNumTel.getText().toString().trim();
                motPasse = etMotPass.getText().toString().trim();
                confMotPass = etConfMotPass.getText().toString().trim();

                //verifications si les cases ne sont pas vides
                if (nom.equals("")) {
                    etPrenomNom.setError("Entrez vos prénom et nom");
                } else if (numTel.equals("")) {
                    etNumTel.setError("Entrez votre numéro de téléphone");
                } else if (numTel.length() < 10) {
                    etNumTel.setError("Entrez un numero de tel correct");
                } else if (motPasse.equals("")) {
                    etMotPass.setError("Entrez un mot de passe");
                } else if (confMotPass.equals("")) {
                    etConfMotPass.setError("Confirmez le mot de passe");
                } else if (!motPasse.equals(confMotPass)) {
                    etConfMotPass.setError("Mots de passe ne correspondent pas");
                }
                // Si tout est bon on s'enregistre.
                else {
                    Log.i(TAG, "btnSignupClick: nom = " + nom + " tel = " + numTel);
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(firebaseAuth)
                                    .setPhoneNumber(numTel)                     // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS)  // Timeout and unit
                                    .setActivity(SignupActivity.this)                         // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)                  // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        // Set OnclickListener sur OTP button btncontinuer
        btnContinuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etOTP.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Please enter OTP", Toast.LENGTH_LONG).show();
                } else {
                    verifyCode(etOTP.getText().toString());
                }
            }
        });
    }

     private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");


                            /**
                             *
                             *
                             * ici on continue vers les ecrans suivants
                             *
                             * il faudra creer la FireStore Collection et y mettre les infos comme
                             * le nom et le num de tel
                             *
                             * **/

                            Intent intent = new Intent(SignupActivity.this, Parametres.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            // Initialize phone auth callbacks
            // [START phone_auth_callbacks]
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
              mVerificationId = verificationId;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                etOTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
               // verifyCode(code);
            }
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
    };
    // [END phone_auth_callbacks]
    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithPhoneAuthCredential(credential);
    }


}