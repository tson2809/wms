/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author laptop368
 */
public class ProductView {
    
    private int productId;
    private Integer categoryId; 
    private Integer brandId; 
    private String productName;
    private String categoryName;
    private String brandName;
    private String picture;
    private String status;

    
    private int variantCount;

    public ProductView() {
    }

    public ProductView(int productId, Integer categoryId, Integer brandId, String productName,
            String categoryName, String brandName, String picture, String status, int variantCount) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.picture = picture;
        this.status = status;
        this.variantCount = variantCount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVariantCount() {
        return variantCount;
    }

    public void setVariantCount(int variantCount) {
        this.variantCount = variantCount;
    }

    @Override
    public String toString() {
        return "ProductView{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", status='" + status + '\'' +
                ", variantCount=" + variantCount +
                '}';
    }
}
