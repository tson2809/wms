/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author thais
 */
public class GoodsReceiptDetail {
    private int receiptDetailId;
    private int receiptId;
    private int variantId;
    private String variantSku;
    private String productName;
    private String variantPicture;
    private String unitName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String notes;
    private List<String> serials;

    public GoodsReceiptDetail() {
    }

    public GoodsReceiptDetail(int receiptDetailId, int receiptId, int variantId, int quantity, 
                             BigDecimal unitPrice, BigDecimal totalAmount, String notes) {
        this.receiptDetailId = receiptDetailId;
        this.receiptId = receiptId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.notes = notes;
    }

    public int getReceiptDetailId() {
        return receiptDetailId;
    }

    public void setReceiptDetailId(int receiptDetailId) {
        this.receiptDetailId = receiptDetailId;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
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

    public String getVariantPicture() {
        return variantPicture;
    }

    public void setVariantPicture(String variantPicture) {
        this.variantPicture = variantPicture;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getSerials() {
        return serials;
    }

    public void setSerials(List<String> serials) {
        this.serials = serials;
    }
    
    public double getSubtotal() {
        if (unitPrice != null) {
            return unitPrice.doubleValue() * quantity;
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "GoodsReceiptDetail{" + "receiptDetailId=" + receiptDetailId + ", receiptId=" + receiptId + 
               ", variantId=" + variantId + ", variantSku=" + variantSku + ", productName=" + productName + 
               ", unitName=" + unitName + ", quantity=" + quantity + ", unitPrice=" + unitPrice + 
               ", totalAmount=" + totalAmount + ", notes=" + notes + ", serials=" + serials + '}';
    }
}
