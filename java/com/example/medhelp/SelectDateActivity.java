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

public class SelectDateActivity extends AppCompatActivity {

    private CalendarView calendar;

    private Button backButton;
    private Button nextButton;

    private String username;
    private String patientUsername;
    private String medicalService;

    private static String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_date_activity);

        calendar = findViewById(R.id.CalendarView);
        backButton = findViewById(R.id.BackButton2);
        nextButton = findViewById(R.id.NextButton2);

        Intent intent = getIntent();
        username = intent.getStringExtra("doctorUsername");

        Intent intent1 = getIntent();
        patientUsername = intent1.getStringExtra("username");

        Intent intent2 = getIntent();
        medicalService = intent2.getStringExtra("medicalService");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2 + "." + (i1 + 1) + "." + i;
            }
        });

        handleNextButton();
        handleBackButton();
    }

    private void handleNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectDateActivity.this, BookAppointmentTimeSlotActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("doctorUsername", username);
                intent.putExtra("username", patientUsername);
                intent.putExtra("activity", "SelectDateActivity");
                intent.putExtra("medicalService", medicalService);
                startActivity(intent);
            }
        });
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectDateActivity.this, BookAppointmentActivity.class);
                intent.putExtra("username", patientUsername);
                startActivity(intent);
            }
        });
    }
}