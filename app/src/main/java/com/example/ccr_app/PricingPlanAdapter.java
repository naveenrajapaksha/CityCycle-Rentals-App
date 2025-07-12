package com.example.ccr_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PricingPlanAdapter extends RecyclerView.Adapter<PricingPlanAdapter.ViewHolder> {

    private ArrayList<PricingPlan> pricingPlans;

    public PricingPlanAdapter(ArrayList<PricingPlan> pricingPlans) {
        this.pricingPlans = pricingPlans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pricing_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PricingPlan plan = pricingPlans.get(position);
        holder.tvPlanType.setText(plan.getPlanType());
        holder.tvPlanRate.setText(plan.getRate());
    }

    @Override
    public int getItemCount() {
        return pricingPlans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanType, tvPlanRate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlanType = itemView.findViewById(R.id.tvPlanType);
            tvPlanRate = itemView.findViewById(R.id.tvPlanRate);
        }
    }
}