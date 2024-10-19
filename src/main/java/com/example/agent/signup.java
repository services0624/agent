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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signup extends AppCompatActivity {
    EditText user, pass, confirm , phone;
    String userName, Password, ConfirmPassword , Phone;
    Button signup;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirmpassword);
        signup = (Button) findViewById(R.id.signup);
        phone=(EditText) findViewById(R.id.phone);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = user.getText().toString().trim();
                Password = pass.getText().toString().trim();
                ConfirmPassword = confirm.getText().toString().trim();
                Phone = phone.getText().toString().trim();


                if (userName.equals("")) {
                    user.setError("No Email Provided!");
                } else if (Password.equals("")) {
                    pass.setError("No Password Provided!");
                } else if (ConfirmPassword.equals("")) {
                    confirm.setError("No Confirm Password Provided!");
                } else if (Phone.equals("")) {
                        phone.setError("No Phone Number Provided!");
                } else {
                    authenticationcheck();
                }
            }
        });
    }

    private void authenticationcheck() {
        mAuth.createUserWithEmailAndPassword(userName, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(signup.this, "Registration Successful.", Toast.LENGTH_SHORT).show();


                    String currentUserID = mAuth.getCurrentUser().getUid();

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", userName);
                    userdataMap.put("password", Password);
                    userdataMap.put("phone", Phone);
                    databaseReference.child("users").child(currentUserID).updateChildren(userdataMap);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //if sign in fails ,display a msg to user
                    Toast.makeText(signup.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

