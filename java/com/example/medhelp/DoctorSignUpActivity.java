package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoctorSignUpActivity extends AppCompatActivity {

    private Button signUpButton;

    private TextInputLayout usernameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText usernameText;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private TextInputEditText confirmPasswordText;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private TextView agree;

    private CheckBox checkBox;

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String type = "doctor";

    private boolean existsUsername = false;
    private boolean existsEmail = false;

    private String hashPassword;
    private String salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_sign_up_activity);

        signUpButton = findViewById(R.id.DoctorSignUpButton);

        usernameLayout = findViewById(R.id.DoctorUsernameLayout);
        emailLayout = findViewById(R.id.DoctorEmailLayout);
        passwordLayout = findViewById(R.id.DoctorPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.DoctorConfirmPasswordLayout);

        usernameText = findViewById(R.id.DoctorUsernameText);
        emailText = findViewById(R.id.DoctorEmailText);
        passwordText = findViewById(R.id.DoctorPasswordText);
        confirmPasswordText = findViewById(R.id.DoctorConfirmPasswordText);

        checkBox = findViewById(R.id.DoctorCheckBox);

        agree = findViewById(R.id.DoctorAgreement);

        handleSignUpButton();
    }

    private void handleSignUpButton(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameText.getText().toString().trim();
                email = emailText.getText().toString().trim();
                password = passwordText.getText().toString().trim();
                confirmPassword = confirmPasswordText.getText().toString().trim();

                existsUsername = false;
                existsEmail = false;

                usernameLayout.setError(null);
                emailLayout.setError(null);

                salt = BCrypt.gensalt(12);
                hashPassword = BCrypt.hashpw(password, salt);

                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("Doctors");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChild(username)){
                            existsUsername = true;
                            usernameLayout.setError("This username already exists!");
                        }

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("email").equals(email)){
                                existsEmail = true;
                                emailLayout.setError("This email already exists!");
                            }
                        }

                        if (!existsUsername && !existsEmail) {
                            usernameLayout.setError(null);
                            emailLayout.setError(null);

                            Doctors doctor = new Doctors(username, email, hashPassword, type, salt);

                            if (filterUsername() && filterEmail() && filterPassword() && filterCheckBox()) {
                                databaseReference.child(username).setValue(doctor);

                                Intent intent = new Intent(DoctorSignUpActivity.this, PersonalDataDoctorsActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            } else
                                Log.d("Denisa", "Eroare la completarea campurilor sign up doctori!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa","Eroare la crearea contului de doctor sign up!");
                    }
                });
            }
        });
    }

    private boolean filterUsername(){
        Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");
        Matcher matcher = pattern.matcher(username);
        boolean isValid = matcher.matches();

        usernameLayout.setError(null);

        if(username.isEmpty()) {
            usernameLayout.setError("This field must be completed!");
            return false;
        }
        else if(username.contains(" ")){
            usernameLayout.setError("Whitespaces are not allowed!");
            return false;
        }
        else if(!isValid){
            usernameLayout.setError("Invalid characters!");
            return false;
        }
        else
            usernameLayout.setError(null);
        return true;
    }

    private boolean filterEmail(){

        emailLayout.setError(null);

        if(email.isEmpty()) {
            emailLayout.setError("Complete this field");
            return false;
        }
        else if(email.contains(" ")){
            emailLayout.setError("Whitespaces are not allowed!");
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid characters!");
            return false;
        }
        else
            emailLayout.setError(null);
        return true;
    }

    private boolean filterPassword(){
        Pattern pattern = Pattern.compile("[A-Za-z0-9!?+-_.~/]+");
        Matcher matcher = pattern.matcher(password);
        boolean isValid = matcher.matches();

        passwordLayout.setError(null);

        if(password.isEmpty()) {
            passwordLayout.setError("Complete this field");
            return false;
        }
        else if(password.contains(" ")){
            passwordLayout.setError("Whitespaces are not allowed!");
            return false;
        }
        else if(!password.equals(confirmPassword)){
            passwordLayout.setError("The passwords do not match!");
            return false;
        }
        else if(!isValid){
            passwordLayout.setError("Invalid characters!");
            return false;
        }
        else if(password.length() < 8){
            passwordLayout.setError("Password must be at least 8 characters long!");
            return false;
        }
        else
            passwordLayout.setError(null);
        return true;
    }

    private boolean filterCheckBox(){
        if(!checkBox.isChecked()) {
            agree.setTextColor(Color.RED);
            return false;
        }
        agree.setTextColor(Color.BLUE);
        return true;
    }
}