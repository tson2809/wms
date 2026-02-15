package model;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author laptop368
 */
public class ReturnOrderDetail {
    private int returnDetailId;
    private int returnOrderId;
    private int variantId;
    private int quantity;
    private BigDecimal originalPrice;
    private BigDecimal refundPrice;
    private BigDecimal totalRefund;

    
    private String variantSku;
    private String productName;
    private String unitName;

    private List<ReturnOrderSerial> serials;

    public ReturnOrderDetail() {
    }

    public int getReturnDetailId() {
        return returnDetailId;
    }

    public void setReturnDetailId(int returnDetailId) {
        this.returnDetailId = returnDetailId;
    }

    public int getReturnOrderId() {
        return returnOrderId;
    }

    public void setReturnOrderId(int returnOrderId) {
        this.returnOrderId = returnOrderId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
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

    public List<ReturnOrderSerial> getSerials() {
        return serials;
    }

    public void setSerials(List<ReturnOrderSerial> serials) {
        this.serials = serials;
    }
}
