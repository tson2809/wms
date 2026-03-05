package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class PurchaseOrder {
    private int purchaseOrderId;
    private String poCode;
    private int supplierId;
    private String supplierName;
    private Date orderDate;
    private Date expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String status;
    private int createdBy;
    private String createdByName;
    private Integer approvedBy;
    private String approvedByName;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PurchaseOrder() {
    }

    public PurchaseOrder(int purchaseOrderId, String poCode, int supplierId, String supplierName,
                        Date orderDate, Date expectedDeliveryDate, BigDecimal totalAmount,
                        String status, int createdBy, String createdByName, Integer approvedBy,
                        String approvedByName, String notes, Timestamp createdAt, Timestamp updatedAt) {
        this.purchaseOrderId = purchaseOrderId;
        this.poCode = poCode;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.approvedBy = approvedBy;
        this.approvedByName = approvedByName;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
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

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "purchaseOrderId=" + purchaseOrderId +
                ", poCode='" + poCode + '\'' +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", orderDate=" + orderDate +
                ", expectedDeliveryDate=" + expectedDeliveryDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                ", notes='" + notes + '\'' +
                '}';
    }
}
