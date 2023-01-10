package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ViewPatientsActivity extends AppCompatActivity {

    private Button backButton;
    private Button okButton;

    private RecyclerView recyclerView;

    private String username;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;

    private boolean existsAppointments = false;

    private View popupView;

    private PopupWindow popupWindow;

    private ArrayList<String> patientUsernames;
    private ArrayList<String> patientNames;

    ViewPatientsAdapter viewPatientsAdapter;

    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_patients_activity);

        Intent intent1 = getIntent();
        username = intent1.getStringExtra("username");
        activity = intent1.getStringExtra("activity");

        patientUsernames = new ArrayList<>();
        patientNames = new ArrayList<>();

        backButton = findViewById(R.id.BackButtonDoctors2);

        recyclerView = findViewById(R.id.DoctorsViewPatientsListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Appointments");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(ds.getKey(), username))
                        existsAppointments = true;
                }

                if (existsAppointments){
                    for (DataSnapshot ds1 : snapshot.child(username).getChildren()){
                        DataSnapshot ds2 = ds1;
                        if (!ds2.child("username").getValue(String.class).equals("0")) {
                            patientUsernames.add(ds2.child("username").getValue(String.class));
                        }
                    }
                    Set<String> set = new HashSet<>(patientUsernames);
                    patientUsernames.clear();
                    patientUsernames.addAll(set);
                    getNames(patientUsernames);
                }
                else{
                    popUpCall(R.layout.no_appointments_popup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };databaseReference.addListenerForSingleValueEvent(valueEventListener);

        handleBackButton();
    }

    private void getNames(ArrayList<String> patientUsernames){
        databaseReference1 = firebaseDatabase.getReference().child("PersonalDataPatients");

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < patientUsernames.size(); i++)
                    patientNames.add(snapshot.child(patientUsernames.get(i)).child("firstName").getValue(String.class) + " " + snapshot.child(patientUsernames.get(i)).child("secondName").getValue(String.class));

                Log.d("Denisa", patientNames.get(0) + " ");
                viewPatientsAdapter = new ViewPatientsAdapter(ViewPatientsActivity.this, patientNames, patientUsernames);
                viewPatientsAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(viewPatientsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };databaseReference1.addListenerForSingleValueEvent(valueEventListener1);
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

        okButton = popupView.findViewById(R.id.OkButton3);

        okButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if(okButton.getId() == 1000073) {
                    if (activity.equals("DoctorsMainActivity")) {
                        Intent intent = new Intent(ViewPatientsActivity.this, DoctorsMainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                    else
                        if (activity.equals("EmergencyDoctorsMainActivity")){
                            Intent intent = new Intent(ViewPatientsActivity.this, MainActivityPatients.class);
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
                    Intent intent = new Intent(ViewPatientsActivity.this, DoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else
                if (activity.equals("EmergencyDoctorsMainActivity")){
                    Intent intent = new Intent(ViewPatientsActivity.this, EmergencyDoctorsMainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
    }
}