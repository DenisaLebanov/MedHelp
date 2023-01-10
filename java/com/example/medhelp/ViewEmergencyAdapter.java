package com.example.medhelp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewEmergencyAdapter extends RecyclerView.Adapter<ViewEmergencyAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> latitudes = new ArrayList<>();
    private ArrayList<String> longitudes = new ArrayList<>();
    private ArrayList<String> details = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;


    private String uname;
    private String username;

    private String phoneNumber;

    public ViewEmergencyAdapter(Context context, ArrayList<String> addresses, ArrayList<String> latitudes, ArrayList<String> longitudes, ArrayList<String> details, ArrayList<String> urls, ArrayList<String> usernames, ArrayList<String> keys, String username) {
        this.context = context;
        this.addresses = addresses;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.details = details;
        this.urls = urls;
        this.usernames = usernames;
        this.keys = keys;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewEmergencyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_emrgency_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewEmergencyAdapter.ViewHolder holder, int position) {
        uname = usernames.get(position);

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("PersonalDataPatients").child(uname);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("secondName").getValue(String.class));
                phoneNumber = snapshot.child("phone").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.address.setText(addresses.get(holder.getAdapterPosition()));
        holder.latitude.setText(latitudes.get(holder.getAdapterPosition()));
        holder.longitude.setText(longitudes.get(holder.getAdapterPosition()));
        holder.detail.setText(details.get(holder.getAdapterPosition()));
        Glide.with(context)
                .load(urls.get(holder.getAdapterPosition()))
                .into(holder.photo);

        holder.sendAmbulanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference().child("Emergencies");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.getKey().equals(keys.get(holder.getAdapterPosition()))) {
                                //snapshot.getRef().removeValue();
                                dataSnapshot.getRef().removeValue();

                                if (usernames.size() > 1) {
                                    Intent intent = new Intent(context, ViewEmergencyActivity.class);
                                    intent.putExtra("username", username);
                                    context.startActivity(intent);
                                } else {
                                    Intent intent = new Intent(context, EmergencyDoctorsMainActivity.class);
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
        });

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        private Button sendAmbulanceButton;
        private Button callButton;

        private TextView name;
        private TextView address;
        private TextView latitude;
        private TextView longitude;
        private TextView detail;

        private ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sendAmbulanceButton = itemView.findViewById(R.id.SendAnAmbulanceButton);
            callButton = itemView.findViewById(R.id.CallButton);

            name = itemView.findViewById(R.id.EmergencyNameTextView);
            address = itemView.findViewById(R.id.AddressEmergencyTextView);
            latitude = itemView.findViewById(R.id.EmergencyLatitudeTextView);
            longitude = itemView.findViewById(R.id.EmrgencyLongitudeTextView);
            detail = itemView.findViewById(R.id.EmergencyDetailsTextView);

            photo = itemView.findViewById(R.id.EmergencyImageView);
        }
    }
}
