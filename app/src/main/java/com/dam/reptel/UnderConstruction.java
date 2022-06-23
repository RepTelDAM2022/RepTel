package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UnderConstruction extends AppCompatActivity {

    /** ajout de FirebaseAuth pour enregistrer l'utilisateur **/
    private FirebaseAuth firebaseAuth;
    /** declaration de la BDD et de la collection**/
    private FirebaseFirestore firestore;
    private CollectionReference contacts;

    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_construction);

        text = findViewById(R.id.textView5);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        contacts.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String contact = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Contact contactInfo = documentSnapshot.toObject(Contact.class);
                            contactInfo.setDocumentId(documentSnapshot.getId());

                            String documentId = contactInfo.getDocumentId();
                            String numTel = contactInfo.getNumTel();
                            String nom = contactInfo.getNom();

                            contact += documentId + "\nNom : " + nom + "\nNumTel : " + numTel + "\n\n";
                        }
                        text.setText(contact);
                    }
                });

    }
}