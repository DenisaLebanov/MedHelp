package com.example.medhelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> dates = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String date;
    private String username;
    private String appointment;
    private String medicalService;
    private String patientUsername;

    public TimeSlotsAdapter(Context context, ArrayList<String> dates, String date, String username, String appointment, String medicalService, String patientUsername) {
        this.context = context;
        this.dates = dates;
        this.date = date;
        this.username = username;
        this.appointment = appointment;
        this.medicalService = medicalService;
        this.patientUsername = patientUsername;
    }

    @NonNull
    @Override
    public TimeSlotsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_time_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TimeSlotsAdapter.ViewHolder holder, int position) {
        if(dates.get(position).contains("Booked")){
            holder.bookButton.setBackgroundColor(Color.RED);
            holder.bookButton.setText("Booked");
            holder.date.setTextColor(Color.RED);
        }
        holder.date.setText(dates.get(position));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Appointments"). child(username);

        String text = holder.bookButton.getText().toString();
        if (!text.contains("Booked")) {
            holder.bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String completeDate = date + " " + dates.get(holder.getAdapterPosition());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Appointments newAppointment = new Appointments(completeDate, medicalService, patientUsername);
                            databaseReference.child(appointment).setValue(newAppointment);

                            Intent intent = new Intent(context, BookAppointmentTimeSlotActivity.class);
                            intent.putExtra("doctorUsername", username);
                            intent.putExtra("date", date);
                            intent.putExtra("activity", "TimeSlotsAdapter");
                            intent.putExtra("username", patientUsername);
                            intent.putExtra("medicalService", medicalService);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        Button bookButton;
        ConstraintLayout constraintLayout;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookButton = itemView.findViewById(R.id.ButtonTimeSlot);
            constraintLayout = itemView.findViewById(R.id.TimeSlotLayout);
            date = itemView.findViewById(R.id.DateTimeSlot);
        }
    }
}
