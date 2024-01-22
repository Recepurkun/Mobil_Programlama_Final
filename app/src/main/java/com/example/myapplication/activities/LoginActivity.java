package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.AddLabelFragment;
import com.example.myapplication.ui.AddPhotoFragment;
import com.example.myapplication.ui.GalleryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

                // Firestore sorgusu yaparak belgeyi alacağız
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersCollectionRef = db.collection("users");
                Query query = usersCollectionRef.whereEqualTo("email", strEmail );

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document2 : task.getResult()){
                            String firePass = document2.getString("password");
                            if (firePass != null && firePass.equals(strPassword)){
                                //diğer classlara intent ile giriş yapan kullanıcının bilgilerini gönderiyoruz
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                Intent intent2 = new Intent(getApplicationContext(), AddLabelFragment.class);
                                Intent intent3 = new Intent(getApplicationContext(), AddPhotoFragment.class);
                                Intent intent4 = new Intent(getApplicationContext(), GalleryFragment.class);
                                intent.putExtra("email", document2.getString("email"));
                                intent.putExtra("firstname", document2.getString("firstname"));
                                intent2.putExtra("email", document2.getString("email"));
                                intent2.putExtra("firstname", document2.getString("firstname"));
                                intent3.putExtra("email", document2.getString("email"));
                                intent3.putExtra("firstname", document2.getString("firstname"));
                                intent4.putExtra("email", document2.getString("email"));
                                intent4.putExtra("firstname", document2.getString("firstname"));
                                startActivity(intent);
                            }
                        }
                    }
                });


                /*auth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseUser kullanici = auth.getCurrentUser();
                            usersCollectionRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            String userEmail = kullanici.getEmail();
                                            Toast.makeText(getApplicationContext(), "UID!: " + uid , Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Hoşgeldiniz!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Email: " + userEmail, Toast.LENGTH_SHORT).show();
                                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Email ya da Parola Hatali", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Email ya da Parola Hatali", Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
            }
        });

    }
}