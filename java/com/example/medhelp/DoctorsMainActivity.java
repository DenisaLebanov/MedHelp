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

public class DoctorsMainActivity extends AppCompatActivity {

    private Button logoutButton;
    private Button viewPatientsButton;
    private Button viewAppointmentsButton;
    private Button checkDeletedAppointments;
    private Button checkRescheduledAppointmentsButton;

    private TextView usernameTextView;

    private String username;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;

    private boolean existsDeletedAppointments = false;
    private boolean hasMoreThanOneChild = false;
    private boolean existsRescheduledAppointments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_main_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        usernameTextView = findViewById(R.id.UsernameDoctorsTextView);

        logoutButton = findViewById(R.id.LogOutDoctorsButton);
        viewAppointmentsButton = findViewById(R.id.ViewAppointmentsDoctorsButton);
        viewPatientsButton = findViewById(R.id.ViewPatientsDoctorsButton);
        checkDeletedAppointments = findViewById(R.id.CheckDeletedAppointmentsEmergencyDoctors);
        checkRescheduledAppointmentsButton = findViewById(R.id.CheckRescheduledAppointmentsDoctorsButton);

        checkDeletedAppointments.setBackgroundColor(Color.GREEN);
        checkRescheduledAppointmentsButton.setBackgroundColor(Color.GREEN);

        usernameTextView.setText(username);

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
        handleCheckAppointmentsButton();
        handleCheckRescheduledAppointmentsButton();
    }

    private void handleViewAppointmentsButton(){
        viewAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorsMainActivity.this, ViewAppointmentsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("activity", "DoctorsMainActivity");
                startActivity(intent);
            }
        });
    }

    private void handleViewPatientsButton(){
        viewPatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasMoreThanOneChild) {
                    Intent intent = new Intent(DoctorsMainActivity.this, ViewPatientsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "DoctorsMainActivity");
                    startActivity(intent);
                }
                else
                    Toast.makeText(DoctorsMainActivity.this, "There are no patients yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogOutButton(){
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorsMainActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleCheckAppointmentsButton(){
        checkDeletedAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existsDeletedAppointments) {
                    Intent intent = new Intent(DoctorsMainActivity.this, CheckDeletedAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "DoctorsMainActivity");
                    startActivity(intent);
                }
                else
                    Toast.makeText(DoctorsMainActivity.this, "There are no deleted appointments.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void handleCheckRescheduledAppointmentsButton(){
        checkRescheduledAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (existsRescheduledAppointments) {
                    Intent intent = new Intent(DoctorsMainActivity.this, CheckReschedulesAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", "DoctorsMainActivity");
                    startActivity(intent);
                }
                else
                    Toast.makeText(DoctorsMainActivity.this, "There are no rescheduled appointments.", Toast.LENGTH_LONG).show();

            }
        });
    }
}