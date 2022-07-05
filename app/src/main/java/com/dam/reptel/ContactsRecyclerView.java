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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.dam.reptel.commons.NodesNames.*;

import java.util.ArrayList;

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

    /** initialisation **/
    private void init(){
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setHasFixedSize(true);   /** ?????? faut-il mettre ca???? la taille de la bdd peut changer a tout moment **/
//        rvContacts.setLayoutManager(new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false));
        rvContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();
        tableauRecords = new ArrayList<ModelRecord>();
        modelRecord = new ModelRecord();

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

    /** recuperer les donnees de la bdd **/
    private void getDataFromFirestore(){
//        Log.i(TAG, "getDataFromFirestore: avant la query userID = " + userID);


        db.collection(userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.i(TAG, document.getId() + " => " + document.get(KEY_CALLERSNUM));

                                    modelRecord.setLienMessageDistant((String) document.get(KEY_MESSAGE));
                                    modelRecord.setLienMessageLocal((String) document.get(KEY_MESSAGE_LOCAL));
                                    modelRecord.setNomdelAppelant((String) document.get(KEY_CALLERSNAME));
                                    modelRecord.setNomdelAppelantMinuscule((String) document.get(KEY_CALLERSNAMELOWERCASE));
                                    modelRecord.setNumTeldelAppelant((String) document.get(KEY_CALLERSNUM));
                                    modelRecord.setRegisteredUserPhoneNumber((String) document.get(KEY_MYNUM));
                                    modelRecord.setTimeStamp((Long) document.get(KEY_TIMESTAMP));
                                    Log.i(TAG, "onComplete: doc timestamp" + document.get(KEY_TIMESTAMP));
                                    modelRecord.setFlag((Boolean) document.get(KEY_FLAG));

                                    tableauRecords.add(modelRecord);
                                    Log.i(TAG, "onComplete: tab timestamp " + tableauRecords.get(0).getTimeStamp());

//                                    Log.i(TAG, "onComplete: taille tableau = " + tableauRecords.size());
//                                    Log.i(TAG, "onComplete: tableau = " + tableauRecords.toString());
//                                    Log.i(TAG, "onComplete: num tel appelant = " + tableauRecords.get(0).getNumTeldelAppelant());
//                                    Log.i(TAG, "onComplete: nom appelant = " + tableauRecords.get(0).getNomdelAppelant());
//                                    Log.i(TAG, "onComplete: timestamp 0.0 = " + tableauRecords.get(0).getTimeStamp());
//                                    if (tableauRecords.size()==2){
//                                    Log.i(TAG, "onComplete: timestamp 1.0 = " + tableauRecords.get(0).getTimeStamp());
//                                    Log.i(TAG, "onComplete: timestamp 1.1 = " + tableauRecords.get(1).getTimeStamp());
//                                    }
                                    if (tableauRecords.size()==3){
                                        Log.i(TAG, "onComplete: timestamp 2.0 = " + tableauRecords.get(0).getTimeStamp());
                                        Log.i(TAG, "onComplete: timestamp 2.1 = " + tableauRecords.get(1).getTimeStamp());
                                        Log.i(TAG, "onComplete: timestamp 2.2 = " + tableauRecords.get(2).getTimeStamp());
                                        }

                                }
                            } else {
                                Log.i(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });



        Query query = db.collection(userID)
                .orderBy(KEY_TIMESTAMP, Query.Direction.DESCENDING)
                ;

        FirestoreRecyclerOptions<ModelRecord> record =
                new FirestoreRecyclerOptions.Builder<ModelRecord>()
                        .setQuery(query, ModelRecord.class)
                        .build();

//        Log.i(TAG, "getDataFromFirestore: record = " + record.toString() );

        adapterContacts = new AdapterContacts(record);
        rvContacts.setAdapter(adapterContacts);

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
        adapterContacts.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(curentUser == null){
            startActivity(new Intent(ContactsRecyclerView.this, SignupEmail.class));
        } else {
//            Log.i(TAG, "onStart: start listening");
            adapterContacts.startListening();
        }
    }
}