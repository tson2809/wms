package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author laptop368
 */
public class ProductVariant {
    private int variantId;
    private int productId;
    private String sku;
    private String barcode;
    private String variantPicture;
    private BigDecimal salePrice;
    private BigDecimal costPrice;
    private int quantity;
    private String status; 
    private Timestamp createdAt;

    public ProductVariant() {
    }

    public ProductVariant(int variantId, int productId, String sku, String barcode,
            String variantPicture, BigDecimal salePrice, BigDecimal costPrice,
            int quantity, String status, Timestamp createdAt) {
        this.variantId = variantId;
        this.productId = productId;
        this.sku = sku;
        this.barcode = barcode;
        this.variantPicture = variantPicture;
        this.salePrice = salePrice;
        this.costPrice = costPrice;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getVariantPicture() {
        return variantPicture;
    }

    public void setVariantPicture(String variantPicture) {
        this.variantPicture = variantPicture;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "variantId=" + variantId +
                ", productId=" + productId +
                ", sku='" + sku + '\'' +
                ", barcode='" + barcode + '\'' +
                ", salePrice=" + salePrice +
                ", costPrice=" + costPrice +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}
