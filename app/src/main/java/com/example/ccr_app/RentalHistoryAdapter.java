package com.example.ccr_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RentalHistoryAdapter extends RecyclerView.Adapter<RentalHistoryAdapter.ViewHolder> {

    private ArrayList<Rental> rentals;

    public RentalHistoryAdapter(ArrayList<Rental> rentals) {
        this.rentals = rentals;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Rental rental = rentals.get(position);
        holder.tvBikeType.setText(rental.getBikeType());
        holder.tvStationName.setText("Station: " + rental.getStationName());
        holder.tvDuration.setText("Duration: " + rental.getDuration());
        holder.tvCost.setText("Cost: " + rental.getCost());
        holder.tvDate.setText("Date: " + rental.getDate());
    }

    @Override
    public int getItemCount() {
        return rentals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBikeType, tvStationName, tvDuration, tvCost, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBikeType = itemView.findViewById(R.id.tvBikeType);
            tvStationName = itemView.findViewById(R.id.tvStationName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}