package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    /** permissions **/

    // The request code used in ActivityCompat.requestPermissions()
    // and returned in the Activity's onRequestPermissionsResult()
    int PERMISSION_ALL = 1;
    String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
    };

    /** Variables globales
     * declaration des deux boutons
     * **/

    Button btnSenregistrer, btnSeConnecter;

    /** Initialisation et lien entre java et le design
     *
     * + creation du repertoire RepTel pour y mettre les fichiers sons de l'applications
     * (annonce + messages.)
     *
     * voir le test a faire dans Annonce.java  --> initUI()
     *
     *
     * **/

    private void initUI(){
        btnSenregistrer = findViewById(R.id.btnSenregistrer);
        btnSeConnecter = findViewById(R.id.btnSeConnecter);

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }
        /**
         * + creation du repertoire RepTel pour y mettre les fichiers sons de l'applications
         * (annonce + messages.)
         *
         * voir le test a faire dans Annonce.java  --> initUI()
         *
         * **/

        File repTel = new File(Environment.getExternalStorageDirectory() + "/Reptel");
        if (!repTel.isDirectory()){
            File repTelDirectory = new File(Environment.getExternalStorageDirectory() + "/RepTel/");
            repTelDirectory.mkdirs();
        }


        btnSenregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                String titre=btnSenregistrer.getText().toString();
                intent.putExtra("TitrePage", titre);
                startActivity(intent);
            }
        });

        btnSeConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                String titre=btnSeConnecter.getText().toString();
                intent.putExtra("TitrePage", titre);
                startActivity(intent);
            }
        });


    }
}