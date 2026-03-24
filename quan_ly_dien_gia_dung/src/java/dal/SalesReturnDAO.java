package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SalesReturn;
import model.SalesReturnDetail;

/**
 * 
 * @author laptop368
 */
public class SalesReturnDAO extends DBContext {

    public int countSalesReturnWithSearchAndFilter(String keyword, String status, String refundStatus, Integer createdBy) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM sales_returns sr WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (sr.sr_code LIKE ? OR sr.description LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (createdBy != null) {
            sql.append("AND sr.created_by = ? ");
            params.add(createdBy);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND sr.status = ? ");
            params.add(status.trim());
        }

        if (refundStatus != null && !refundStatus.trim().isEmpty()) {
            sql.append("AND sr.refund_status = ? ");
            params.add(refundStatus.trim());
        }

        try (PreparedStatement pre = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pre.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pre.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public List<SalesReturn> getSalesReturnWithSearchAndFilter(
            String keyword,
            String status,
            String refundStatus,
            Integer createdBy,
            int offset,
            int size
    ) {
        List<SalesReturn> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT sr.sales_return_id, sr.sr_code, sr.return_date,
                   sr.status, sr.refund_status, sr.description,
                   sr.created_by, sr.received_by, sr.created_at,
                   u1.full_name AS created_by_name,
                   u2.full_name AS received_by_name,
                   COALESCE(d.total_qty, 0) AS total_quantity,
                   COALESCE(d.total_refund_amount, 0) AS total_refund_amount
            FROM sales_returns sr
            LEFT JOIN users u1 ON sr.created_by = u1.user_id
            LEFT JOIN users u2 ON sr.received_by = u2.user_id
            LEFT JOIN (
                SELECT sales_return_id,
                       SUM(quantity) AS total_qty,
                       SUM(total_refund) AS total_refund_amount
                FROM sales_return_details
                GROUP BY sales_return_id
            ) d ON sr.sales_return_id = d.sales_return_id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (sr.sr_code LIKE ? OR sr.description LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (createdBy != null) {
            sql.append("AND sr.created_by = ? ");
            params.add(createdBy);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND sr.status = ? ");
            params.add(status.trim());
        }

        if (refundStatus != null && !refundStatus.trim().isEmpty()) {
            sql.append("AND sr.refund_status = ? ");
            params.add(refundStatus.trim());
        }

        sql.append("ORDER BY sr.sales_return_id DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (PreparedStatement pre = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pre.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                SalesReturn sr = new SalesReturn();
                sr.setSalesReturnId(rs.getInt("sales_return_id"));
                sr.setSrCode(rs.getString("sr_code"));
                sr.setReturnDate(rs.getTimestamp("return_date"));
                sr.setStatus(rs.getString("status"));
                sr.setRefundStatus(rs.getString("refund_status"));
                sr.setDescription(rs.getString("description"));
                sr.setCreatedBy(rs.getObject("created_by") != null ? rs.getInt("created_by") : null);
                sr.setReceivedBy(rs.getObject("received_by") != null ? rs.getInt("received_by") : null);
                sr.setCreatedAt(rs.getTimestamp("created_at"));
                sr.setCreatedByUserName(rs.getString("created_by_name"));
                sr.setReceivedByUserName(rs.getString("received_by_name"));
                sr.setTotalQuantity(rs.getInt("total_quantity"));
                sr.setTotalRefundAmount(rs.getBigDecimal("total_refund_amount"));
                list.add(sr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public boolean srCodeExists(String srCode) {
        if (srCode == null || srCode.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM sales_returns WHERE sr_code = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, srCode.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean srCodeExistsExcludingId(String srCode, int excludeId) {
        if (srCode == null || srCode.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM sales_returns WHERE sr_code = ? AND sales_return_id != ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, srCode.trim());
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public SalesReturn getSalesReturnById(int salesReturnId) {
        String sql = """
            SELECT sr.sales_return_id, sr.sr_code, sr.return_date,
                   sr.status, sr.refund_status, sr.description,
                   sr.created_by, sr.received_by, sr.created_at,
                   u1.full_name AS created_by_name,
                   u2.full_name AS received_by_name
            FROM sales_returns sr
            LEFT JOIN users u1 ON sr.created_by = u1.user_id
            LEFT JOIN users u2 ON sr.received_by = u2.user_id
            WHERE sr.sales_return_id = ?
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, salesReturnId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SalesReturn sr = new SalesReturn();
                sr.setSalesReturnId(rs.getInt("sales_return_id"));
                sr.setSrCode(rs.getString("sr_code"));
                sr.setReturnDate(rs.getTimestamp("return_date"));
                sr.setStatus(rs.getString("status"));
                sr.setRefundStatus(rs.getString("refund_status"));
                sr.setDescription(rs.getString("description"));
                sr.setCreatedBy(rs.getObject("created_by") != null ? rs.getInt("created_by") : null);
                sr.setReceivedBy(rs.getObject("received_by") != null ? rs.getInt("received_by") : null);
                sr.setCreatedAt(rs.getTimestamp("created_at"));
                sr.setCreatedByUserName(rs.getString("created_by_name"));
                sr.setReceivedByUserName(rs.getString("received_by_name"));
                return sr;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<SalesReturnDetail> getSalesReturnDetailsByOrderId(int salesReturnId) {
        List<SalesReturnDetail> list = new ArrayList<>();
        String sql = """
            SELECT srd.sr_detail_id, srd.sales_return_id, srd.variant_id, srd.quantity,
                   srd.original_price, srd.total_refund,
                   pv.sku, p.product_name, u.unit_name
            FROM sales_return_details srd
            INNER JOIN product_variants pv ON srd.variant_id = pv.variant_id
            INNER JOIN products p ON pv.product_id = p.product_id
            LEFT JOIN units u ON p.unit_id = u.unit_id
            WHERE srd.sales_return_id = ?
            ORDER BY srd.sr_detail_id
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, salesReturnId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SalesReturnDetail d = new SalesReturnDetail();
                d.setSalesReturnDetailId(rs.getInt("sr_detail_id"));
                d.setSalesReturnId(rs.getInt("sales_return_id"));
                d.setVariantId(rs.getInt("variant_id"));
                d.setQuantity(rs.getInt("quantity"));
                d.setOriginalPrice(rs.getBigDecimal("original_price"));
                d.setTotalRefund(rs.getBigDecimal("total_refund"));
                d.setVariantSku(rs.getString("sku"));
                d.setProductName(rs.getString("product_name"));
                d.setUnitName(rs.getString("unit_name"));
                list.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    
    public String searchProductsForSalesReturnJson(String keyword, int saleUserId) {
        StringBuilder sql = new StringBuilder("""
            SELECT x.variant_id,
                   x.sku,
                   x.product_name,
                   x.unit_name,
                   x.original_price,
                   (x.received_qty - COALESCE(r.returned_qty, 0)) AS available_qty
            FROM (
                SELECT pod.variant_id,
                       pv.sku,
                       p.product_name,
                       u.unit_name,
                       MAX(pod.unit_price) AS original_price,
                       SUM(pod.quantity) AS received_qty
                FROM purchase_order_details pod
                INNER JOIN purchase_orders po ON pod.purchase_order_id = po.purchase_order_id
                INNER JOIN product_variants pv ON pod.variant_id = pv.variant_id
                INNER JOIN products p ON pv.product_id = p.product_id
                LEFT JOIN units u ON p.unit_id = u.unit_id
                WHERE po.created_by = ?
                  AND po.status = 'received'
                  AND (pv.sku LIKE ? OR p.product_name LIKE ?)
                GROUP BY pod.variant_id, pv.sku, p.product_name, u.unit_name
            ) x
            LEFT JOIN (
                SELECT srd.variant_id,
                       SUM(srd.quantity) AS returned_qty
                FROM sales_return_details srd
                INNER JOIN sales_returns sr ON srd.sales_return_id = sr.sales_return_id
                WHERE sr.created_by = ?
                  AND sr.status IN ('processing', 'completed')
                GROUP BY srd.variant_id
            ) r ON x.variant_id = r.variant_id
            WHERE (x.received_qty - COALESCE(r.returned_qty, 0)) > 0
            ORDER BY x.product_name
            LIMIT 20
        """);

        String pattern = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : "%";

        StringBuilder json = new StringBuilder("[");
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            ps.setInt(1, saleUserId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setInt(4, saleUserId);

            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                int variantId = rs.getInt("variant_id");
                String sku = escapeJson(rs.getString("sku"));
                String name = escapeJson(rs.getString("product_name"));
                String unit = escapeJson(rs.getString("unit_name"));
                java.math.BigDecimal originalPrice = rs.getBigDecimal("original_price");
                int availableQty = rs.getInt("available_qty");

                json.append("{")
                    .append("\"variantId\":").append(variantId).append(",")
                    .append("\"code\":\"").append(sku).append("\",")
                    .append("\"name\":\"").append(name).append("\",")
                    .append("\"unit\":\"").append(unit).append("\",")
                    .append("\"costPrice\":").append(originalPrice != null ? originalPrice : 0).append(",")
                    .append("\"stock\":").append(availableQty)
                    .append("}");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        json.append("]");
        return json.toString();
    }

    public boolean createSalesReturn(String srCode, Date returnDate, String description, int createdBy, List<SalesReturnDetail> details) {
        if (srCode == null || srCode.trim().isEmpty() || returnDate == null || details == null || details.isEmpty()) {
            return false;
        }
        if (srCodeExists(srCode)) return false;

        java.sql.Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlSr = """
                INSERT INTO sales_returns (sr_code, return_date, status, refund_status, description, created_by)
                VALUES (?, ?, 'pending', 'not_refunded', ?, ?)
            """;

            int srId;
            try (PreparedStatement psSr = conn.prepareStatement(sqlSr, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psSr.setString(1, srCode.trim());
                psSr.setDate(2, returnDate);
                psSr.setString(3, description != null ? description : "");
                psSr.setInt(4, createdBy);
                psSr.executeUpdate();

                ResultSet keys = psSr.getGeneratedKeys();
                if (keys.next()) srId = keys.getInt(1);
                else throw new SQLException("Không lấy được sales_return_id");
            }

            String sqlDetail = """
                INSERT INTO sales_return_details
                (sales_return_id, variant_id, quantity, original_price, total_refund)
                VALUES (?, ?, ?, ?, ?)
            """;

            String sqlAllowedQty = """
                SELECT
                    COALESCE((
                        SELECT SUM(pod.quantity)
                        FROM purchase_order_details pod
                        INNER JOIN purchase_orders po ON pod.purchase_order_id = po.purchase_order_id
                        WHERE pod.variant_id = ?
                          AND po.created_by = ?
                          AND po.status = 'received'
                    ), 0)
                    -
                    COALESCE((
                        SELECT SUM(srd.quantity)
                        FROM sales_return_details srd
                        INNER JOIN sales_returns sr ON srd.sales_return_id = sr.sales_return_id
                        WHERE srd.variant_id = ?
                          AND sr.created_by = ?
                          AND sr.status IN ('processing', 'completed')
                    ), 0) AS allowed_qty
            """;

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for (SalesReturnDetail d : details) {
                    if (d == null) continue;

                    int variantId = d.getVariantId();
                    int qty = d.getQuantity();
                    java.math.BigDecimal originalPrice = d.getOriginalPrice() != null ? d.getOriginalPrice() : java.math.BigDecimal.ZERO;

                    if (variantId <= 0 || qty <= 0) return false;
                    if (originalPrice.compareTo(java.math.BigDecimal.ZERO) < 0) return false;

                    int allowedQty = 0;
                    try (PreparedStatement psAllowed = conn.prepareStatement(sqlAllowedQty)) {
                        psAllowed.setInt(1, variantId);
                        psAllowed.setInt(2, createdBy);
                        psAllowed.setInt(3, variantId);
                        psAllowed.setInt(4, createdBy);
                        ResultSet rsAllowed = psAllowed.executeQuery();
                        if (rsAllowed.next()) allowedQty = rsAllowed.getInt("allowed_qty");
                    }
                    if (qty > allowedQty) return false;

                    java.math.BigDecimal totalRefund = originalPrice.multiply(java.math.BigDecimal.valueOf(qty));

                    psDetail.setInt(1, srId);
                    psDetail.setInt(2, variantId);
                    psDetail.setInt(3, qty);
                    psDetail.setBigDecimal(4, originalPrice);
                    psDetail.setBigDecimal(5, totalRefund);
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    public boolean updateSalesReturnRefundStatusOnly(int salesReturnId, String refundStatus) {
        String sql = "UPDATE sales_returns SET refund_status = ? WHERE sales_return_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, refundStatus);
            ps.setInt(2, salesReturnId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateSalesReturn(int salesReturnId, String srCode, Date returnDate, String description,
            String refundStatus, int createdBy, List<SalesReturnDetail> details) {
        if (salesReturnId <= 0 || srCode == null || srCode.trim().isEmpty() || returnDate == null || details == null || details.isEmpty()) {
            return false;
        }
        if (srCodeExistsExcludingId(srCode, salesReturnId)) return false;

        java.sql.Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlUpdate = """
                UPDATE sales_returns
                SET sr_code = ?, return_date = ?, description = ?, refund_status = ?
                WHERE sales_return_id = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, srCode.trim());
                ps.setDate(2, returnDate);
                ps.setString(3, description != null ? description : "");
                ps.setString(4, refundStatus != null ? refundStatus : "not_refunded");
                ps.setInt(5, salesReturnId);
                if (ps.executeUpdate() <= 0) return false;
            }

            String sqlDelete = "DELETE FROM sales_return_details WHERE sales_return_id = ?";
            try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                psDel.setInt(1, salesReturnId);
                psDel.executeUpdate();
            }

            String sqlAllowedQty = """
                SELECT
                    COALESCE((
                        SELECT SUM(pod.quantity)
                        FROM purchase_order_details pod
                        INNER JOIN purchase_orders po ON pod.purchase_order_id = po.purchase_order_id
                        WHERE pod.variant_id = ?
                          AND po.created_by = ?
                          AND po.status = 'received'
                    ), 0)
                    -
                    COALESCE((
                        SELECT SUM(srd.quantity)
                        FROM sales_return_details srd
                        INNER JOIN sales_returns sr ON srd.sales_return_id = sr.sales_return_id
                        WHERE srd.variant_id = ?
                          AND sr.created_by = ?
                          AND sr.status IN ('processing', 'completed')
                    ), 0) AS allowed_qty
            """;

            String sqlDetail = """
                INSERT INTO sales_return_details
                (sales_return_id, variant_id, quantity, original_price, total_refund)
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for (SalesReturnDetail d : details) {
                    if (d == null) continue;
                    int variantId = d.getVariantId();
                    int qty = d.getQuantity();
                    java.math.BigDecimal originalPrice = d.getOriginalPrice() != null ? d.getOriginalPrice() : java.math.BigDecimal.ZERO;
                    if (variantId <= 0 || qty <= 0 || originalPrice.compareTo(java.math.BigDecimal.ZERO) < 0) return false;

                    int allowedQty = 0;
                    try (PreparedStatement psAllowed = conn.prepareStatement(sqlAllowedQty)) {
                        psAllowed.setInt(1, variantId);
                        psAllowed.setInt(2, createdBy);
                        psAllowed.setInt(3, variantId);
                        psAllowed.setInt(4, createdBy);
                        ResultSet rsAllowed = psAllowed.executeQuery();
                        if (rsAllowed.next()) allowedQty = rsAllowed.getInt("allowed_qty");
                    }
                    if (qty > allowedQty) return false;

                    java.math.BigDecimal totalRefund = originalPrice.multiply(java.math.BigDecimal.valueOf(qty));
                    psDetail.setInt(1, salesReturnId);
                    psDetail.setInt(2, variantId);
                    psDetail.setInt(3, qty);
                    psDetail.setBigDecimal(4, originalPrice);
                    psDetail.setBigDecimal(5, totalRefund);
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    public boolean claimSalesReturn(int salesReturnId, int userId) {
        String sql = """
            UPDATE sales_returns
            SET received_by = ?, status = 'processing'
            WHERE sales_return_id = ?
              AND status = 'pending'
              AND received_by IS NULL
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, salesReturnId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Hủy việc nhân đơn (rollback): đưa đơn từ 'processing' về 'pending' và xóa received_by.
     * Không cho hủy nếu đã tồn tại goods_receipts cho sales_return đó mà status khác 'cancelled'.
     */
    public boolean cancelClaimSalesReturn(int salesReturnId, int userId) {
        String sql = """
            UPDATE sales_returns
            SET received_by = NULL, status = 'pending'
            WHERE sales_return_id = ?
              AND status = 'processing'
              AND received_by = ?
              AND NOT EXISTS (
                  SELECT 1
                  FROM goods_receipts gr
                  WHERE gr.sales_return_id = sales_returns.sales_return_id
                    AND gr.status <> 'cancelled'
              )
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, salesReturnId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean completeSalesReturn(int salesReturnId) {
        String sql = """
            UPDATE sales_returns
            SET status = 'completed'
            WHERE sales_return_id = ?
              AND status = 'processing'
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, salesReturnId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SalesReturnDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}

