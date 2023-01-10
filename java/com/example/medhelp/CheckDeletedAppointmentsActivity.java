package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class CheckDeletedAppointmentsActivity extends AppCompatActivity {

    private TextView usernameTextView;

    private RecyclerView recyclerView;

    private Button backButton;

    private String username;
    private String activity;

    private DatabaseReference databaseReference1;
    private FirebaseDatabase firebaseDatabase;

    private ArrayList<String> from = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();

    private CheckDeletedAppointmentsAdapter checkDeletedAppointmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_deleted_appointments_activity);

        backButton = findViewById(R.id.BackButton4);
        usernameTextView = findViewById(R.id.UsernameCheckAppointments);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        activity = intent.getStringExtra("activity");

        usernameTextView.setText(username);

        recyclerView = findViewById(R.id.CheckDeletedAppointmentsDoctorRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkDeletedAppointmentsAdapter = new CheckDeletedAppointmentsAdapter(CheckDeletedAppointmentsActivity.this, dates, from, username, activity);
        recyclerView.setAdapter(checkDeletedAppointmentsAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference1 = firebaseDatabase.getReference().child("DeletedAppointments");

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long number = snapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    number--;
                    if (Objects.equals(dataSnapshot.child("to").getValue(String.class), username)) {
                        dates.add(dataSnapshot.child("date").getValue(String.class));
                        from.add(dataSnapshot.child("from").getValue(String.class));
                    }
                    if (number == 0){
                        checkDeletedAppointmentsAdapter.notifyDataSetChanged();
                    }
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
                    Intent intent = new Intent(CheckDeletedAppointmentsActivity.this, EmergencyDoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else if (activity.equals("DoctorsMainActivity")){
                    Intent intent = new Intent(CheckDeletedAppointmentsActivity.this, DoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }

                else if (activity.equals("MainActivityPatients")){
                    Intent intent = new Intent(CheckDeletedAppointmentsActivity.this, MainActivityPatients.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
    }
}