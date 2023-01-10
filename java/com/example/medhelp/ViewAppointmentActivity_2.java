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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAppointmentActivity_2 extends AppCompatActivity {

    private ArrayList<String> dates;
    private ArrayList<String> medicalServices;
    private ArrayList<String> usernames;

    private String username;
    private String date;
    private String activity;

    private RecyclerView recyclerView;

    private TextView selectedDate;

    private ViewAppointmentsAdapter viewAppointmentsAdapter;

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_appointment_2_activity);

        backButton = findViewById(R.id.BackButton6);

        dates = new ArrayList<>();
        medicalServices = new ArrayList<>();
        usernames = new ArrayList<>();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        dates = intent.getStringArrayListExtra("dates");
        medicalServices = intent.getStringArrayListExtra("medicalServices");
        usernames = intent.getStringArrayListExtra("usernames");
        activity = intent.getStringExtra("activity");

        date = dates.get(0).substring(0, dates.get(0).length() - 5);

        selectedDate = findViewById(R.id.SelectedDateTextView);
        selectedDate.setText(date);

        recyclerView = findViewById(R.id.AppointmentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewAppointmentsAdapter = new ViewAppointmentsAdapter(ViewAppointmentActivity_2.this, dates, usernames, medicalServices, username, activity);
        viewAppointmentsAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(viewAppointmentsAdapter);

        handleBackButton();
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewAppointmentActivity_2.this, ViewAppointmentsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
    }
}