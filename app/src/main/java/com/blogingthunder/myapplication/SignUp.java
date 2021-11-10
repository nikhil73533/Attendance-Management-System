package com.blogingthunder.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    Button signUpbutton;
    ActionCodeSettings actionCodeSettings;
    EditText fullName;
    EditText Email;
    EditText Id;
    ImageView googleSignUp;
    EditText Code;
    EditText Password;
    TextView gottoLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        fullName = findViewById(R.id.inputName);
        signUpbutton = findViewById(R.id.btnSignUp);
        googleSignUp = findViewById(R.id.googlesignOIn);
        gottoLogin = findViewById(R.id.gotoLogin);
        Email = findViewById(R.id.inputEmailSignUp);
        Id = findViewById(R.id.Id);
        Code = findViewById(R.id.code);
        Password = findViewById(R.id.passwordLogin);



        firebaseAuth = FirebaseAuth.getInstance();


        actionCodeSettings =  ActionCodeSettings.newBuilder().setUrl("https://www.example.com/finishSignUp?cartId=1234")
                 .setHandleCodeInApp(true).setIOSBundleId("com.example.ios")
                .setAndroidPackageName(
                        "com.example.android",
                        true, /* installIfNotAvailable */
                        "12"    /* minimumVersion */)
                .build();



        signUpbutton.setOnClickListener(
                view -> {
                    createuser();
                }
        );


        gottoLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent new_intent = new Intent(SignUp.this, MainActivity.class);
                        startActivity(new_intent);
                    }
                }
        );

googleSignUp.setOnClickListener(
        view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SignUp.this, MainActivity.class));
        }
);
    }
    private void createuser()
    {

        String name = fullName.getText().toString();
        String email = Email.getText().toString();
        String id= Id.getText().toString();
        String code = Code.getText().toString();
        String password = Password.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            fullName.setError("Name Can not be empty");
            fullName.requestFocus();
        }
        if(TextUtils.isEmpty(email))
        {
            Email.setError("Email can not be empty");
            Email.requestFocus();
        }

        if(TextUtils.isEmpty(id))
        {
            Id.setError("Id can not be empty");
            Id.requestFocus();
        }

         if(TextUtils.isEmpty(code))
        {
            Code.setError("Code can not be empty");
            Code.requestFocus();
        }

         if(TextUtils.isEmpty(password))
        {
            Password.setError("Password can not be empty");
            Password.requestFocus();
        }

        else
        {



            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    Toast.makeText(SignUp.this, "Registered Successfully: Please check your email for verifaction", Toast.LENGTH_LONG).show();
                                                    Email.setText("");
                                                    fullName.setText("");
                                                    Password.setText("");
                                                    Code.setText("");
                                                    Id.setText("");
                                                    startActivity(new Intent(SignUp.this,MainActivity.class));
                                                }
                                                else
                                                {
                                                    Toast.makeText(SignUp.this, "Get Exeception:- "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }


                                            }
                                        }
                                );

                            }
                            else
                            {
                                Toast.makeText(SignUp.this, "Get Exeception:- "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );

            progressBar.setVisibility(View.GONE);
        }

    }
}