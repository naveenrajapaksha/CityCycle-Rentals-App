package com.example.ccr_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

    private ArrayList<PaymentMethod> paymentMethods;
    private OnDeletePaymentListener listener;

    public interface OnDeletePaymentListener {
        void onDeletePayment(int paymentId);
    }

    public PaymentMethodAdapter(ArrayList<PaymentMethod> paymentMethods, OnDeletePaymentListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentMethod payment = paymentMethods.get(position);
        holder.tvCardNumber.setText(payment.getCardNumber());
        holder.tvCardholderName.setText(payment.getCardholderName());
        holder.tvExpiryDate.setText(payment.getExpiryDate());

        holder.btnDeletePayment.setOnClickListener(v -> listener.onDeletePayment(payment.getPaymentId()));
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardNumber, tvCardholderName, tvExpiryDate;
        MaterialButton btnDeletePayment;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCardNumber = itemView.findViewById(R.id.tvCardNumber);
            tvCardholderName = itemView.findViewById(R.id.tvCardholderName);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            btnDeletePayment = itemView.findViewById(R.id.btnDeletePayment);
        }
    }
}