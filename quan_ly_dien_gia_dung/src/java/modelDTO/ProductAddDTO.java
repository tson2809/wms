package modelDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO để nhận dữ liệu từ form product_add.jsp
 * Đơn giản hóa: chỉ chứa thông tin cơ bản + danh sách variants (SKU + Barcode)
 * 
 * @author laptop368
 */
public class ProductAddDTO {
    // Thông tin cơ bản (PRODUCTS table)
    private String productName;
    private String baseSku;
    private String baseBarcode;
    private Integer categoryId;
    private Integer brandId;
    private Integer supplierId;
    private Integer unitId;
    private String picture;
    private String description;

    // Danh sách variants đã chọn (SKU + Barcode + attribute values)
    private List<ProductVariantSimpleDTO> variants;

    /** Tên các thuộc tính (VD: Màu sắc, Kích thước) - theo thứ tự tương ứng attributeValues trong mỗi variant */
    private List<String> attributeNames = new ArrayList<>();

    public ProductAddDTO() {
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBaseSku() {
        return baseSku;
    }

    public void setBaseSku(String baseSku) {
        this.baseSku = baseSku;
    }

    public String getBaseBarcode() {
        return baseBarcode;
    }

    public void setBaseBarcode(String baseBarcode) {
        this.baseBarcode = baseBarcode;
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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductVariantSimpleDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantSimpleDTO> variants) {
        this.variants = variants;
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames != null ? attributeNames : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ProductAddDTO{" +
                "productName='" + productName + '\'' +
                ", baseSku='" + baseSku + '\'' +
                ", categoryId=" + categoryId +
                ", brandId=" + brandId +
                ", supplierId=" + supplierId +
                ", unitId=" + unitId +
                ", variantsCount=" + (variants != null ? variants.size() : 0) +
                '}';
    }
}
