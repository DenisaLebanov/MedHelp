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
import java.util.HashMap;
import java.util.Map;

public class EditAppointmentTimeSlotAdapter extends RecyclerView.Adapter<EditAppointmentTimeSlotAdapter.ViewHolder> {

    private ArrayList<String> slots = new ArrayList<>();
    private Context context;
    private String date;
    private String doctorsUsername;
    private String appointmentNumber;
    private String completeDate;
    private String username;
    private String type;
    private String oldDate;
    private String activity;

    private int number = 1;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;

    public EditAppointmentTimeSlotAdapter(Context context, ArrayList<String> slots, String date, String doctorsUsername, String appointmentNumber, String username, String type, String activity) {
        this.slots = slots;
        this.context = context;
        this.date = date;
        this.doctorsUsername = doctorsUsername;
        this.appointmentNumber = appointmentNumber;
        this.username = username;
        this.type = type;
        this.activity =activity;
    }

    @NonNull
    @Override
    public EditAppointmentTimeSlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_time_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditAppointmentTimeSlotAdapter.ViewHolder holder, int position) {
        holder.dateTextView.setText(slots.get(position));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");

        holder.selectTimeSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference = firebaseDatabase.getReference().child("Appointments").child(doctorsUsername).child(appointmentNumber);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        oldDate = snapshot.child("date").getValue(String.class);

                        completeDate = date + " " + slots.get(holder.getAdapterPosition());
                        Map<String, Object> newAppointmentDate = new HashMap<>();
                        newAppointmentDate.put("date", completeDate);

                        snapshot.getRef().updateChildren(newAppointmentDate);

                        Toast.makeText(context, "Your appointment has been successfully rescheduled.", Toast.LENGTH_LONG).show();

                        databaseReference1 = firebaseDatabase.getReference().child("RescheduledAppointments");

                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                while (snapshot.hasChild("rescheduledAppointment" + number))
                                    number++;

                                if (type.equals("doctor")) {
                                    RescheduledAppointment rescheduledAppointment = new RescheduledAppointment(oldDate, completeDate, doctorsUsername, username);
                                    databaseReference1.child("rescheduledAppointment" + number).setValue(rescheduledAppointment);

                                    if (activity.equals("DoctorsMainActivity")) {
                                        Intent intent = new Intent(context, DoctorsMainActivity.class);
                                        intent.putExtra("username", doctorsUsername);
                                        context.startActivity(intent);
                                    }
                                    else if (activity.equals("EmergencyDoctorsMainActivity")){
                                        Intent intent = new Intent(context, EmergencyDoctorsMainActivity.class);
                                        intent.putExtra("username", doctorsUsername);
                                        context.startActivity(intent);
                                    }
                                }
                                else{
                                    RescheduledAppointment rescheduledAppointment = new RescheduledAppointment(oldDate, completeDate, username, doctorsUsername);
                                    databaseReference1.child("rescheduledAppointment" + number).setValue(rescheduledAppointment);

                                    Intent intent = new Intent(context, MainActivityPatients.class);
                                    intent.putExtra("username", username);
                                    context.startActivity(intent);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
        return slots.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        TextView dateTextView;
        Button selectTimeSlotButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.DateTimeSlot);
            selectTimeSlotButton = itemView.findViewById(R.id.ButtonTimeSlot);
        }
    }
}
