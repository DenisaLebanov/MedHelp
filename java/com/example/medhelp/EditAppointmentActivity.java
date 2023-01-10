package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAppointmentActivity extends AppCompatActivity {

    private CalendarView calendar;

    private Button backButton;
    private Button nextButton;

    private String username;
    private String patientUsername;
    private String oldDate;

    private String date;
    private String appointmentNumber;
    private String type;
    private String activity;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_appointment_activity);

        backButton = findViewById(R.id.BackRescheduleButton);
        nextButton = findViewById(R.id.NextRescheduleButton);

        calendar = findViewById(R.id.CalendarViewReschedule);

        Intent intent = getIntent();
        username = intent.getStringExtra("doctorUsername");

        Intent intent1 = getIntent();
        patientUsername = intent1.getStringExtra("username");

        Intent intent2 = getIntent();
        oldDate = intent2.getStringExtra("date");

        Intent intent3 = getIntent();
        type = intent3.getStringExtra("type");

        if (type.equals("doctor"))
            activity = intent3.getStringExtra("activity");

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Appointments").child(username);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (dataSnapshot.child("username").getValue(String.class).equals(patientUsername) && dataSnapshot.child("date").getValue(String.class).equals(oldDate))
                        appointmentNumber = dataSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2 + "." + (i1 + 1) + "." + i;
            }
        });

        handleNextButton();
        handleBackButton();
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("patient")) {
                    Intent intent = new Intent(EditAppointmentActivity.this, ViewAppointmentsPatientsActivity.class);
                    intent.putExtra("username", patientUsername);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(EditAppointmentActivity.this, ViewAppointmentsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("activity", activity);
                    startActivity(intent);
                }
            }
        });
    }

    private void handleNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditAppointmentActivity.this, EditAppointmentTimeSlotChoiceActivity.class);
                intent.putExtra("username", patientUsername);
                intent.putExtra("doctorsUsername", username);
                intent.putExtra("newDate", date);
                intent.putExtra("oldDate", oldDate);
                intent.putExtra("appointmentNumber", appointmentNumber);
                intent.putExtra("type", type);
                if (type.equals("doctor"))
                    intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
    }
}