package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewEmergencyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Button goBack;

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> latitude = new ArrayList<>();
    private ArrayList<String> longitude = new ArrayList<>();
    private ArrayList<String> details = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();

    private String username;

    private int counter = 0;

    private ViewEmergencyAdapter viewEmergencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_emergency_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        recyclerView = findViewById(R.id.EmergencyActivityRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        goBack = findViewById(R.id.GoBackButton1);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewEmergencyActivity.this, EmergencyDoctorsMainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Emergencies");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                    counter++;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    counter--;

                    DataSnapshot dataSnapshot1 = dataSnapshot;
                    if (!dataSnapshot1.child("username").getValue(String.class).equals("0")) {
                        keys.add(dataSnapshot1.getKey());
                        Log.d("Denisa", dataSnapshot1.getKey() + " KEY");
                        usernames.add(dataSnapshot1.child("username").getValue(String.class));
                        addresses.add(dataSnapshot1.child("address").getValue(String.class));
                        latitude.add(dataSnapshot1.child("latitude").getValue(String.class));
                        longitude.add(dataSnapshot1.child("longitude").getValue(String.class));
                        urls.add(dataSnapshot1.child("url").getValue(String.class));
                        details.add(dataSnapshot1.child("description").getValue(String.class));
                    }
                }

                if (counter == 0){
                    viewEmergencyAdapter = new ViewEmergencyAdapter(ViewEmergencyActivity.this, addresses, latitude, longitude, details, urls, usernames, keys, username);
                    viewEmergencyAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(viewEmergencyAdapter);
                }

                if (counter == 1){
                    Toast.makeText(ViewEmergencyActivity.this, "There are no emergencies at the moment.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}