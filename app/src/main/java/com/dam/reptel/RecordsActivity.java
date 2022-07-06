package com.dam.reptel;

import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNUM;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.dam.reptel.commons.NodesNames.*;
public class RecordsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference userIdCollection;

    private FirebaseUser currentUser;

    private String appelant;

    // Les widgets
    TextView tvNomContact;
    ImageView ivPhotoContact;

    //init widget
    private void init(){
        tvNomContact = findViewById(R.id.tvNomContact);
        ivPhotoContact = findViewById(R.id.ivPhotoContact);
    }

    private void initFB(){
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userIdCollection = db.collection(currentUser.toString());
    }

    private void getDataFromCaller(){
        appelant = "0789516857";
       // Apple aux méthodes pour afficher le nom et la photo

       // Set des datas vers les widgets

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // Numéro de l'appeleant depuis l'intent lors du itemclic du premier recycler

        init();
        initFB();
        getDataFromCaller();
        // méthode pour remplir le rv



    }
}