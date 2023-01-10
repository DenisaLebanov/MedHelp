package com.example.medhelp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
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
import java.util.Objects;

public class CheckDeletedAppointmentsAdapter extends RecyclerView.Adapter<CheckDeletedAppointmentsAdapter.ViewHolder> {
    private Context context;

    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> from = new ArrayList<>();

    private String finalText;
    private String username;
    private String name;
    private String activity;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference1;

    public CheckDeletedAppointmentsAdapter(Context context, ArrayList<String> dates, ArrayList<String> from, String username, String activity) {
        this.context = context;
        this.dates = dates;
        this.from = from;
        this.username = username;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CheckDeletedAppointmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_deleted_appointments_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckDeletedAppointmentsAdapter.ViewHolder holder, int position) {
        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");

        if (activity.equals("EmergencyDoctorsMainActivity") || activity.equals("DoctorsMainActivity")) {
            databaseReference1 = firebaseDatabase.getReference().child("PersonalDataPatients").child(from.get(holder.getAdapterPosition()));

            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name = snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("secondName").getValue(String.class);
                    finalText = name + " " + dates.get(holder.getAdapterPosition());
                    holder.text.setText(finalText);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (activity.equals("MainActivityPatients")){
            databaseReference1 = firebaseDatabase.getReference().child("PersonalDataDoctors").child(from.get(holder.getAdapterPosition()));

            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name = snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("secondName").getValue(String.class);
                    finalText = name + " " + dates.get(holder.getAdapterPosition());
                    holder.text.setText(finalText);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference().child("DeletedAppointments");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if (Objects.equals(dataSnapshot.child("from").getValue(String.class), from.get(holder.getAdapterPosition())) && Objects.equals(dataSnapshot.child("date").getValue(String.class), dates.get(holder.getAdapterPosition()))) {
                                dataSnapshot.getRef().removeValue();

                                Toast.makeText(context, "The appointment was successfully deleted!", Toast.LENGTH_SHORT).show();

                                if (from.size() > 1){
                                    Intent intent = new Intent(context, CheckDeletedAppointmentsActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("activity", activity);
                                    context.startActivity(intent);
                                }
                                else {

                                    if (activity.equals("EmergencyDoctorsMainActivity")) {
                                        Intent intent = new Intent(context, EmergencyDoctorsMainActivity.class);
                                        intent.putExtra("username", username);
                                        context.startActivity(intent);
                                    }
                                    else if (activity.equals("DoctorsMainActivity")) {
                                        Intent intent = new Intent(context, DoctorsMainActivity.class);
                                        intent.putExtra("username", username);
                                        context.startActivity(intent);
                                    }
                                    else if (activity.equals("MainActivityPatients")) {
                                        Intent intent = new Intent(context, MainActivityPatients.class);
                                        intent.putExtra("username", username);
                                        context.startActivity(intent);
                                    }
                                }
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
        return from.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        Button okButton;
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            okButton = itemView.findViewById(R.id.DeletedButton);
            text = itemView.findViewById(R.id.DeletedTextView);
        }
    }
}
