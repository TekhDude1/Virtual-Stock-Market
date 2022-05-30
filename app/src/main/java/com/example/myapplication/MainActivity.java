package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    ImageView splasher1;
    TextView splasher2;
    int autologin=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        splasher1=findViewById(R.id.splasher1);
        splasher2=findViewById(R.id.splasher2);

        splasher1.setAnimation(topAnim);
        splasher2.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AutoLogin();
            }
        },1500);

    }
    private void AutoLogin()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);

        String auto_phoneno = sharedPreferences.getString("phoneno","");
        String auto_password = sharedPreferences.getString("password","");
        Log.d("saved phoneno",auto_phoneno);
        Log.d("saved password",auto_password);
        DatabaseReference reference = FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        Query checkUsername= reference.orderByChild("phoneno").equalTo(auto_phoneno);
        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String database_password = snapshot.child(auto_phoneno).child("password").getValue(String.class);

                    if(database_password.equals(auto_password))
                    {
                        //Intent intent = new Intent(MainActivity.this,testing_page.class);
                        Intent intent = new Intent(MainActivity.this,Dashboard.class);
                        intent.putExtra("phoneno",auto_phoneno);
                        intent.putExtra("startingpage","1");
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Intent intent = new Intent(MainActivity.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}