package com.dam.reptel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.dam.reptel.commons.NodesNames.*;

import java.util.ArrayList;

public class RecordsRecyclerView extends AppCompatActivity {
    private static final String TAG = "RecordsRecyclerView";

    /** Variables Globales **/
    private Context context;
    private RecyclerView rvRecords;
    private AdapterRecords adapterRecords;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userID;
    private ArrayList<ModelRecord> tableauRecords;
    private ModelRecord modelRecord;
    private String numTel;
    private String contactName;
    private long dateTime;

    private void initUi(){

        rvRecords = findViewById(R.id.rvRecords);
        rvRecords.setHasFixedSize(true);
        rvRecords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();
        tableauRecords = new ArrayList<ModelRecord>();
        modelRecord = new ModelRecord();
    }

    private void getRecordsDataFromFirestore(){

        Query query = db.collection(userID)
                .orderBy(KEY_CALLERSNUM)
                .orderBy(KEY_TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(KEY_CALLERSNUM, numTel);

        FirestoreRecyclerOptions<ModelRecord> record =
                new FirestoreRecyclerOptions.Builder<ModelRecord>()
                        .setQuery(query, ModelRecord.class)
                        .build();

        adapterRecords = new AdapterRecords(record);
        rvRecords.setAdapter(adapterRecords);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_records);
        Intent intent = getIntent();
        numTel = intent.getStringExtra("numTel");

        initUi();
        getRecordsDataFromFirestore();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterRecords.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(RecordsRecyclerView.this, SignupEmail.class));
        } else {
            adapterRecords.startListening();
        }
    }
}
