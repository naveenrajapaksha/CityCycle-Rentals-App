package com.example.ccr_app;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class ReserveBikeFragment extends Fragment {

    private EditText etSearchLocation;
    private DBHelper dbHelper;
    private BikeStationAdapter adapter;
    private ArrayList<BikeStation> stations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve_bike, container, false);

        etSearchLocation = view.findViewById(R.id.etSearchLocation);
        MaterialButton btnSearch = view.findViewById(R.id.btnSearch);
        RecyclerView rvBikeStations = view.findViewById(R.id.rvBikeStations);

        dbHelper = new DBHelper(getActivity());
        stations = new ArrayList<>();
        adapter = new BikeStationAdapter(stations, dbHelper);
        rvBikeStations.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBikeStations.setAdapter(adapter);

        loadStations("");

        btnSearch.setOnClickListener(v -> {
            String searchQuery = etSearchLocation.getText().toString().trim();
            loadStations(searchQuery);
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadStations(String searchQuery) {
        stations.clear();
        Cursor cursor = dbHelper.getAvailableStations(searchQuery);
        while (cursor.moveToNext()) {
            int stationId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STATION_ID));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STATION_LOCATION));
            int availableBikes = cursor.getInt(cursor.getColumnIndexOrThrow("availableBikes"));
            stations.add(new BikeStation(stationId, location, availableBikes));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        if (stations.isEmpty()) {
            Toast.makeText(getActivity(), "No stations found", Toast.LENGTH_SHORT).show();
        }
    }
}