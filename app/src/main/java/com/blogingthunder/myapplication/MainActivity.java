package com.blogingthunder.myapplication;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.auth.User;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private ImageView signInButton;
    private EditText Email;
    CallbackManager callbackManager;
    private EditText Password;
    private ImageView facebookLogin;
    private Button btnlogin;
    private TextView gotoRegister;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    private TextView forgotPassword;
    private GoogleApiClient googleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        forgotPassword = findViewById(R.id.forgotPassword);
        facebookLogin=  findViewById(R.id.facebookLogin);
        btnlogin = findViewById(R.id.btnLogin);
        Email = findViewById(R.id.inputEmail);
        gotoRegister = findViewById(R.id.gotoRegister);
        Password= findViewById(R.id.inputPassword);

        //Initializing the inflater
        inflater = this.getLayoutInflater();
        // This is line removes the default header of app
        getSupportActionBar().hide();
        // Hide the upper header of mobile.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing the alert diloag
         reset_alert = new AlertDialog.Builder(MainActivity.this);

        // Configure sign-in to request the user's ID, email address, and basic
        //  profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // In this code we configure google sign in to request user's Id email address, and basic profile.

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();



        //configure forgot poassword textview
        forgotPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            View v = inflater.inflate(R.layout.activity_forgot_password,null);

                        reset_alert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //first validate the email
                                EditText e = v.findViewById(R.id.resetEmail);
                                if(TextUtils.isEmpty(e.getText().toString()))
                                {
                                    e.setError("Enter the email");
                                    e.requestFocus();
                                    return;
                                }
                                else
                                {
                                   firebaseAuth.sendPasswordResetEmail(e.getText().toString()).addOnSuccessListener(
                                           new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   Toast.makeText(MainActivity.this, "Email has sent", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                   ).addOnFailureListener(
                                           new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   Toast.makeText(MainActivity.this, "Error detected " +e, Toast.LENGTH_SHORT).show();

                                               }
                                           }
                                   );

                                }

                            }
                        }).setNegativeButton("Cancle",null).setView(v).create().show();

                    }
                }
        );



        // Initialize Facebook Login button
       facebookLogin.setOnClickListener(
               new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       facebookLogin();
                   }
               }
       );
// ...














        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.googleLogin);
        // assign the on click listener on the sign In button.

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);

        signInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.googleLogin:
                                signIn();
                                break;
                        }
                    }
                }
        );

        gotoRegister.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,SignUp.class));
        });
        btnlogin.setOnClickListener(view -> {
            getlogin();
        });

    }


    // Check for existing Google Sign In account, if the user is already signed in
    // the GoogleSignInAccount will be non-null.
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {

            if(firebaseAuth.getCurrentUser().isEmailVerified())
            {
                Toast.makeText(MainActivity.this, "User Login Successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this,Student_DashBoard.class));
                finish();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Please Verify your email", Toast.LENGTH_SHORT).show();
            }


        }

    }

    // Set the dimensions of the sign-in button.

public void updateUI(FirebaseUser user)
{
    if (user != null) {
        // User is signed in
        // you could place other firebase code
        //logic to save the user details to Firebase
        Intent new_intent = new Intent(MainActivity.this, Student_DashBoard.class);
        startActivity(new_intent);
        Toast.makeText(MainActivity.this, "User Login Successfully", Toast.LENGTH_LONG).show();

        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
    } else {
        // User is signed out
        Toast.makeText(MainActivity.this, "you have to sign up ", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onAuthStateChanged:signed_out");
    }
}

    // SignIn method satarts the new activity
    private void signIn() {
        // created the signinIntent object using google sign in client.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        // Pass the activity result back to the Facebook SDK
//        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    //Get Login that login the user with email and password
    private void getlogin()
    {
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Email.setError("Email can not be empty");
            Email.requestFocus();
        }
        else if(TextUtils.isEmpty(password))
        {
            Password.setError("Password can't be empty");
            Password.requestFocus();
        }
        else
        {
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                if(firebaseAuth.getCurrentUser().isEmailVerified())
                                {
                                    Toast.makeText(MainActivity.this, "User Login Successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this,Student_DashBoard.class));
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Please Verify your email", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Get Exeception:- "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );
        }

    }



    //facebook login
    private void facebookLogin()
    {
         String EMAIL = "email";
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }



}
