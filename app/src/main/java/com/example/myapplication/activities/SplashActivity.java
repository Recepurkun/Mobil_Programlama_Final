package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth auth; //classtan nesne referansı tanımladık

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //nesne referansına firebase'nin instance'sini aldık
        auth = FirebaseAuth.getInstance();
        auth.signOut(); // çıkış yapsın

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(auth.getCurrentUser() != null){
                    Toast.makeText(getApplicationContext(), "Anasayfaya yönlendiriliyorsunuz", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Lütfen giriş yapınız", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(new Intent(getApplicationContext(), LoginActivity.class)));
                    finish();
                }
            }
        },2500);


    }
}