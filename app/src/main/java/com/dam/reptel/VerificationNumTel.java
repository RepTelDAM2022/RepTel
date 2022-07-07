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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * verification du numero de telephone par l'OTP
 * methode desuete, la verification se fait directement dans la page de creation de l'utilisateur dans SignupActivity
 */

public class VerificationNumTel extends AppCompatActivity {

    private static final String TAG = "VerificationNumTel";

    /** variables globales **/

    Button boutton;
    PhoneAuthCredential credential;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /** declaration de la BDD et de la collection**/
    private FirebaseFirestore firestore;
    private CollectionReference contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_num_tel);

        boutton=findViewById(R.id.btnContinuer);
        firestore = FirebaseFirestore.getInstance();
        contacts = firestore.collection("Contacts");

        Intent intent = getIntent();
        String verificationID = intent.getStringExtra("verificationID");
        String code = intent.getStringExtra("Token");
        String nom = intent.getStringExtra("nom");
        String numTel = intent.getStringExtra("numTel");

        Log.i(TAG, "onCreate: dans verification num tel " + verificationID);
        Log.i(TAG, "onCreate: token = " + code);


        boutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "onClick: dans verification num tel " + verificationID);
                Log.i(TAG, "onClick: token = " + code);
                Log.i(TAG, "onClick: nom = " + nom + " tel " + numTel);

                verifyPhoneNumberWithCode(verificationID, code);
                signInWithPhoneAuthCredential(credential);

                Contact contactInfo = new Contact(nom, numTel);
                contacts.add(contactInfo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(VerificationNumTel.this, "Signup de " + numTel + " reussi", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(VerificationNumTel.this, "Signup de " + numTel + " echouè", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });



//                Intent intent3 = new Intent(VerificationNumTel.this, UnderConstruction.class);
//                startActivity(intent3);

            }
        });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
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