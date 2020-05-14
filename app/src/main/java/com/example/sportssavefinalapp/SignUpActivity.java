package com.example.sportssavefinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private Button signUp;
    private EditText nameEditText, emailEditText, passwordEditText, ageEditText;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String name, email, passwordOne, age;
    String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUp = (Button) findViewById(R.id.signUpButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id. emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        registerUser();

    }
    private void registerUser(){
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                passwordOne = passwordEditText.getText().toString().trim();
                age = ageEditText.getText().toString().trim();
                if(name.isEmpty()){
                    nameEditText.setError("Name is required");
                    nameEditText.requestFocus();
                    return;
                }
                if(age.isEmpty()){
                    ageEditText.setError("Name is required");
                    ageEditText.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }
                if(passwordOne.isEmpty()){
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditText.setError("Please enter a valid email address");
                    emailEditText.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, passwordOne).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "User registration has been sucessful", Toast.LENGTH_SHORT);
                            lat = "0";
                            lng = "0";
                            Map<String, Object> users = new HashMap<>();
                            String authID = mAuth.getCurrentUser().getUid();
                            users.put("name", name);
                            users.put("age", age);
                            Map<String, Object> locationMap = new HashMap<>();
                            locationMap.put("lat", lat);
                            locationMap.put("long", lng);
                            db.collection("Users").document(authID).set(users);
                            db.collection("Location").document(authID).set(locationMap);
                            Intent c = new Intent(SignUpActivity.this, LoginActivity.class);
                            c.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(c);

                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignUpActivity.this, "This email is already registered", Toast.LENGTH_SHORT);
                            }
                            else{
                                Toast.makeText(SignUpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}
