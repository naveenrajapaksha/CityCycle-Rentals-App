package com.example.ccr_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class RentBikeFragment extends Fragment implements BikeAdapter.OnBikeRentListener {

    private RecyclerView rvAvailableBikes;
    private CardView cardOngoingRental;
    private TextView tvBikeDetails, tvRentalDuration, tvRentalCost;
    private MaterialButton btnEndRental;
    private DBHelper dbHelper;
    private BikeAdapter adapter;
    private ArrayList<Bike> bikes;
    private Handler handler;
    private Runnable updateTimerRunnable;
    private long rentalId = -1;
    private long startTime;
    private static final double HOURLY_RATE = 5.0; // $5 per hour (simplified pricing)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent_bike, container, false);

        // Initialize views
        rvAvailableBikes = view.findViewById(R.id.rvAvailableBikes);
        cardOngoingRental = view.findViewById(R.id.cardOngoingRental);
        tvBikeDetails = view.findViewById(R.id.tvBikeDetails);
        tvRentalDuration = view.findViewById(R.id.tvRentalDuration);
        tvRentalCost = view.findViewById(R.id.tvRentalCost);
        btnEndRental = view.findViewById(R.id.btnEndRental);

        // Initialize DBHelper and data
        dbHelper = new DBHelper(getActivity());
        bikes = new ArrayList<>();
        adapter = new BikeAdapter(bikes, this);
        rvAvailableBikes.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAvailableBikes.setAdapter(adapter);

        // Initialize timer handler
        handler = new Handler();

        // Check for ongoing rental
        checkOngoingRental();

        // Load available bikes if no ongoing rental
        if (rentalId == -1) {
            loadAvailableBikes();
        }

        // End rental button logic
        btnEndRental.setOnClickListener(v -> endRental());

        return view;
    }

    private void checkOngoingRental() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);
        if (userEmail == null) return;

        Cursor cursor = dbHelper.getOngoingRental(userEmail);
        if (cursor.moveToFirst()) {
            rentalId = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.RENTAL_ID));
            int bikeId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RENTAL_BIKE_ID));
            startTime = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.RENTAL_START_TIME));

            // Fetch bike details
            Cursor bikeCursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT b." + DBHelper.BIKE_TYPE + ", s." + DBHelper.STATION_LOCATION + " " +
                            "FROM " + DBHelper.TABLE_BIKES + " b " +
                            "JOIN " + DBHelper.TABLE_STATIONS + " s ON b." + DBHelper.BIKE_STATION_ID + " = s." + DBHelper.STATION_ID + " " +
                            "WHERE b." + DBHelper.BIKE_ID + " = ?",
                    new String[]{String.valueOf(bikeId)});
            if (bikeCursor.moveToFirst()) {
                String bikeType = bikeCursor.getString(bikeCursor.getColumnIndexOrThrow(DBHelper.BIKE_TYPE));
                String stationName = bikeCursor.getString(bikeCursor.getColumnIndexOrThrow(DBHelper.STATION_LOCATION));
                tvBikeDetails.setText("Bike: " + bikeType + " at " + stationName);
            }
            bikeCursor.close();

            cardOngoingRental.setVisibility(View.VISIBLE);
            rvAvailableBikes.setVisibility(View.GONE);
            startTimer();
        }
        cursor.close();
    }

    private void loadAvailableBikes() {
        bikes.clear();
        Cursor cursor = dbHelper.getAvailableBikes();
        while (cursor.moveToNext()) {
            int bikeId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.BIKE_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.BIKE_TYPE));
            String stationName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STATION_LOCATION));
            bikes.add(new Bike(bikeId, type, stationName));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        if (bikes.isEmpty()) {
            Toast.makeText(getActivity(), "No bikes available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBikeRent(int bikeId) {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);
        if (userEmail == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        rentalId = dbHelper.startRental(userEmail, bikeId);
        if (rentalId != -1) {
            startTime = System.currentTimeMillis();
            cardOngoingRental.setVisibility(View.VISIBLE);
            rvAvailableBikes.setVisibility(View.GONE);

            // Update bike details
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT b." + DBHelper.BIKE_TYPE + ", s." + DBHelper.STATION_LOCATION + " " +
                            "FROM " + DBHelper.TABLE_BIKES + " b " +
                            "JOIN " + DBHelper.TABLE_STATIONS + " s ON b." + DBHelper.BIKE_STATION_ID + " = s." + DBHelper.STATION_ID + " " +
                            "WHERE b." + DBHelper.BIKE_ID + " = ?",
                    new String[]{String.valueOf(bikeId)});
            if (cursor.moveToFirst()) {
                String bikeType = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.BIKE_TYPE));
                String stationName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STATION_LOCATION));
                tvBikeDetails.setText("Bike: " + bikeType + " at " + stationName);
            }
            cursor.close();

            startTimer();
        } else {
            Toast.makeText(getActivity(), "Failed to start rental", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer() {
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedMillis / 1000) % 60;
                int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
                int hours = (int) (elapsedMillis / (1000 * 60 * 60));
                tvRentalDuration.setText(String.format("Duration: %02d:%02d:%02d", hours, minutes, seconds));

                // Calculate cost (simplified: $5/hour)
                double hoursElapsed = elapsedMillis / (1000.0 * 60 * 60);
                double cost = hoursElapsed * HOURLY_RATE;
                tvRentalCost.setText("Cost: $" + new DecimalFormat("0.00").format(cost));

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTimerRunnable);
    }

    // Replace the endRental method in RentBikeFragment.java
    private void endRental() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        double hoursElapsed = elapsedMillis / (1000.0 * 60 * 60);
        double cost = hoursElapsed * HOURLY_RATE;

        Bundle args = new Bundle();
        args.putLong("rentalId", rentalId);
        args.putLong("startTime", startTime);

        EndRentalFragment endRentalFragment = new EndRentalFragment();
        endRentalFragment.setArguments(args);

        handler.removeCallbacks(updateTimerRunnable);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, endRentalFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (updateTimerRunnable != null) {
            handler.removeCallbacks(updateTimerRunnable);
        }
    }
}