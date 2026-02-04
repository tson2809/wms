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
        String sql
                = "SELECT s.sheet_id, s.sheet_code, s.inventory_date, s.status, s.notes, "
                + "c.category_name, u.full_name AS created_by, s.created_at "
                + "FROM inventory_sheets s "
                + "LEFT JOIN categories c ON s.category_id = c.category_id "
                + "LEFT JOIN users u ON s.created_by = u.user_id "
                + "WHERE s.sheet_id = ?";

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
                s.setCreatedByName(rs.getString("created_by"));
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
                = "SELECT d.detail_id, p.product_name, pv.sku, "
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

}
