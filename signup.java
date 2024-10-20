package com.example.agent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signup extends AppCompatActivity {
    EditText user, pass, confirm, phone;
    String userName, Password, ConfirmPassword, Phone;
    Button signup, googleSignup;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmpassword);
        phone = findViewById(R.id.phone);
        signup = findViewById(R.id.signup);
        googleSignup = findViewById(R.id.google_signup);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = user.getText().toString().trim();
                Password = pass.getText().toString().trim();
                ConfirmPassword = confirm.getText().toString().trim();
                Phone = phone.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    user.setError("No Email Provided!");
                } else if (TextUtils.isEmpty(Password)) {
                    pass.setError("No Password Provided!");
                } else if (TextUtils.isEmpty(ConfirmPassword)) {
                    confirm.setError("No Confirm Password Provided!");
                } else if (TextUtils.isEmpty(Phone)) {
                    phone.setError("No Phone Number Provided!");
                } else if (!Password.equals(ConfirmPassword)) {
                    confirm.setError("Passwords do not match!");
                } else {
                    authenticationcheck();
                }
            }
        });

        googleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Password = pass.getText().toString().trim();
                ConfirmPassword = confirm.getText().toString().trim();
                if (TextUtils.isEmpty(Password)||TextUtils.isEmpty(ConfirmPassword)) {
                    pass.setError("Please enter password");
                    confirm.setError("Please enter confirm password");
                } else if (!Password.equals(ConfirmPassword)) {
                    confirm.setError("Passwords do not match!");
                }else {
                signInWithGoogle();}
            }
        });
    }

    private void authenticationcheck() {
        mAuth.createUserWithEmailAndPassword(userName, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;

                    // Store user data in Firebase Database
                    String currentUserID = user.getUid();
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", userName);
                    userdataMap.put("password", Password);
                    userdataMap.put("phone", Phone);
                    databaseReference.child("users").child(currentUserID).updateChildren(userdataMap);

                    // Redirect to a different activity (e.g., MainActivity)
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(signup.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Google Sign-In was successful, authenticate with Firebase
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Get ID token from Google account
        String idToken = acct.getIdToken();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign-in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;

                    // Store user data in Firebase Database
                    String currentUserID = user.getUid();
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", user.getEmail());
                    userdataMap.put("password", Password); // Store the entered phone number
                    databaseReference.child("users").child(currentUserID).updateChildren(userdataMap);

                    // Redirect to a different activity (e.g., MainActivity)
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(signup.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
