package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddServicesActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button addButton;
    private Button doneButton;

    private TextInputLayout medicalServiceLayout;
    private TextInputLayout priceLayout;

    private TextInputEditText medicalServiceText;
    private TextInputEditText priceText;

    private String medicalService;
    private String price;
    private String username;
    private String specialization;
    private String name;

    private boolean isPressed = false;
    private boolean isAlreadySet = false;

    private static int serviceNumber = 1;

    private String serviceNumberString = "";

    private boolean isEmergencyDoctor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_services_activity);

        medicalServiceLayout = findViewById(R.id.MedicalServiceLayout);
        priceLayout = findViewById(R.id.PriceLayout);

        medicalServiceText = findViewById(R.id.MedicalServiceText);
        priceText = findViewById(R.id.PriceText);

        addButton = findViewById(R.id.AddButton);
        doneButton = findViewById(R.id.DoneButton);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Intent intent1 = getIntent();
        specialization = intent1.getStringExtra("specialization");

        Intent intent2 = getIntent();
        name = intent2.getStringExtra("name");

        handleAddButton();
        handleDoneButton();
    }

    private void handleAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("Services");

                medicalService = medicalServiceText.getText().toString().trim();
                price = priceText.getText().toString().trim();
                isPressed = false;

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MedicalServices medicalServices = new MedicalServices(medicalService, price);
                        Services service = new Services(specialization, name);

                        if (service.getSpecialization().equals("Emergency Medicine"))
                            isEmergencyDoctor = true;

                        if(filterMedicalService() && filterPrice()){
                            serviceNumberString = "";
                            serviceNumberString = Integer.toString(serviceNumber);
                            if(!isPressed) {
                                if(!isAlreadySet) {
                                    databaseReference.child(username).setValue(service);
                                    isAlreadySet = true;
                                }

                                databaseReference.child(username).child("medical services").child("service" + serviceNumberString).setValue(medicalServices);
                                isPressed = true;
                                serviceNumber++;
                            }

                            medicalServiceText.setText("");
                            priceText.setText("");
                        }
                        else
                            Log.d("Denisa", "Eroare adaugare medical service!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa", "Eroare adaugare medical service!");
                    }
                });
            }
        });
    }

    private void handleDoneButton() {
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAlreadySet) {
                    if (isEmergencyDoctor) {
                        Intent intent = new Intent(AddServicesActivity.this, EmergencyDoctorsMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(AddServicesActivity.this, DoctorsMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(AddServicesActivity.this, "You have to add at least one medical service.", Toast.LENGTH_LONG).show();

            }
        });
    }

    private boolean filterMedicalService() {
        Pattern pattern = Pattern.compile("[A-Za-z ]+");
        Matcher matcher = pattern.matcher(medicalService);
        boolean isValid = matcher.matches();

        medicalServiceLayout.setError(null);

        if (medicalService.isEmpty()) {
            medicalServiceLayout.setError("This field must be completed!");
            return false;
        }
        else if (!isValid){
            medicalServiceLayout.setError("Invalid characters!");
            return false;
        }
        else
            medicalServiceLayout.setError(null);
        return true;
    }

    private boolean filterPrice(){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(price);
        boolean isValid = matcher.matches();

        priceLayout.setError(null);

        if (price.isEmpty()){
            priceLayout.setError("This field must be completed!");
            return false;
        }

        if (!isValid){
            priceLayout.setError("Invalid characters!");
            return false;
        }

        else
            priceLayout.setError(null);
        return true;
    }
}