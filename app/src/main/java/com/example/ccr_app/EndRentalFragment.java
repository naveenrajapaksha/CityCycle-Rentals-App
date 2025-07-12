package com.example.ccr_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;

public class EndRentalFragment extends Fragment {

    private TextView tvBikeDetails, tvRentalDuration, tvRentalCost;
    private MaterialButton btnCancel, btnConfirmEnd;
    private DBHelper dbHelper;
    private long rentalId;
    private long startTime;
    private static final double HOURLY_RATE = 5.0; // $5 per hour (simplified pricing)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_rental, container, false);

        // Initialize views
        tvBikeDetails = view.findViewById(R.id.tvBikeDetails);
        tvRentalDuration = view.findViewById(R.id.tvRentalDuration);
        tvRentalCost = view.findViewById(R.id.tvRentalCost);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirmEnd = view.findViewById(R.id.btnConfirmEnd);

        // Initialize DBHelper
        dbHelper = new DBHelper(getActivity());

        // Retrieve rental details from arguments (passed from RentBikeFragment)
        Bundle args = getArguments();
        if (args != null) {
            rentalId = args.getLong("rentalId", -1);
            startTime = args.getLong("startTime", System.currentTimeMillis());
            loadRentalDetails();
        } else {
            Toast.makeText(getActivity(), "No rental data available", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return view;
        }

        // Cancel button logic
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Confirm End button logic
        btnConfirmEnd.setOnClickListener(v -> {
            endRental();
            getParentFragmentManager().popBackStack(); // Return to RentBikeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RentBikeFragment())
                    .commit();
        });

        return view;
    }

    private void loadRentalDetails() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT b." + DBHelper.BIKE_TYPE + ", s." + DBHelper.STATION_LOCATION + " " +
                        "FROM " + DBHelper.TABLE_RENTALS + " r " +
                        "JOIN " + DBHelper.TABLE_BIKES + " b ON r." + DBHelper.RENTAL_BIKE_ID + " = b." + DBHelper.BIKE_ID + " " +
                        "JOIN " + DBHelper.TABLE_STATIONS + " s ON b." + DBHelper.BIKE_STATION_ID + " = s." + DBHelper.STATION_ID + " " +
                        "WHERE r." + DBHelper.RENTAL_ID + " = ?",
                new String[]{String.valueOf(rentalId)});
        if (cursor.moveToFirst()) {
            String bikeType = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.BIKE_TYPE));
            String stationName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STATION_LOCATION));
            tvBikeDetails.setText("Bike: " + bikeType + " at " + stationName);
        }
        cursor.close();

        long elapsedMillis = System.currentTimeMillis() - startTime;
        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
        int hours = (int) (elapsedMillis / (1000 * 60 * 60));
        tvRentalDuration.setText(String.format("Duration: %02d:%02d:%02d", hours, minutes, seconds));

        double hoursElapsed = elapsedMillis / (1000.0 * 60 * 60);
        double cost = hoursElapsed * HOURLY_RATE;
        tvRentalCost.setText("Cost: $" + new DecimalFormat("0.00").format(cost));
    }

    private void endRental() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        double hoursElapsed = elapsedMillis / (1000.0 * 60 * 60);
        double cost = hoursElapsed * HOURLY_RATE;

        if (dbHelper.endRental(rentalId, cost)) {
            Toast.makeText(getActivity(), "Rental ended. Cost: $" + new DecimalFormat("0.00").format(cost), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Failed to end rental", Toast.LENGTH_SHORT).show();
        }
    }
}