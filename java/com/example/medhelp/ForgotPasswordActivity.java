package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.spec.IvParameterSpec;

public class ForgotPasswordActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button sendButton;

    private TextInputLayout emailLayout;
    private TextInputEditText emailText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordText;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordText;

    private String email;
    private String password;
    private String confirmPassword;
    private String hashPassword;
    private String subject = "New password";
    private String salt;
    private String username;

    private boolean exists = false;
    private boolean exists1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        sendButton = findViewById(R.id.SendEmailButton);

        emailLayout = findViewById(R.id.ForgotEmailLayout);
        emailText = findViewById(R.id.ForgotEmailText);

        passwordLayout = findViewById(R.id.NewPasswordLayout);
        passwordText = findViewById(R.id.NewPasswordText);

        confirmPasswordLayout = findViewById(R.id.ConfirmNewPasswordLayout);
        confirmPasswordText = findViewById(R.id.ConfirmNewPasswordText);

        handleSendButton();
    }

    private void getNotifications(String password){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("m", "m", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "m").setContentText("Emergencies").setSmallIcon(R.drawable.password).setAutoCancel(true).setContentText("The new password is: " + password);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(999, builder.build());
    }

    public void handleSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailText.getText().toString().trim();
                password = passwordText.getText().toString().trim();
                confirmPassword = confirmPasswordText.getText().toString().trim();

                emailLayout.setError(null);
                passwordLayout.setError(null);

                exists = false;
                exists1 = false;

                if (confirmPassword.equals(password)) {

                    firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                    databaseReference = firebaseDatabase.getReference("Patients");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.child("email").getValue(String.class).equals(email)) {

                                    getNotifications(password);

                                    exists = true;

                                    username = dataSnapshot.child("username").getValue(String.class);

                                    salt = BCrypt.gensalt(12);
                                    hashPassword = BCrypt.hashpw(password, salt);

                                    Map<String, Object> passwordHashMap = new HashMap<>();
                                    passwordHashMap.put("password", hashPassword);

                                    Map<String, Object> saltHashMap = new HashMap<>();
                                    saltHashMap.put("salt", salt);

                                    dataSnapshot.getRef().updateChildren(passwordHashMap);
                                    dataSnapshot.getRef().updateChildren(saltHashMap);

                                    Intent intent = new Intent(ForgotPasswordActivity.this, LogInActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Denisa", "Eroare ForgotPasswordActivity trimitere email!");
                        }
                    });

                    databaseReference = firebaseDatabase.getReference("Doctors");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.child("email").getValue(String.class).equals(email)) {
                                    getNotifications(password);

                                    exists1 = true;

                                    username = dataSnapshot.child("username").getValue(String.class);

                                    salt = BCrypt.gensalt(12);
                                    hashPassword = BCrypt.hashpw(password, salt);

                                    Map<String, Object> passwordHashMap = new HashMap<>();
                                    passwordHashMap.put("password", hashPassword);

                                    Map<String, Object> saltHashMap = new HashMap<>();
                                    saltHashMap.put("salt", salt);

                                    dataSnapshot.getRef().updateChildren(passwordHashMap);
                                    dataSnapshot.getRef().updateChildren(saltHashMap);

                                    Intent intent = new Intent(ForgotPasswordActivity.this, LogInActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            }

                            if (!exists && !exists1)
                                emailLayout.setError("This email is not valid!");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Denisa", "Eroare ForgotPasswordActivity trimitere email!");
                        }
                    });
                }
                else
                    passwordLayout.setError("Passwords do not match!");
            }
        });
    }
}