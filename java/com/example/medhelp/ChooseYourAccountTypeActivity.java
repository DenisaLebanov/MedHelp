package com.example.medhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseYourAccountTypeActivity extends AppCompatActivity {

    private Button doctorButton;
    private Button patientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_your_account_type_activity);

        doctorButton = findViewById(R.id.DoctorButton);
        patientButton = findViewById(R.id.PatientButton);

        handleDoctorButton();
        handlePatientButton();
    }

    public void handleDoctorButton(){
        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseYourAccountTypeActivity.this, DoctorSignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void handlePatientButton(){
        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseYourAccountTypeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}