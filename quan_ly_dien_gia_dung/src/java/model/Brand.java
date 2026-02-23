/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp; 

/**
 *
 * @author laptop368
 */
public class Brand {
    private int brandId;
    private String brandName;
    private String description;
    private String status;
    private Timestamp createdAt;

    public Brand() {
    }

    public Brand(int brandId, String brandName, String description, String status, Timestamp createdAt) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Brand{" + "brandId=" + brandId + ", brandName=" + brandName + ", description=" + description + ", status=" + status + ", createdAt=" + createdAt + '}';
    }
    
    
}
