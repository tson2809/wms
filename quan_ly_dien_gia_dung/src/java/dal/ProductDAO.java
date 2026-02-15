/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.ProductInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<ProductInventory> getInventoryAlerts(
            Integer minQty, Integer maxQty, int page, int pageSize) {

        List<ProductInventory> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT p.product_id, p.product_name, c.category_name, u.unit_name, "
                + "COALESCE(SUM(pv.quantity),0) AS total_quantity "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN units u ON p.unit_id = u.unit_id "
                + "LEFT JOIN product_variants pv "
                + " ON p.product_id = pv.product_id AND pv.status = 'active' "
                + "WHERE p.status = 'active' "
                + "GROUP BY p.product_id, p.product_name, c.category_name, u.unit_name ");

        if (minQty != null && maxQty != null) {
            sql.append("HAVING total_quantity < ? OR total_quantity > ? ");
        } else if (minQty != null) {
            sql.append("HAVING total_quantity < ? ");
        } else if (maxQty != null) {
            sql.append("HAVING total_quantity > ? ");
        }

        sql.append("ORDER BY total_quantity ASC LIMIT ? OFFSET ?");

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int i = 1;
            if (minQty != null && maxQty != null) {
                ps.setInt(i++, minQty);
                ps.setInt(i++, maxQty);
            } else if (minQty != null) {
                ps.setInt(i++, minQty);
            } else if (maxQty != null) {
                ps.setInt(i++, maxQty);
            }

            ps.setInt(i++, pageSize);
            ps.setInt(i++, (page - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductInventory p = new ProductInventory();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setCategoryName(rs.getString("category_name"));
                p.setUnitName(rs.getString("unit_name"));

                int qty = rs.getInt("total_quantity");
                p.setTotalQuantity(qty);

                if (minQty != null && qty < minQty) {
                    p.setStatus("Low Stock");
                } else {
                    p.setStatus("High Stock");
                }
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countInventoryAlerts(Integer minQty, Integer maxQty) {

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM ( "
                + "SELECT p.product_id "
                + "FROM products p "
                + "LEFT JOIN product_variants pv "
                + " ON p.product_id = pv.product_id AND pv.status = 'active' "
                + "WHERE p.status = 'active' "
                + "GROUP BY p.product_id ");

        if (minQty != null && maxQty != null) {
            sql.append("HAVING SUM(pv.quantity) < ? OR SUM(pv.quantity) > ? ");
        } else if (minQty != null) {
            sql.append("HAVING SUM(pv.quantity) < ? ");
        } else if (maxQty != null) {
            sql.append("HAVING SUM(pv.quantity) > ? ");
        }

        sql.append(") t");

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int i = 1;
            if (minQty != null && maxQty != null) {
                ps.setInt(i++, minQty);
                ps.setInt(i++, maxQty);
            } else if (minQty != null) {
                ps.setInt(i++, minQty);
            } else if (maxQty != null) {
                ps.setInt(i++, maxQty);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<ProductInventory> getInventoryForCounting(int categoryId) {
        List<ProductInventory> list = new ArrayList<>();

        String sql
                = "SELECT pv.variant_id, p.product_name, pv.sku, "
                + "COALESCE(pv.quantity, 0) AS system_quantity "
                + "FROM products p "
                + "JOIN product_variants pv ON p.product_id = pv.product_id "
                + "WHERE p.status = 'active' "
                + "AND pv.status = 'active' "
                + "AND p.category_id = ? "
                + "ORDER BY p.product_name";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductInventory p = new ProductInventory();
                p.setVariantId(rs.getInt("variant_id"));
                p.setProductName(rs.getString("product_name"));
                p.setSku(rs.getString("sku"));
                p.setSystemQuantity(rs.getInt("system_quantity"));
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ProductInventory> getInventoryList(String keyword, Integer categoryId,
            String status, int page, int pageSize, String sort, String dir) {

        List<ProductInventory> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String orderBy = "p.product_name";
        if ("quantity".equals(sort)) {
            orderBy = "v.quantity";
        }

        String direction = "ASC";
        if ("desc".equalsIgnoreCase(dir)) {
            direction = "DESC";
        }

        String sql
                = "SELECT v.variant_id, v.sku, v.quantity, v.cost_price, v.sale_price, "
                + "v.variant_picture, "
                + "p.product_name, "
                + "c.category_name, "
                + "b.brand_name, "
                + "GROUP_CONCAT(a.attribute_value ORDER BY a.attribute_name SEPARATOR ', ') AS variant_name "
                + "FROM product_variants v "
                + "JOIN products p ON v.product_id = p.product_id "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                + "LEFT JOIN product_variant_attributes a ON v.variant_id = a.variant_id "
                + "WHERE p.status = 'active' AND v.status = 'active' "
                + "AND (? IS NULL OR p.product_name LIKE ?) "
                + "GROUP BY v.variant_id, v.sku, v.quantity, v.cost_price, v.sale_price, "
                + "v.variant_picture, p.product_name, c.category_name, b.brand_name "
                + "ORDER BY " + orderBy + " " + direction + " "
                + "LIMIT ? OFFSET ?";

        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setObject(1, keyword);
            ps.setString(2, keyword == null ? null : "%" + keyword + "%");
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductInventory p = new ProductInventory();
                    p.setVariantId(rs.getInt("variant_id"));
                    p.setSku(rs.getString("sku"));
                    p.setProductName(rs.getString("product_name"));
                    p.setCategoryName(rs.getString("category_name"));
                    p.setBrandName(rs.getString("brand_name"));
                    p.setVariantName(rs.getString("variant_name"));
                    p.setImage(rs.getString("variant_picture"));
                    p.setCostPrice(rs.getDouble("cost_price"));
                    p.setSalePrice(rs.getDouble("sale_price"));
                    p.setTotalQuantity(rs.getInt("quantity"));

                    int q = rs.getInt("quantity");
                    if (q == 0) {
                        p.setStatus("Out of Stock");
                    } else if (q < 5) {
                        p.setStatus("Low");
                    } else {
                        p.setStatus("In Stock");
                    }

                    if (status != null && !status.isEmpty()
                            && !p.getStatus().equals(status)) {
                        continue;
                    }

                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countInventory(String keyword, Integer categoryId) {
        int total = 0;
        String sql = "SELECT COUNT(*) " + "FROM product_variants v " + "JOIN products p ON v.product_id = p.product_id " + "WHERE p.status='active' AND v.status='active' " + "AND (? IS NULL OR p.product_name LIKE ?) " + "AND (? IS NULL OR p.category_id = ?)";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, keyword);
            ps.setString(2, keyword == null ? null : "%" + keyword + "%");
            ps.setObject(3, categoryId);
            ps.setObject(4, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public Map<String, Integer> getInventorySummary() {

        Map<String, Integer> map = new HashMap<>();

        String sql
                = "SELECT COUNT(*) totalSku, "
                + "SUM(quantity) totalQty, "
                + "SUM(CASE WHEN quantity<5 AND quantity>0 THEN 1 ELSE 0 END) lowStock, "
                + "SUM(CASE WHEN quantity=0 THEN 1 ELSE 0 END) outStock "
                + "FROM product_variants WHERE status='active'";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                map.put("totalSku", rs.getInt("totalSku"));
                map.put("totalQty", rs.getInt("totalQty"));
                map.put("lowStock", rs.getInt("lowStock"));
                map.put("outStock", rs.getInt("outStock"));
            }

        } catch (Exception e) {
        }

        return map;
    }
}
