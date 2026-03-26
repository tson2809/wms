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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.GoodsReceipt;
import model.Supplier;
import model.User;

/**
 *
 * @author thais
 */
public class GoodsReceiptDAO extends DBContext {

    public List<GoodsReceipt> getAllGoodsReceipts() {
        List<GoodsReceipt> list = new ArrayList<>();
        String sql = """
                     SELECT gr.*, 
                            s.supplier_id, s.supplier_name, s.contact_person, s.email, s.phone, s.status as supplier_status, s.description as supplier_description, s.created_at as supplier_created_at,
                            u1.user_id as created_by_id, u1.username as created_by_username, u1.email as created_by_email, u1.full_name as created_by_name, u1.phone as created_by_phone, u1.address as created_by_address, u1.avatar as created_by_avatar, u1.role_id as created_by_role_id, u1.is_active as created_by_active, u1.created_at as created_by_created_at,
                            u2.user_id as approved_by_id, u2.username as approved_by_username, u2.email as approved_by_email, u2.full_name as approved_by_name, u2.phone as approved_by_phone, u2.address as approved_by_address, u2.avatar as approved_by_avatar, u2.role_id as approved_by_role_id, u2.is_active as approved_by_active, u2.created_at as approved_by_created_at
                     FROM goods_receipts gr
                     LEFT JOIN suppliers s ON gr.supplier_id = s.supplier_id
                     LEFT JOIN users u1 ON gr.created_by = u1.user_id
                     LEFT JOIN users u2 ON gr.approved_by = u2.user_id
                     ORDER BY gr.receipt_id DESC
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql);
             ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                list.add(extractGoodsReceipt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<GoodsReceipt> searchGoodsReceipts(String search) {
        List<GoodsReceipt> list = new ArrayList<>();
        String sql = """
                     SELECT gr.*, 
                            s.supplier_id, s.supplier_name, s.contact_person, s.email, s.phone, s.status as supplier_status, s.description as supplier_description, s.created_at as supplier_created_at,
                            u1.user_id as created_by_id, u1.username as created_by_username, u1.email as created_by_email, u1.full_name as created_by_name, u1.phone as created_by_phone, u1.address as created_by_address, u1.avatar as created_by_avatar, u1.role_id as created_by_role_id, u1.is_active as created_by_active, u1.created_at as created_by_created_at,
                            u2.user_id as approved_by_id, u2.username as approved_by_username, u2.email as approved_by_email, u2.full_name as approved_by_name, u2.phone as approved_by_phone, u2.address as approved_by_address, u2.avatar as approved_by_avatar, u2.role_id as approved_by_role_id, u2.is_active as approved_by_active, u2.created_at as approved_by_created_at
                     FROM goods_receipts gr
                     LEFT JOIN suppliers s ON gr.supplier_id = s.supplier_id
                     LEFT JOIN users u1 ON gr.created_by = u1.user_id
                     LEFT JOIN users u2 ON gr.approved_by = u2.user_id
                     WHERE gr.receipt_code LIKE ? OR s.supplier_name LIKE ? OR gr.notes LIKE ?
                     ORDER BY gr.receipt_id DESC
                     """;
        String pattern = "%" + search.trim() + "%";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, pattern);
            pre.setString(2, pattern);
            pre.setString(3, pattern);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                list.add(extractGoodsReceipt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public GoodsReceipt getGoodsReceiptById(int receiptId) {
        String sql = """
                     SELECT gr.*, 
                            s.supplier_id, s.supplier_name, s.contact_person, s.email, s.phone, s.status as supplier_status, s.description as supplier_description, s.created_at as supplier_created_at,
                            u1.user_id as created_by_id, u1.username as created_by_username, u1.email as created_by_email, u1.full_name as created_by_name, u1.phone as created_by_phone, u1.address as created_by_address, u1.avatar as created_by_avatar, u1.role_id as created_by_role_id, u1.is_active as created_by_active, u1.created_at as created_by_created_at,
                            u2.user_id as approved_by_id, u2.username as approved_by_username, u2.email as approved_by_email, u2.full_name as approved_by_name, u2.phone as approved_by_phone, u2.address as approved_by_address, u2.avatar as approved_by_avatar, u2.role_id as approved_by_role_id, u2.is_active as approved_by_active, u2.created_at as approved_by_created_at
                     FROM goods_receipts gr
                     LEFT JOIN suppliers s ON gr.supplier_id = s.supplier_id
                     LEFT JOIN users u1 ON gr.created_by = u1.user_id
                     LEFT JOIN users u2 ON gr.approved_by = u2.user_id
                     WHERE gr.receipt_id = ?
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, receiptId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return extractGoodsReceipt(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
        private GoodsReceipt extractGoodsReceipt(ResultSet rs) throws SQLException {
        GoodsReceipt gr = new GoodsReceipt();
        gr.setReceiptId(rs.getInt("receipt_id"));
        gr.setReceiptCode(rs.getString("receipt_code"));
        gr.setPurchaseOrderId((Integer) rs.getObject("purchase_order_id"));
        
        if (rs.getObject("supplier_id") != null) {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(rs.getInt("supplier_id"));
            supplier.setSupplierName(rs.getString("supplier_name"));
            supplier.setContactPerson(rs.getString("contact_person"));
            supplier.setEmail(rs.getString("email"));
            supplier.setPhone(rs.getString("phone"));
            supplier.setStatus(rs.getString("supplier_status"));
            supplier.setDescription(rs.getString("supplier_description"));
            supplier.setCreatedAt(rs.getTimestamp("supplier_created_at"));
            gr.setSupplier(supplier);
        }
        
        gr.setReceiptDate(rs.getDate("receipt_date"));
        gr.setTotalAmount(rs.getBigDecimal("total_amount"));
        gr.setStatus(rs.getString("status"));
        gr.setSalesReturnId((Integer) rs.getObject("sales_return_id"));
        
        if (rs.getObject("created_by_id") != null) {
            User createdByUser = new User();
            createdByUser.setUserId(rs.getInt("created_by_id"));
            createdByUser.setUsername(rs.getString("created_by_username"));
            createdByUser.setEmail(rs.getString("created_by_email"));
            createdByUser.setFullName(rs.getString("created_by_name"));
            createdByUser.setPhone(rs.getString("created_by_phone"));
            createdByUser.setAddress(rs.getString("created_by_address"));
            createdByUser.setAvatar(rs.getString("created_by_avatar"));
            createdByUser.setIsActive(rs.getBoolean("created_by_active"));
            createdByUser.setCreatedAt(rs.getTimestamp("created_by_created_at"));
            gr.setCreatedByUser(createdByUser);
        }
        
        if (rs.getObject("approved_by_id") != null) {
            User approvedByUser = new User();
            approvedByUser.setUserId(rs.getInt("approved_by_id"));
            approvedByUser.setUsername(rs.getString("approved_by_username"));
            approvedByUser.setEmail(rs.getString("approved_by_email"));
            approvedByUser.setFullName(rs.getString("approved_by_name"));
            approvedByUser.setPhone(rs.getString("approved_by_phone"));
            approvedByUser.setAddress(rs.getString("approved_by_address"));
            approvedByUser.setAvatar(rs.getString("approved_by_avatar"));
            approvedByUser.setIsActive(rs.getBoolean("approved_by_active"));
            approvedByUser.setCreatedAt(rs.getTimestamp("approved_by_created_at"));
            gr.setApprovedByUser(approvedByUser);
        }
        
        gr.setNotes(rs.getString("notes"));
        gr.setCreatedAt(rs.getTimestamp("created_at"));
        gr.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return gr;
    }

    /**
     * Cập nhật trạng thái phiếu nhập. Khi status = 'completed' thì mới tăng số lượng tồn kho và ghi inventory_transactions.
     * @param approvedBy User duyệt (dùng khi status = 'completed'), có thể null khi hủy.
     */
    public boolean updateGoodsReceiptStatus(int receiptId, String status, Integer approvedBy) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            GoodsReceipt receipt = getGoodsReceiptById(receiptId);
            if (receipt == null) {
                return false;
            }

            if ("completed".equals(status)) {
                if ("completed".equals(receipt.getStatus())) {
                    conn.rollback();
                    return true; // Đã duyệt rồi, không áp dụng lại
                }
                applyReceiptToInventory(conn, receiptId, approvedBy != null ? approvedBy : 0);
            }

            String sql = "UPDATE goods_receipts SET status = ?, approved_by = ?, updated_at = CURRENT_TIMESTAMP WHERE receipt_id = ?";
            try (PreparedStatement pre = conn.prepareStatement(sql)) {
                pre.setString(1, status);
                pre.setObject(2, "completed".equals(status) ? approvedBy : null);
                pre.setInt(3, receiptId);
                pre.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /** Tăng tồn kho theo chi tiết phiếu nhập và ghi inventory_transactions. Gọi khi duyệt phiếu (completed). */
    private void applyReceiptToInventory(Connection conn, int receiptId, int createdBy) throws SQLException {
        List<model.GoodsReceiptDetail> details = getGoodsReceiptDetails(receiptId);
        String sqlQty = "SELECT quantity FROM product_variants WHERE variant_id = ?";
        String sqlUpdateQty = "UPDATE product_variants SET quantity = quantity + ? WHERE variant_id = ?";
        String sqlTrx = """
            INSERT INTO inventory_transactions
            (variant_id, transaction_type, reference_type, reference_id, quantity_change, quantity_before, quantity_after, created_by)
            VALUES (?, 'import', 'goods_receipt', ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement psQty = conn.prepareStatement(sqlQty);
             PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateQty);
             PreparedStatement psTrx = conn.prepareStatement(sqlTrx)) {
            for (model.GoodsReceiptDetail d : details) {
                int qtyChange = d.getQuantity();
                int qtyBefore = 0;
                psQty.setInt(1, d.getVariantId());
                try (ResultSet rs = psQty.executeQuery()) {
                    if (rs.next()) {
                        qtyBefore = rs.getInt("quantity");
                    }
                }
                psUpdate.setInt(1, qtyChange);
                psUpdate.setInt(2, d.getVariantId());
                psUpdate.executeUpdate();
                int qtyAfter = qtyBefore + qtyChange;
                psTrx.setInt(1, d.getVariantId());
                psTrx.setInt(2, receiptId);
                psTrx.setInt(3, qtyChange);
                psTrx.setInt(4, qtyBefore);
                psTrx.setInt(5, qtyAfter);
                psTrx.setInt(6, createdBy);
                psTrx.executeUpdate();
            }
        }
    }
    
    public boolean receiptCodeExists(String receiptCode) {
        String sql = "SELECT COUNT(*) FROM goods_receipts WHERE receipt_code = ?";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, receiptCode);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /** Lấy receipt_id theo mã phiếu (dùng sau khi tạo phiếu để hoàn tất nhập kho). */
    public Integer getReceiptIdByReceiptCode(String receiptCode) {
        if (receiptCode == null || receiptCode.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT receipt_id FROM goods_receipts WHERE receipt_code = ? LIMIT 1";
        try (PreparedStatement pre = getConnection().prepareStatement(sql)) {
            pre.setString(1, receiptCode.trim());
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt("receipt_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean serialNumberExists(String serialNumber) {
        String sql = "SELECT COUNT(*) FROM product_serials WHERE serial_number = ?";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, serialNumber);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Check serial validity for receipt source.
     * - Normal source: serial is blocked if it already exists.
     * - Sale source: serial is blocked unless current status is 'sold'.
     */
    public boolean isSerialBlockedForReceipt(String serialNumber, boolean isFromSale) {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            return true;
        }
        String sql = "SELECT status FROM product_serials WHERE serial_number = ? LIMIT 1";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, serialNumber.trim());
            ResultSet rs = pre.executeQuery();
            if (!rs.next()) {
                return false;
            }
            String status = rs.getString("status");
            if (isFromSale) {
                return !"sold".equalsIgnoreCase(status);
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public boolean createGoodsReceipt(String receiptCode, Integer supplierId, String receiptDate, double totalAmount, 
                                     String notes, int createdBy, java.util.List<model.GoodsReceiptDetail> details) {
        return createGoodsReceipt(receiptCode, supplierId, receiptDate, totalAmount, notes, createdBy, details, null, null);
    }

    public boolean createGoodsReceipt(String receiptCode, Integer supplierId, String receiptDate, double totalAmount, 
                                     String notes, int createdBy, java.util.List<model.GoodsReceiptDetail> details,
                                     Integer salesReturnId) {
        return createGoodsReceipt(receiptCode, supplierId, receiptDate, totalAmount, notes, createdBy, details, salesReturnId, null);
    }

    public boolean createGoodsReceipt(String receiptCode, Integer supplierId, String receiptDate, double totalAmount, 
                                     String notes, int createdBy, java.util.List<model.GoodsReceiptDetail> details,
                                     Integer salesReturnId, Integer purchaseOrderId) {
        java.sql.Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // "Đầu vào từ Sale" được map supplier_id = NULL ở controller.
            // Vì vậy cần nhận diện nguồn Sale bằng cả salesReturnId và supplierId.
            boolean isFromSale = salesReturnId != null || supplierId == null;
            
            // Validate serial numbers first.
            String sqlCheckSerial = "SELECT status, variant_id FROM product_serials WHERE serial_number = ? LIMIT 1";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckSerial)) {
                Set<String> payloadSerials = new HashSet<>();
                for (model.GoodsReceiptDetail detail : details) {
                    if (detail.getSerials() != null && !detail.getSerials().isEmpty()) {
                        for (String serial : detail.getSerials()) {
                            String normalizedSerial = serial != null ? serial.trim() : "";
                            if (normalizedSerial.isEmpty()) {
                                throw new SQLException("Serial number không được để trống.");
                            }
                            if (!payloadSerials.add(normalizedSerial)) {
                                throw new SQLException("Serial number '" + normalizedSerial + "' bị trùng trong phiếu nhập.");
                            }

                            psCheck.setString(1, normalizedSerial);
                            try (ResultSet rs = psCheck.executeQuery()) {
                                if (rs.next()) {
                                    String currentStatus = rs.getString("status");
                                    int currentVariantId = rs.getInt("variant_id");
                                    if (isFromSale) {
                                        if (!"sold".equalsIgnoreCase(currentStatus)) {
                                            throw new SQLException("Serial number '" + normalizedSerial
                                                    + "' không hợp lệ để nhập từ Sale (chỉ chấp nhận serial đang sold).");
                                        }
                                        if (currentVariantId != detail.getVariantId()) {
                                            throw new SQLException("Serial number '" + normalizedSerial
                                                    + "' không thuộc đúng sản phẩm.");
                                        }
                                    } else {
                                        throw new SQLException("Serial number '" + normalizedSerial + "' đã tồn tại trong hệ thống!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            String sqlReceipt;
            PreparedStatement ps;
            if (purchaseOrderId != null) {
                sqlReceipt = "INSERT INTO goods_receipts " +
                             "(receipt_code, supplier_id, receipt_date, total_amount, status, created_by, notes, purchase_order_id, sales_return_id) " +
                             "VALUES (?, ?, ?, ?, 'draft', ?, ?, ?, ?)";
                ps = conn.prepareStatement(sqlReceipt, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, receiptCode);
                ps.setObject(2, supplierId);
                ps.setString(3, receiptDate);
                ps.setDouble(4, totalAmount);
                ps.setInt(5, createdBy);
                ps.setString(6, notes);
                ps.setObject(7, purchaseOrderId);
                ps.setObject(8, salesReturnId);
            } else {
                sqlReceipt = "INSERT INTO goods_receipts " +
                             "(receipt_code, supplier_id, receipt_date, total_amount, status, created_by, notes, sales_return_id) " +
                             "VALUES (?, ?, ?, ?, 'draft', ?, ?, ?)";
                ps = conn.prepareStatement(sqlReceipt, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, receiptCode);
                ps.setObject(2, supplierId);
                ps.setString(3, receiptDate);
                ps.setDouble(4, totalAmount);
                ps.setInt(5, createdBy);
                ps.setString(6, notes);
                ps.setObject(7, salesReturnId);
            }
            int receiptId;
            try (ps) {
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    receiptId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to get receipt_id");
                }
            }
            
            // Insert details + serials
            String sqlDetail = """
                              INSERT INTO goods_receipt_details 
                              (receipt_id, variant_id, quantity, unit_price, total_amount)
                              VALUES (?, ?, ?, ?, ?)
                              """;
            String sqlSerialInsert = """
                              INSERT INTO product_serials 
                              (variant_id, receipt_detail_id, serial_number, status)
                              VALUES (?, ?, ?, 'in_stock')
                              """;
            String sqlSerialRestock = """
                              UPDATE product_serials
                              SET variant_id = ?, receipt_detail_id = ?, status = 'in_stock'
                              WHERE serial_number = ? AND status = 'sold'
                              """;
            // Số lượng chỉ tăng khi phiếu được duyệt (completed), không tăng lúc tạo draft
            
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail, PreparedStatement.RETURN_GENERATED_KEYS)) {
                for (model.GoodsReceiptDetail detail : details) {
                    psDetail.setInt(1, receiptId);
                    psDetail.setInt(2, detail.getVariantId());
                    psDetail.setInt(3, detail.getQuantity());
                    psDetail.setBigDecimal(4, detail.getUnitPrice());
                    psDetail.setBigDecimal(5, detail.getTotalAmount());
                    psDetail.executeUpdate();
                    
                    // Get the generated detail_id
                    ResultSet rsDetail = psDetail.getGeneratedKeys();
                    int detailId = 0;
                    if (rsDetail.next()) {
                        detailId = rsDetail.getInt(1);
                    }
                    rsDetail.close();
                    
                    // Insert serials if exists
                    if (detail.getSerials() != null && !detail.getSerials().isEmpty() && detailId > 0) {
                        for (String serial : detail.getSerials()) {
                            String normalizedSerial = serial != null ? serial.trim() : "";
                            if (isFromSale) {
                                try (PreparedStatement psSerialUpdate = conn.prepareStatement(sqlSerialRestock)) {
                                    psSerialUpdate.setInt(1, detail.getVariantId());
                                    psSerialUpdate.setInt(2, detailId);
                                    psSerialUpdate.setString(3, normalizedSerial);
                                    int affected = psSerialUpdate.executeUpdate();
                                    if (affected <= 0) {
                                        // Serial chưa tồn tại: cho phép tạo mới ở nguồn nhập từ Sale.
                                        try (PreparedStatement psSerialInsert = conn.prepareStatement(sqlSerialInsert)) {
                                            psSerialInsert.setInt(1, detail.getVariantId());
                                            psSerialInsert.setInt(2, detailId);
                                            psSerialInsert.setString(3, normalizedSerial);
                                            psSerialInsert.executeUpdate();
                                        }
                                    }
                                }
                            } else {
                                try (PreparedStatement psSerialInsert = conn.prepareStatement(sqlSerialInsert)) {
                                    psSerialInsert.setInt(1, detail.getVariantId());
                                    psSerialInsert.setInt(2, detailId);
                                    psSerialInsert.setString(3, normalizedSerial);
                                    psSerialInsert.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    // Search products and return JSON for goods receipt (for AJAX)
    public String searchProductsForReceiptJson(String keyword) {
        String sql = """
                     SELECT pv.variant_id, pv.sku, pv.cost_price, 
                            p.product_name, u.unit_name 
                     FROM product_variants pv 
                     INNER JOIN products p ON pv.product_id = p.product_id 
                     LEFT JOIN units u ON p.unit_id = u.unit_id 
                     WHERE pv.status = 'active' AND p.status = 'active' 
                     AND (pv.sku LIKE ? OR p.product_name LIKE ?) 
                     """;
        
        StringBuilder json = new StringBuilder("[");
        String pattern = (keyword != null && !keyword.trim().isEmpty()) 
                ? "%" + keyword.trim() + "%" 
                : "%";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;
                
                json.append("{")
                    .append("\"variantId\":").append(rs.getInt("variant_id")).append(",")
                    .append("\"code\":\"").append(escapeJson(rs.getString("sku"))).append("\",")
                    .append("\"name\":\"").append(escapeJson(rs.getString("product_name"))).append("\",")
                    .append("\"unit\":\"").append(escapeJson(rs.getString("unit_name"))).append("\",")
                    .append("\"price\":").append(rs.getBigDecimal("cost_price"))
                    .append("}");
            }
        } catch (SQLException e) {
        }
        json.append("]");
        return json.toString();
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    // Search product by SKU (for Excel import)
    public String searchProductBySKU(String sku) {
        String sql = """
                     SELECT pv.variant_id, pv.sku, pv.cost_price, 
                            p.product_name, u.unit_name 
                     FROM product_variants pv 
                     INNER JOIN products p ON pv.product_id = p.product_id 
                     LEFT JOIN units u ON p.unit_id = u.unit_id 
                     WHERE pv.status = 'active' AND p.status = 'active' 
                     AND pv.sku = ?
                     LIMIT 1
                     """;
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, sku);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder json = new StringBuilder("{");
                json.append("\"variantId\":").append(rs.getInt("variant_id")).append(",")
                    .append("\"code\":\"").append(escapeJson(rs.getString("sku"))).append("\",")
                    .append("\"name\":\"").append(escapeJson(rs.getString("product_name"))).append("\",")
                    .append("\"unit\":\"").append(escapeJson(rs.getString("unit_name"))).append("\",")
                    .append("\"price\":").append(rs.getBigDecimal("cost_price"))
                    .append("}");
                return json.toString();
            }
        } catch (SQLException e) {
        }
        return "{}";
    }

    
    public List<model.GoodsReceiptDetail> getGoodsReceiptDetails(int receiptId) {
        List<model.GoodsReceiptDetail> details = new ArrayList<>();
        String sql = """
                     SELECT grd.*, 
                            pv.sku,
                            p.product_name,
                            u.unit_name
                     FROM goods_receipt_details grd
                     INNER JOIN product_variants pv ON grd.variant_id = pv.variant_id
                     INNER JOIN products p ON pv.product_id = p.product_id
                     INNER JOIN units u ON p.unit_id = u.unit_id
                     WHERE grd.receipt_id = ?
                     """;        
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, receiptId);
            ResultSet rs = pre.executeQuery();        
            while (rs.next()) {
                model.GoodsReceiptDetail detail = new model.GoodsReceiptDetail();
                detail.setReceiptDetailId(rs.getInt("receipt_detail_id"));
                detail.setReceiptId(rs.getInt("receipt_id"));
                detail.setVariantId(rs.getInt("variant_id"));
                detail.setVariantSku(rs.getString("sku"));
                detail.setProductName(rs.getString("product_name"));
                detail.setUnitName(rs.getString("unit_name"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                detail.setTotalAmount(rs.getBigDecimal("total_amount"));
                detail.setNotes(rs.getString("notes"));
                
                List<String> serials = getSerialsForDetail(rs.getInt("receipt_detail_id"));
                detail.setSerials(serials);
                
                details.add(detail);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return details;
    }
    
    private List<String> getSerialsForDetail(int receiptDetailId) {
        List<String> serials = new ArrayList<>();
        String sql = """
                     SELECT ps.serial_number 
                     FROM product_serials ps
                     WHERE ps.receipt_detail_id = ?
                     ORDER BY ps.serial_id
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, receiptDetailId);
            ResultSet rs = pre.executeQuery();
            
            while (rs.next()) {
                serials.add(rs.getString("serial_number"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoodsReceiptDAO.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return serials;
    }
}
