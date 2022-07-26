package com.dam.reptel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import static com.dam.reptel.commons.NodesNames.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RecyclerView des contacts.
 */

public class ContactsRecyclerView extends AppCompatActivity {
    private static final String TAG = "ContactsRecyclerView";

    /** var globales **/
    private Context context;
    private RecyclerView rvContacts;
    private AdapterContacts adapterContacts;
    private FirebaseFirestore db;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userID;
    private String ColKeyPhoneNumber;
    private ArrayList<ModelRecord> tableauRecords;
    private ModelRecord modelRecord;
    private ArrayList<String> listeSansDoublons;
    private ArrayList<String> listeSansDoublonsLus;
    private ArrayList<Long> nbreMessages;
    private ArrayList<Long> nbreMessagesLus;

    /** initialisation **/
    private void init(){
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setHasFixedSize(true);
        rvContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();
        tableauRecords = new ArrayList<ModelRecord>();
        modelRecord = new ModelRecord();

        listeSansDoublons = new ArrayList<String>();
        listeSansDoublonsLus = new ArrayList<String>();
        nbreMessages = new ArrayList<Long>();
        nbreMessagesLus = new ArrayList<Long>();

        Log.i(TAG, "init: userId = " + userID);
    }

    public class LinearLayoutManagerWrapper extends LinearLayoutManager {
        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }
        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }
    }

    /** la gestion du menu**/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // liaison avec le widget de recherche
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContact(newText.toString());
                return false;
            }
        });

        return true;
    }

    private void searchContact(String s) {
        Query query = db.collection(userID);
        if (!String.valueOf(s).equals("")){
            query = query
                    .orderBy("nom_appelant_minuscule")
                    .startAt(s.toLowerCase())
                    .endAt(s.toLowerCase() + "\uf8ff");
        }

        FirestoreRecyclerOptions<ModelRecord> searchContact =
                new FirestoreRecyclerOptions.Builder<ModelRecord>()
                        .setQuery(query, ModelRecord.class)
                        .build();

        adapterContacts = new AdapterContacts(searchContact);
        rvContacts.setAdapter(adapterContacts);
        adapterContacts.startListening();
    }

        private void getDataFromFirestore(){

        /** essai d'affichage sans doublons **/
            Log.i(TAG, "getDataFromFirestore: userId " + userID);
            Query query2 = db.collection(userID);
        query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> listeContacts=new ArrayList<>();
                ArrayList<String> listeContactsLus=new ArrayList<>();
                if (task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        Log.i(TAG, "onComplete: " + documentSnapshot.getString(KEY_CALLERSNUM));
                        listeContacts.add(documentSnapshot.getString(KEY_CALLERSNUM));
                        // La meme chose mais avec le flag lu
                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean(KEY_FLAG))) listeContactsLus.add(documentSnapshot.getString(KEY_CALLERSNUM));
                    }
                    Log.i(TAG, "onComplete: " + listeContacts);
                    Log.i(TAG, "onComplete: " + listeContactsLus);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        // Création du tableau avec le nb d'appels totals en fonction du numéro de KEY_CALLERSNUM
                        Map<String, Long> counts = listeContacts.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
                        Log.i(TAG, "compteOccurrences: " + counts);
                        Map<String, Long> countsLus = listeContactsLus.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
                        Log.i(TAG, "compteOccurrencesLus: " + countsLus);
                        // Récupération du nombre d'appels
                        for(Long value : counts.values()){
                            Log.i(TAG, "Nb appels => " + value);
                            nbreMessages.add(value);
                        }
                        // Récupération d'une seule valeur de KEY_CALLERSNUM
                        for(String key: counts.keySet()){
                            Log.i(TAG, "Nb appels => " + key);
                            listeSansDoublons.add(key);
                        }
                        for(Long value : countsLus.values()){
                            Log.i(TAG, "Nb appels => " + value);
                            nbreMessagesLus.add(value);
                        }
                        // Récupération d'une seule valeur de KEY_CALLERSNUM
                        for(String key: countsLus.keySet()){
                            Log.i(TAG, "Nb appels => " + key);
                            listeSansDoublonsLus.add(key);
                        }
                    }
                    ContactsAdapter myContactsAdapter = new ContactsAdapter(ContactsRecyclerView.this, listeSansDoublons, nbreMessages, listeSansDoublonsLus, nbreMessagesLus);
                    rvContacts.setAdapter(myContactsAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ContactsRecyclerView.this,
                            LinearLayoutManager.VERTICAL, false);
                    rvContacts.setLayoutManager(layoutManager);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_recycler_view);

        init();
        getDataFromFirestore();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(curentUser == null){
            startActivity(new Intent(ContactsRecyclerView.this, SignupEmail.class));
        }
    }
}