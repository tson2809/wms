package modelDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO đơn giản cho variant sản phẩm
 * Chứa SKU, Barcode và danh sách giá trị thuộc tính (attribute values)
 * 
 * @author laptop368
 */
public class ProductVariantSimpleDTO {
    private String sku;
    private String barcode;
    /** Giá trị thuộc tính theo thứ tự (tương ứng attributeNames trong ProductAddDTO) */
    private List<String> attributeValues = new ArrayList<>();

    public ProductVariantSimpleDTO() {
    }

    public ProductVariantSimpleDTO(String sku, String barcode) {
        this.sku = sku;
        this.barcode = barcode;
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

    public List<String> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<String> attributeValues) {
        this.attributeValues = attributeValues != null ? attributeValues : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ProductVariantSimpleDTO{" +
                "sku='" + sku + '\'' +
                ", barcode='" + barcode + '\'' +
                ", attributeValues=" + attributeValues +
                '}';
    }
}
