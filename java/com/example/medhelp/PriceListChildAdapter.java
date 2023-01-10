package com.example.medhelp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PriceListChildAdapter extends RecyclerView.Adapter<PriceListChildAdapter.ChildViewHolder> {
    private ArrayList<String> priceListChild;

    public PriceListChildAdapter(ArrayList<String> priceListChild) {
        this.priceListChild = priceListChild;
    }

    @NonNull
    @Override
    public PriceListChildAdapter.ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_list_child_layout, parent, false);
        return new PriceListChildAdapter.ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListChildAdapter.ChildViewHolder holder, int position) {
        holder.textView.setText(priceListChild.get(position));
    }

    @Override
    public int getItemCount() {
        return priceListChild.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.ChildTextView);
        }
    }
}
