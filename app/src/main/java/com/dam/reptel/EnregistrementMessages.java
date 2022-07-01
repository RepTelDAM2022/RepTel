package com.dam.reptel;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Context;
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

import static com.dam.reptel.commons.NodesNames.*;

public class EnregistrementMessages extends AppCompatActivity {

    private static final String TAG = "EnregistrementMessages";
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    /** la base de donnees et le storage **/
    private static String urlStorageMessage;

    public static CollectionReference productsRef = FirebaseFirestore.getInstance().collection(TABLE_USER);
    public static StorageReference storageRef = FirebaseStorage.getInstance().getReference(MESSAGE_FOLDER);


    /** variables globales**/

    private Button btnREC;
    private Boolean recording;
    private EditText numAppelant;
    private TextView tv_Contact;
    private String nomContact;
    private String time;
    private Boolean flagLu;

    private MediaRecorder mRecorder;
    private static String mFileName = null;

    private void initUI() {
        btnREC = findViewById(R.id.btnEnrMes);
        numAppelant = findViewById(R.id.et_num_appelant);
        tv_Contact = findViewById(R.id.tv_nomContact);
        recording = false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE);
        String time = dateFormat.format(new Date());

        File repTel = new File(Environment.getExternalStorageDirectory() + "/Reptel");
        if (!repTel.isDirectory()) {
            File repTelDirectory = new File(Environment.getExternalStorageDirectory() + "/RepTel/");
            repTelDirectory.mkdirs();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement_messages);

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
                    enregistrerDansLaBDD();
                } else {
                    if (!numAppelant.getText().toString().equals("")) {
                        //Log.i(TAG, "onClick: num appelant " + numAppelant.getText().toString());
                        nomContact = getContactNameByPhoneNumber(EnregistrementMessages.this, numAppelant.getText().toString());
                        tv_Contact.setText(nomContact);
                        startRecording();
                        btnREC.setText("STOP");
                        //Log.i(TAG, "onCreate: mFilename apres start recording= " + mFileName);
                    } else {
                        Log.i(TAG, "onClick: num appelant vide");
                    }
                }
            }
        });
    }

    private void enregistrerDansLaBDD() {

        String myPhoneNumber = "0621818524"; // TODO recuperer le numero du SignupEmail.java
        String uriToParse = mFileName;
        flagLu = false;

//        Log.i(TAG, "My phone Number = " + myPhoneNumber);
//        Log.i(TAG, "Num Tel appelant = " + numAppelant.getText().toString());
//        Log.i(TAG, "Nom Appelant = " + nomContact);
//        Log.i(TAG, "Lien message local = " + uriToParse);
//        Log.i(TAG, "TimeStamp = " + time);
//        Log.i(TAG, "Flag = " + flagLu);
//        Log.i(TAG, "Nom appelant minuscule = " + nomContact.toLowerCase(Locale.ROOT));

        Uri messageUri = Uri.fromFile(new File(uriToParse).getAbsoluteFile());
        Log.i(TAG, "messageUri = " + messageUri);

        StorageReference fileReference = storageRef.child("M" + time + ".3gp");
        fileReference.putFile(messageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // on demande au storage l'adresse URL de stockage
                       fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                           @Override
                           public void onComplete(@NonNull Task<Uri> task) {
                               // Quand on récupére l'URL on la transforme en texte pour l'insérer dans Firestore avec les autres données
                               urlStorageMessage = task.getResult().toString();
                               // on prepare les donnees pour les envoyer dans la bdd
                               Map<String, Object> datas = new HashMap<>();
                               datas.put(KEY_MYNUM, myPhoneNumber);
                               datas.put(KEY_CALLERSNUM, numAppelant.getText().toString());
                               datas.put(KEY_CALLERSNAME, nomContact);
                               datas.put(KEY_MESSAGE, urlStorageMessage);
                               datas.put(KEY_MESSAGE_LOCAL, mFileName);
                               datas.put(KEY_TIMESTAMP, time);
                               datas.put(KEY_FLAG, flagLu);

                               Log.i(TAG, "My phone Number = " + myPhoneNumber);
                               Log.i(TAG, "Num Tel appelant = " + numAppelant.getText().toString());
                               Log.i(TAG, "Nom Appelant = " + nomContact);
                               Log.i(TAG, "Lien message local = " + uriToParse);
                               Log.i(TAG, "TimeStamp = " + time);
                               Log.i(TAG, "Flag = " + flagLu);
                               Log.i(TAG, "Nom appelant minuscule = " + nomContact.toLowerCase(Locale.ROOT));


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
                       });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: on n'a pas enregistré");
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