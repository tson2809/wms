package model;

import java.math.BigDecimal;

/**
 * 
 * @author laptop368
 */
public class SalesReturnDetail {
    private int salesReturnDetailId;
    private int salesReturnId;
    private int variantId;
    private String variantSku;
    private String productName;
    private String unitName;
    private int quantity;
    private BigDecimal originalPrice;
    private BigDecimal refundPrice;
    private BigDecimal totalRefund;

    public SalesReturnDetail() {
    }

    public int getSalesReturnDetailId() {
        return salesReturnDetailId;
    }

    public void setSalesReturnDetailId(int salesReturnDetailId) {
        this.salesReturnDetailId = salesReturnDetailId;
    }

    public int getSalesReturnId() {
        return salesReturnId;
    }

    public void setSalesReturnId(int salesReturnId) {
        this.salesReturnId = salesReturnId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getRefundPrice() {
        return refundPrice;
    }

    public void setRefundPrice(BigDecimal refundPrice) {
        this.refundPrice = refundPrice;
    }

    public BigDecimal getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }
}

