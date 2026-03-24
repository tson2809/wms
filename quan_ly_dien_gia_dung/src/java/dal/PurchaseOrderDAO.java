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
                .append("u1.full_name as created_by_name, u2.full_name as approved_by_name, ")
                .append("(SELECT u_gi.full_name FROM goods_issues gi JOIN users u_gi ON gi.approved_by = u_gi.user_id WHERE gi.issue_type = 'sale' AND gi.notes LIKE CONCAT('%[PO_ID:', po.purchase_order_id, ']%') ORDER BY gi.issue_id DESC LIMIT 1) as gi_approved_by_name ")
                .append("FROM purchase_orders po ")
                .append("LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id ")
                .append("LEFT JOIN users u1 ON po.created_by = u1.user_id ")
                .append("LEFT JOIN users u2 ON po.approved_by = u2.user_id ")
                .append("WHERE po.supplier_id IS NOT NULL ");

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

    /** Lấy đơn từ Sale (supplier_id IS NULL) - hiển thị cho Staff và Sale. */
    public List<PurchaseOrder> getSaleOrders(String status, String keyword, int offset, int limit) {
        List<PurchaseOrder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT po.*, NULL as supplier_name, ")
                .append("u1.full_name as created_by_name, u2.full_name as approved_by_name, ")
                .append("(SELECT u_gi.full_name FROM goods_issues gi JOIN users u_gi ON gi.approved_by = u_gi.user_id WHERE gi.issue_type = 'sale' AND gi.notes LIKE CONCAT('%[PO_ID:', po.purchase_order_id, ']%') ORDER BY gi.issue_id DESC LIMIT 1) as gi_approved_by_name ")
                .append("FROM purchase_orders po ")
                .append("LEFT JOIN users u1 ON po.created_by = u1.user_id ")
                .append("LEFT JOIN users u2 ON po.approved_by = u2.user_id ")
                .append("WHERE po.supplier_id IS NULL ");
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND po.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
        }
        sql.append("ORDER BY po.created_at DESC LIMIT ? OFFSET ?");
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
                pre.setString(idx++, status);
            if (keyword != null && !keyword.trim().isEmpty())
                pre.setString(idx++, "%" + keyword.trim() + "%");
            pre.setInt(idx++, limit);
            pre.setInt(idx, offset);
            ResultSet rs = pre.executeQuery();
            while (rs.next())
                list.add(mapResultSetToPurchaseOrder(rs));
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /** Đếm đơn Sale (supplier_id IS NULL). */
    public int countSaleOrders(String status, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM purchase_orders po WHERE po.supplier_id IS NULL ");
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
            sql.append("AND po.status = ? ");
        if (keyword != null && !keyword.trim().isEmpty())
            sql.append("AND po.po_code LIKE ? ");
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
                pre.setString(idx++, status);
            if (keyword != null && !keyword.trim().isEmpty())
                pre.setString(idx, "%" + keyword.trim() + "%");
            ResultSet rs = pre.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /** Sale xem đơn do mình tạo (supplier_id IS NULL, created_by = userId). */
    public List<PurchaseOrder> getSaleOrdersByCreator(int userId, String status, int offset, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT po.*, NULL as supplier_name, ")
                .append("u1.full_name as created_by_name, u2.full_name as approved_by_name, ")
                .append("(SELECT u_gi.full_name FROM goods_issues gi JOIN users u_gi ON gi.approved_by = u_gi.user_id WHERE gi.issue_type = 'sale' AND gi.notes LIKE CONCAT('%[PO_ID:', po.purchase_order_id, ']%') ORDER BY gi.issue_id DESC LIMIT 1) as gi_approved_by_name ")
                .append("FROM purchase_orders po ")
                .append("LEFT JOIN users u1 ON po.created_by = u1.user_id ")
                .append("LEFT JOIN users u2 ON po.approved_by = u2.user_id ")
                .append("WHERE po.supplier_id IS NULL AND po.created_by = ? ");
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
            sql.append("AND po.status = ? ");
        sql.append("ORDER BY po.created_at DESC LIMIT ? OFFSET ?");
        List<PurchaseOrder> list = new ArrayList<>();
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            pre.setInt(idx++, userId);
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
                pre.setString(idx++, status);
            pre.setInt(idx++, limit);
            pre.setInt(idx, offset);
            ResultSet rs = pre.executeQuery();
            while (rs.next())
                list.add(mapResultSetToPurchaseOrder(rs));
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public int countSaleOrdersByCreator(int userId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM purchase_orders po WHERE po.supplier_id IS NULL AND po.created_by = ? ");
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
            sql.append("AND po.status = ? ");
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            pre.setInt(idx++, userId);
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status))
                pre.setString(idx, status);
            ResultSet rs = pre.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
        // Dùng LEFT JOIN để không bỏ qua Sale Orders có supplier_id = NULL
        String sql = "SELECT po.*, s.supplier_name, " +
                "u1.full_name as created_by_name, u2.full_name as approved_by_name, " +
                "(SELECT u_gi.full_name FROM goods_issues gi JOIN users u_gi ON gi.approved_by = u_gi.user_id WHERE gi.issue_type = 'sale' AND gi.notes LIKE CONCAT('%[PO_ID:', po.purchase_order_id, ']%') ORDER BY gi.issue_id DESC LIMIT 1) as gi_approved_by_name " +
                "FROM purchase_orders po " +
                "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
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
            // supplier_id = NULL (sales order) khi getSupplierId() == 0
            if (po.getSupplierId() > 0) {
                pre.setInt(2, po.getSupplierId());
            } else {
                pre.setNull(2, java.sql.Types.INTEGER);
            }
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
            if (po.getSupplierId() > 0) {
                pre.setInt(1, po.getSupplierId());
            } else {
                pre.setNull(1, java.sql.Types.INTEGER);
            }
            pre.setDate(2, po.getOrderDate());
            pre.setDate(3, po.getExpectedDeliveryDate());
            pre.setBigDecimal(4, po.getTotalAmount());
            pre.setString(5, po.getNotes());
            pre.setInt(6, po.getPurchaseOrderId());

            return pre.executeUpdate() > 0;
        }
    }

    /**
     * Staff nhận đơn: set approved_by = staffId, status = 'submitted'. Chỉ khi
     * draft và chưa có người nhận.
     */
    public boolean claimPurchaseOrder(int purchaseOrderId, int staffId) {
        String sql = "UPDATE purchase_orders SET approved_by = ?, status = 'submitted' "
                + "WHERE purchase_order_id = ? AND status = 'draft' AND (approved_by IS NULL OR approved_by = 0)";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, purchaseOrderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /** Huỷ đơn: chỉ khi draft hoặc submitted. */
    public boolean cancelPurchaseOrder(int purchaseOrderId) {
        String sql = "UPDATE purchase_orders SET status = 'cancelled' "
                + "WHERE purchase_order_id = ? AND status IN ('draft', 'submitted')";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, purchaseOrderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /** Hoàn tất đơn: khi manager duyệt phiếu nhập kho liên kết. */
    public boolean completePurchaseOrder(int purchaseOrderId) {
        String sql = "UPDATE purchase_orders SET status = 'received' "
                + "WHERE purchase_order_id = ? AND status = 'submitted'";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, purchaseOrderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /** Staff: lấy tất cả đơn (có NCC + không có NCC), hỗ trợ filter status/keyword. */
    public List<PurchaseOrder> getAllOrdersForStaff(String status, String keyword, int offset, int limit) {
        List<PurchaseOrder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT po.*, s.supplier_name, " +
            "u1.full_name as created_by_name, u2.full_name as approved_by_name, " +
            "(SELECT u_gi.full_name FROM goods_issues gi JOIN users u_gi ON gi.approved_by = u_gi.user_id WHERE gi.issue_type = 'sale' AND gi.notes LIKE CONCAT('%[PO_ID:', po.purchase_order_id, ']%') ORDER BY gi.issue_id DESC LIMIT 1) as gi_approved_by_name " +
            "FROM purchase_orders po " +
            "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
            "LEFT JOIN users u1 ON po.created_by = u1.user_id " +
            "LEFT JOIN users u2 ON po.approved_by = u2.user_id " +
            "WHERE 1=1 "
        );
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND po.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
        }
        sql.append("ORDER BY po.created_at DESC LIMIT ? OFFSET ?");
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) ps.setString(idx++, status);
            if (keyword != null && !keyword.trim().isEmpty()) ps.setString(idx++, "%" + keyword.trim() + "%");
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToPurchaseOrder(rs));
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /** Đếm tổng tất cả đơn cho Staff. */
    public int countAllOrdersForStaff(String status, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM purchase_orders po WHERE 1=1 ");
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) sql.append("AND po.status = ? ");
        if (keyword != null && !keyword.trim().isEmpty()) sql.append("AND po.po_code LIKE ? ");
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) ps.setString(idx++, status);
            if (keyword != null && !keyword.trim().isEmpty()) ps.setString(idx, "%" + keyword.trim() + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
        
        try {
            po.setGoodsIssueApprovedByName(rs.getString("gi_approved_by_name"));
        } catch (SQLException e) {
            // Ignore if not queried
        }

        po.setCreatedAt(rs.getTimestamp("created_at"));
        po.setUpdatedAt(rs.getTimestamp("updated_at"));
        return po;
    }
}
