package com.dam.reptel;

import static com.dam.reptel.commons.NodesNames.KEY_CALLERSNUM;
import static com.dam.reptel.commons.NodesNames.KEY_TIMESTAMP;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Affichage des messages laisses par un appelant
 * RecyclerView des contacts
 */

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

    Bitmap photo;

    // Les widgets
    TextView tvNomContact;
    ImageView ivPhotoContact;

    /**
     * initialisation
     */

    private void initUi(){
        tvNomContact = findViewById(R.id.tvNomContact);
        ivPhotoContact = findViewById(R.id.ivPhotoContact);

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

    /**
     * retrait des informations de la base de donnees et envoie de ces info à l'Adapter
     */
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

    /**
     * on Create
     * appel aux methodes
     * initialisation
     * envoie des donnees à l'Adapter
     *
     * affichage du nom et de la photo de l'appelant en haut de l'ecran
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        Intent intent = getIntent();
        numTel = intent.getStringExtra("numTel");
        Log.i(TAG, "************: " + numTel);

        initUi();
        getRecordsDataFromFirestore();

        String nom = intent.getStringExtra("nomAppelant");
        photo = getDisplayPhoto(this, numTel);
        if (nom!=null){
            tvNomContact.setText(nom);
        } else {
            tvNomContact.setText(numTel);
        }
        // ajout des options pour afficher les photos des contacts.
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .error(R.drawable.ic_contacts_24)
                .placeholder(R.drawable.ic_contacts_24);

        Context context = ivPhotoContact.getContext();
        Glide.with(context)
                .load(photo)
                .apply(options)
                .fitCenter()
                .circleCrop()
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivPhotoContact);
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

    /**
     * methode pour retirer des contacts du telephone la photo du contact s'il existe.
     * @param context
     * @param contactNumber
     * @return
     */

    public static Bitmap getDisplayPhoto(Context context, String contactNumber) {

        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = -1;
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber),
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID }, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        Bitmap photo = null;
        if (phoneContactID != -1) {
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, phoneContactID);
            Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
            try {
                AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");

                photo = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
            } catch (IOException e) {
            }
        }

        return photo;
    }
}
