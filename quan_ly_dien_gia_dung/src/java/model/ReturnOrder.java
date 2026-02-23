package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author laptop368
 */
public class ReturnOrder {
    private int returnOrderId;
    private String returnCode;
    private int supplierId;
    private Date returnDate;
    private BigDecimal totalRefundAmount;
    private String refundStatus; 
    private String status; 
    private String description;
    private Integer createdBy;
    private Integer receivedBy;
    private Timestamp createdAt;

    
    private String supplierName;
    private String createdByUserName;
    private String receivedByUserName;

    
    private int totalQuantity; 

    public ReturnOrder() {
    }

    public int getReturnOrderId() {
        return returnOrderId;
    }

    public void setReturnOrderId(int returnOrderId) {
        this.returnOrderId = returnOrderId;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getTotalRefundAmount() {
        return totalRefundAmount;
    }

    public void setTotalRefundAmount(BigDecimal totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(Integer receivedBy) {
        this.receivedBy = receivedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public String getReceivedByUserName() {
        return receivedByUserName;
    }

    public void setReceivedByUserName(String receivedByUserName) {
        this.receivedByUserName = receivedByUserName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
