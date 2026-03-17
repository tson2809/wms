/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;

/**
 *
 * @author thais
 */
public class GoodsIssueDetail {
    private int issueDetailId;
    private int issueId;
    private int variantId;
    private String variantSku;
    private String productName;
    private String unitName;
    private int quantity;
    private String notes;
    private List<String> serials;

    public GoodsIssueDetail() {}

    public int getIssueDetailId() { return issueDetailId; }
    public void setIssueDetailId(int issueDetailId) { this.issueDetailId = issueDetailId; }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public String getVariantSku() { return variantSku; }
    public void setVariantSku(String variantSku) { this.variantSku = variantSku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getSerials() { return serials; }
    public void setSerials(List<String> serials) { this.serials = serials; }
}
