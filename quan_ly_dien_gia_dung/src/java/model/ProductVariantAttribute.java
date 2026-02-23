package model;

/**
 *
 * @author laptop368
 */
public class ProductVariantAttribute {
    private int attributeId;
    private int variantId;
    private String attributeName; 
    private String attributeValue; 

    public ProductVariantAttribute() {
    }

    public ProductVariantAttribute(int attributeId, int variantId, String attributeName, String attributeValue) {
        this.attributeId = attributeId;
        this.variantId = variantId;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public ProductVariantAttribute(int variantId, String attributeName, String attributeValue) {
        this.variantId = variantId;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    // Getters and Setters
    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return "ProductVariantAttribute{" +
                "attributeId=" + attributeId +
                ", variantId=" + variantId +
                ", attributeName='" + attributeName + '\'' +
                ", attributeValue='" + attributeValue + '\'' +
                '}';
    }
}
