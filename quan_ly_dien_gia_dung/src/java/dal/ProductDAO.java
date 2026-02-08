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

    public List<ProductInventory> getProductInventory(String keyword, Integer categoryId, String status, int page,
            int pageSize, String sort, String dir) {
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
        String sql = "SELECT p.product_id, p.product_name, c.category_name, "
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

        String sql = "SELECT COUNT(DISTINCT p.product_id) "
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

        String sql = "SELECT pv.variant_id, p.product_name, pv.sku, "
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
                                && variantDTO.getAttributeValues() != null
                                && !variantDTO.getAttributeValues().isEmpty()) {
                            insertVariantAttributes(conn, variantId, dto.getAttributeNames(),
                                    variantDTO.getAttributeValues());
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

    /**
     * Kiểm tra SKU đã tồn tại bởi sản phẩm khác (dùng khi edit, loại trừ product hiện tại).
     */
    public boolean isSkuExistsExcludingProduct(String sku, int productId) {
        if (sku == null || sku.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM product_variants WHERE sku = ? AND product_id != ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, sku.trim());
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra Barcode đã tồn tại bởi sản phẩm khác (dùng khi edit).
     */
    public boolean isBarcodeExistsExcludingProduct(String barcode, int productId) {
        if (barcode == null || barcode.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM product_variants WHERE barcode = ? AND product_id != ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, barcode.trim());
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy thông tin sản phẩm đầy đủ để hiển thị form chỉnh sửa (product + variants + attributes).
     */
    public ProductAddDTO getProductAddDTOById(int productId) {
        ProductAddDTO dto = new ProductAddDTO();
        dto.setProductId(productId);

        String sqlProduct = "SELECT product_name, category_id, brand_id, supplier_id, unit_id, picture, description "
                + "FROM products WHERE product_id = ? AND status = 'active'";
        try (Connection conn = getConnection();
             PreparedStatement psProduct = conn.prepareStatement(sqlProduct)) {
            psProduct.setInt(1, productId);
            try (ResultSet rsProduct = psProduct.executeQuery()) {
                if (!rsProduct.next()) return null;
                dto.setProductName(rsProduct.getString("product_name"));
                dto.setCategoryId(rsProduct.getInt("category_id"));
                dto.setBrandId(rsProduct.getInt("brand_id"));
                dto.setSupplierId(rsProduct.getInt("supplier_id"));
                dto.setUnitId(rsProduct.getInt("unit_id"));
                dto.setPicture(rsProduct.getString("picture"));
                dto.setDescription(rsProduct.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String sqlVariants = "SELECT variant_id, sku, barcode FROM product_variants WHERE product_id = ? AND status = 'active' ORDER BY variant_id";
        List<ProductVariantSimpleDTO> variantList = new ArrayList<>();
        List<Integer> variantIds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement psVar = conn.prepareStatement(sqlVariants)) {
            psVar.setInt(1, productId);
            try (ResultSet rsVar = psVar.executeQuery()) {
                while (rsVar.next()) {
                    ProductVariantSimpleDTO v = new ProductVariantSimpleDTO();
                    v.setVariantId(rsVar.getInt("variant_id"));
                    v.setSku(rsVar.getString("sku"));
                    v.setBarcode(rsVar.getString("barcode") != null ? rsVar.getString("barcode") : "");
                    variantList.add(v);
                    variantIds.add(rsVar.getInt("variant_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return dto;
        }

        if (variantList.isEmpty()) return dto;

        String sqlAttr = "SELECT variant_id, attribute_name, attribute_value FROM product_variant_attributes WHERE variant_id = ?";
        java.util.Map<Integer, java.util.Map<String, String>> variantAttrMap = new java.util.LinkedHashMap<>();
        java.util.Set<String> allAttrNamesSet = new java.util.LinkedHashSet<>();

        for (int vid : variantIds) {
            java.util.Map<String, String> nameToValue = new java.util.LinkedHashMap<>();
            try (PreparedStatement psAttr = getConnection().prepareStatement(sqlAttr)) {
                psAttr.setInt(1, vid);
                try (ResultSet rsAttr = psAttr.executeQuery()) {
                    while (rsAttr.next()) {
                        String n = rsAttr.getString("attribute_name");
                        String val = rsAttr.getString("attribute_value");
                        if (n != null && !n.trim().isEmpty()) {
                            n = n.trim();
                            nameToValue.put(n, val != null ? val.trim() : "");
                            allAttrNamesSet.add(n);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            variantAttrMap.put(vid, nameToValue);
        }

        List<String> attributeNamesOrdered = new ArrayList<>(allAttrNamesSet);
        java.util.Collections.sort(attributeNamesOrdered);
        dto.setAttributeNames(attributeNamesOrdered);

        for (ProductVariantSimpleDTO v : variantList) {
            java.util.Map<String, String> nameToValue = variantAttrMap.get(v.getVariantId());
            List<String> values = new ArrayList<>();
            if (nameToValue != null) {
                for (String attrName : attributeNamesOrdered) {
                    values.add(nameToValue.getOrDefault(attrName, ""));
                }
            }
            v.setAttributeValues(values);
        }

        if (variantList.size() == 1 && attributeNamesOrdered.isEmpty()) {
            ProductVariantSimpleDTO single = variantList.get(0);
            dto.setBaseSku(single.getSku());
            dto.setBaseBarcode(single.getBarcode() != null ? single.getBarcode() : "");
            dto.setVariants(new ArrayList<>());
            dto.setAttributeNames(new ArrayList<>());
        } else {
            dto.setVariants(variantList);
        }

        return dto;
    }

    /**
     * Cập nhật sản phẩm và đồng bộ variants/attributes từ DTO (chỉnh sửa).
     */
    public boolean updateProductFromDTO(ProductAddDTO dto) {
        if (dto == null || dto.getProductId() == null) return false;
        int productId = dto.getProductId();
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlUpdateProduct = "UPDATE products SET product_name=?, category_id=?, brand_id=?, supplier_id=?, unit_id=?, picture=?, description=? WHERE product_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateProduct)) {
                ps.setString(1, dto.getProductName());
                ps.setInt(2, dto.getCategoryId());
                ps.setInt(3, dto.getBrandId());
                ps.setInt(4, dto.getSupplierId());
                ps.setInt(5, dto.getUnitId());
                ps.setString(6, dto.getPicture() != null ? dto.getPicture() : "");
                ps.setString(7, dto.getDescription() != null ? dto.getDescription() : "");
                ps.setInt(8, productId);
                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            List<ProductVariantSimpleDTO> variants = dto.getVariants() != null ? dto.getVariants() : new ArrayList<>();
            List<String> attributeNames = dto.getAttributeNames() != null ? dto.getAttributeNames() : new ArrayList<>();

            if (variants.isEmpty()) {
                String baseSku = dto.getBaseSku() != null ? dto.getBaseSku().trim() : "";
                String baseBarcode = dto.getBaseBarcode() != null ? dto.getBaseBarcode().trim() : "";
                List<ProductVariantSimpleDTO> dbVariants = getVariantsByProductId(conn, productId);
                if (dbVariants.size() == 1) {
                    updateVariant(conn, dbVariants.get(0).getVariantId(), baseSku, baseBarcode);
                    deleteVariantAttributes(conn, dbVariants.get(0).getVariantId());
                } else if (dbVariants.isEmpty() && !baseSku.isEmpty()) {
                    ProductVariantSimpleDTO def = new ProductVariantSimpleDTO(baseSku, baseBarcode);
                    insertVariant(conn, productId, def);
                } else if (dbVariants.size() > 1) {
                    int keepId = dbVariants.get(0).getVariantId();
                    updateVariant(conn, keepId, baseSku, baseBarcode);
                    deleteVariantAttributes(conn, keepId);
                    for (int i = 1; i < dbVariants.size(); i++) {
                        deleteVariant(conn, dbVariants.get(i).getVariantId());
                    }
                }
            } else {
                java.util.Set<Integer> processedVariantIds = new java.util.HashSet<>();
                for (ProductVariantSimpleDTO v : variants) {
                    Integer vid = v.getVariantId();
                    String sku = v.getSku() != null ? v.getSku().trim() : "";
                    String barcode = v.getBarcode() != null ? v.getBarcode().trim() : "";
                    if (vid != null && vid > 0) {
                        updateVariant(conn, vid, sku, barcode);
                        deleteVariantAttributes(conn, vid);
                        if (attributeNames.size() > 0 && v.getAttributeValues() != null && !v.getAttributeValues().isEmpty()) {
                            insertVariantAttributes(conn, vid, attributeNames, v.getAttributeValues());
                        }
                        processedVariantIds.add(vid);
                    } else {
                        int newId = insertVariant(conn, productId, v);
                        if (newId > 0) {
                            processedVariantIds.add(newId);
                            if (attributeNames.size() > 0 && v.getAttributeValues() != null && !v.getAttributeValues().isEmpty()) {
                                if (attributeNames.size() == v.getAttributeValues().size()) {
                                    insertVariantAttributes(conn, newId, attributeNames, v.getAttributeValues());
                                } else {
                                    System.err.println("WARNING: attributeNames.size()=" + attributeNames.size() 
                                        + " != attributeValues.size()=" + v.getAttributeValues().size() 
                                        + " for variant SKU=" + v.getSku());
                                }
                            }
                        }
                    }
                }
                List<ProductVariantSimpleDTO> dbVariants = getVariantsByProductId(conn, productId);
                for (ProductVariantSimpleDTO dbv : dbVariants) {
                    if (!processedVariantIds.contains(dbv.getVariantId())) {
                        deleteVariantAttributes(conn, dbv.getVariantId());
                        deleteVariant(conn, dbv.getVariantId());
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<ProductVariantSimpleDTO> getVariantsByProductId(Connection conn, int productId) throws SQLException {
        List<ProductVariantSimpleDTO> list = new ArrayList<>();
        String sql = "SELECT variant_id, sku, barcode FROM product_variants WHERE product_id = ? AND status = 'active'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariantSimpleDTO v = new ProductVariantSimpleDTO();
                    v.setVariantId(rs.getInt("variant_id"));
                    v.setSku(rs.getString("sku"));
                    v.setBarcode(rs.getString("barcode"));
                    list.add(v);
                }
            }
        }
        return list;
    }

    private void updateVariant(Connection conn, int variantId, String sku, String barcode) throws SQLException {
        String sql = "UPDATE product_variants SET sku=?, barcode=? WHERE variant_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            ps.setString(2, barcode == null || barcode.isEmpty() ? null : barcode);
            ps.setInt(3, variantId);
            ps.executeUpdate();
        }
    }

    private void deleteVariantAttributes(Connection conn, int variantId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product_variant_attributes WHERE variant_id=?")) {
            ps.setInt(1, variantId);
            ps.executeUpdate();
        }
    }

    private void deleteVariant(Connection conn, int variantId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product_variants WHERE variant_id=?")) {
            ps.setInt(1, variantId);
            ps.executeUpdate();
        }
    }

}