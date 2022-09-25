package com.rocasoftware.myworkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    Button signin, signup, resetPassword;
    EditText email, password;
    boolean valid = true;
    FirebaseAuth localAuth;
    FirebaseFirestore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localAuth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        signin = findViewById(R.id.signInBt);
        signup = findViewById(R.id.signUpBt);
        resetPassword = findViewById(R.id.resetPasswordBt);

        signup.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SignUpActivity.class);
            startActivity(intent);
        });

        resetPassword.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ResetPasswordActivity.class);
            startActivity(intent);
        });

        signin.setOnClickListener(view -> {
            LoginActivity.this.signin.setEnabled(false);
            LoginActivity.this.signup.setEnabled(false);
            checkField(email,"email");
            checkField(password,"password");

            if(valid) {
                localAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            System.out.println(e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            LoginActivity.this.signin.setEnabled(true);
                            LoginActivity.this.signup.setEnabled(true);
                        });
            } else {
                LoginActivity.this.signin.setEnabled(true);
                LoginActivity.this.signup.setEnabled(true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
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