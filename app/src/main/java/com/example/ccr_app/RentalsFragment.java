package com.example.ccr_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RentalsFragment extends Fragment {

    private RecyclerView rvRentalHistory;
    private DBHelper dbHelper;
    private RentalHistoryAdapter adapter;
    private ArrayList<Rental> rentals;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rentals, container, false);

        // Initialize views
        rvRentalHistory = view.findViewById(R.id.rvRentalHistory);

        // Initialize DBHelper and data
        dbHelper = new DBHelper(getActivity());
        rentals = new ArrayList<>();
        adapter = new RentalHistoryAdapter(rentals);
        rvRentalHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRentalHistory.setAdapter(adapter);

        // Load rental history
        loadRentalHistory();

        return view;
    }

    private void loadRentalHistory() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);
        if (userEmail == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        rentals.clear();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT r." + DBHelper.RENTAL_ID + ", b." + DBHelper.BIKE_TYPE + ", s." + DBHelper.STATION_LOCATION + ", " +
                        "(r." + DBHelper.RENTAL_END_TIME + " - r." + DBHelper.RENTAL_START_TIME + ") as duration, " +
                        "r." + DBHelper.RENTAL_COST + ", r." + DBHelper.RENTAL_START_TIME + " " +
                        "FROM " + DBHelper.TABLE_RENTALS + " r " +
                        "JOIN " + DBHelper.TABLE_BIKES + " b ON r." + DBHelper.RENTAL_BIKE_ID + " = b." + DBHelper.BIKE_ID + " " +
                        "JOIN " + DBHelper.TABLE_STATIONS + " s ON b." + DBHelper.BIKE_STATION_ID + " = s." + DBHelper.STATION_ID + " " +
                        "WHERE r." + DBHelper.RENTAL_USER_EMAIL + " = ? AND r." + DBHelper.RENTAL_END_TIME + " IS NOT NULL",
                new String[]{userEmail});
        while (cursor.moveToNext()) {
            int rentalId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RENTAL_ID));
            String bikeType = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.BIKE_TYPE));
            String stationName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STATION_LOCATION));
            long durationMillis = cursor.getLong(cursor.getColumnIndexOrThrow("duration"));
            double cost = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.RENTAL_COST));
            long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.RENTAL_START_TIME));
            rentals.add(new Rental(rentalId, bikeType, stationName, durationMillis, cost, startTime));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        if (rentals.isEmpty()) {
            Toast.makeText(getActivity(), "No rental history available", Toast.LENGTH_SHORT).show();
        }
    }
}