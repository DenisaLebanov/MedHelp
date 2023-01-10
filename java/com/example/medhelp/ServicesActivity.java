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

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServicesActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button logoutButton;

    private ArrayList<String> buttonsList;

    private String specialization;

    private RecyclerView recyclerView;
    private ServicesAdapter servicesAdapter;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        logoutButton = findViewById(R.id.LogoutServicesButton);
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonsList = new ArrayList<>();
        servicesAdapter = new ServicesAdapter(this, buttonsList, username);
        recyclerView.setAdapter(servicesAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("PersonalDataDoctors");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    specialization = ds.child("specialization").getValue(String.class);
                    buttonsList.add(specialization);
                }
                Set<String> set = new HashSet<>(buttonsList);
                buttonsList.clear();
                buttonsList.addAll(set);

                servicesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Denisa", "Eroare citire specializare din baza de date Services Activity!");
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        handleLogoutButton();
    }

    public void handleLogoutButton(){
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServicesActivity.this, MainActivityPatients.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}