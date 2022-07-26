package com.dam.reptel;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.reptel.commons.Util;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.dam.reptel.commons.NodesNames.*;
import static com.dam.reptel.commons.Util.*;

/**
 * Enregistrement des messages en local pour contourner l'interdiction d'Android d'enregistrer une communication telephonique
 */

public class EnregistrementMessages extends AppCompatActivity {

    private static final String TAG = "EnregistrementMessages";
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    /**
     * urlStorageMessage contiendra l'url du message dans la bdd une fois qu'il y sera enregistré
     **/
    private static String urlStorageMessage;

    //public static CollectionReference productsRef = FirebaseFirestore.getInstance().collection(TABLE_USER);

    /**
     * variables globales
     **/

    private Button btnREC;
    private Boolean recording;
    private EditText numAppelant;
    private TextView tv_Contact;
    private String nomAppelant;
    private long time;
    private boolean flagLu;
    private boolean firstMessage;
    private String myNumTel;
    private String num_Appelant;

    /** declaration de la bdd **/
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;
    private StorageReference storageReference;

    private MediaRecorder mRecorder;
    private static String mFileName = null;


    /**
     * initialisation
     */

    private void initUI() {
        btnREC = findViewById(R.id.btnEnrMes);
        numAppelant = findViewById(R.id.et_num_appelant);
        tv_Contact = findViewById(R.id.tv_nomContact);
        recording = false;

        File repTel = new File(Environment.getExternalStorageDirectory() + "/Reptel");
        if (!repTel.isDirectory()) {
            File repTelDirectory = new File(Environment.getExternalStorageDirectory() + "/RepTel/");
            repTelDirectory.mkdirs();
        }

        /** initialisation de la bdd **/
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        Log.i(TAG, "initUI: userID = " + userId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement_messages);

        Intent intent = getIntent();
        myNumTel = intent.getStringExtra("numTel");
        String titre = intent.getStringExtra("Titre");
        setTitle(titre);


        initUI();

        /**
         * enregistrement du message en local
         */

        btnREC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    btnREC.setText("REC");
                    recording = false;
                    num_Appelant = numAppelant.getText().toString();
                    enregistrerDansLaBDD();
//                    verifierExistenceNumero(num_Appelant);
//                    uploadAudiotoDB();
                } else {
                    if (!numAppelant.getText().toString().equals("")) {
                        nomAppelant = Util.getContactNameByPhoneNumber(EnregistrementMessages.this, numAppelant.getText().toString());
                        tv_Contact.setText(nomAppelant);
                        startRecording();
                        btnREC.setText("STOP");
                    } else {
                        Log.i(TAG, "onClick: num appelant vide");
                    }
                }
            }
        });
    }

    /**
     * telechargement du message local en base de donnees.
     * possible bloccage par google de cette action
     */
    private void uploadAudiotoDB() {
        String messageFileName = "M" + time + ".3gp";
        StorageReference filepath = storageReference.child("Messages").child(messageFileName);

        Uri uri = Uri.fromFile(new File(mFileName));

        filepath.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // on demande au storage l'adresse url de stockage
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                urlStorageMessage = task.getResult().toString();
                            }
                        });
                        Log.i(TAG, "onSuccess: \n"
                                + "uri = " + uri + "\n"
                                + "mFilename = " + mFileName + "\n"
                                + "messageFilename = " + messageFileName + "\n"
                                + "urlStorageMassege = " + urlStorageMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: \n" + e);
                    }
                });

    }

    /**
     * methode de verification de la pre-exsistence du numero dans la base de donnees.
     * ne marche pas pour l'instant
     * pour qu'elle marche, il faut reprendre la methode de ContactRecyclerView d'unification des doublons
     * et tester si le numero existe dans le fichier resultat
     * cette methode sera inutile si nous mettons en place l'iunification des doublons qui n'est pas encore optimisée.
     *
     * @param n
     */
    private void verifierExistenceNumero(String n) {

        num_Appelant = n;
        Log.i(TAG, "j'entre dans la methode verifierExistenceNumero: " + num_Appelant);

        db.collection(userId)
                .orderBy(KEY_CALLERSNUM)
                .whereEqualTo(KEY_CALLERSNUM, num_Appelant)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.i(TAG, "onSuccess: ");
                    }
                });

        Log.i(TAG, "verifierExistenceNumero: on sort de la methode verifier existence numero");
    }

    /**
     * enregistrement dans la base de donnees.
     */
    private void enregistrerDansLaBDD() {

        String myPhoneNumber = myNumTel;
        String uriToParse = mFileName;
        flagLu = false;
        String messageName = "M" + time + ".3gp";
        num_Appelant = numAppelant.getText().toString();

        CollectionReference productsRef = FirebaseFirestore.getInstance().collection(userId);
        DocumentReference documentReference = productsRef.document("M"+time);

//        Log.i(TAG, "enregistrerDansLaBDD: userID = " + userId + " docref = " + "M" + time);
        // on prepare les donnees pour les envoyer dans la bdd
        Map<String, Object> datas = new HashMap<>();
        datas.put(KEY_MYNUM, myPhoneNumber);
        datas.put(KEY_CALLERSNUM, numAppelant.getText().toString());
        datas.put(KEY_CALLERSNAME, nomAppelant);
        datas.put(KEY_MESSAGE, urlStorageMessage);
        datas.put(KEY_MESSAGE_LOCAL, mFileName);
        datas.put(KEY_TIMESTAMP, time);
        datas.put(KEY_FLAG, flagLu);
        datas.put(KEY_FIRSTMESSAGE, firstMessage);
        if (nomAppelant != null) {
            datas.put(KEY_CALLERSNAMELOWERCASE, nomAppelant.toLowerCase(Locale.ROOT));
        } else {
            datas.put(KEY_CALLERSNAMELOWERCASE, null);
        }

        productsRef.document("M"+time).set(datas)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "onSuccess: document added with ID = " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: error adding document to db " + e);
                    }
                });
    }

    /**
     * enregistrement du message vocal en local
     */
    private void startRecording() {

        time = System.currentTimeMillis();

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/RepTel/M" + time + ".3gp";

        if (CheckPermissions()) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            mRecorder.start();
            recording = true;
        } else {
            RequestPermissions();
        }
    }

    /**
     * verification et demande des permissions requises
     * @return
     */
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(EnregistrementMessages.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}