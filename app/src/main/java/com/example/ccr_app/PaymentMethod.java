package com.example.ccr_app;

public class PaymentMethod {
    private int paymentId;
    private String cardNumber;
    private String cardholderName;
    private String expiryDate;

    public PaymentMethod(int paymentId, String cardNumber, String cardholderName, String expiryDate) {
        this.paymentId = paymentId;
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
    }

    public int getPaymentId() { return paymentId; }
    public String getCardNumber() { return "Card: **** **** **** " + cardNumber.substring(cardNumber.length() - 4); }
    public String getCardholderName() { return "Cardholder: " + cardholderName; }
    public String getExpiryDate() { return "Expires: " + expiryDate; }
}