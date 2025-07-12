package com.example.ccr_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.ViewHolder> {

    private ArrayList<Bike> bikes;
    private OnBikeRentListener listener;

    public interface OnBikeRentListener {
        void onBikeRent(int bikeId);
    }

    public BikeAdapter(ArrayList<Bike> bikes, OnBikeRentListener listener) {
        this.bikes = bikes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bike, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bike bike = bikes.get(position);
        holder.tvBikeType.setText(bike.getType());
        holder.tvStationName.setText("Station: " + bike.getStationName());

        holder.btnStartRental.setOnClickListener(v -> {
            listener.onBikeRent(bike.getBikeId());
            Toast.makeText(holder.itemView.getContext(), "Starting rental for " + bike.getType(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bikes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBikeType, tvStationName;
        MaterialButton btnStartRental;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBikeType = itemView.findViewById(R.id.tvBikeType);
            tvStationName = itemView.findViewById(R.id.tvStationName);
            btnStartRental = itemView.findViewById(R.id.btnStartRental);
        }
    }
}