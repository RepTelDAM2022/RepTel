package com.dam.reptel;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class Annonce extends AppCompatActivity {

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    /**
     * variables
     **/

    private Button btnREC, btnPLAY;
    private Boolean recording;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static String mFileName = null;

    private void initUI() {
        btnREC = findViewById(R.id.btnREC);
        btnPLAY = findViewById(R.id.btnPLAY);
        recording = false;

        /**
         * Creation du repertoire RepTel pour y mettre le fichier Annonce.3gp et plus tard les fichiers des messages.
         *
         * cette creation se fait dans MainActivity avec les demandes des permissions.
         *
         * Ici la Methode Annonce verifie que ce repertoire existe vraiment avant d'enregistrer
         * paranoia check
         */

        File repTel = new File(Environment.getExternalStorageDirectory() + "/Reptel");
        if (repTel.isDirectory()) {
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/RepTel/Annonce.3gp";
        } else {
            File repTelDirectory = new File(Environment.getExternalStorageDirectory() + "/RepTel/");
            repTelDirectory.mkdirs();
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/RepTel/Annonce.3gp";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);

        Intent intent = getIntent();
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
                } else {
                    startRecording();
                }
            }
        });
        btnPLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

    }

    /**
     * methode pour lire les fichiers audio
     **/
    private void playAudio() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            ;
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "playAudio: failed");
        }
    }

    /**
     * methode pour enregistrer les fichiers audio
     * mais d'abord on reverifie qu'on a les permissions requises.
     **/

    private void startRecording() {
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
            btnREC.setText("STOP");
            recording = true;
        } else {
            RequestPermissions();
        }
    }

    /** verification des permissions **/
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    /** demande des permissions si on ne les a pas **/
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(Annonce.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
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