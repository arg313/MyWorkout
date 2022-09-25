package com.rocasoftware.myworkout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.hardware.lights.Light;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText resetEmail;
    Button confirm,button2;
    FirebaseAuth localAuth;
    FirebaseFirestore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        store = FirebaseFirestore.getInstance();
        localAuth = FirebaseAuth.getInstance();

        resetEmail = findViewById(R.id.et_resetEmail);

        confirm = findViewById(R.id.bt_confirm);
        confirm.setOnClickListener(view -> {
            String userEmail = resetEmail.getText().toString();
            if (!userEmail.isEmpty()) {
                localAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this,"Por favor, revisa tu cuenta de correo",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),"Por favor, introduce un correo",Toast.LENGTH_SHORT).show();
            }
        });
    }
}