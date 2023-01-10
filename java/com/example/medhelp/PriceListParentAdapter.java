package com.example.medhelp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PriceListParentAdapter extends RecyclerView.Adapter<PriceListParentAdapter.ParentViewHolder> {
    private ArrayList<String> priceListParent;
    private ArrayList<String> names;
    private ArrayList<String> uniqueNames;
    private ArrayList<String> array;
    private Activity activity;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private String finalText;
    private String service;
    private PriceListChildAdapter priceListChildAdapter;
    private static int serviceNumber = 0;

    public PriceListParentAdapter(Activity activity, ArrayList<String> priceListParent, ArrayList<String> names) {
        this.activity = activity;
        this.priceListParent = priceListParent;
        this.names = names;
    }

    @NonNull
    @Override
    public PriceListParentAdapter.ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_list_parent_layout, parent, false);
        return new PriceListParentAdapter.ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListParentAdapter.ParentViewHolder holder, int position) {
        String completeSpecialization = priceListParent.get(position) + "  ~  Dr. " + names.get(position);
        holder.specialization.setText(completeSpecialization);

        array = new ArrayList<>();
        uniqueNames = new ArrayList<>(names);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("Services");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> setS = new HashSet<>(uniqueNames);
                uniqueNames.clear();
                uniqueNames.addAll(setS);

                for(int i = 0; i < uniqueNames.size(); i++){
                    array = new ArrayList<>();
                    for (DataSnapshot ds1 : snapshot.getChildren()){
                        if (ds1.child("name").getValue(String.class).equals(names.get(holder.getAdapterPosition()))){
                            serviceNumber = 1;
                            service = "service" + serviceNumber;
                            while(ds1.child("medical services").hasChild(service)){
                                finalText = ds1.child("medical services").child(service).child("medicalService").getValue(String.class) + " " + ds1.child("medical services").child(service).child("price").getValue(String.class) + " RON";
                                array.add(finalText);
                                serviceNumber++;
                                service = "service" + serviceNumber;
                                finalText = "";
                            }
                        }
                    }
                    priceListChildAdapter = new PriceListChildAdapter(array);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                    holder.childRecyclerView.setLayoutManager(layoutManager);
                    holder.childRecyclerView.setAdapter(priceListChildAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public int getItemCount() {
        return priceListParent.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder{
        private TextView specialization;
        private RecyclerView childRecyclerView;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            specialization = itemView.findViewById(R.id.PricesSpecializationText);
            childRecyclerView = itemView.findViewById(R.id.ChildRecyclerView);
        }
    }
}
