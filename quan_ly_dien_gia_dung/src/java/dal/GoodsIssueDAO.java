/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.GoodsIssue;
import model.GoodsIssueDetail;
import model.User;

/**
 *
 * @author thais
 */
public class GoodsIssueDAO extends DBContext {

    public static class VariantInfo {
        private int variantId;
        private String sku;
        private String productName;
        private String unitName;
        private int stock;

        public int getVariantId() {
            return variantId;
        }

        public void setVariantId(int variantId) {
            this.variantId = variantId;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }
    }

    public List<GoodsIssue> getAllGoodsIssues() {
        List<GoodsIssue> list = new ArrayList<>();
        String sql = """
                SELECT gi.*,
                       u1.user_id as created_by_id, u1.username as created_by_username, u1.full_name as created_by_name, u1.email as created_by_email, u1.phone as created_by_phone, u1.address as created_by_address, u1.avatar as created_by_avatar, u1.is_active as created_by_active, u1.created_at as created_by_created_at,
                       u2.user_id as approved_by_id, u2.username as approved_by_username, u2.full_name as approved_by_name, u2.email as approved_by_email, u2.phone as approved_by_phone, u2.address as approved_by_address, u2.avatar as approved_by_avatar, u2.is_active as approved_by_active, u2.created_at as approved_by_created_at
                FROM goods_issues gi
                LEFT JOIN users u1 ON gi.created_by = u1.user_id
                LEFT JOIN users u2 ON gi.approved_by = u2.user_id
                ORDER BY gi.issue_id DESC
                """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql);
                ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                list.add(extractGoodsIssue(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<GoodsIssue> searchGoodsIssues(String search) {
        List<GoodsIssue> list = new ArrayList<>();
        String sql = """
                SELECT gi.*,
                       u1.user_id as created_by_id, u1.username as created_by_username, u1.full_name as created_by_name, u1.email as created_by_email, u1.phone as created_by_phone, u1.address as created_by_address, u1.avatar as created_by_avatar, u1.is_active as created_by_active, u1.created_at as created_by_created_at,
                       u2.user_id as approved_by_id, u2.username as approved_by_username, u2.full_name as approved_by_name, u2.email as approved_by_email, u2.phone as approved_by_phone, u2.address as approved_by_address, u2.avatar as approved_by_avatar, u2.is_active as approved_by_active, u2.created_at as approved_by_created_at
                FROM goods_issues gi
                LEFT JOIN users u1 ON gi.created_by = u1.user_id
                LEFT JOIN users u2 ON gi.approved_by = u2.user_id
                WHERE gi.issue_code LIKE ? OR gi.receiver_name LIKE ? OR gi.notes LIKE ?
                ORDER BY gi.issue_id DESC
                """;
        String pattern = "%" + search.trim() + "%";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, pattern);
            pre.setString(2, pattern);
            pre.setString(3, pattern);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                list.add(extractGoodsIssue(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public GoodsIssue getGoodsIssueById(int issueId) {
        String sql = """
                SELECT gi.*,
                       u1.user_id as created_by_id, u1.username as created_by_username, u1.full_name as created_by_name, u1.email as created_by_email, u1.phone as created_by_phone, u1.address as created_by_address, u1.avatar as created_by_avatar, u1.is_active as created_by_active, u1.created_at as created_by_created_at,
                       u2.user_id as approved_by_id, u2.username as approved_by_username, u2.full_name as approved_by_name, u2.email as approved_by_email, u2.phone as approved_by_phone, u2.address as approved_by_address, u2.avatar as approved_by_avatar, u2.is_active as approved_by_active, u2.created_at as approved_by_created_at
                FROM goods_issues gi
                LEFT JOIN users u1 ON gi.created_by = u1.user_id
                LEFT JOIN users u2 ON gi.approved_by = u2.user_id
                WHERE gi.issue_id = ?
                """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, issueId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return extractGoodsIssue(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private GoodsIssue extractGoodsIssue(ResultSet rs) throws SQLException {
        GoodsIssue gi = new GoodsIssue();
        gi.setIssueId(rs.getInt("issue_id"));
        gi.setIssueCode(rs.getString("issue_code"));
        gi.setIssueType(rs.getString("issue_type"));
        gi.setIssueDate(rs.getTimestamp("issue_date"));
        gi.setReceiverName(rs.getString("receiver_name"));
        gi.setStatus(rs.getString("status"));
        gi.setNotes(rs.getString("notes"));
        gi.setCreatedAt(rs.getTimestamp("created_at"));
        gi.setUpdatedAt(rs.getTimestamp("updated_at"));
        if (rs.getObject("return_order_id") != null) {
            gi.setReturnOrderId(rs.getInt("return_order_id"));
        }

        if (rs.getObject("created_by_id") != null) {
            User u = new User();
            u.setUserId(rs.getInt("created_by_id"));
            u.setUsername(rs.getString("created_by_username"));
            u.setFullName(rs.getString("created_by_name"));
            u.setEmail(rs.getString("created_by_email"));
            u.setPhone(rs.getString("created_by_phone"));
            u.setAddress(rs.getString("created_by_address"));
            u.setAvatar(rs.getString("created_by_avatar"));
            u.setIsActive(rs.getBoolean("created_by_active"));
            u.setCreatedAt(rs.getTimestamp("created_by_created_at"));
            gi.setCreatedByUser(u);
        }

        if (rs.getObject("approved_by_id") != null) {
            User u = new User();
            u.setUserId(rs.getInt("approved_by_id"));
            u.setUsername(rs.getString("approved_by_username"));
            u.setFullName(rs.getString("approved_by_name"));
            u.setEmail(rs.getString("approved_by_email"));
            u.setPhone(rs.getString("approved_by_phone"));
            u.setAddress(rs.getString("approved_by_address"));
            u.setAvatar(rs.getString("approved_by_avatar"));
            u.setIsActive(rs.getBoolean("approved_by_active"));
            u.setCreatedAt(rs.getTimestamp("approved_by_created_at"));
            gi.setApprovedByUser(u);
        }

        return gi;
    }

    public boolean issueCodeExists(String issueCode) {
        String sql = "SELECT COUNT(*) FROM goods_issues WHERE issue_code = ?";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, issueCode);
            ResultSet rs = pre.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /** Lấy issue_id theo mã phiếu (dùng sau khi tạo phiếu để hoàn tất xuất kho). */
    public Integer getIssueIdByIssueCode(String issueCode) {
        if (issueCode == null || issueCode.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT issue_id FROM goods_issues WHERE issue_code = ? LIMIT 1";
        try (PreparedStatement pre = getConnection().prepareStatement(sql)) {
            pre.setString(1, issueCode.trim());
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt("issue_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Lấy danh sách serial in_stock của một variant (cho dropdown chọn serial khi
     * xuất).
     */
    public String getAvailableSerialsJson(int variantId) {
        String sql = """
                SELECT serial_id, serial_number
                FROM product_serials
                WHERE variant_id = ? AND status = 'in_stock'
                ORDER BY serial_number
                """;
        StringBuilder json = new StringBuilder("[");
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first)
                    json.append(",");
                first = false;
                json.append("{")
                        .append("\"serialId\":").append(rs.getInt("serial_id")).append(",")
                        .append("\"serialNumber\":\"").append(escapeJson(rs.getString("serial_number"))).append("\"")
                        .append("}");
            }
        } catch (SQLException e) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Lấy thông tin variant (sku, name, unit, tồn) theo danh sách variantId (không
     * filter quantity).
     */
    public Map<Integer, VariantInfo> getVariantInfoByIds(List<Integer> variantIds) {
        Map<Integer, VariantInfo> map = new HashMap<>();
        if (variantIds == null || variantIds.isEmpty())
            return map;

        StringBuilder in = new StringBuilder();
        for (int i = 0; i < variantIds.size(); i++) {
            if (i > 0)
                in.append(",");
            in.append("?");
        }

        String sql = """
                SELECT pv.variant_id, pv.sku, pv.quantity,
                       p.product_name, u.unit_name
                FROM product_variants pv
                INNER JOIN products p ON pv.product_id = p.product_id
                LEFT JOIN units u ON p.unit_id = u.unit_id
                WHERE pv.variant_id IN (%s)
                """.formatted(in.toString());

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < variantIds.size(); i++) {
                ps.setInt(i + 1, variantIds.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                VariantInfo vi = new VariantInfo();
                vi.setVariantId(rs.getInt("variant_id"));
                vi.setSku(rs.getString("sku"));
                vi.setProductName(rs.getString("product_name"));
                vi.setUnitName(rs.getString("unit_name"));
                vi.setStock(rs.getInt("quantity"));
                map.put(vi.getVariantId(), vi);
            }
        } catch (SQLException e) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return map;
    }

    /** Lọc các serialNumber thuộc variant và đang in_stock. */
    public List<String> filterInStockSerialNumbers(int variantId, List<String> serialNumbers) {
        List<String> result = new ArrayList<>();
        if (serialNumbers == null || serialNumbers.isEmpty())
            return result;

        StringBuilder in = new StringBuilder();
        for (int i = 0; i < serialNumbers.size(); i++) {
            if (i > 0)
                in.append(",");
            in.append("?");
        }

        String sql = """
                SELECT serial_number
                FROM product_serials
                WHERE variant_id = ? AND status = 'in_stock' AND serial_number IN (%s)
                """.formatted(in.toString());

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, variantId);
            for (int i = 0; i < serialNumbers.size(); i++) {
                ps.setString(i + 2, serialNumbers.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("serial_number"));
            }
        } catch (SQLException e) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    /** Tìm kiếm sản phẩm có tồn kho (quantity > 0) để xuất kho. */
    public String searchProductsForIssueJson(String keyword) {
        String sql = """
                SELECT pv.variant_id, pv.sku, pv.quantity, pv.sale_price,
                       p.product_name, u.unit_name
                FROM product_variants pv
                INNER JOIN products p ON pv.product_id = p.product_id
                LEFT JOIN units u ON p.unit_id = u.unit_id
                WHERE pv.status = 'active' AND p.status = 'active' AND pv.quantity > 0
                AND (pv.sku LIKE ? OR p.product_name LIKE ?)
                """;
        StringBuilder json = new StringBuilder("[");
        String pattern = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : "%";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first)
                    json.append(",");
                first = false;
                json.append("{")
                        .append("\"variantId\":").append(rs.getInt("variant_id")).append(",")
                        .append("\"code\":\"").append(escapeJson(rs.getString("sku"))).append("\",")
                        .append("\"name\":\"").append(escapeJson(rs.getString("product_name"))).append("\",")
                        .append("\"unit\":\"").append(escapeJson(rs.getString("unit_name"))).append("\",")
                        .append("\"stock\":").append(rs.getInt("quantity")).append(",")
                        .append("\"price\":").append(rs.getBigDecimal("sale_price"))
                        .append("}");
            }
        } catch (SQLException e) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        json.append("]");
        return json.toString();
    }

    public String searchProductBySKUJson(String sku) {
        String sql = """
                SELECT pv.variant_id, pv.sku, pv.quantity, pv.sale_price,
                       p.product_name, u.unit_name
                FROM product_variants pv
                INNER JOIN products p ON pv.product_id = p.product_id
                LEFT JOIN units u ON p.unit_id = u.unit_id
                WHERE pv.status = 'active' AND p.status = 'active' AND pv.quantity > 0
                AND pv.sku = ?
                LIMIT 1
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, sku != null ? sku.trim() : "");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "{" +
                        "\"variantId\":" + rs.getInt("variant_id") + "," +
                        "\"code\":\"" + escapeJson(rs.getString("sku")) + "\"," +
                        "\"name\":\"" + escapeJson(rs.getString("product_name")) + "\"," +
                        "\"unit\":\"" + escapeJson(rs.getString("unit_name")) + "\"," +
                        "\"stock\":" + rs.getInt("quantity") + "," +
                        "\"price\":" + rs.getBigDecimal("sale_price") +
                        "}";
            }
        } catch (SQLException e) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return "{}";
    }

    /**
     * Tạo phiếu xuất kho (status = draft). Tồn kho chỉ giảm khi Manager duyệt
     * (completed).
     */
    public boolean createGoodsIssue(String issueCode, String issueType, String issueDate,
            String receiverName, String notes,
            int createdBy, List<GoodsIssueDetail> details) {
        return createGoodsIssue(issueCode, issueType, issueDate, receiverName, notes, createdBy, details,
                null);
    }

    /** Tạo phiếu xuất kho với return_order_id (khi tạo từ đơn trả hàng). */
    public boolean createGoodsIssue(String issueCode, String issueType, String issueDate,
            String receiverName, String notes,
            int createdBy, List<GoodsIssueDetail> details, Integer returnOrderId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String normalizedIssueType = normalizeIssueType(issueType);

            String sqlIssue = """
                    INSERT INTO goods_issues
                    (issue_code, issue_type, issue_date, receiver_name, status, created_by, notes, return_order_id)
                    VALUES (?, ?, ?, ?, 'draft', ?, ?, ?)
                    """;
            int issueId;
            try (PreparedStatement ps = conn.prepareStatement(sqlIssue, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, issueCode);
                ps.setString(2, normalizedIssueType);
                ps.setString(3, issueDate);
                ps.setString(4, receiverName);
                ps.setInt(5, createdBy);
                ps.setString(6, notes != null ? notes : "");
                ps.setObject(7, returnOrderId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                    issueId = rs.getInt(1);
                else
                    throw new SQLException("Không lấy được issue_id");
            }

            String sqlDetail = "INSERT INTO goods_issue_details (issue_id, variant_id, quantity, notes) VALUES (?, ?, ?, ?)";
            String sqlSerial = "INSERT INTO issue_serials (issue_detail_id, serial_id) VALUES (?, ?)";
            String sqlGetSerial = "SELECT serial_id FROM product_serials WHERE serial_number = ? AND status = 'in_stock' AND variant_id = ?";

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                for (GoodsIssueDetail detail : details) {
                    psDetail.setInt(1, issueId);
                    psDetail.setInt(2, detail.getVariantId());
                    psDetail.setInt(3, detail.getQuantity());
                    psDetail.setString(4, detail.getNotes() != null ? detail.getNotes() : "");
                    psDetail.executeUpdate();

                    int detailId;
                    try (ResultSet rs = psDetail.getGeneratedKeys()) {
                        if (rs.next())
                            detailId = rs.getInt(1);
                        else
                            throw new SQLException("Không lấy được issue_detail_id");
                    }

                    if (detail.getSerials() != null) {
                        for (String serialNumber : detail.getSerials()) {
                            int serialId;
                            try (PreparedStatement psGet = conn.prepareStatement(sqlGetSerial)) {
                                psGet.setString(1, serialNumber);
                                psGet.setInt(2, detail.getVariantId());
                                ResultSet rs = psGet.executeQuery();
                                if (rs.next())
                                    serialId = rs.getInt("serial_id");
                                else
                                    throw new SQLException("Serial '" + serialNumber + "' không hợp lệ hoặc đã xuất");
                            }
                            try (PreparedStatement psSerial = conn.prepareStatement(sqlSerial)) {
                                psSerial.setInt(1, detailId);
                                psSerial.setInt(2, serialId);
                                psSerial.executeUpdate();
                            }
                        }
                    }
                }
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException e) {
                }
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                }
        }
    }

    /**
     * Cập nhật trạng thái phiếu xuất. Khi completed: giảm tồn kho + cập nhật serial
     * status.
     */
    public boolean updateGoodsIssueStatus(int issueId, String status, Integer approvedBy) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            GoodsIssue issue = getGoodsIssueById(issueId);
            if (issue == null)
                return false;

            if ("completed".equals(status) && !"completed".equals(issue.getStatus())) {
                applyIssueToInventory(conn, issueId, issue.getIssueType(), approvedBy != null ? approvedBy : 0);
            }

            String sql = "UPDATE goods_issues SET status = ?, approved_by = ?, updated_at = CURRENT_TIMESTAMP WHERE issue_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setObject(2, "completed".equals(status) ? approvedBy : null);
                ps.setInt(3, issueId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException e) {
                }
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                }
        }
    }

    /**
     * Giảm tồn kho, cập nhật serial status, ghi inventory_transactions khi duyệt
     * phiếu xuất.
     */
    private void applyIssueToInventory(Connection conn, int issueId, String issueType, int approvedBy)
            throws SQLException {
        List<GoodsIssueDetail> details = getGoodsIssueDetails(issueId);

        String sqlSerialStatus = "UPDATE product_serials SET status = ? WHERE serial_id = ?";
        String sqlGetSerials = """
                SELECT ps.serial_id FROM issue_serials isl
                INNER JOIN product_serials ps ON isl.serial_id = ps.serial_id
                WHERE isl.issue_detail_id = ?
                """;
        String sqlQty = "SELECT quantity FROM product_variants WHERE variant_id = ?";
        String sqlUpdateQty = "UPDATE product_variants SET quantity = quantity - ? WHERE variant_id = ?";
        String sqlTrx = """
                INSERT INTO inventory_transactions
                (variant_id, transaction_type, reference_type, reference_id, quantity_change, quantity_before, quantity_after, created_by)
                VALUES (?, 'export', 'goods_issue', ?, ?, ?, ?, ?)
                """;

        String serialStatus = "sale".equals(issueType) ? "sold" : "shipped";

        for (GoodsIssueDetail d : details) {
            int qtyBefore = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlQty)) {
                ps.setInt(1, d.getVariantId());
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    qtyBefore = rs.getInt("quantity");
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateQty)) {
                ps.setInt(1, d.getQuantity());
                ps.setInt(2, d.getVariantId());
                ps.executeUpdate();
            }

            int qtyAfter = qtyBefore - d.getQuantity();
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx)) {
                ps.setInt(1, d.getVariantId());
                ps.setInt(2, issueId);
                ps.setInt(3, -d.getQuantity());
                ps.setInt(4, qtyBefore);
                ps.setInt(5, qtyAfter);
                ps.setInt(6, approvedBy);
                ps.executeUpdate();
            }

            try (PreparedStatement psGet = conn.prepareStatement(sqlGetSerials)) {
                psGet.setInt(1, d.getIssueDetailId());
                ResultSet rs = psGet.executeQuery();
                while (rs.next()) {
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlSerialStatus)) {
                        psUpdate.setString(1, serialStatus);
                        psUpdate.setInt(2, rs.getInt("serial_id"));
                        psUpdate.executeUpdate();
                    }
                }
            }
        }
    }

    public List<GoodsIssueDetail> getGoodsIssueDetails(int issueId) {
        List<GoodsIssueDetail> details = new ArrayList<>();
        String sql = """
                SELECT gid.*, pv.sku, p.product_name, u.unit_name
                FROM goods_issue_details gid
                INNER JOIN product_variants pv ON gid.variant_id = pv.variant_id
                INNER JOIN products p ON pv.product_id = p.product_id
                LEFT JOIN units u ON p.unit_id = u.unit_id
                WHERE gid.issue_id = ?
                """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, issueId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                GoodsIssueDetail d = new GoodsIssueDetail();
                d.setIssueDetailId(rs.getInt("issue_detail_id"));
                d.setIssueId(rs.getInt("issue_id"));
                d.setVariantId(rs.getInt("variant_id"));
                d.setVariantSku(rs.getString("sku"));
                d.setProductName(rs.getString("product_name"));
                d.setUnitName(rs.getString("unit_name"));
                d.setQuantity(rs.getInt("quantity"));
                d.setNotes(rs.getString("notes"));
                d.setSerials(getSerialsForIssueDetail(rs.getInt("issue_detail_id")));
                details.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return details;
    }

    private List<String> getSerialsForIssueDetail(int issueDetailId) {
        List<String> serials = new ArrayList<>();
        String sql = """
                SELECT ps.serial_number
                FROM issue_serials isl
                INNER JOIN product_serials ps ON isl.serial_id = ps.serial_id
                WHERE isl.issue_detail_id = ?
                ORDER BY ps.serial_number
                """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, issueDetailId);
            ResultSet rs = pre.executeQuery();
            while (rs.next())
                serials.add(rs.getString("serial_number"));
        } catch (SQLException ex) {
            Logger.getLogger(GoodsIssueDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return serials;
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t",
                "\\t");
    }

    private String normalizeIssueType(String issueType) {
        if (issueType == null) {
            return "sale";
        }
        String normalized = issueType.trim().toLowerCase();
        if ("return_supplier".equals(normalized) || "other".equals(normalized)) {
            return normalized;
        }
        return "sale";
    }
}
