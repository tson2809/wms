/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author laptop368
 */
public class Unit {
     private int unitId;
    private String unitName;
    private String status;

    public Unit() {
    }

    public Unit(int unitId, String unitName, String status) {
        this.unitId = unitId;
        this.unitName = unitName;
        this.status = status;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Unit{" + "unitId=" + unitId + ", unitName=" + unitName + ", status=" + status + '}';
    }
    
}
