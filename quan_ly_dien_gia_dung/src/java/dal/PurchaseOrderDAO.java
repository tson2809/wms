package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.PurchaseOrder;

public class PurchaseOrderDAO extends DBContext {

    public List<PurchaseOrder> getPurchaseOrdersWithFilter(String status, Integer supplierId, 
            Date fromDate, Date toDate, String keyword, int offset, int limit) {
        List<PurchaseOrder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT po.*, s.supplier_name, ")
           .append("u1.full_name as created_by_name, u2.full_name as approved_by_name ")
           .append("FROM purchase_orders po ")
           .append("INNER JOIN suppliers s ON po.supplier_id = s.supplier_id ")
           .append("LEFT JOIN users u1 ON po.created_by = u1.user_id ")
           .append("LEFT JOIN users u2 ON po.approved_by = u2.user_id ")
           .append("WHERE 1=1 ");

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND po.status = ? ");
        }
        if (supplierId != null && supplierId > 0) {
            sql.append("AND po.supplier_id = ? ");
        }
        if (fromDate != null) {
            sql.append("AND po.order_date >= ? ");
        }
        if (toDate != null) {
            sql.append("AND po.order_date <= ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
        }
        sql.append("ORDER BY po.created_at DESC LIMIT ? OFFSET ?");

        try (PreparedStatement pre = this.getConnection().prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                pre.setString(paramIndex++, status);
            }
            if (supplierId != null && supplierId > 0) {
                pre.setInt(paramIndex++, supplierId);
            }
            if (fromDate != null) {
                pre.setDate(paramIndex++, fromDate);
            }
            if (toDate != null) {
                pre.setDate(paramIndex++, toDate);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                pre.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            pre.setInt(paramIndex++, limit);
            pre.setInt(paramIndex++, offset);

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToPurchaseOrder(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public int countPurchaseOrdersWithFilter(String status, Integer supplierId, 
            Date fromDate, Date toDate, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM purchase_orders po WHERE 1=1 ");

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND po.status = ? ");
        }
        if (supplierId != null && supplierId > 0) {
            sql.append("AND po.supplier_id = ? ");
        }
        if (fromDate != null) {
            sql.append("AND po.order_date >= ? ");
        }
        if (toDate != null) {
            sql.append("AND po.order_date <= ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
        }

        try (PreparedStatement pre = this.getConnection().prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                pre.setString(paramIndex++, status);
            }
            if (supplierId != null && supplierId > 0) {
                pre.setInt(paramIndex++, supplierId);
            }
            if (fromDate != null) {
                pre.setDate(paramIndex++, fromDate);
            }
            if (toDate != null) {
                pre.setDate(paramIndex++, toDate);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                pre.setString(paramIndex++, "%" + keyword.trim() + "%");
            }

            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public PurchaseOrder getPurchaseOrderById(int purchaseOrderId) {
        String sql = "SELECT po.*, s.supplier_name, " +
                    "u1.full_name as created_by_name, u2.full_name as approved_by_name " +
                    "FROM purchase_orders po " +
                    "INNER JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                    "LEFT JOIN users u1 ON po.created_by = u1.user_id " +
                    "LEFT JOIN users u2 ON po.approved_by = u2.user_id " +
                    "WHERE po.purchase_order_id = ?";

        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, purchaseOrderId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return mapResultSetToPurchaseOrder(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int insertPurchaseOrder(PurchaseOrder po, Connection conn) throws SQLException {
        String sql = "INSERT INTO purchase_orders (po_code, supplier_id, order_date, " +
                    "expected_delivery_date, total_amount, status, created_by, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pre = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pre.setString(1, po.getPoCode());
            pre.setInt(2, po.getSupplierId());
            pre.setDate(3, po.getOrderDate());
            pre.setDate(4, po.getExpectedDeliveryDate());
            pre.setBigDecimal(5, po.getTotalAmount());
            pre.setString(6, po.getStatus() != null ? po.getStatus() : "draft");
            pre.setInt(7, po.getCreatedBy());
            pre.setString(8, po.getNotes());

            int affectedRows = pre.executeUpdate();
            if (affectedRows > 0) {
                ResultSet keys = pre.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updatePurchaseOrder(PurchaseOrder po, Connection conn) throws SQLException {
        String sql = "UPDATE purchase_orders SET supplier_id = ?, order_date = ?, " +
                    "expected_delivery_date = ?, total_amount = ?, notes = ? " +
                    "WHERE purchase_order_id = ?";

        try (PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setInt(1, po.getSupplierId());
            pre.setDate(2, po.getOrderDate());
            pre.setDate(3, po.getExpectedDeliveryDate());
            pre.setBigDecimal(4, po.getTotalAmount());
            pre.setString(5, po.getNotes());
            pre.setInt(6, po.getPurchaseOrderId());

            return pre.executeUpdate() > 0;
        }
    }

    public boolean updatePurchaseOrderStatus(int purchaseOrderId, String status, Integer approvedBy) {
        String sql = "UPDATE purchase_orders SET status = ?, approved_by = ? WHERE purchase_order_id = ?";
        
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, status);
            if (approvedBy != null) {
                pre.setInt(2, approvedBy);
            } else {
                pre.setNull(2, Types.INTEGER);
            }
            pre.setInt(3, purchaseOrderId);
            return pre.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String generatePoCode() {
        return "PO" + System.currentTimeMillis();
    }

    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPurchaseOrderId(rs.getInt("purchase_order_id"));
        po.setPoCode(rs.getString("po_code"));
        po.setSupplierId(rs.getInt("supplier_id"));
        po.setSupplierName(rs.getString("supplier_name"));
        po.setOrderDate(rs.getDate("order_date"));
        po.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
        po.setTotalAmount(rs.getBigDecimal("total_amount"));
        po.setStatus(rs.getString("status"));
        po.setCreatedBy(rs.getInt("created_by"));
        po.setCreatedByName(rs.getString("created_by_name"));
        
        int approvedById = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            po.setApprovedBy(approvedById);
            po.setApprovedByName(rs.getString("approved_by_name"));
        }
        
        po.setNotes(rs.getString("notes"));
        po.setCreatedAt(rs.getTimestamp("created_at"));
        po.setUpdatedAt(rs.getTimestamp("updated_at"));
        return po;
    }
}
