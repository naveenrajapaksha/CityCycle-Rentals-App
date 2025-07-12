package com.example.ccr_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class BikeStationAdapter extends RecyclerView.Adapter<BikeStationAdapter.ViewHolder> {

    private ArrayList<BikeStation> stations;
    private DBHelper dbHelper;

    public BikeStationAdapter(ArrayList<BikeStation> stations, DBHelper dbHelper) {
        this.stations = stations;
        this.dbHelper = dbHelper;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bike_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BikeStation station = stations.get(position);
        holder.tvStationName.setText(station.getLocation());
        holder.tvStationLocation.setText("Location: " + station.getLocation());
        holder.tvAvailableBikes.setText("Available Bikes: " + station.getAvailableBikes());

        holder.btnReserve.setOnClickListener(v -> {
            if (station.getAvailableBikes() > 0) {
                if (dbHelper.reserveBike(station.getStationId())) {
                    Toast.makeText(holder.itemView.getContext(), "Bike reserved at " + station.getLocation(), Toast.LENGTH_SHORT).show();
                    // Create a new station object with updated available bikes
                    BikeStation updatedStation = new BikeStation(station.getStationId(), station.getLocation(), station.getAvailableBikes() - 1);
                    stations.set(position, updatedStation);
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Reservation failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "No bikes available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationName, tvStationLocation, tvAvailableBikes;
        MaterialButton btnReserve;

        public ViewHolder(View itemView) {
            super(itemView);
            tvStationName = itemView.findViewById(R.id.tvStationName);
            tvStationLocation = itemView.findViewById(R.id.tvStationLocation);
            tvAvailableBikes = itemView.findViewById(R.id.tvAvailableBikes);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }
}