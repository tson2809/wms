/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author thais
 */
public class GoodsReceipt {
    private int receiptId;
    private String receiptCode;
    private Integer purchaseOrderId;
    private Date receiptDate;
    private BigDecimal totalAmount;
    private String status; 
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Supplier supplier;
    private User createdByUser;
    private User approvedByUser;

    public GoodsReceipt() {
    }

    public GoodsReceipt(int receiptId, String receiptCode, Integer purchaseOrderId, 
                       Date receiptDate, BigDecimal totalAmount, String status, 
                       String notes, Timestamp createdAt, Timestamp updatedAt, 
                       Supplier supplier, User createdByUser, User approvedByUser) {
        this.receiptId = receiptId;
        this.receiptCode = receiptCode;
        this.purchaseOrderId = purchaseOrderId;
        this.receiptDate = receiptDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.supplier = supplier;
        this.createdByUser = createdByUser;
        this.approvedByUser = approvedByUser;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public Integer getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Integer purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public User getApprovedByUser() {
        return approvedByUser;
    }

    public void setApprovedByUser(User approvedByUser) {
        this.approvedByUser = approvedByUser;
    }

    @Override
    public String toString() {
        return "GoodsReceipt{" + "receiptId=" + receiptId + ", receiptCode=" + receiptCode + 
               ", purchaseOrderId=" + purchaseOrderId + ", receiptDate=" + receiptDate + 
               ", totalAmount=" + totalAmount + ", status=" + status + 
               ", notes=" + notes + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + 
               ", supplier=" + supplier + ", createdByUser=" + createdByUser + 
               ", approvedByUser=" + approvedByUser + '}';
    }
}
