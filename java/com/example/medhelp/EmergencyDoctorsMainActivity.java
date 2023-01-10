package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmergencyDoctorsMainActivity extends AppCompatActivity {

    private Button logoutButton;
    private Button viewPatientsButton;
    private Button viewAppointmentsButton;
    private Button viewEmergenciesButton;
    private Button checkDeletedAppointments;
    private Button checkRescheduledAppointmentsButton;

    private TextView usernameTextView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;

    private String username;

    private long counter;

    private boolean existsDeletedAppointments = false;
    private boolean hasMoreThanOneChild = false;
    private boolean existsRescheduledAppointments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_doctors_main_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Emergencies");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counter = snapshot.getChildrenCount();
                if (counter > 1)
                    getNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usernameTextView = findViewById(R.id.EmergencyDoctorUsernameTextView);

        logoutButton = findViewById(R.id.EmergencyDoctorsLogoutButton);
        viewAppointmentsButton = findViewById(R.id.EmergencyDoctorsViewAppointmentsButton);
        viewPatientsButton = findViewById(R.id.EmergencyDoctorsViewPatientsButton);
        viewEmergenciesButton = findViewById(R.id.ViewEmergenciesDoctorButton);
        checkDeletedAppointments = findViewById(R.id.CheckDeletedAppointmentsEmergencyDoctors);
        checkRescheduledAppointmentsButton = findViewById(R.id.CheckRescheduledAppointmentsEmergencyDoctorsButton);

        checkDeletedAppointments.setBackgroundColor(Color.GREEN);
        checkRescheduledAppointmentsButton.setBackgroundColor(Color.GREEN);

        usernameTextView.setText(username);

        databaseReference = firebaseDatabase.getReference().child("DeletedAppointments");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("to").getValue(String.class).equals(username)) {
                        checkDeletedAppointments.setBackgroundColor(Color.RED);
                        existsDeletedAppointments = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference2 = firebaseDatabase.getReference().child("RescheduledAppointments");

        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("to").getValue(String.class).equals(username)) {
                        checkRescheduledAppointmentsButton.setBackgroundColor(Color.RED);
                        existsRescheduledAppointments = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference1 = firebaseDatabase.getReference().child("Appointments").child(username);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 1)
                    hasMoreThanOneChild = true;
                else
                    hasMoreThanOneChild = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        handleViewAppointmentsButton();
        handleViewPatientsButton();
        handleLogOutButton();
        handleEmergencyButton();
        handleCheckAppointmentsButton();
        handleCheckRescheduledAppointmentsButton();
    }

    private void getNotifications(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        long val = counter - 1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n").setContentText("Emergencies").setSmallIcon(R.drawable.logo).setAutoCancel(true).setContentText("There are " + val + " emergencies");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(999, builder.build());
    }

    private void handleViewAppointmentsButton(){
        checkDeletedAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existsDeletedAppointments) {
                    Intent intent = new Intent(EmergencyDoctorsMainActivity.this, CheckDeletedAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "EmergencyDoctorsMainActivity");
                    startActivity(intent);
                }
                else
                    Toast.makeText(EmergencyDoctorsMainActivity.this, "There are no deleted appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleViewPatientsButton(){
        viewPatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasMoreThanOneChild) {
                    Intent intent = new Intent(EmergencyDoctorsMainActivity.this, ViewPatientsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "EmergencyDoctorsMainActivity");
                    startActivity(intent);
                }
                else
                    Toast.makeText(EmergencyDoctorsMainActivity.this, "There are no patients yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogOutButton(){
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyDoctorsMainActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleEmergencyButton(){
        viewEmergenciesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter == 1)
                    Toast.makeText(EmergencyDoctorsMainActivity.this, "There are no emergencies at the moment.", Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(EmergencyDoctorsMainActivity.this, ViewEmergencyActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
    }

    private void handleCheckAppointmentsButton(){
        viewAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyDoctorsMainActivity.this, ViewAppointmentsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("activity", "EmergencyDoctorsMainActivity");
                startActivity(intent);
            }
        });
    }

    private void handleCheckRescheduledAppointmentsButton(){
        checkRescheduledAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existsRescheduledAppointments) {
                    Intent intent = new Intent(EmergencyDoctorsMainActivity.this, CheckReschedulesAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "EmergencyDoctorsMainActivity");
                    startActivity(intent);
                }
                else
                    Toast.makeText(EmergencyDoctorsMainActivity.this, "There are no rescheduled appointments.", Toast.LENGTH_LONG).show();

            }
        });
    }
}