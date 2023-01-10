package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDataPatientsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button saveButton;

    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;

    private TextInputLayout firstNameLayout;
    private TextInputLayout secondNameLayout;
    private TextInputLayout ageLayout;
    private TextInputLayout cnpLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout detailsLayout;

    private TextInputEditText firstNameText;
    private TextInputEditText secondNameText;
    private TextInputEditText ageText;
    private TextInputEditText cnpText;
    private TextInputEditText phoneText;
    private TextInputEditText detailsText;

    private String firstName;
    private String secondName;
    private String age;
    private String phone;
    private String cnp;
    private String details;
    private String gender;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data_patients_activity);

        firstNameLayout = findViewById(R.id.FirstNamePersonalDataPatientLayout);
        secondNameLayout = findViewById(R.id.SecondNamePersonalDataPatientLayout);
        ageLayout = findViewById(R.id.AgePersonalDataPatientLayout);
        phoneLayout = findViewById(R.id.PhonePersonalDataPatientLayout);
        cnpLayout = findViewById(R.id.CNPPersonalDataPatientLayout);
        detailsLayout = findViewById(R.id.AdditionalPersonalDataPatientLayout);

        firstNameText = findViewById(R.id.FirstNamePersonalDataPatientText);
        secondNameText = findViewById(R.id.SecondNamePersonalDataPatientText);
        ageText = findViewById(R.id.AgePersonalDataPatientText);
        phoneText = findViewById(R.id.PhonePersonalDataPatientText);
        cnpText = findViewById(R.id.CNPPersonalDataPatientText);
        detailsText = findViewById(R.id.AdditionalPersonalDataPatientText);

        femaleCheckBox = findViewById(R.id.FemaleCheckBoxPatient);
        maleCheckBox = findViewById(R.id.MaleCheckBoxPatient);

        saveButton = findViewById(R.id.SaveButtonPatient);

        handleCheckBoxFemale();
        handleCheckBoxMale();
        handleSaveButton();
    }

    public void handleSaveButton(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("PersonalDataPatients");

                firstName = firstNameText.getText().toString().trim();
                secondName = secondNameText.getText().toString().trim();
                cnp = cnpText.getText().toString().trim();
                age = ageText.getText().toString().trim();
                phone = phoneText.getText().toString().trim();
                details = detailsText.getText().toString().trim();

                Intent intent = getIntent();
                username = intent.getStringExtra("username");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        filterDetails();
                        PPersonalData data = new PPersonalData(firstName, secondName, cnp, age, phone, details, gender);
                        if(filterFirstName() && filterSecondName() && filterCNP() && filterAge() && filterPhone() && filterGender()) {
                            databaseReference.child(username).setValue(data);
                            Intent intent = new Intent(PersonalDataPatientsActivity.this, MainActivityPatients.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                        else
                            Log.d("Denisa", "Eroare scriere informatii aditionale pacient in baza de date!");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa", "Eroare scriere informatii aditionale pacient in baza de date!");
                    }
                });
            }
        });
    }

    public void handleCheckBoxFemale(){
        femaleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                gender = "";

                if (isChecked){
                    maleCheckBox.setChecked(false);
                    gender = "Female";
                }
            }
        });
    }

    public void handleCheckBoxMale(){
        maleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                gender = "";

                if(isChecked){
                    femaleCheckBox.setChecked(false);
                    gender = "Male";
                }
            }
        });
    }

    public boolean filterFirstName(){
        Pattern pattern = Pattern.compile("[A-Za-z]+");
        Matcher matcher = pattern.matcher(firstName);
        boolean isValid = matcher.matches();

        firstNameLayout.setError(null);

        if (firstName.isEmpty()) {
            firstNameLayout.setError("This field must be completed!");
            return false;
        }
        else if (!isValid){
            firstNameLayout.setError("Invalid characters!");
            return false;
        }
        else
            firstNameLayout.setError(null);
        return true;
    }

    public boolean filterSecondName(){
        Pattern pattern = Pattern.compile("[A-Za-z]+");
        Matcher matcher = pattern.matcher(secondName);
        boolean isValid = matcher.matches();

        secondNameLayout.setError(null);

        if (secondName.isEmpty()) {
            secondNameLayout.setError("This field must be completed!");
            return false;
        }
        else if (!isValid){
            secondNameLayout.setError("Invalid characters!");
            return false;
        }
        else
            secondNameLayout.setError(null);
        return true;
    }

    public boolean filterAge(){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(age);
        boolean isValid = matcher.matches();

        ageLayout.setError(null);

        if (age.isEmpty()){
            ageLayout.setError("This field must be completed!");
            return false;
        }

        if (!isValid){
            ageLayout.setError("Invalid characters!");
            return false;
        }

        else
            ageLayout.setError(null);
        return true;
    }

    public boolean filterPhone(){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(phone);
        boolean isValid = matcher.matches();

        phoneLayout.setError(null);

        if (phone.isEmpty()){
            phoneLayout.setError("This field must be completed!");
            return false;
        }

        if (!isValid){
            phoneLayout.setError("Invalid characters!");
            return false;
        }

        else
            phoneLayout.setError(null);
        return true;
    }

    public boolean filterCNP(){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(cnp);
        boolean isValid = matcher.matches();

        cnpLayout.setError(null);

        if (cnp.isEmpty()){
            cnpLayout.setError("This field must be completed!");
            return false;
        }

        if (!isValid){
            cnpLayout.setError("Invalid characters!");
            return false;
        }

        else
            cnpLayout.setError(null);
        return true;
    }

    public void filterDetails(){

        if (details.isEmpty())
            details = "No details";
    }

    public boolean filterGender(){
        if(!femaleCheckBox.isChecked() && !maleCheckBox.isChecked()) {
            femaleCheckBox.setTextColor(Color.RED);
            maleCheckBox.setTextColor(Color.RED);
            return false;
        }
        femaleCheckBox.setTextColor(Color.BLUE);
        maleCheckBox.setTextColor(Color.BLUE);
        return true;
    }
}