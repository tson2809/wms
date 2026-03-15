package modelDTO;

import java.util.ArrayList;
import java.util.List;

/**

 * 
 * @author laptop368
 */
public class ProductVariantSimpleDTO {
   
    private Integer variantId;
    private String sku;
    private String barcode;
    private String variantPicture;
    /** Giá trị thuộc tính theo thứ tự (tương ứng attributeNames trong ProductAddDTO) */
    private List<String> attributeValues = new ArrayList<>();

    public ProductVariantSimpleDTO() {
    }

    public ProductVariantSimpleDTO(String sku, String barcode) {
        this.sku = sku;
        this.barcode = barcode;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
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
