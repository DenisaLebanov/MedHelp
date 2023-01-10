package com.example.medhelp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAppointmentsAdapter extends RecyclerView.Adapter<ViewAppointmentsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> medicalServices = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;

    private ArrayList<String> detailsArray = new ArrayList<>();
    private ArrayList<String> phoneArray = new ArrayList<>();
    private ArrayList<String> genderArray = new ArrayList<>();
    private ArrayList<String> ageArray = new ArrayList<>();
    private ArrayList<String> cnpArray = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    private String username;
    private String doctorsUsername;
    private String activity;

    private View popupView;

    private Button closeButton;

    private PopupWindow popupWindow;

    private TextView nameTextView;
    private TextView ageTextView;
    private TextView phoneTextView;
    private TextView cnpTextView;
    private TextView detailsTextView;
    private TextView genderTextView;

    private boolean hasMoreThanOneChild = false;

    public ViewAppointmentsAdapter(Context context, ArrayList<String> dates, ArrayList<String> usernames, ArrayList<String> medicalServices, String doctorsUsername, String activity) {
        this.context = context;
        this.dates = dates;
        this.usernames = usernames;
        this.medicalServices = medicalServices;
        this.doctorsUsername = doctorsUsername;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewAppointmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_appointment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAppointmentsAdapter.ViewHolder holder, int position) {
        holder.medicalService.setText(medicalServices.get(position));
        holder.date.setText(dates.get(position));
        //holder.name.setText(names.get(holder.getAdapterPosition()));

        username = usernames.get(position);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("PersonalDataPatients").child(username);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ageArray.add(snapshot.child("age").getValue(String.class));
                phoneArray.add(snapshot.child("phone").getValue(String.class));
                cnpArray.add(snapshot.child("cnp").getValue(String.class));
                genderArray.add(snapshot.child("gender").getValue(String.class));
                detailsArray.add(snapshot.child("details").getValue(String.class));
                names.add(snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("secondName").getValue(String.class));
                holder.name.setText(names.get(holder.getAdapterPosition()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                popupView = layoutInflater.inflate(R.layout.details_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                popupWindow = new PopupWindow(popupView, width, height, true);

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                View background = (View) popupWindow.getContentView().getParent();
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) background.getLayoutParams();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                layoutParams.dimAmount = 0.75f;
                windowManager.updateViewLayout(background, layoutParams);

                nameTextView = popupView.findViewById(R.id.NameDetailsTextView);
                ageTextView = popupView.findViewById(R.id.AgeDetailsTextView);
                phoneTextView = popupView.findViewById(R.id.PhoneDetailsTextView);
                detailsTextView = popupView.findViewById(R.id.DetailsTextView);
                genderTextView = popupView.findViewById(R.id.GenderDetailsTextView);
                cnpTextView = popupView.findViewById(R.id.CnpDetailsTextView);

                nameTextView.setText(names.get(holder.getAdapterPosition()));
                ageTextView.setText(ageArray.get(holder.getAdapterPosition()));
                cnpTextView.setText(cnpArray.get(holder.getAdapterPosition()));
                detailsTextView.setText(detailsArray.get(holder.getAdapterPosition()));
                phoneTextView.setText(phoneArray.get(holder.getAdapterPosition()));
                genderTextView.setText(genderArray.get(holder.getAdapterPosition()));

                closeButton = popupView.findViewById(R.id.DetailsCloseButton);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

        databaseReference1 = firebaseDatabase.getReference().child("DeletedAppointments");

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String from = doctorsUsername;
                        String to = usernames.get(holder.getAdapterPosition());
                        String date = dates.get(holder.getAdapterPosition());

                        DeletedAppointments deletedAppointment = new DeletedAppointments(from, to, date);
                        int number = 1;
                        while(snapshot.hasChild("deletedAppointment" + number)) {
                            number++;
                        }

                        databaseReference1.child("deletedAppointment" + number).setValue(deletedAppointment);
                        deleteFromDatabase(from, date);

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
                intent.putExtra("doctorUsername", doctorsUsername);
                intent.putExtra("username", usernames.get(holder.getAdapterPosition()));
                intent.putExtra("date", dates.get(holder.getAdapterPosition()));
                intent.putExtra("type", "doctor");
                intent.putExtra("activity", activity);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView medicalService;
        TextView date;
        Button details;
        Button delete;
        Button edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.PatientDetailsButton);
            name = itemView.findViewById(R.id.AppointmentPatientNameTextView);
            medicalService = itemView.findViewById(R.id.AppointmentMedicalServiceTextView);
            date = itemView.findViewById(R.id.AppointmentDateTextView);
            delete = itemView.findViewById(R.id.DeleteAppointmentDoctors);
            edit = itemView.findViewById(R.id.EditAppointmentDoctors);
        }
    }

    private void deleteFromDatabase(String from, String date){
        databaseReference2 = firebaseDatabase.getReference().child("Appointments").child(from);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 2)
                    hasMoreThanOneChild = true;
                else
                    hasMoreThanOneChild = false;

                for (DataSnapshot ds1: snapshot.getChildren()){
                    if (ds1.child("date").getValue(String.class).equals(date)) {
                        ds1.getRef().removeValue();
                        if (hasMoreThanOneChild) {
                            Intent intent = new Intent(context, ViewAppointmentsActivity.class);
                            intent.putExtra("username", doctorsUsername);
                            intent.putExtra("activity", activity);
                            context.startActivity(intent);
                        }
                        else {
                            if (activity.equals("DoctorsMainActivity")) {
                                Intent intent = new Intent(context, DoctorsMainActivity.class);
                                intent.putExtra("username", doctorsUsername);
                                context.startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(context, EmergencyDoctorsMainActivity.class);
                                intent.putExtra("username", doctorsUsername);
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
}
