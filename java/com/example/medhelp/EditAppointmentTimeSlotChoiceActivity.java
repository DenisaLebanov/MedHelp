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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditAppointmentTimeSlotChoiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private Button backButton;

    private ArrayList<String> timeSlotArray = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;

    private String doctorsUsername;
    private String username;
    private String newDate;
    private String oldDate;
    private String appointmentNumber;
    private String activity;

    private String compareString;
    private String type;

    private EditAppointmentTimeSlotAdapter editAppointmentTimeSlotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_appointment_time_slot_choice_activity);

        Intent intent = getIntent();
        doctorsUsername = intent.getStringExtra("doctorsUsername");

        Intent intent1 = getIntent();
        username = intent1.getStringExtra("username");

        Intent intent2 = getIntent();
        newDate = intent2.getStringExtra("newDate");

        Intent intent3 = getIntent();
        oldDate = intent3.getStringExtra("oldDate");

        Intent intent4 = getIntent();
        appointmentNumber = intent4.getStringExtra("appointmentNumber");
        type = intent4.getStringExtra("type");

        if (type.equals("doctor"))
            activity = intent4.getStringExtra("activity");

        backButton = findViewById(R.id.BackButton7);

        recyclerView = findViewById(R.id.EditAppointmentRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editAppointmentTimeSlotAdapter = new EditAppointmentTimeSlotAdapter(EditAppointmentTimeSlotChoiceActivity.this, timeSlotArray, newDate, doctorsUsername, appointmentNumber, username, type, activity);
        recyclerView.setAdapter(editAppointmentTimeSlotAdapter);

        timeSlotArray.add("08-09");
        timeSlotArray.add("09-10");
        timeSlotArray.add("10-11");
        timeSlotArray.add("11-12");
        timeSlotArray.add("12-13");
        timeSlotArray.add("13-14");
        timeSlotArray.add("14-15");
        timeSlotArray.add("15-16");

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference1 = firebaseDatabase.getReference().child("Appointments").child(doctorsUsername);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    compareString = dataSnapshot.child("date").getValue(String.class);
                    if (compareString.contains(newDate)) {
                        for (int i = 0; i < timeSlotArray.size(); i++) {
                            if (compareString.contains(timeSlotArray.get(i)))
                                timeSlotArray.remove(i);
                        }
                    }
                }
                editAppointmentTimeSlotAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(EditAppointmentTimeSlotChoiceActivity.this, EditAppointmentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("doctorUsername", doctorsUsername);
                intent.putExtra("date", oldDate);
                intent.putExtra("type", type);
                if (type.equals("doctor"))
                    intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
    }
}