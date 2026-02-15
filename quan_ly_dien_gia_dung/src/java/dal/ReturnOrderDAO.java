/*
 * ReturnOrderDAO - DAO cho đơn trả hàng (return_orders).
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ReturnOrder;
import model.ReturnOrderDetail;
import model.ReturnOrderSerial;

/**
 *
 * @author laptop368
 */
public class ReturnOrderDAO extends DBContext {

  
    public int countReturnOrderWithSearchAndFilter(String keyword, Integer supplierId, String status,
            String refundStatus) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM return_orders ro " +
                        "LEFT JOIN suppliers s ON ro.supplier_id = s.supplier_id " +
                        "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (ro.return_code LIKE ? OR s.supplier_name LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (supplierId != null) {
            sql.append("AND ro.supplier_id = ? ");
            params.add(supplierId);
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND ro.status = ? ");
            params.add(status);
        }
        if (refundStatus != null && !refundStatus.isEmpty()) {
            sql.append("AND ro.refund_status = ? ");
            params.add(refundStatus);
        }

        try (PreparedStatement pre = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pre.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

   
    public List<ReturnOrder> getReturnOrderWithSearchAndFilter(String keyword, Integer supplierId, String status,
            String refundStatus, int offset, int size) {
        List<ReturnOrder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT ro.return_order_id, ro.return_code, ro.supplier_id, ro.return_date, ro.total_refund_amount,
                       ro.refund_status, ro.status, ro.description, ro.created_by, ro.received_by, ro.created_at,
                       s.supplier_name,
                       u1.full_name AS created_by_name,
                       u2.full_name AS received_by_name,
                       COALESCE(d.total_qty, 0) AS total_quantity
                FROM return_orders ro
                LEFT JOIN suppliers s ON ro.supplier_id = s.supplier_id
                LEFT JOIN users u1 ON ro.created_by = u1.user_id
                LEFT JOIN users u2 ON ro.received_by = u2.user_id
                LEFT JOIN (
                    SELECT return_order_id, SUM(quantity) AS total_qty
                    FROM return_order_details
                    GROUP BY return_order_id
                ) d ON ro.return_order_id = d.return_order_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (ro.return_code LIKE ? OR s.supplier_name LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (supplierId != null) {
            sql.append("AND ro.supplier_id = ? ");
            params.add(supplierId);
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND ro.status = ? ");
            params.add(status);
        }
        if (refundStatus != null && !refundStatus.isEmpty()) {
            sql.append("AND ro.refund_status = ? ");
            params.add(refundStatus);
        }

        sql.append("ORDER BY ro.return_order_id DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (PreparedStatement pre = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pre.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                ReturnOrder ro = new ReturnOrder();
                ro.setReturnOrderId(rs.getInt("return_order_id"));
                ro.setReturnCode(rs.getString("return_code"));
                ro.setSupplierId(rs.getInt("supplier_id"));
                ro.setReturnDate(rs.getDate("return_date"));
                ro.setTotalRefundAmount(rs.getBigDecimal("total_refund_amount"));
                ro.setRefundStatus(rs.getString("refund_status"));
                ro.setStatus(rs.getString("status"));
                ro.setDescription(rs.getString("description"));
                ro.setCreatedBy(rs.getObject("created_by") != null ? rs.getInt("created_by") : null);
                ro.setReceivedBy(rs.getObject("received_by") != null ? rs.getInt("received_by") : null);
                ro.setCreatedAt(rs.getTimestamp("created_at"));
                ro.setSupplierName(rs.getString("supplier_name"));
                ro.setCreatedByUserName(rs.getString("created_by_name"));
                ro.setReceivedByUserName(rs.getString("received_by_name"));
                ro.setTotalQuantity(rs.getInt("total_quantity"));
                list.add(ro);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    
    public boolean returnCodeExists(String returnCode) {
        if (returnCode == null || returnCode.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM return_orders WHERE return_code = ?";
        try (PreparedStatement pre = getConnection().prepareStatement(sql)) {
            pre.setString(1, returnCode.trim());
            ResultSet rs = pre.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

  
    public String searchProductsForReturnJson(String keyword, Integer supplierId) {
        StringBuilder sql = new StringBuilder("""
                SELECT pv.variant_id, pv.sku, pv.cost_price,
                       p.product_name, u.unit_name
                FROM product_variants pv
                INNER JOIN products p ON pv.product_id = p.product_id
                LEFT JOIN units u ON p.unit_id = u.unit_id
                WHERE pv.status = 'active' AND p.status = 'active'
                AND (pv.sku LIKE ? OR p.product_name LIKE ?)
                """);
        if (supplierId != null && supplierId > 0) {
            sql.append(" AND p.supplier_id = ? ");
        }
        sql.append(" ORDER BY p.product_name ");

        StringBuilder json = new StringBuilder("[");
        String pattern = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : "%";

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, pattern);
            ps.setString(idx++, pattern);
            if (supplierId != null && supplierId > 0) ps.setInt(idx++, supplierId);
            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;
                json.append("{")
                    .append("\"variantId\":").append(rs.getInt("variant_id")).append(",")
                    .append("\"code\":\"").append(escapeJson(rs.getString("sku"))).append("\",")
                    .append("\"sku\":\"").append(escapeJson(rs.getString("sku"))).append("\",")
                    .append("\"name\":\"").append(escapeJson(rs.getString("product_name"))).append("\",")
                    .append("\"productName\":\"").append(escapeJson(rs.getString("product_name"))).append("\",")
                    .append("\"unit\":\"").append(escapeJson(rs.getString("unit_name"))).append("\",")
                    .append("\"price\":").append(rs.getBigDecimal("cost_price")).append(",")
                    .append("\"costPrice\":").append(rs.getBigDecimal("cost_price"))
                    .append("}");
            }
        } catch (SQLException e) {
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

   
    public Integer getSerialIdByNumberAndVariant(int variantId, String serialNumber) {
        if (serialNumber == null || serialNumber.trim().isEmpty()) return null;
        String sql = "SELECT serial_id FROM product_serials WHERE variant_id = ? AND serial_number = ? AND status = 'in_stock'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.setString(2, serialNumber.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("serial_id");
        } catch (SQLException e) {
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

  
    public String getAvailableSerialsJson(int variantId) {
        String sql = "SELECT serial_id, serial_number FROM product_serials WHERE variant_id = ? AND status = 'in_stock' ORDER BY serial_number";
        StringBuilder json = new StringBuilder("[");
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;
                json.append("{\"serialId\":").append(rs.getInt("serial_id"))
                    .append(",\"serialNumber\":\"").append(escapeJson(rs.getString("serial_number"))).append("\"}");
            }
        } catch (SQLException e) {
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        json.append("]");
        return json.toString();
    }

  
    public boolean createReturnOrder(String returnCode, int supplierId, java.sql.Date returnDate,
            java.math.BigDecimal totalRefundAmount, String description, int createdBy,
            List<ReturnOrderDetail> details) {
        java.sql.Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlOrder = "INSERT INTO return_orders (return_code, supplier_id, return_date, total_refund_amount, refund_status, status, description, created_by) VALUES (?, ?, ?, ?, 'not_refunded', 'pending', ?, ?)";
            int returnOrderId;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, returnCode);
                ps.setInt(2, supplierId);
                ps.setDate(3, returnDate);
                ps.setBigDecimal(4, totalRefundAmount);
                ps.setString(5, description);
                ps.setInt(6, createdBy);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    returnOrderId = rs.getInt(1);
                } else {
                    throw new SQLException("Không lấy được return_order_id");
                }
            }

            String sqlDetail = "INSERT INTO return_order_details (return_order_id, variant_id, quantity, original_price, refund_price, total_refund) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlSerial = "INSERT INTO return_order_serials (return_detail_id, serial_id) VALUES (?, ?)";

            for (ReturnOrderDetail d : details) {
                try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    java.math.BigDecimal totalRefund = d.getRefundPrice().multiply(java.math.BigDecimal.valueOf(d.getQuantity()));
                    psDetail.setInt(1, returnOrderId);
                    psDetail.setInt(2, d.getVariantId());
                    psDetail.setInt(3, d.getQuantity());
                    psDetail.setBigDecimal(4, d.getOriginalPrice());
                    psDetail.setBigDecimal(5, d.getRefundPrice());
                    psDetail.setBigDecimal(6, totalRefund);
                    psDetail.executeUpdate();

                    ResultSet rsDetail = psDetail.getGeneratedKeys();
                    int detailId = rsDetail.next() ? rsDetail.getInt(1) : 0;

                    if (detailId > 0 && d.getSerials() != null && !d.getSerials().isEmpty()) {
                        try (PreparedStatement psSerial = conn.prepareStatement(sqlSerial)) {
                            for (ReturnOrderSerial serial : d.getSerials()) {
                                psSerial.setInt(1, detailId);
                                psSerial.setInt(2, serial.getSerialId());
                                psSerial.addBatch();
                            }
                            psSerial.executeBatch();
                        }
                    }
                }
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException e) { Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, e); }
            }
            Logger.getLogger(ReturnOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }
}
