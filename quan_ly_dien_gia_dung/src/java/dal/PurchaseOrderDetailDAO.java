package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.PurchaseOrderDetail;

public class PurchaseOrderDetailDAO extends DBContext {

    public List<PurchaseOrderDetail> getDetailsByPurchaseOrderId(int purchaseOrderId) {
        List<PurchaseOrderDetail> list = new ArrayList<>();
        String sql = "SELECT pod.*, pv.sku, p.product_name " +
                    "FROM purchase_order_details pod " +
                    "INNER JOIN product_variants pv ON pod.variant_id = pv.variant_id " +
                    "INNER JOIN products p ON pv.product_id = p.product_id " +
                    "WHERE pod.purchase_order_id = ?";

        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, purchaseOrderId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setPoDetailId(rs.getInt("po_detail_id"));
                detail.setPurchaseOrderId(rs.getInt("purchase_order_id"));
                detail.setVariantId(rs.getInt("variant_id"));
                detail.setSku(rs.getString("sku"));
                detail.setProductName(rs.getString("product_name"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                detail.setTotalAmount(rs.getBigDecimal("total_amount"));
                detail.setNotes(rs.getString("notes"));
                list.add(detail);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDetailDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public boolean insertDetail(PurchaseOrderDetail detail, Connection conn) throws SQLException {
        String sql = "INSERT INTO purchase_order_details (purchase_order_id, variant_id, " +
                    "quantity, unit_price, total_amount, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setInt(1, detail.getPurchaseOrderId());
            pre.setInt(2, detail.getVariantId());
            pre.setInt(3, detail.getQuantity());
            pre.setBigDecimal(4, detail.getUnitPrice());
            pre.setBigDecimal(5, detail.getTotalAmount());
            pre.setString(6, detail.getNotes());

            return pre.executeUpdate() > 0;
        }
    }

    public boolean deleteDetailsByPurchaseOrderId(int purchaseOrderId, Connection conn) throws SQLException {
        String sql = "DELETE FROM purchase_order_details WHERE purchase_order_id = ?";
        
        try (PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setInt(1, purchaseOrderId);
            pre.executeUpdate();
            return true;
        }
    }
}
