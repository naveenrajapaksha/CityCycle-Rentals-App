package com.example.ccr_app;

public class PricingPlan {
    private int planId;
    private String planType;
    private double rate;

    public PricingPlan(int planId, String planType, double rate) {
        this.planId = planId;
        this.planType = planType;
        this.rate = rate;
    }

    public String getPlanType() { return planType; }
    public String getRate() { return String.format("$%.2f", rate); }
}