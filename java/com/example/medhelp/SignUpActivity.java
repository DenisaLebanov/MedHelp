package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText usernameText;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private TextInputEditText confirmPasswordText;

    private TextView agree;

    private CheckBox checkBox;

    private Button signUpButton;

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String type;

    private boolean existsUsername = false;
    private boolean existsEmail = false;

    private String hashPassword;
    private String salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        signUpButton = findViewById(R.id.SignUpButton1);

        usernameLayout = (TextInputLayout) findViewById(R.id.UsernameLayout);
        emailLayout = (TextInputLayout) findViewById(R.id.EmailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.PasswordLayout);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.ConfirmPasswordLayout);

        usernameText = findViewById(R.id.UsernameText);
        emailText = findViewById(R.id.EmailText);
        passwordText = findViewById(R.id.PasswordText);
        confirmPasswordText = findViewById(R.id.ConfirmPasswordText);

        checkBox = findViewById(R.id.CheckBox);

        agree = findViewById(R.id.AgreeText);

        handleSignUpButton();
    }

    private void handleSignUpButton(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                username = usernameText.getText().toString().trim();
                email = emailText.getText().toString().trim();
                password = passwordText.getText().toString().trim();
                confirmPassword = confirmPasswordText.getText().toString().trim();
                type = "patient";

                salt = BCrypt.gensalt(12);
                hashPassword = BCrypt.hashpw(password, salt);

                existsUsername = false;
                existsEmail = false;

                usernameLayout.setError(null);
                emailLayout.setError(null);

                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("Patients");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChild(username)) {
                            existsUsername = true;
                            usernameLayout.setError("This username already exists!");
                        }

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("email").equals(email)) {
                                existsEmail = true;
                                emailLayout.setError("This email already exists!");
                            }
                        }

                        if(!existsUsername && !existsEmail){
                            usernameLayout.setError(null);

                            Patients patient = new Patients(username, email, hashPassword, type, salt);

                            if(filterUsername() && filterEmail() && filterPassword() && filterCheckBox()) {
                                databaseReference.child(username).setValue(patient);

                                Intent intent = new Intent(SignUpActivity.this, PersonalDataPatientsActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                            else Log.d("Denisa", "Eroare la completarea campurilor sign up pacienti!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa","Eroare la crearea contului sign up pacient!");
                    }
                });
            }
        });
    }

    private boolean filterUsername(){
        Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");
        Matcher matcher = pattern.matcher(username);
        boolean isValid = matcher.matches();

        usernameLayout.setError("");

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

        emailLayout.setError("");

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