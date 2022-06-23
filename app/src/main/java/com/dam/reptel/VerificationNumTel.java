package com.dam.reptel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerificationNumTel extends AppCompatActivity {

    Button boutton = findViewById(R.id.btnContinuer);
    PhoneAuthCredential credential;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final String TAG = "VerificationNumTel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_num_tel);

        Intent intent = getIntent();
        String verificationID = intent.getStringExtra("verificationID");
        String code = intent.getStringExtra("Token");

        Log.i(TAG, "onCreate: dans verification num tel " + verificationID);
        Log.i(TAG, "onCreate: token = " + code);


        boutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "onClick: dans verification num tel " + verificationID);
                Log.i(TAG, "onClick: token = " + code);

                verifyPhoneNumberWithCode(verificationID, code);
                signInWithPhoneAuthCredential(credential);

                Intent intent3 = new Intent(VerificationNumTel.this, UnderConstruction.class);
                startActivity(intent3);

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

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
    }

}