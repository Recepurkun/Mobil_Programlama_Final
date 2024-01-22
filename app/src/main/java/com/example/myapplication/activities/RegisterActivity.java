package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity {

    EditText firstName, lastName, email, password;
    Button BtnSignup, BtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.et_reg_firstname);
        lastName = findViewById(R.id.et_reg_lastname);
        email = findViewById(R.id.et_reg_email);
        password = findViewById(R.id.et_reg_password);

        BtnSignup = findViewById(R.id.btn_reg_signup);
        BtnLogin = findViewById(R.id.btn_reg_login);

        BtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strFirstName = firstName.getText().toString();
                String strLastName = lastName.getText().toString();
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();

                if (strFirstName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ilk isim alani bos olamaz!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (strLastName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ikinci isim alani bos olamaz!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (strEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email alani bos olamaz!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (strPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Sifreni alani bos olamaz!", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference userCollectionRef = db.collection("users");

                            userCollectionRef.whereEqualTo("email", strEmail)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                if(task.getResult() != null && !task.getResult().isEmpty()){
                                                    Toast.makeText(getApplicationContext(), "Kayit basarisiz!", Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Users user = new Users(strFirstName,strLastName,strEmail,strPassword);
                                                    userCollectionRef.document(strEmail).set(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(getApplicationContext(), "Kaydiniz basariyla gerceklesti!", Toast.LENGTH_LONG).show();
                                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                                    }
                                                                    else{
                                                                        Toast.makeText(getApplicationContext(), "Kaydiniz ekleme işi başarısız!", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "Kayit basarisiz!", Toast.LENGTH_LONG).show();
                                            }
                                        };
                                    });
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Bu eposta adresi zaten kullanimda!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}