package com.blogingthunder.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Student_DashBoard extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dash_board);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.imageMenue);
        logout.setOnClickListener(
                view -> {
                    firebaseAuth.signOut();
                    startActivity(new Intent(Student_DashBoard.this,MainActivity.class));
                    Toast.makeText(Student_DashBoard.this, "logout successfully compoleted" , Toast.LENGTH_SHORT).show();
                    finish();
                }
        );

    }
}