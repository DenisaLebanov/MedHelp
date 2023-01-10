package com.example.medhelp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MedicalServicesAdapter extends RecyclerView.Adapter<MedicalServicesAdapter.ViewHolder> {

    private ArrayList<String> medicalServices = new ArrayList<>();

    public MedicalServicesAdapter (ArrayList medicalServices){
        this.medicalServices = medicalServices;
    }

    @NonNull
    @Override
    public MedicalServicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_services_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MedicalServicesAdapter.ViewHolder holder, int position) {
        holder.medicalService.setText("~ " + medicalServices.get(position));
    }

    @Override
    public int getItemCount() {
        return medicalServices.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        private TextView medicalService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicalService = itemView.findViewById(R.id.MedicalServiceAdapterTextView);
        }
    }
}
