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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.LongFunction;

public class PricesListActivity extends AppCompatActivity {

    private Button backButton;

    private RecyclerView recyclerView;

    private ArrayList<String> specializations;
    private ArrayList<String> names;

    private String spec;
    private String name;
    private String username;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prices_list_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        backButton = findViewById(R.id.BackButtonPricesList);

        specializations = new ArrayList<>();
        names = new ArrayList<>();

        recyclerView = findViewById(R.id.ParentRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        PriceListParentAdapter priceListParentAdapter = new PriceListParentAdapter(PricesListActivity.this, specializations, names);
        recyclerView.setAdapter(priceListParentAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Services");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    spec = ds.child("specialization").getValue(String.class);
                    specializations.add(spec);
                    name = ds.child("name").getValue(String.class);
                    names.add(name);
                }
                priceListParentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Denisa", "Eroare arrauy specializari din baza de date Prices Activity!");
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        handleBackButton();
    }

    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PricesListActivity.this, MainActivityPatients.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}