package com.example.ccr_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvRentalHistory;
    private EditText etSearchStation;
    private MaterialButton btnSearch, btnReserveBike, btnRentBike;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        etSearchStation = view.findViewById(R.id.etSearchStation);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnReserveBike = view.findViewById(R.id.btnReserveBike);
        btnRentBike = view.findViewById(R.id.btnRentBike);
        tvRentalHistory = view.findViewById(R.id.tvRentalHistory);

        dbHelper = new DBHelper(getActivity());

        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String email = prefs.getString("email", "User");

            String username = email.contains("@") ? email.split("@")[0] : email;
            tvWelcome.setText("Welcome, " + username + "!");
        }

        btnSearch.setOnClickListener(v -> {
            String searchQuery = etSearchStation.getText().toString().trim();
            if (searchQuery.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a station name", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Searching for: " + searchQuery, Toast.LENGTH_SHORT).show();
                // TODO: Add search logic here
            }
        });

        btnReserveBike.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReserveBikeFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnRentBike.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RentBikeFragment())
                    .addToBackStack(null)
                    .commit();
        });

        tvRentalHistory.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Viewing Rental History", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RentalsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
