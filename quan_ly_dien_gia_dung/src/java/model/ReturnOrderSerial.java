package model;

/**
 *
 * @author laptop368
 */
public class ReturnOrderSerial {
    private int returnSerialId;
    private int returnDetailId;
    private int serialId;

    
    private String serialNumber;

    public ReturnOrderSerial() {
    }

    public int getReturnSerialId() {
        return returnSerialId;
    }

    public void setReturnSerialId(int returnSerialId) {
        this.returnSerialId = returnSerialId;
    }

    public int getReturnDetailId() {
        return returnDetailId;
    }

    public void setReturnDetailId(int returnDetailId) {
        this.returnDetailId = returnDetailId;
    }

    public int getSerialId() {
        return serialId;
    }

    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
