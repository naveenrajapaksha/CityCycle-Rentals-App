package com.example.ccr_app;

import android.content.Context;
import android.content.SharedPreferences;
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

public class PricingPaymentFragment extends Fragment implements PaymentMethodAdapter.OnDeletePaymentListener {

    private RecyclerView rvPricingPlans, rvPaymentMethods;
    private EditText etCardNumber, etCardholderName, etExpiryDate, etCvv;
    private MaterialButton btnAddPayment;
    private DBHelper dbHelper;
    private PricingPlanAdapter pricingAdapter;
    private PaymentMethodAdapter paymentAdapter;
    private ArrayList<PricingPlan> pricingPlans;
    private ArrayList<PaymentMethod> paymentMethods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pricing_payment, container, false);

        // Initialize views
        rvPricingPlans = view.findViewById(R.id.rvPricingPlans);
        rvPaymentMethods = view.findViewById(R.id.rvPaymentMethods);
        etCardNumber = view.findViewById(R.id.etCardNumber);
        etCardholderName = view.findViewById(R.id.etCardholderName);
        etExpiryDate = view.findViewById(R.id.etExpiryDate);
        etCvv = view.findViewById(R.id.etCvv);
        btnAddPayment = view.findViewById(R.id.btnAddPayment);

        // Initialize DBHelper and data
        dbHelper = new DBHelper(getActivity());
        pricingPlans = new ArrayList<>();
        paymentMethods = new ArrayList<>();
        pricingAdapter = new PricingPlanAdapter(pricingPlans);
        paymentAdapter = new PaymentMethodAdapter(paymentMethods, this);
        rvPricingPlans.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPricingPlans.setAdapter(pricingAdapter);
        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPaymentMethods.setAdapter(paymentAdapter);

        // Load data
        loadPricingPlans();
        loadPaymentMethods();

        // Add payment method button logic
        btnAddPayment.setOnClickListener(v -> addPaymentMethod());

        return view;
    }

    private void loadPricingPlans() {
        pricingPlans.clear();
        Cursor cursor = dbHelper.getPricingPlans();
        while (cursor.moveToNext()) {
            int planId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PLAN_ID));
            String planType = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PLAN_TYPE));
            double rate = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.PLAN_RATE));
            pricingPlans.add(new PricingPlan(planId, planType, rate));
        }
        cursor.close();
        pricingAdapter.notifyDataSetChanged();
    }

    private void loadPaymentMethods() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);
        if (userEmail == null) return;

        paymentMethods.clear();
        Cursor cursor = dbHelper.getPaymentMethods(userEmail);
        while (cursor.moveToNext()) {
            int paymentId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PAYMENT_ID));
            String cardNumber = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PAYMENT_CARD_NUMBER));
            String cardholderName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PAYMENT_CARDHOLDER_NAME));
            String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PAYMENT_EXPIRY_DATE));
            paymentMethods.add(new PaymentMethod(paymentId, cardNumber, cardholderName, expiryDate));
        }
        cursor.close();
        paymentAdapter.notifyDataSetChanged();
    }

    private void addPaymentMethod() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);
        if (userEmail == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String cardNumber = etCardNumber.getText().toString().trim();
        String cardholderName = etCardholderName.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (cardNumber.isEmpty() || cardholderName.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic validation (you can enhance this)
        if (!cardNumber.matches("\\d{16}")) {
            Toast.makeText(getActivity(), "Invalid card number (must be 16 digits)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(getActivity(), "Invalid expiry date (MM/YY)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cvv.matches("\\d{3}")) {
            Toast.makeText(getActivity(), "Invalid CVV (must be 3 digits)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addPaymentMethod(userEmail, cardNumber, cardholderName, expiryDate, cvv)) {
            Toast.makeText(getActivity(), "Payment method added", Toast.LENGTH_SHORT).show();
            etCardNumber.setText("");
            etCardholderName.setText("");
            etExpiryDate.setText("");
            etCvv.setText("");
            loadPaymentMethods();
        } else {
            Toast.makeText(getActivity(), "Failed to add payment method", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeletePayment(int paymentId) {
        if (dbHelper.deletePaymentMethod(paymentId)) {
            Toast.makeText(getActivity(), "Payment method deleted", Toast.LENGTH_SHORT).show();
            loadPaymentMethods();
        } else {
            Toast.makeText(getActivity(), "Failed to delete payment method", Toast.LENGTH_SHORT).show();
        }
    }
}