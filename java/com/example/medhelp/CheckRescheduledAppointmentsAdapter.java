package com.example.medhelp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckRescheduledAppointmentsAdapter extends RecyclerView.Adapter<CheckRescheduledAppointmentsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> oldDates = new ArrayList<>();
    private ArrayList<String> newDates = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();

    private String activity;
    private String username;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    public CheckRescheduledAppointmentsAdapter(Context context, ArrayList<String> oldDates, ArrayList<String> newDates, ArrayList<String> names, ArrayList<String> keys, String activity, String username) {
        this.context = context;
        this.oldDates = oldDates;
        this.newDates = newDates;
        this.names = names;
        this.keys = keys;
        this.activity = activity;
        this.username = username;
    }

    @NonNull
    @Override
    public CheckRescheduledAppointmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rescheduled_appointment_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckRescheduledAppointmentsAdapter.ViewHolder holder, int position) {
        holder.fromTextView.setText(names.get(position));
        holder.newDateTextView.setText(newDates.get(position));
        holder.oldDateTextView.setText(oldDates.get(position));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("RescheduledAppointments").child(keys.get(holder.getAdapterPosition()));

        holder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();

                        if (oldDates.size() > 1){
                            Intent intent = new Intent(context, CheckReschedulesAppointmentsActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("activity", activity);
                            context.startActivity(intent);
                        }
                        else {
                            if (activity.equals("EmergencyDoctorsMainActivity")) {
                                Intent intent = new Intent(context, EmergencyDoctorsMainActivity.class);
                                intent.putExtra("username", username);
                                context.startActivity(intent);
                            } else if (activity.equals("DoctorsMainActivity")) {
                                Intent intent = new Intent(context, DoctorsMainActivity.class);
                                intent.putExtra("username", username);
                                context.startActivity(intent);
                            } else if (activity.equals("MainActivityPatients")) {
                                Intent intent = new Intent(context, MainActivityPatients.class);
                                intent.putExtra("username", username);
                                context.startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return oldDates.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        TextView oldDateTextView;
        TextView newDateTextView;
        TextView fromTextView;
        Button okButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            oldDateTextView = itemView.findViewById(R.id.RescheduleOldAppointmentTextView);
            newDateTextView = itemView.findViewById(R.id.RescheduleNewAppointmentTextView);
            fromTextView = itemView.findViewById(R.id.RescheduleNameTextView);
            okButton = itemView.findViewById(R.id.OkRescheduleOkButton);
        }
    }
}
