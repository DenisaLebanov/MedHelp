package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ViewAppointmentsActivity extends AppCompatActivity {

    private Button backButton;
    private Button okButton;

    private CalendarView calendarView;

    private String username;
    private String date;
    private String activity;

    private View popupView;

    private PopupWindow popupWindow;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private boolean existsAppointments = false;
    private boolean existsAppointmentsToday = false;

    private ArrayList<String> dates;
    private ArrayList<String> medicalServices;
    private ArrayList<String> usernames;

    private String databaseDate;
    private String databaseMedicalService;
    private String databaseUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_appointments_activity);

        Intent intent1 = getIntent();
        username = intent1.getStringExtra("username");

        Intent intent2 = getIntent();
        activity = intent2.getStringExtra("activity");

        backButton = findViewById(R.id.GoBackDoctosButton);

        calendarView = findViewById(R.id.DoctorsCalendarView);

        handleBackButton();
        handleCalendarView();
    }

    private void handleCalendarView(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2 + "." + (i1 + 1) + "." + i;
                existsAppointmentsToday = false;

                dates = new ArrayList<>();
                medicalServices = new ArrayList<>();
                usernames = new ArrayList<>();

                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference().child("Appointments");

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            if (Objects.equals(ds.getKey(), username))
                                if (ds.getChildrenCount() > 1)
                                    existsAppointments = true;
                        }

                        if (existsAppointments) {
                            for (DataSnapshot ds1 : snapshot.child(username).getChildren()) {
                                DataSnapshot ds2 = ds1;
                                if (ds2.child("date").getValue(String.class).contains(date)) {

                                    existsAppointmentsToday = true;

                                    databaseDate = ds2.child("date").getValue(String.class);
                                    databaseMedicalService = ds2.child("medicalService").getValue(String.class);
                                    databaseUsername = ds2.child("username").getValue(String.class);

                                    dates.add(databaseDate);
                                    medicalServices.add(databaseMedicalService);
                                    usernames.add(databaseUsername);

                                    Intent intent = new Intent(ViewAppointmentsActivity.this, ViewAppointmentActivity_2.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("activity", activity);
                                    intent.putStringArrayListExtra("dates", dates);
                                    intent.putStringArrayListExtra("medicalServices", medicalServices);
                                    intent.putStringArrayListExtra("usernames", usernames);
                                    startActivity(intent);
                                }
                            }
                            if (!existsAppointmentsToday)
                                popUpCall(R.layout.no_appointments_today_popup);

                        }
                        else
                            popUpCall(R.layout.no_appointments_popup);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };databaseReference.addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }

    private void popUpCall(int popup){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(popup, null);

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

        if (popup == R.layout.no_appointments_popup)
            okButton = popupView.findViewById(R.id.OkButton3);
        else
            okButton = popupView.findViewById(R.id.OkButton2);

        okButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if(okButton.getId() == 1000073) {
                    if (activity.equals("DoctorsMainActivity")) {
                        Intent intent = new Intent(ViewAppointmentsActivity.this, DoctorsMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                    else
                    if (activity.equals("EmergencyDoctorsMainActivity")){
                        Intent intent = new Intent(ViewAppointmentsActivity.this, EmergencyDoctorsMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.equals("DoctorsMainActivity")) {
                    Intent intent = new Intent(ViewAppointmentsActivity.this, DoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else
                    if (activity.equals("EmergencyDoctorsMainActivity")){
                        Intent intent = new Intent(ViewAppointmentsActivity.this, EmergencyDoctorsMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                }
            }
        });
    }
}