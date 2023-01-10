package com.example.medhelp;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class ViewPatientsAdapter extends RecyclerView.Adapter<ViewPatientsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private View popupView;

    private Button closeButton;

    private PopupWindow popupWindow;

    private TextView nameTextView;
    private TextView ageTextView;
    private TextView phoneTextView;
    private TextView cnpTextView;
    private TextView detailsTextView;
    private TextView genderTextView;

    private ArrayList<String> detailsArray = new ArrayList<>();
    private ArrayList<String> phoneArray = new ArrayList<>();
    private ArrayList<String> genderArray = new ArrayList<>();
    private ArrayList<String> ageArray = new ArrayList<>();
    private ArrayList<String> cnpArray = new ArrayList<>();

    public ViewPatientsAdapter(Context context, ArrayList<String> names, ArrayList<String> usernames){
        this.context = context;
        this.names = names;
        this.usernames = usernames;
    }

    @NonNull
    @Override
    public ViewPatientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_patients_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewPatientsAdapter.ViewHolder holder, int position) {

        holder.counter.setText(position + 1 + ".");
        holder.nameTextView.setText(names.get(position));

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("PersonalDataPatients").child(usernames.get(holder.getAdapterPosition()));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ageArray.add(snapshot.child("age").getValue(String.class));
                phoneArray.add(snapshot.child("phone").getValue(String.class));
                cnpArray.add(snapshot.child("cnp").getValue(String.class));
                genderArray.add(snapshot.child("gender").getValue(String.class));
                detailsArray.add(snapshot.child("details").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.seeMore.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView counter;
        TextView  nameTextView;
        Button seeMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            counter = itemView.findViewById(R.id.CounterTextView);
            nameTextView = itemView.findViewById(R.id.NameItemList);
            seeMore = itemView.findViewById(R.id.SeeMoreDetailsButton);
        }
    }
}
