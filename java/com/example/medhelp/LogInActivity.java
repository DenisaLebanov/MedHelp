package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

public class LogInActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;
    private FirebaseDatabase firebaseDatabase1;

    private Button logInButton;
    private Button registerNowButton;
    private Button forgotPasswordButton;

    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText usernameText;
    private TextInputEditText passwordText;

    private String username;
    private String password;

    private boolean isPatient = false;
    private boolean isDoctor = false;
    private boolean isCheckedP = false;
    private boolean isCheckedD = false;

    private String hashedPassword;
    private String salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);

        logInButton = findViewById(R.id.LogInButton1);

        usernameLayout = findViewById(R.id.UsernameLayoutLogin);
        passwordLayout = findViewById(R.id.PasswordLayoutLogin);

        usernameText = findViewById(R.id.UsernameTextLogin);
        passwordText = findViewById(R.id.PasswordTextLogin);

        forgotPasswordButton = findViewById(R.id.ForgotPasswordButton);
        registerNowButton = findViewById(R.id.RegisterNowButton);

        handleLogInButton();
        handleForgotPasswordButton();
        handleRegisterNowButton();

    }

    public void handleLogInButton(){
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usernameLayout.setError(null);
                passwordLayout.setError(null);

                isPatient = true;
                isDoctor = true;

                username = usernameText.getText().toString().trim();
                password = passwordText.getText().toString().trim();

                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("Patients");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild(username)){
                            salt = snapshot.child(username).child("salt").getValue(String.class);
                            hashedPassword = BCrypt.hashpw(password, salt);
                            if (snapshot.child(username).child("username").getValue(String.class).equals(username) && snapshot.child(username).child("password").getValue(String.class).equals(hashedPassword)){
                                Intent intent = new Intent(LogInActivity.this, MainActivityPatients.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                            else if (snapshot.child(username).child("username").getValue(String.class).equals(username) && !snapshot.child(username).child("password").getValue(String.class).equals(hashedPassword)){
                                passwordLayout.setError("The password is incorrect");
                            }
                        }
                        else
                            isPatient = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa", "Eroare Log in pacient!");
                    }
                });


                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("Doctors");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(username)){
                            salt = snapshot.child(username).child("salt").getValue(String.class);
                            hashedPassword = BCrypt.hashpw(password, salt);
                            if (snapshot.child(username).child("username").getValue(String.class).equals(username) && snapshot.child(username).child("password").getValue(String.class).equals(hashedPassword)){
                                detectDoctorsType(username);
                            }
                            else if (snapshot.child(username).child("username").getValue(String.class).equals(username) && !snapshot.child(username).child("password").getValue(String.class).equals(hashedPassword)){
                                passwordLayout.setError("The password is incorrect");
                            }
                        }
                        else {
                            isDoctor = false;
                            if (!isPatient)
                                usernameLayout.setError("This account does not exist!");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa", "Eroare Log in doctor!");
                    }
                });
            }
        });
    }

    private void detectDoctorsType(String uname){
        firebaseDatabase1 = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference1 = firebaseDatabase1.getReference("PersonalDataDoctors").child(uname);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("specialization").getValue(String.class).equals("Emergency Medicine")){
                    Intent intent = new Intent(LogInActivity.this, EmergencyDoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(LogInActivity.this, DoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void handleForgotPasswordButton(){
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void handleRegisterNowButton(){
        registerNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, ChooseYourAccountTypeActivity.class);
                startActivity(intent);
            }
        });
    }
}