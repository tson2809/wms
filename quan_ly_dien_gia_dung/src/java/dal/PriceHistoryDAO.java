/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GIAKHANHPC
 */
public class PriceHistoryDAO extends DBContext {

    public List<String[]> getPriceHistoryList() {
        List<String[]> list = new ArrayList<>();
        String sql = """
        SELECT pv.variant_id,
               pv.sku,
               p.product_name,
               ph.old_cost_price,
               ph.new_cost_price,
               ph.old_sale_price,
               ph.new_sale_price,
               ph.change_date,
               u.full_name,
               ph.reason
        FROM price_history ph
        JOIN product_variants pv ON ph.variant_id = pv.variant_id
        JOIN products p ON pv.product_id = p.product_id
        JOIN users u ON ph.changed_by = u.user_id
        ORDER BY ph.change_date DESC
    """;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] s = new String[10];
                s[0] = rs.getString("variant_id");
                s[1] = rs.getString("sku");
                s[2] = rs.getString("product_name");
                s[3] = rs.getString("old_cost_price");
                s[4] = rs.getString("new_cost_price");
                s[5] = rs.getString("old_sale_price");
                s[6] = rs.getString("new_sale_price");
                s[7] = rs.getString("change_date");
                s[8] = rs.getString("full_name");
                s[9] = rs.getString("reason");
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getPriceHistoryByVariant(int variantId,
            String fromDate, String toDate,
            int page, int pageSize) {
        List<String[]> list = new ArrayList<>();
        String sql = """
    SELECT ph.old_cost_price,
           ph.new_cost_price,
           ph.old_sale_price,
           ph.new_sale_price,
           ph.change_date,
           ph.reason,
           u.full_name
    FROM price_history ph
    JOIN users u ON ph.changed_by = u.user_id
    WHERE ph.variant_id = ?
    AND (? IS NULL OR ph.change_date >= ?)
    AND (? IS NULL OR ph.change_date <= ?)
    ORDER BY ph.change_date DESC
    LIMIT ?,?
    """;
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.setString(2, fromDate);
            ps.setString(3, fromDate);
            ps.setString(4, toDate);
            ps.setString(5, toDate);
            ps.setInt(6, (page - 1) * pageSize);
            ps.setInt(7, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] s = new String[7];
                s[0] = rs.getString("old_cost_price");
                s[1] = rs.getString("new_cost_price");
                s[2] = rs.getString("old_sale_price");
                s[3] = rs.getString("new_sale_price");
                s[4] = rs.getString("change_date");
                s[5] = rs.getString("reason");
                s[6] = rs.getString("full_name");
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countPriceHistory(int variantId, String fromDate, String toDate) {
        String sql = """
        SELECT COUNT(*)
        FROM price_history
        WHERE variant_id = ?
        AND (? IS NULL OR change_date >= ?)
        AND (? IS NULL OR change_date <= ?)
    """;
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.setString(2, fromDate);
            ps.setString(3, fromDate);
            ps.setString(4, toDate);
            ps.setString(5, toDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updatePrice(int variantId, double newCost, double newSale,
            String reason, int changedBy) {

        String getPrice = """
        SELECT cost_price, sale_price
        FROM product_variants
        WHERE variant_id = ?
    """;
        String insertHistory = """
        INSERT INTO price_history
        (variant_id, old_cost_price, new_cost_price,
         old_sale_price, new_sale_price, changed_by, reason)
        VALUES (?,?,?,?,?,?,?)
    """;
        String updateVariant = """
        UPDATE product_variants
        SET cost_price = ?, sale_price = ?
        WHERE variant_id = ?
    """;
        try (Connection con = getConnection()) {
            PreparedStatement ps1 = con.prepareStatement(getPrice);
            ps1.setInt(1, variantId);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                double oldCost = rs.getDouble("cost_price");
                double oldSale = rs.getDouble("sale_price");
                PreparedStatement ps2 = con.prepareStatement(insertHistory);
                ps2.setInt(1, variantId);
                ps2.setDouble(2, oldCost);
                ps2.setDouble(3, newCost);
                ps2.setDouble(4, oldSale);
                ps2.setDouble(5, newSale);
                ps2.setInt(6, changedBy);
                ps2.setString(7, reason);
                ps2.executeUpdate();
                PreparedStatement ps3 = con.prepareStatement(updateVariant);
                ps3.setDouble(1, newCost);
                ps3.setDouble(2, newSale);
                ps3.setInt(3, variantId);
                ps3.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getVariantPrice(int variantId) {
        String sql = """
        SELECT sku, cost_price, sale_price
        FROM product_variants
        WHERE variant_id = ?
    """;
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] v = new String[3];
                v[0] = rs.getString("sku");
                v[1] = rs.getString("cost_price");
                v[2] = rs.getString("sale_price");
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getVariantInfo(int variantId) {
        String sql = """
        SELECT pv.sku, p.product_name
        FROM product_variants pv
        JOIN products p ON pv.product_id = p.product_id
        WHERE pv.variant_id = ?
    """;
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String[] v = new String[2];
                v[0] = rs.getString("sku");
                v[1] = rs.getString("product_name");
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
