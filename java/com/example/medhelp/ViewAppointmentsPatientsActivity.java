package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.Objects;

public class ViewAppointmentsPatientsActivity extends AppCompatActivity {

    private Button backButton;

    private RecyclerView recyclerView;

    private TextView usernameTextView;

    private String username;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> medicalServices = new ArrayList<>();

    private ViewAppointmentsPatientsAdapter viewAppointmentsPatientsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_appointments_patients_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        recyclerView = findViewById(R.id.PatientsViewAppointmentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewAppointmentsPatientsAdapter = new ViewAppointmentsPatientsAdapter(ViewAppointmentsPatientsActivity.this, dates, usernames,medicalServices, username);
        recyclerView.setAdapter(viewAppointmentsPatientsAdapter);

        backButton = findViewById(R.id.BackButton5);

        usernameTextView = findViewById(R.id.UsernamePatientViewAppointments);
        usernameTextView.setText(username);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Appointments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        if (Objects.equals(ds.child("username").getValue(String.class), username)){
                            usernames.add(dataSnapshot.getKey());
                            dates.add(ds.child("date").getValue(String.class));
                            medicalServices.add(ds.child("medicalService").getValue(String.class));
                        }
                    }
                }
                if (usernames.size() != 0)
                    viewAppointmentsPatientsAdapter.notifyDataSetChanged();
                else{
                    Toast.makeText(ViewAppointmentsPatientsActivity.this, "There are no doctor's appointments yet.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewAppointmentsPatientsActivity.this, MainActivityPatients.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
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
                Intent intent = new Intent(ViewAppointmentsPatientsActivity.this, MainActivityPatients.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}