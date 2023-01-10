package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CheckReschedulesAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rescheduledAppointmentsRecyclerView;

    private String username;
    private String activity;
    private String name;

    private Button backButton;

    private TextView usernameTextView;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;

    private ArrayList<String> oldDates = new ArrayList<>();
    private ArrayList<String> newDates = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    private CheckRescheduledAppointmentsAdapter checkRescheduledAppointmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_reschedules_appointments_activity);

        backButton = findViewById(R.id.BackButton8);

        usernameTextView = findViewById(R.id.UsernameRescheduleTextView);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        activity = intent.getStringExtra("activity");

        usernameTextView.setText(username);

        rescheduledAppointmentsRecyclerView = findViewById(R.id.CheckRescheduledAppointmentsDoctorRecyclerView);
        rescheduledAppointmentsRecyclerView.setHasFixedSize(true);
        rescheduledAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkRescheduledAppointmentsAdapter = new CheckRescheduledAppointmentsAdapter(CheckReschedulesAppointmentsActivity.this, oldDates, newDates, names, keys, activity, username);
        rescheduledAppointmentsRecyclerView.setAdapter(checkRescheduledAppointmentsAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("RescheduledAppointments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    if (Objects.equals(dataSnapshot.child("to").getValue(String.class), username)) {
                        oldDates.add(dataSnapshot.child("oldDate").getValue(String.class));
                        newDates.add(dataSnapshot.child("newDate").getValue(String.class));
                        usernames.add(dataSnapshot.child("from").getValue(String.class));
                        keys.add(dataSnapshot.getKey());
                    }
                }
                if (activity.equals("MainActivityPatients")) {

                    databaseReference1 = firebaseDatabase.getReference().child("PersonalDataDoctors");
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (int i = 0; i < usernames.size(); i++) {
                                name = snapshot.child(usernames.get(i)).child("firstName").getValue(String.class) + " " + snapshot.child(usernames.get(i)).child("secondName").getValue(String.class);
                                names.add(name);
                            }
                            checkRescheduledAppointmentsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    databaseReference1 = firebaseDatabase.getReference().child("PersonalDataPatients");
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (int i = 0; i < usernames.size(); i++) {
                                name = snapshot.child(usernames.get(i)).child("firstName").getValue(String.class) + " " + snapshot.child(usernames.get(i)).child("secondName").getValue(String.class);
                                names.add(name);
                            }
                            checkRescheduledAppointmentsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        handleBackButton();
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.equals("EmergencyDoctorsMainActivity")) {
                    Intent intent = new Intent(CheckReschedulesAppointmentsActivity.this, EmergencyDoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else if (activity.equals("DoctorsMainActivity")){
                    Intent intent = new Intent(CheckReschedulesAppointmentsActivity.this, DoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }

                else if (activity.equals("MainActivityPatients")){
                    Intent intent = new Intent(CheckReschedulesAppointmentsActivity.this, MainActivityPatients.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
    }
}