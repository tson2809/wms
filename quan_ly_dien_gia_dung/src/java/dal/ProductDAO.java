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
import model.ProductInventory;
import modelDTO.ProductAddDTO;
import modelDTO.ProductVariantSimpleDTO;
import model.ProductVariant;



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
    
    public int insertProductFromDTO(ProductAddDTO dto) {
        Connection conn = null;
        PreparedStatement psProduct = null;
        PreparedStatement psVariant = null;
        ResultSet rs = null;
        int productId = 0;

        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. INSERT vào bảng PRODUCTS
            String sqlProduct = "INSERT INTO products (product_name, category_id, brand_id, " +
                    "supplier_id, unit_id, picture, description, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'active', NOW())";

            psProduct = conn.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS);
            psProduct.setString(1, dto.getProductName());
            psProduct.setInt(2, dto.getCategoryId());
            psProduct.setInt(3, dto.getBrandId());
            psProduct.setInt(4, dto.getSupplierId());
            psProduct.setInt(5, dto.getUnitId());
            psProduct.setString(6, dto.getPicture());
            psProduct.setString(7, dto.getDescription());

            int rowsAffected = psProduct.executeUpdate();

            if (rowsAffected > 0) {
                // Lấy productId vừa insert
                rs = psProduct.getGeneratedKeys();
                if (rs.next()) {
                    productId = rs.getInt(1);
                }

                // 2. INSERT vào bảng PRODUCT_VARIANTS và PRODUCT_VARIANT_ATTRIBUTES
                if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
                    for (ProductVariantSimpleDTO variantDTO : dto.getVariants()) {
                        int variantId = insertVariant(conn, productId, variantDTO);
                        if (variantId > 0 && dto.getAttributeNames() != null && !dto.getAttributeNames().isEmpty()
                                && variantDTO.getAttributeValues() != null && !variantDTO.getAttributeValues().isEmpty()) {
                            insertVariantAttributes(conn, variantId, dto.getAttributeNames(), variantDTO.getAttributeValues());
                        }
                    }
                } else {
                    ProductVariantSimpleDTO defaultVariant = new ProductVariantSimpleDTO();
                    defaultVariant.setSku(dto.getBaseSku());
                    defaultVariant.setBarcode(dto.getBaseBarcode());
                    insertVariant(conn, productId, defaultVariant);
                }

                conn.commit(); // Commit transaction
            }

        } catch (SQLException e) {
            // Rollback nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            productId = 0;
        } finally {
            // Đóng resources
            try {
                if (rs != null)
                    rs.close();
                if (psProduct != null)
                    psProduct.close();
                if (psVariant != null)
                    psVariant.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return productId;
    }

    private int insertVariant(Connection conn, int productId, ProductVariantSimpleDTO variantDTO)
            throws SQLException {
        String sql = "INSERT INTO product_variants (product_id, sku, barcode, " +
                "sale_price, cost_price, quantity, status, created_at) " +
                "VALUES (?, ?, ?, 0, 0, 0, 'active', NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, productId);
            ps.setString(2, variantDTO.getSku());
            String barcode = variantDTO.getBarcode();
            ps.setString(3, (barcode == null || barcode.trim().isEmpty()) ? null : barcode.trim());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return 0;
    }

    private void insertVariantAttributes(Connection conn, int variantId,
            List<String> attributeNames, List<String> attributeValues) throws SQLException {
        if (attributeNames == null || attributeValues == null || attributeNames.size() != attributeValues.size()) {
            return;
        }
        String sql = "INSERT INTO product_variant_attributes (variant_id, attribute_name, attribute_value) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < attributeNames.size(); i++) {
                String name = attributeNames.get(i);
                String value = attributeValues.get(i);
                if (name != null && !name.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                    ps.setInt(1, variantId);
                    ps.setString(2, name.trim());
                    ps.setString(3, value.trim());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT * FROM product_variants WHERE variant_id = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductVariant variant = new ProductVariant();
                    variant.setVariantId(rs.getInt("variant_id"));
                    variant.setProductId(rs.getInt("product_id"));
                    variant.setSku(rs.getString("sku"));
                    variant.setBarcode(rs.getString("barcode"));
                    variant.setVariantPicture(rs.getString("variant_picture"));
                    variant.setSalePrice(rs.getBigDecimal("sale_price"));
                    variant.setCostPrice(rs.getBigDecimal("cost_price"));
                    variant.setQuantity(rs.getInt("quantity"));
                    variant.setStatus(rs.getString("status"));
                    variant.setCreatedAt(rs.getTimestamp("created_at"));
                    return variant;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isSkuExists(String sku) {
        String sql = "SELECT COUNT(*) FROM product_variants WHERE sku = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, sku);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isBarcodeExists(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return false; // Barcode không bắt buộc
        }

        String sql = "SELECT COUNT(*) FROM product_variants WHERE barcode = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, barcode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}