/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.ProductInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hung
 */
public class ProductDAO extends DBContext {

    public List<ProductInventory> getProductInventory(String keyword, Integer categoryId, String status, int page, int pageSize, String sort, String dir) {
        List<ProductInventory> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String orderBy = "p.product_id";
        if ("name".equals(sort)) {
            orderBy = "p.product_name";
        } else if ("quantity".equals(sort)) {
            orderBy = "total_quantity";
        }
        String direction = "ASC";
        if ("desc".equalsIgnoreCase(dir)) {
            direction = "DESC";
        }
        String sql
                = "SELECT p.product_id, p.product_name, c.category_name, "
                + "u.unit_name, COALESCE(SUM(pv.quantity),0) AS total_quantity "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN units u ON p.unit_id = u.unit_id "
                + "LEFT JOIN product_variants pv "
                + "  ON p.product_id = pv.product_id AND pv.status = 'active' "
                + "WHERE p.status = 'active' "
                + "AND (? IS NULL OR p.product_name LIKE ?) "
                + "AND (? IS NULL OR p.category_id = ?) "
                + "GROUP BY p.product_id, p.product_name, c.category_name, u.unit_name "
                + "ORDER BY " + orderBy + " " + direction + " "
                + "LIMIT ? OFFSET ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, keyword);
            ps.setString(2, keyword == null ? null : "%" + keyword + "%");
            ps.setObject(3, categoryId);
            ps.setObject(4, categoryId);
            ps.setInt(5, pageSize);
            ps.setInt(6, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductInventory p = new ProductInventory();
                    p.setProductId(rs.getInt("product_id"));
                    p.setProductName(rs.getString("product_name"));
                    p.setCategoryName(rs.getString("category_name"));
                    p.setUnitName(rs.getString("unit_name"));
                    int qty = rs.getInt("total_quantity");
                    p.setTotalQuantity(qty);
                    String computedStatus = qty > 0 ? "In Stock" : "Out of Stock";
                    p.setStatus(computedStatus);
                    if (status != null && !status.isEmpty()
                            && !computedStatus.equals(status)) {
                        continue;
                    }
                    list.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countProductInventory(String keyword, Integer categoryId) {

        String sql
                = "SELECT COUNT(DISTINCT p.product_id) "
                + "FROM products p "
                + "LEFT JOIN product_variants pv ON p.product_id = pv.product_id "
                + "WHERE p.status = 'active' "
                + "AND (? IS NULL OR p.product_name LIKE ?) "
                + "AND (? IS NULL OR p.category_id = ?)";

        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setObject(1, keyword);
            ps.setString(2, keyword == null ? null : "%" + keyword + "%");
            ps.setObject(3, categoryId);
            ps.setObject(4, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
