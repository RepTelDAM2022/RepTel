package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    /** Variables globales
     * declaration des deux boutons
     * **/

    Button btnSenregistrer, btnSeConnecter;

    /** Initialisation et lien entre java et le design **/

    private void initUI(){
        btnSenregistrer = findViewById(R.id.btnSenregistrer);
        btnSeConnecter = findViewById(R.id.btnSeConnecter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

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