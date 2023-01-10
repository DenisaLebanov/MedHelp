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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.ViewHolder>{

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> specialization = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();
    private ArrayList<String> phone = new ArrayList<>();
    private Context context;
    private String username;

    public DoctorsListAdapter(Context context, ArrayList<String> name, ArrayList<String> specialization, ArrayList<String> url, String username, ArrayList<String> phone) {
        this.context = context;
        this.name = name;
        this.specialization = specialization;
        this.url = url;
        this.username = username;
        this.phone = phone;
    }

    @NonNull
    @Override
    public DoctorsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_doctors, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorsListAdapter.ViewHolder holder, int position) {
        holder.nameText.setText(name.get(position));
        holder.specializationText.setText(specialization.get(position));
        holder.phoneText.setText(phone.get(position));
        Glide.with(context)
                .load(url.get(position))
                .into(holder.photo);
        holder.bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BookAppointmentActivity.class);
                intent.putExtra("username", username);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView specializationText;
        TextView phoneText;
        CircleImageView photo;
        ConstraintLayout layout;
        Button bookAppointmentButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.NameText);
            specializationText = itemView.findViewById(R.id.SpecializationText);
            photo = itemView.findViewById(R.id.DoctorsPhoto);
            layout = itemView.findViewById(R.id.DoctorsListLayout);
            bookAppointmentButton = itemView.findViewById(R.id.BookAppointmentButton);
            phoneText = itemView.findViewById(R.id.PhoneDoctorsTextView);
        }
    }
}
