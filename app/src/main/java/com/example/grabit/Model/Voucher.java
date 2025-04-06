// Voucher.java
package com.example.grabit.Model;

public class Voucher {
    private String orderId;
    private String orderAmount;
    private String orderDate;
    private String validity;
    private String transactionId;
    private String voucherCode;
    private String userId;

    // No-argument constructor required by Firestore
    public Voucher() {
        // Default constructor required for Firestore
    }

    public Voucher(String orderId, String orderAmount, String orderDate, String validity, String transactionId, String voucherCode, String userId) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.orderDate = orderDate;
        this.validity = validity;
        this.transactionId = transactionId;
        this.voucherCode = voucherCode;
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
