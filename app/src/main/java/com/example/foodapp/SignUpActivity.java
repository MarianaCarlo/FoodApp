package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView tittle;
    private EditText editFullName, editAge, editEmail, editPassword;
    private ProgressBar progressBar;
    private Button sendSingUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        editFullName = findViewById(R.id.editFullName);
        editAge = findViewById(R.id.editAge);
        editEmail = findViewById(R.id.editMail);
        editPassword = findViewById(R.id.editPassword);
        progressBar = findViewById(R.id.progressBar);

        tittle = findViewById(R.id.tittle);
        sendSingUp = findViewById(R.id.btnSendSignUp);

        tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        sendSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String fullName = editFullName.getText().toString().trim();
                String age = editAge.getText().toString().trim();

                if (fullName.isEmpty()) {
                    editFullName.setError("Full name is required!");
                    editFullName.requestFocus();
                    return;
                }

                if (age.isEmpty()) {
                    editAge.setError("Age is required!");
                    editAge.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editEmail.setError("Mail is required!");
                    editEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Please provide valid email");
                    editEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editPassword.setError("Password is required!");
                    editPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    editPassword.setError("Min password length should be 6 charaters!");
                    editPassword.requestFocus();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, age, email);

                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this, "Success! user registered", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Failed! TRY AGAIN", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "FAILED TO REGISTER", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }
}