package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    Button loginBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.btnLogin);
        signUpBtn = findViewById(R.id.btnSignUp);
        email = findViewById(R.id.et_Email);
        password = findViewById(R.id.et_Password);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString().toLowerCase();
                String strPassword = password.getText().toString();

                if (strEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email alani bos olamaz!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (strPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Parola alani bos olamaz!!", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Hoşgeldiniz!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Email ya da Parola Hatali", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class ));
            }
        });

    }
}