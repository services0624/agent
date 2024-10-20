package com.example.agent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agent.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {
    EditText user, pass, confirm, phone;

    Button signup, googleSignInButton;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmpassword);
        phone = findViewById(R.id.phone);
        signup = findViewById(R.id.signup);
        googleSignInButton = findViewById(R.id.google_signup);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = user.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String confirmPassword = confirm.getText().toString().trim();
                String Phone = phone.getText().toString().trim();


                if (TextUtils.isEmpty(userName)) {
                    user.setError("No Email Provided!");
                } else if (TextUtils.isEmpty(password)) {
                    pass.setError("No Password Provided!");
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    confirm.setError("No Confirm Password Provided!");
                } else if (TextUtils.isEmpty(Phone)) {
                    phone.setError("No Phone Number Provided!");}
                else if (!password.equals(confirmPassword)) {
                    confirm.setError("Passwords do not match!");
                } else if (!isValidEmail(userName)) {
                    user.setError("Invalid Email Format!");
                } else {
                    authenticationCheck(userName, password);
                }
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void authenticationCheck(String userName, String password) {
        signup.setEnabled(false); // Disable button to prevent multiple clicks
        mAuth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signup.setEnabled(true); // Re-enable button

                if (task.isSuccessful()) {
                    // Send verification email
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(signup.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                    String currentUserID = user.getUid();
                                    HashMap<String, Object> userdataMap = new HashMap<>();
                                    userdataMap.put("email", userName);
                                    databaseReference.child("users").child(currentUserID).updateChildren(userdataMap);

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(signup.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String currentUserID = user.getUid();
                            HashMap<String, Object> userdataMap = new HashMap<>();
                            userdataMap.put("email", user.getEmail());
                            // Store additional user data if needed
                            databaseReference.child("users").child(currentUserID).updateChildren(userdataMap);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(signup.this, "Google Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }
}
