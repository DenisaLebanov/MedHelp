package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookAppointmentTimeSlotActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private RecyclerView recyclerView;
    private Button backButton;
    private Button bookButton;

    private TimeSlotsAdapter timeSlotsAdapter;

    private ArrayList<String> slots = new ArrayList<>();

    private String date;
    private String username;
    private String value;
    private String compareString;
    private String finalAppointment;
    private String activity;
    private String patientUsername;
    private String medicalService;

    private int number = 1;

    private boolean exists = false;

    private View popupView;

    private Button okButton;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_appointment_time_slot_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("doctorUsername");

        Intent intent1 = getIntent();
        date = intent1.getStringExtra("date");

        Intent intent3 = getIntent();
        activity = intent3.getStringExtra("activity");

        Intent intent4 = getIntent();
        patientUsername = intent4.getStringExtra("username");

        Intent intent5 = getIntent();
        medicalService = intent5.getStringExtra("medicalService");

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");

        recyclerView = findViewById(R.id.BookAppointmentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.BackButton3);
        bookButton = findViewById(R.id.BookButton3);

        databaseReference = firebaseDatabase.getReference().child("Appointments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slots.add("08-09");
                slots.add("09-10");
                slots.add("10-11");
                slots.add("11-12");
                slots.add("12-13");
                slots.add("13-14");
                slots.add("14-15");
                slots.add("15-16");
                if (snapshot.hasChild(username)) {
                    exists = true;
                    number = 0;
                    for (DataSnapshot dataSnapshot : snapshot.child(username).getChildren()) {
                        if (snapshot.child(username).getChildrenCount() == 1)
                            finalAppointment = "appointment1";
                        while (dataSnapshot.getKey().equals("appointment" + number)){
                            number++;
                        }

                        if (!dataSnapshot.getKey().equals("appointment0")) {
                            if (dataSnapshot.child("date").getValue(String.class).contains(date)) {
                                value = dataSnapshot.child("date").getValue(String.class);
                                compareString = value.substring(value.length() - 5);
                                if (slots.contains(compareString))
                                    slots.set(slots.indexOf(compareString), "Booked");
                            }
                        }
                        finalAppointment = "appointment" + number;
                        Log.d("Denisa", finalAppointment + " Se face programarea nr:");
                        timeSlotsAdapter = new TimeSlotsAdapter(BookAppointmentTimeSlotActivity.this, slots, date, username, finalAppointment, medicalService, patientUsername);
                        timeSlotsAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(timeSlotsAdapter);

                    }
                }

                if(!exists){
                    Appointments newAppointment = new Appointments("0", "0", "0");
                    databaseReference.child(username).child("appointment0").setValue(newAppointment);
                    exists = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        handleBackButton();
        handleBookButton();
    }

    private void handleBookButton(){
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.equals("TimeSlotsAdapter")) {
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    popupView = layoutInflater.inflate(R.layout.appointment_booked_popup, null);

                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                    popupWindow = new PopupWindow(popupView, width, height, true);

                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    View background = (View) popupWindow.getContentView().getParent();
                    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) background.getLayoutParams();
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    layoutParams.dimAmount = 0.75f;
                    windowManager.updateViewLayout(background, layoutParams);

                    okButton = popupView.findViewById(R.id.OkButton4);

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                            Intent intent = new Intent(BookAppointmentTimeSlotActivity.this, MainActivityPatients.class);
                            intent.putExtra("username", patientUsername);
                            startActivity(intent);
                        }
                    });
                }
                else
                    Toast.makeText(BookAppointmentTimeSlotActivity.this, "Please select a time slot.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookAppointmentTimeSlotActivity.this, SelectDateActivity.class);
                intent.putExtra("doctorUsername", username);
                intent.putExtra("username", patientUsername);
                intent.putExtra("medicalService", medicalService);
                startActivity(intent);
            }
        });
    }
}