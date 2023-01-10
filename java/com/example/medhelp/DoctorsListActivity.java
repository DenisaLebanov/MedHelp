package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DoctorsListActivity extends AppCompatActivity {

    private Button backButton;

    private RecyclerView recyclerView;

    DoctorsListAdapter doctorsListAdapter;

    private ArrayList<String> nameArray;
    private ArrayList<String> specializationArray;
    private ArrayList<String> urlArray;
    private ArrayList<String> phoneArray;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private String name;
    private String specialization;
    private String url;
    private String username;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_list_activity);

        nameArray = new ArrayList<>();
        specializationArray = new ArrayList<>();
        urlArray = new ArrayList<>();
        phoneArray = new ArrayList<>();

        backButton = findViewById(R.id.BackButton);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        recyclerView = findViewById(R.id.recyclerViewDoctors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorsListAdapter = new DoctorsListAdapter(this, nameArray, specializationArray, urlArray, username, phoneArray);
        recyclerView.setAdapter(doctorsListAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("PersonalDataDoctors");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    name = ds. child("firstName").getValue(String.class) + " " + ds.child("secondName").getValue(String.class);
                    specialization = ds.child("specialization").getValue(String.class);
                    url = ds.child("url").getValue(String.class);
                    phone = ds.child("phone").getValue(String.class);

                    nameArray.add(name);
                    specializationArray.add(specialization);
                    urlArray.add(url);
                    phoneArray.add(phone);

                    doctorsListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Denisa", "Eroare citire informatii din baza de date Doctors List Activity!");
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        handleBackButton();
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorsListActivity.this, MainActivityPatients.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}