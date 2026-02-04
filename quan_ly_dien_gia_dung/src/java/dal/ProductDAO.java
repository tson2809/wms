package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import modelDTO.ProductAddDTO;
import modelDTO.ProductVariantSimpleDTO;
import model.ProductVariant;

/**
 *
 * @author laptop368
 */
public class ProductDAO extends DBContext {

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
