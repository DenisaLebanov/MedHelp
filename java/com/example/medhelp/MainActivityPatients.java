package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivityPatients extends AppCompatActivity {

    private Button servicesButton;
    private Button emergencyButton;
    private Button pricesButton;
    private Button doctorsButton;
    private Button logoutButton;
    private Button checkDeletedAppointments;
    private Button myAppointmentsButton;
    private Button checkRescheduledAppointmentsButton;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;

    private TextView usernameText;

    private String username;

    private boolean existsDeletedAppointments = false;
    private boolean existsRescheduledAppointments = false;

    private boolean hasMoreThanOneChild = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_patients_activity);

        logoutButton = findViewById(R.id.LogoutPatientButton);
        servicesButton = findViewById(R.id.ServicesButton);
        emergencyButton = findViewById(R.id.EmergencyButton);
        pricesButton = findViewById(R.id.PricesButton);
        doctorsButton = findViewById(R.id.DoctorsButton);
        checkDeletedAppointments = findViewById(R.id.CheckDeletedAppointmentsEmergencyDoctors);
        myAppointmentsButton = findViewById(R.id.MyAppointmentsButton);
        checkRescheduledAppointmentsButton = findViewById(R.id.ViewRescheduledAppointmentsButton);

        checkDeletedAppointments.setBackgroundColor(Color.GREEN);
        checkRescheduledAppointmentsButton.setBackgroundColor(Color.GREEN);

        usernameText = findViewById(R.id.UsernamePatientText);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        usernameText.setText(username);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
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

        databaseReference = firebaseDatabase.getReference().child("RescheduledAppointments");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

        databaseReference1 = firebaseDatabase.getReference().child("Appointments");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        if (Objects.equals(dataSnapshot1.child("username").getValue(String.class), username)) {
                            hasMoreThanOneChild = true;
                            break;
                        }
                    }
                    if (hasMoreThanOneChild)
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        handleLogoutButton();
        handleServicesButton();
        handlePricesButton();
        handleEmergencyButton();
        handleDoctorsButton();
        handleCheckAppointmentsButton();
        handleMyAppointmentsButton();
        handleCheckRescheduledAppointmentsButton();
    }

    private void handleLogoutButton(){
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityPatients.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleServicesButton(){
        servicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityPatients.this, ServicesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void handlePricesButton(){
        pricesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityPatients.this, PricesListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void handleDoctorsButton(){
        doctorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityPatients.this, DoctorsListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void handleEmergencyButton(){
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityPatients.this, EmergencyActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void handleCheckAppointmentsButton(){
        checkDeletedAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existsDeletedAppointments) {
                    Intent intent = new Intent(MainActivityPatients.this, CheckDeletedAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "MainActivityPatients");
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivityPatients.this, "There are no deleted appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleMyAppointmentsButton(){
        myAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasMoreThanOneChild) {
                    Intent intent = new Intent(MainActivityPatients.this, ViewAppointmentsPatientsActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivityPatients.this, "There are no appointments yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCheckRescheduledAppointmentsButton(){
        checkRescheduledAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existsRescheduledAppointments) {
                    Intent intent = new Intent(MainActivityPatients.this, CheckReschedulesAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "MainActivityPatients");
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivityPatients.this, "There are no rescheduled appointments.", Toast.LENGTH_LONG).show();

            }
        });
    }
}