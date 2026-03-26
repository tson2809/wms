/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.InventorySheet;
import model.ProductInventory;

/**
 *
 * @author hung
 */
public class InventorySheetDAO extends DBContext {

    public InventorySheet getSheetById(int sheetId) {
        String sql = """
        SELECT s.sheet_id,
               s.sheet_code,
               s.inventory_date,
               s.status,
               s.notes,
               s.created_by,
               c.category_name,
               u.full_name AS created_by_name,
               s.created_at
        FROM inventory_sheets s
        LEFT JOIN categories c ON s.category_id = c.category_id
        LEFT JOIN users u ON s.created_by = u.user_id
        WHERE s.sheet_id = ?
    """;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sheetId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                InventorySheet s = new InventorySheet();
                s.setSheetId(rs.getInt("sheet_id"));
                s.setSheetCode(rs.getString("sheet_code"));
                s.setInventoryDate(rs.getDate("inventory_date"));
                s.setStatus(rs.getString("status"));
                s.setNotes(rs.getString("notes"));
                s.setCategoryName(rs.getString("category_name"));
                s.setCreatedBy(rs.getInt("created_by"));
                s.setCreatedByName(rs.getString("created_by_name"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<InventorySheet> getSheetsByYear(int year) {
        List<InventorySheet> list = new ArrayList<>();

        String sql = """
            SELECT s.sheet_id, s.sheet_code, s.inventory_date, s.status,
                   c.category_name, u.full_name AS created_by,
                   s.created_at
            FROM inventory_sheets s
            LEFT JOIN categories c ON s.category_id = c.category_id
            LEFT JOIN users u ON s.created_by = u.user_id
            WHERE YEAR(s.inventory_date) = ?
            ORDER BY s.inventory_date DESC
        """;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InventorySheet s = new InventorySheet();
                s.setSheetId(rs.getInt("sheet_id"));
                s.setSheetCode(rs.getString("sheet_code"));
                s.setInventoryDate(rs.getDate("inventory_date"));
                s.setStatus(rs.getString("status"));
                s.setCategoryName(rs.getString("category_name"));
                s.setCreatedByName(rs.getString("created_by"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<InventorySheet> getSheetsByYearMonth(int year, Integer month) {

        List<InventorySheet> list = new ArrayList<>();

        String sql
                = "SELECT s.sheet_id, s.sheet_code, s.inventory_date, s.status, "
                + "c.category_name, u.full_name AS created_by, s.created_at "
                + "FROM inventory_sheets s "
                + "LEFT JOIN categories c ON s.category_id = c.category_id "
                + "LEFT JOIN users u ON s.created_by = u.user_id "
                + "WHERE YEAR(s.inventory_date) = ? "
                + "AND (? IS NULL OR MONTH(s.inventory_date) = ?) "
                + "ORDER BY s.inventory_date DESC";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setObject(2, month);
            ps.setObject(3, month);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InventorySheet s = new InventorySheet();
                s.setSheetId(rs.getInt("sheet_id"));
                s.setSheetCode(rs.getString("sheet_code"));
                s.setInventoryDate(rs.getDate("inventory_date"));
                s.setStatus(rs.getString("status"));
                s.setCategoryName(rs.getString("category_name"));
                s.setCreatedByName(rs.getString("created_by"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int createSheet(Integer categoryId, Date date, int userId) {

        String sql = """
        INSERT INTO inventory_sheets
        (sheet_code, category_id, inventory_date, status, created_by)
        VALUES (?, ?, ?, 'draft', ?)
    """;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, "INV-" + System.currentTimeMillis());
            ps.setInt(2, categoryId);
            ps.setDate(3, date);
            ps.setInt(4, userId);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertSheetDetails(int sheetId,
            String[] variantIds,
            String[] systemQtys,
            String[] countedQtys) {

        String sql = """
        INSERT INTO inventory_sheet_details
        (sheet_id, variant_id, system_quantity, counted_quantity)
        VALUES (?, ?, ?, ?)
    """;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < variantIds.length; i++) {
                ps.setInt(1, sheetId);
                ps.setInt(2, Integer.parseInt(variantIds[i]));
                ps.setInt(3, Integer.parseInt(systemQtys[i]));
                ps.setInt(4, Integer.parseInt(countedQtys[i]));
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int sheetId, String status) {
        String sql = "UPDATE inventory_sheets SET status = ? WHERE sheet_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, sheetId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProductInventory> getSheetDetails(int sheetId) {
        List<ProductInventory> list = new ArrayList<>();
        String sql
                = "SELECT d.detail_id, d.variant_id, p.product_name, pv.sku, "
                + "d.system_quantity, d.counted_quantity "
                + "FROM inventory_sheet_details d "
                + "JOIN product_variants pv ON d.variant_id = pv.variant_id "
                + "JOIN products p ON pv.product_id = p.product_id "
                + "WHERE d.sheet_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sheetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductInventory pi = new ProductInventory();
                pi.setDetailId(rs.getInt("detail_id"));
                pi.setVariantId(rs.getInt("variant_id"));
                pi.setProductName(rs.getString("product_name"));
                pi.setSku(rs.getString("sku"));
                pi.setSystemQuantity(rs.getInt("system_quantity"));
                pi.setCountedQuantity(rs.getInt("counted_quantity"));
                list.add(pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateCountedQuantities(String[] detailIds, String[] countedQtys) {

        String sql
                = "UPDATE inventory_sheet_details "
                + "SET counted_quantity = ? "
                + "WHERE detail_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < detailIds.length; i++) {
                ps.setInt(1, Integer.parseInt(countedQtys[i]));
                ps.setInt(2, Integer.parseInt(detailIds[i]));
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<InventorySheet> getSheetsPaging(
            Integer year, Integer month, String createdBy,
            String sort, String dir,
            int page, int pageSize) {

        List<InventorySheet> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        if (sort == null) {
            sort = "date";
        }
        if (dir == null) {
            dir = "asc";
        }

        String order = "s.inventory_date";
        if ("status".equals(sort)) {
            order = "s.status";
        }

        StringBuilder sql = new StringBuilder("""
       SELECT s.sheet_id, s.sheet_code, s.inventory_date, s.status,
                      s.created_by,
                      c.category_name, u.full_name,
                      EXISTS (
                          SELECT 1 FROM inventory_sheet_details d 
                          JOIN product_variants pv ON d.variant_id = pv.variant_id
                          WHERE d.sheet_id = s.sheet_id 
                          AND d.counted_quantity < d.system_quantity
                          AND (SELECT COUNT(*) FROM product_serials WHERE variant_id = d.variant_id AND status = 'in_stock') > pv.quantity
                      ) AS needs_resolution
        FROM inventory_sheets s
        LEFT JOIN categories c ON s.category_id = c.category_id
        LEFT JOIN users u ON s.created_by = u.user_id
        WHERE 1=1
    """);

        if (year != null) {
            sql.append(" AND YEAR(s.inventory_date)=? ");
        }
        if (month != null) {
            sql.append(" AND MONTH(s.inventory_date)=? ");
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            sql.append(" AND u.full_name LIKE ? ");
        }

        sql.append(" ORDER BY ").append(order).append(" ").append(dir);
        sql.append(" LIMIT ?,?");

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (year != null) {
                ps.setInt(index++, year);
            }
            if (month != null) {
                ps.setInt(index++, month);
            }
            if (createdBy != null && !createdBy.isEmpty()) {
                ps.setString(index++, "%" + createdBy + "%");
            }

            ps.setInt(index++, offset);
            ps.setInt(index, pageSize);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InventorySheet s = new InventorySheet();
                s.setSheetId(rs.getInt("sheet_id"));
                s.setSheetCode(rs.getString("sheet_code"));
                s.setInventoryDate(rs.getDate("inventory_date"));
                s.setStatus(rs.getString("status"));
                s.setCategoryName(rs.getString("category_name"));
                s.setCreatedByName(rs.getString("full_name"));
                s.setCreatedBy(rs.getInt("created_by"));
                s.setNeedsResolution(rs.getBoolean("needs_resolution"));
                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countSheets(Integer year, Integer month, String createdBy) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM inventory_sheets s
        LEFT JOIN users u ON s.created_by = u.user_id
        WHERE 1=1
    """);

        if (year != null) {
            sql.append(" AND YEAR(s.inventory_date)=?");
        }
        if (month != null) {
            sql.append(" AND MONTH(s.inventory_date)=?");
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            sql.append(" AND u.full_name LIKE ?");
        }

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (year != null) {
                ps.setInt(index++, year);
            }
            if (month != null) {
                ps.setInt(index++, month);
            }
            if (createdBy != null && !createdBy.isEmpty()) {
                ps.setString(index++, "%" + createdBy + "%");
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Integer> getAvailableYears() {
        List<Integer> list = new ArrayList<>();

        String sql = """
        SELECT DISTINCT YEAR(inventory_date) AS y
        FROM inventory_sheets
        ORDER BY y DESC
    """;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getInt("y"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> searchUserName(String keyword) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT full_name FROM users WHERE full_name LIKE ? LIMIT 10";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void approveSheet(int sheetId, int approvedBy) {

        String selectDetails = """
        SELECT d.variant_id,
               d.system_quantity,
               d.counted_quantity,
               pv.quantity
        FROM inventory_sheet_details d
        JOIN product_variants pv ON d.variant_id = pv.variant_id
        WHERE d.sheet_id = ?
    """;

        String updateVariant = """
        UPDATE product_variants
        SET quantity = ?
        WHERE variant_id = ?
    """;

        String insertTransaction = """
        INSERT INTO inventory_transactions
        (variant_id, transaction_type, reference_type,
         reference_id, quantity_change,
         quantity_before, quantity_after, created_by, notes)
        VALUES (?, 'adjustment', 'inventory_sheet',
                ?, ?, ?, ?, ?, ?)
    """;

        String insertSerial = """
        INSERT INTO product_serials 
        (variant_id, serial_number, status, notes)
        VALUES (?, ?, 'in_stock', ?)
    """;

        String updateSheet = """
        UPDATE inventory_sheets
        SET status = 'approved', approved_by = ?
        WHERE sheet_id = ?
    """;
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(selectDetails); 
                 PreparedStatement ps2 = con.prepareStatement(updateVariant); 
                 PreparedStatement ps3 = con.prepareStatement(insertTransaction); 
                 PreparedStatement psSerial = con.prepareStatement(insertSerial);
                 PreparedStatement ps4 = con.prepareStatement(updateSheet)) {
                 
                ps1.setInt(1, sheetId);
                ResultSet rs = ps1.executeQuery();
                while (rs.next()) {
                    int variantId = rs.getInt("variant_id");
                    int systemQty = rs.getInt("system_quantity");
                    int countedQty = rs.getInt("counted_quantity");
                    int currentQty = rs.getInt("quantity");
                    int difference = countedQty - systemQty;
                    
                    if (difference != 0) {
                        ps2.setInt(1, countedQty);
                        ps2.setInt(2, variantId);
                        ps2.executeUpdate();
                        
                        ps3.setInt(1, variantId);
                        ps3.setInt(2, sheetId);
                        ps3.setInt(3, difference);
                        ps3.setInt(4, currentQty);
                        ps3.setInt(5, countedQty);
                        ps3.setInt(6, approvedBy);
                        ps3.setString(7, "Điều chỉnh tồn kho sau kiểm kê");
                        ps3.executeUpdate();
                        
                        // Nếu thừa số lượng (surplus), auto generate serials
                        if (difference > 0) {
                            int maxIndex = 0;
                            String prefix = "V" + variantId + "S";
                            String sqlMax = "SELECT serial_number FROM product_serials WHERE variant_id = ? AND serial_number LIKE ?";
                            try (PreparedStatement psMax = con.prepareStatement(sqlMax)) {
                                psMax.setInt(1, variantId);
                                psMax.setString(2, prefix + "%");
                                try (ResultSet rsMax = psMax.executeQuery()) {
                                    while (rsMax.next()) {
                                        String sn = rsMax.getString(1);
                                        try {
                                            int idx = Integer.parseInt(sn.substring(prefix.length()));
                                            if (idx > maxIndex) {
                                                maxIndex = idx;
                                            }
                                        } catch (Exception e) {}
                                    }
                                }
                            }
                            
                            for (int i = 1; i <= difference; i++) {
                                int nextIdx = maxIndex + i;
                                String serialNumber = prefix + String.format("%03d", nextIdx); // padded to at least 3 digits
                                psSerial.setInt(1, variantId);
                                psSerial.setString(2, serialNumber);
                                psSerial.setString(3, "Generated from Inventory Sheet #" + sheetId);
                                psSerial.executeUpdate();
                            }
                        }
                    }
                }
                ps4.setInt(1, approvedBy);
                ps4.setInt(2, sheetId);
                ps4.executeUpdate();
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
