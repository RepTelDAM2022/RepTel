package com.dam.reptel;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.dam.reptel.commons.NodesNames.*;

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

    /** initialisation **/
    private void init(){
        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setHasFixedSize(true);   /** ?????? faut-il mettre ca???? la taille de la bdd peut changer a tout moment **/
//        rvContacts.setLayoutManager(new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false));
        rvContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();

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

//        Query query = db.collection(userID).orderBy(KEY_MYNUM);
        Query query = db.collection(userID)
                .orderBy(KEY_TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo("flag", false);

        FirestoreRecyclerOptions<ModelRecord> record =
                new FirestoreRecyclerOptions.Builder<ModelRecord>()
                        .setQuery(query, ModelRecord.class)
                        .build();

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
            adapterContacts.startListening();
        }
    }
}