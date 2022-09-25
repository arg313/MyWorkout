package com.rocasoftware.myworkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText name, lastName, email, password, confirmPassword;
    Button signup;
    FirebaseAuth localAuth;
    FirebaseFirestore store;
    boolean valid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        localAuth = FirebaseAuth.getInstance();
        localAuth.setLanguageCode("es");
        store = FirebaseFirestore.getInstance();

        name = findViewById(R.id.et_name);
        lastName = findViewById(R.id.et_lastname);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirmPassword);
        signup = findViewById(R.id.bt_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.signup.setEnabled(false);
                checkField(name, "name");
                checkField(lastName, "lastName");
                checkField(email, "email");
                checkField(password, "password");

                if(valid) {
                    localAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnSuccessListener(authResult -> {
                                FirebaseUser user = localAuth.getCurrentUser();
                                DocumentReference dr = store.collection("Users").document(user.getEmail());
                                Map<String,Object> userInfo = new HashMap<>();
                                userInfo.put("Name",name.getText().toString());
                                userInfo.put("LastName", lastName.getText().toString());
                                userInfo.put("Email", email.getText().toString());
                                dr.set(userInfo);
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "No se ha podido registrar el usuario", Toast.LENGTH_SHORT).show();
                                    SignUpActivity.this.signup.setEnabled(true);
                                }
                            });
                } else {
                    SignUpActivity.this.signup.setEnabled(true);
                }
            }
        });
    }

    public boolean checkField(EditText textField, String type) {
        switch (type) {
            case "email":
                valid = true;
                if (textField.getText().toString().isEmpty()) {
                    textField.setError("Debe rellenar todos los campos");
                    valid = false;
                } else {
                    if (!isValidEmailAddress(textField.getText().toString())) {
                        textField.setError("Debe introducir un correo válido");
                        valid = false;
                    }
                }
                break;
            case "password":
                valid = true;
                if (textField.getText().toString().isEmpty()) {
                    textField.setError("Debe rellenar todos los campos");
                    valid = false;
                } else {
                    if (textField.getText().toString().length() < 6) {
                        textField.setError("Debe tener como mínimo 6 caracteres");
                        valid = false;
                    }
                }
                if (!textField.getText().toString().equals(confirmPassword.getText().toString())) {
                    valid = false;
                    confirmPassword.setError("La contraseña debe coincidir");
                }
                break;
            default:
                if(textField.getText().toString().isEmpty()) {
                    textField.setError("Debe rellenar todos los campos");
                    valid = false;
                } else {
                    valid = true;
                }
                break;
        }
        return valid;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}