package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BookAppointmentActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;

    private Spinner specializationSpinner;
    private Spinner medicalServiceSpinner;

    private Button nextButton;
    private Button backButton;

    private ArrayList<String> medicalServices;
    private ArrayList<String> allSpecializations;

    private String spec;
    private String medicalServiceToAdd;
    private String service;
    private String choice;
    private String username;
    private String username1;
    private String medicalServiceChoice;

    private ArrayAdapter<String> medicalServicesAdapter;

    private static int serviceNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_appointment_activity);

        Intent intent = getIntent();
        username1 = intent.getStringExtra("username");

        nextButton = findViewById(R.id.NextButton);
        backButton = findViewById(R.id.BackButtonAppointment);

        specializationSpinner = findViewById(R.id.SpecializationSpinner);
        medicalServiceSpinner = findViewById(R.id.MedicalServiceSpinner);

        allSpecializations = new ArrayList<>();
        medicalServices = new ArrayList<>();

        ArrayAdapter<String> specializationAdapter = new ArrayAdapter<String>(BookAppointmentActivity.this, R.layout.item_spinner, allSpecializations);
        specializationAdapter.setDropDownViewResource(R.layout.drop_down_item);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Services");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    spec = ds.child("specialization").getValue(String.class) + " -  Dr. " + ds.child("name").getValue(String.class);
                    Log.d("Denisa", spec);
                    allSpecializations.add(spec);
                }

                specializationSpinner.setAdapter(specializationAdapter);

                specializationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        choice = specializationSpinner.getSelectedItem().toString();

                        databaseReference1 = firebaseDatabase.getReference().child("Services");
                        ValueEventListener valueEventListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds1 : snapshot.getChildren()) {
                                    medicalServices = new ArrayList<>();
                                    if (choice.contains(ds1.child("name").getValue(String.class))){
                                        username = ds1.getKey();
                                        serviceNumber = 1;
                                        service = "service" + serviceNumber;
                                        while(ds1.child("medical services").hasChild(service)){
                                            medicalServiceToAdd = ds1.child("medical services").child(service).child("medicalService").getValue(String.class);
                                            medicalServices.add(medicalServiceToAdd);
                                            serviceNumber++;
                                            service = "service" + serviceNumber;
                                            medicalServiceToAdd = "";
                                        }
                                        medicalServicesAdapter = new ArrayAdapter<String>(BookAppointmentActivity.this, R.layout.item_spinner, medicalServices);
                                        medicalServicesAdapter.setDropDownViewResource(R.layout.drop_down_item);

                                        medicalServicesAdapter.notifyDataSetChanged();
                                        medicalServiceSpinner.setAdapter(medicalServicesAdapter);

                                        medicalServiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                medicalServiceChoice = medicalServiceSpinner.getSelectedItem().toString();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        databaseReference1.addListenerForSingleValueEvent(valueEventListener1);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        handleBackButton();
        handleNextButton();
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookAppointmentActivity.this, DoctorsListActivity.class);
                intent.putExtra("username", username1);
                startActivity(intent);
            }
        });
    }

    private void handleNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookAppointmentActivity.this, SelectDateActivity.class);
                intent.putExtra("doctorUsername",username);
                intent.putExtra("username", username1);
                intent.putExtra("medicalService", medicalServiceChoice);
                startActivity(intent);
            }
        });
    }
}