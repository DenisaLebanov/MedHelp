package com.example.medhelp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder>{

    private ArrayList<String> specializationArray = new ArrayList<>();
    private Context context;
    private String username;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private MedicalServicesAdapter medicalServicesAdapter;

    private ArrayList<String> medicalService = new ArrayList<>();

    public ServicesAdapter(Context context, ArrayList<String> specializationArray, String username) {
        this.specializationArray = specializationArray;
        this.context = context;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_services, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.serviceTextView.setText(specializationArray.get(position));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Services");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < specializationArray.size(); i++) {
                    medicalService = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (Objects.equals(dataSnapshot.child("specialization").getValue(String.class), specializationArray.get(holder.getAdapterPosition()))) {
                            DataSnapshot dataSnapshot1 = dataSnapshot.child("medical services");
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                medicalService.add(dataSnapshot2.child("medicalService").getValue(String.class));
                            }
                        }
                    }

                    Set<String> set = new HashSet<>(medicalService);
                    medicalService.clear();
                    medicalService.addAll(set);

                    medicalServicesAdapter = new MedicalServicesAdapter(medicalService);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    holder.recyclerView.setAdapter(medicalServicesAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return specializationArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView serviceTextView;
        ConstraintLayout servicesViewLayout;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTextView = itemView.findViewById(R.id.ServicesListTextView);
            servicesViewLayout = itemView.findViewById(R.id.ServicesListLayout);
            recyclerView = itemView.findViewById(R.id.RecyclerViewMedicalServices);
        }
    }
}
