package com.example.medhelp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAppointmentsPatientsAdapter extends RecyclerView.Adapter<ViewAppointmentsPatientsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> medicalServices = new ArrayList<>();
    private String username;
    private String doctorsName;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;


    public ViewAppointmentsPatientsAdapter(Context context, ArrayList<String> dates, ArrayList<String> usernames, ArrayList<String> medicalServices, String username) {
        this.context = context;
        this.dates = dates;
        this.usernames = usernames;
        this.medicalServices = medicalServices;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewAppointmentsPatientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_appointments_patients_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAppointmentsPatientsAdapter.ViewHolder holder, int position) {

        String uname = usernames.get(position);
        String ms = medicalServices.get(position);
        String d = dates.get(position);


        holder.date.setText(dates.get(position));
        holder.medicalService.setText(medicalServices.get(position));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("PersonalDataDoctors").child(usernames.get(position));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorsName = snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("secondName").getValue(String.class);
                holder.name.setText(doctorsName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference1 = firebaseDatabase.getReference().child("DeletedAppointments");

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String from = username;
                        String to = usernames.get(holder.getAdapterPosition());
                        String date = dates.get(holder.getAdapterPosition());

                        DeletedAppointments deletedAppointment = new DeletedAppointments(from, to, date);
                        int number = 1;
                        while (snapshot.hasChild("deletedAppointment" + number)) {
                            number++;
                        }

                        databaseReference1.child("deletedAppointment" + number).setValue(deletedAppointment);
                        deleteFromDatabase(from, date, to);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditAppointmentActivity.class);
                intent.putExtra("doctorUsername", uname);
                intent.putExtra("username", username);
                intent.putExtra("medicalService", ms);
                intent.putExtra("date", d);
                intent.putExtra("type", "patient");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView medicalService;
        TextView date;
        Button delete;
        Button edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.NamePatientsViewAppointmentsTextView);
            medicalService = itemView.findViewById(R.id.MedicalServicePatientsViewAppointmentsTextView);
            date = itemView.findViewById(R.id.DatePatientsViewAppointmentsTextView);
            delete = itemView.findViewById(R.id.DeletePatientsViewAppointmentsButton);
            edit = itemView.findViewById(R.id.PatientEditButton);
        }
    }

    private void deleteFromDatabase(String from, String date, String to){
        databaseReference2 = firebaseDatabase.getReference().child("Appointments").child(to);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (dataSnapshot.child("date").getValue(String.class).equals(date) && dataSnapshot.child("username").getValue(String.class).equals(from)) {
                        dataSnapshot.getRef().removeValue();
                        if (dates.size() > 1) {
                            Intent intent = new Intent(context, ViewAppointmentsPatientsActivity.class);
                            intent.putExtra("username", username);
                            context.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(context, MainActivityPatients.class);
                            intent.putExtra("username", username);
                            context.startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
