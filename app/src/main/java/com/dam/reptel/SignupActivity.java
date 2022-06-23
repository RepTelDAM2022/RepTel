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
    /** declaration de la BDD et de la collection**/
    private FirebaseFirestore firestore;
    private CollectionReference contacts;

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
        firestore = FirebaseFirestore.getInstance();
        contacts = firestore.collection("Contacts");


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
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                Log.i(TAG, "onVerificationCompleted: " + phoneAuthCredential);

//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mResendToken.toString());
//                signInWithPhoneAuthCredential(credential);

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                updateUI(currentUser);

                Contact contactInfo = new Contact(nom, numTel);
                contacts.add(contactInfo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(SignupActivity.this, "Signup de " + numTel + " reussi", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, "Signup de " + numTel + " echouè", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });

                Intent intent1 = new Intent(SignupActivity.this, UnderConstruction.class);
                startActivity(intent1);



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

//            String phoneNum = "+16505551111";
//            String testVerificationCode = "123456";
//
//            Log.i(TAG, "btnSignupClick: phone num = " + phoneNum + " code = " + testVerificationCode);
//
//// Whenever verification is triggered with the whitelisted number,
//// provided it is not set for auto-retrieval, onCodeSent will be triggered.
//           // FirebaseAuth auth = FirebaseAuth.getInstance();
//            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                    .setPhoneNumber(phoneNum)
//                    .setTimeout(60L, TimeUnit.SECONDS)
//                    .setActivity(this)
//                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                        @Override
//                        public void onCodeSent(String verificationId,
//                                               PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                            // Save the verification id somewhere
//                            // ...
//                            Log.i(TAG, "onCodeSent: verificationID = " + verificationId + " ");
//
//                            // The corresponding whitelisted code above should be used to complete sign-in.
//                            //this.enableUserManuallyInputCode();
//                            //
//                            /** il faut creer le user ici **/
//                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, testVerificationCode);
//
//                            signInWithPhoneAuthCredential(credential);
//                        }
//
//
//
//                        @Override
//                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                            // Sign in with the credential
//                            // ...
//                            //signInWithPhoneAuthCredential(phoneAuthCredential);
//
//                        }
//
//                        @Override
//                        public void onVerificationFailed(FirebaseException e) {
//                            // ...
//                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                                // Invalid request
//                                Log.i(TAG, "onVerificationFailed 2 : invalid request");
//                            } else if (e instanceof FirebaseTooManyRequestsException) {
//                                // The SMS quota for the project has been exceeded
//                                Log.i(TAG, "onVerificationFailed 2 : SMS quota for the project has been exceeded");
//                            }
//
//                            // Show a message and update the UI
//                            Log.i(TAG, "onVerificationFailed 2 : ");
//
//                        }
//
//                    })
//
//                    .build();
//
//            PhoneAuthProvider.verifyPhoneNumber(options);
//
        }

    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
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
    // [END sign_in_with_phone]

    private void updateUI(FirebaseUser user) {
        Log.i(TAG, "updateUI: user name = " + user.getDisplayName() + "\n");
        Log.i(TAG, "updateUI: user number = " + user.getPhoneNumber());

    }
}