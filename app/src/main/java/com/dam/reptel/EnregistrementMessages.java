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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.dam.reptel.commons.NodesNames.*;

public class EnregistrementMessages extends AppCompatActivity {

    private static final String TAG = "EnregistrementMessages";
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    /** urlStorageMessage contiendra l'url du message dans la bdd une fois qu'il y sera enregistré **/
    private static String urlStorageMessage;

    //public static CollectionReference productsRef = FirebaseFirestore.getInstance().collection(TABLE_USER);

    /** variables globales**/

    private Button btnREC;
    private Boolean recording;
    private EditText numAppelant;
    private TextView tv_Contact;
    private String nomAppelant;
    private String time;
    private Boolean flagLu;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private String myNumTel;

    private MediaRecorder mRecorder;
    private static String mFileName = null;

    private StorageReference storageReference;

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

        firebaseAuth = FirebaseAuth.getInstance();
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

        btnREC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    btnREC.setText("REC");
                    recording = false;
                    uploadAudiotoDB();
                    enregistrerDansLaBDD();
                } else {
                    if (!numAppelant.getText().toString().equals("")) {
                        nomAppelant = getContactNameByPhoneNumber(EnregistrementMessages.this, numAppelant.getText().toString());
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
                                + "uri = " + uri +"\n"
                                + "mFilename = " + mFileName +"\n"
                                + "messageFilename = " + messageFileName + "\n"
                                + "urlStorageMassege = " + urlStorageMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: \n" + e);
                    }
                });

    }

    private void enregistrerDansLaBDD() {

        String myPhoneNumber = myNumTel;
        String uriToParse = mFileName;
        flagLu = false;
        String messageName = "M" + time + ".3gp";

//        Log.i(TAG, "My phone Number = " + myPhoneNumber);
//        Log.i(TAG, "Num Tel appelant = " + numAppelant.getText().toString());
//        Log.i(TAG, "Nom Appelant = " + nomContact);
//        Log.i(TAG, "Lien message local = " + uriToParse);
//        Log.i(TAG, "TimeStamp = " + time);
//        Log.i(TAG, "Flag = " + flagLu);
//        Log.i(TAG, "Nom appelant minuscule = " + nomContact.toLowerCase(Locale.ROOT));

       CollectionReference productsRef = FirebaseFirestore.getInstance().collection(myPhoneNumber);
       // on prepare les donnees pour les envoyer dans la bdd
        Map<String, Object> datas = new HashMap<>();
        datas.put(KEY_MYNUM, myPhoneNumber);
        datas.put(KEY_CALLERSNUM, numAppelant.getText().toString());
        datas.put(KEY_CALLERSNAME, nomAppelant);
        datas.put(KEY_MESSAGE, urlStorageMessage);
        datas.put(KEY_MESSAGE_LOCAL, mFileName);
        datas.put(KEY_TIMESTAMP, time);
        datas.put(KEY_FLAG, flagLu);
        if (nomAppelant != null) {
            datas.put(KEY_CALLERSNAMELOWERCASE, nomAppelant.toLowerCase(Locale.ROOT));
        } else {
            datas.put(KEY_CALLERSNAMELOWERCASE, null);
        };

//        Log.i(TAG, "My phone Number = " + myPhoneNumber);
//        Log.i(TAG, "Num Tel appelant = " + numAppelant.getText().toString());
//        Log.i(TAG, "Nom Appelant = " + nomAppelant);
//        Log.i(TAG, "Lien message distant = " + urlStorageMessage);
//        Log.i(TAG, "Lien message local = " + uriToParse);
//        Log.i(TAG, "TimeStamp = " + time);
//        Log.i(TAG, "Flag = " + flagLu);
//        if (nomAppelant != null)  Log.i(TAG, "Nom appelant minuscule = " + nomAppelant.toLowerCase(Locale.ROOT));


        productsRef.add(datas)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "onSuccess: DocumentSnapshot added with ID = " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: error adding document to db " + e);
                    }
                });
    }

    private void startRecording() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE);
        time =  dateFormat.format(new Date()) ;

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
            recording=true;
        } else {
            RequestPermissions();
        }
    }

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

    public static String getContactNameByPhoneNumber(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

}