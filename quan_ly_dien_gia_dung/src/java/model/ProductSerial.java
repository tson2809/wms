package model;

import java.sql.Timestamp;

public class ProductSerial {
    private int serialId;
    private int variantId;
    private Integer receiptDetailId;
    private String serialNumber;
    private String status;
    private String notes;
    private Timestamp createdAt;

    public ProductSerial() {
    }

    public ProductSerial(int serialId, int variantId, Integer receiptDetailId, String serialNumber, String status, String notes, Timestamp createdAt) {
        this.serialId = serialId;
        this.variantId = variantId;
        this.receiptDetailId = receiptDetailId;
        this.serialNumber = serialNumber;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getSerialId() {
        return serialId;
    }

    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public Integer getReceiptDetailId() {
        return receiptDetailId;
    }

    public void setReceiptDetailId(Integer receiptDetailId) {
        this.receiptDetailId = receiptDetailId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
