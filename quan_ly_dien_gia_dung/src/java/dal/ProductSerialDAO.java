package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ProductSerial;

public class ProductSerialDAO extends DBContext {

    public List<ProductSerial> getInStockSerialsByVariant(int variantId) {
        List<ProductSerial> list = new ArrayList<>();
        String sql = "SELECT * FROM product_serials WHERE variant_id = ? AND status = 'in_stock' ORDER BY created_at ASC";
        
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductSerial serial = new ProductSerial();
                serial.setSerialId(rs.getInt("serial_id"));
                serial.setVariantId(rs.getInt("variant_id"));
                serial.setReceiptDetailId(rs.getObject("receipt_detail_id") != null ? rs.getInt("receipt_detail_id") : null);
                serial.setSerialNumber(rs.getString("serial_number"));
                serial.setStatus(rs.getString("status"));
                serial.setNotes(rs.getString("notes"));
                serial.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(serial);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateSerialsStatus(List<Integer> serialIds, String status, String note) {
        if (serialIds == null || serialIds.isEmpty()) return false;
        
        String sql = "UPDATE product_serials SET status = ?, notes = ? WHERE serial_id = ?";
        
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            
            for (Integer id : serialIds) {
                ps.setString(1, status);
                ps.setString(2, note);
                ps.setInt(3, id);
                ps.addBatch();
            }
            
            ps.executeBatch();
            con.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
