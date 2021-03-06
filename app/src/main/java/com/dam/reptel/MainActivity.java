package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

/**
 * Main Activity
 * Ecran d'accueil de l'application avec direction vers Signup ou Signin
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    /** permissions **/

    // The request code used in ActivityCompat.requestPermissions()
    // and returned in the Activity's onRequestPermissionsResult()
    int PERMISSION_ALL = 1;
    String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
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


    /** ajout de FirebaseAuth pour enregistrer l'utilisateur **/
    private FirebaseAuth firebaseAuth;

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
        firebaseAuth= FirebaseAuth.getInstance();
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

        /**
         * ici test ci le currentUser est null, on s'inscrit sinon on va vers l'ecran suivant.
         *
         */
        FirebaseUser currentUser = null;
        String currentUid = firebaseAuth.getUid();
        currentUser= firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.i(TAG, "onCreate(), current user = " + currentUser);
            Log.i(TAG, "onCreate(), current user Id = " + currentUid);

            Intent intent1 = new Intent(MainActivity.this, Parametres.class);
            startActivity(intent1);
        }


        /**
        * setOnClickListener s'enregistrer
        */

        btnSenregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupEmail.class);
                String titre=btnSenregistrer.getText().toString();
                intent.putExtra("TitrePage", titre);
                startActivity(intent);
            }
        });

        /**
        * setOnClickListener se connecter
        */

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