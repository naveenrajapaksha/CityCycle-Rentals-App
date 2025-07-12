package com.example.ccr_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvEmail, tvContact, tvDob, tvGender;
    private ImageView imgProfile;
    private MaterialButton btnLogout;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvContact = view.findViewById(R.id.tvContact);
        tvDob = view.findViewById(R.id.tvDob);
        tvGender = view.findViewById(R.id.tvGender);
        imgProfile = view.findViewById(R.id.imgProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        dbHelper = new DBHelper(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "User");
        loadUserData(email);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().apply();
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    private void loadUserData(String email) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " + DBHelper.COLUMN_EMAIL + " = ?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
            String contact = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CONTACT));
            String dob = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DOB));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENDER));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROFILE_IMAGE));

            tvProfileName.setText(name);
            tvEmail.setText("Email: " + email);
            tvContact.setText("Contact: " + contact);
            tvDob.setText("Date of Birth: " + dob);
            tvGender.setText("Gender: " + gender);

            if (imageBytes != null) {
            }
        }
        cursor.close();
    }
}