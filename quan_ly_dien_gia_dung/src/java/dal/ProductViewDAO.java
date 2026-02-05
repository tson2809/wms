/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ProductView;

/**
 *
 * @author laptop368
 */
public class ProductViewDAO extends DBContext {

    
    public int countProduct() {
        String sql = "SELECT COUNT(*) FROM products";
        try (PreparedStatement st = this.getConnection().prepareStatement(sql);
                ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public List<ProductView> getProductWithPaging(int offset, int size) {
        List<ProductView> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.category_id, p.brand_id, "
                + "p.picture, p.status, "
                + "c.category_name, "
                + "b.brand_name, "
                + "COUNT(DISTINCT pv.variant_id) AS variant_count "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                + "LEFT JOIN product_variants pv ON p.product_id = pv.product_id AND pv.status = 'active' "
                + "GROUP BY p.product_id, p.product_name, p.category_id, p.brand_id, "
                + "p.picture, p.status, c.category_name, b.brand_name "
                + "ORDER BY p.product_id DESC "
                + "LIMIT ? OFFSET ?";

        try (PreparedStatement st = this.getConnection().prepareStatement(sql)) {
            st.setInt(1, size);
            st.setInt(2, offset);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ProductView pv = new ProductView();
                pv.setProductId(rs.getInt("product_id"));
                pv.setProductName(rs.getString("product_name"));

                // Integer cho nullable fields
                int catId = rs.getInt("category_id");
                pv.setCategoryId(rs.wasNull() ? null : catId);

                int brId = rs.getInt("brand_id");
                pv.setBrandId(rs.wasNull() ? null : brId);

                pv.setPicture(rs.getString("picture"));
                pv.setStatus(rs.getString("status"));
                pv.setCategoryName(rs.getString("category_name"));
                pv.setBrandName(rs.getString("brand_name"));
                pv.setVariantCount(rs.getInt("variant_count"));

                list.add(pv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    
    public int countProductWithFilter(Integer categoryId, Integer brandId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT p.product_id) FROM products p WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (brandId != null) {
            sql.append("AND p.brand_id = ? ");
            params.add(brandId);
        }

        if (status != null && !status.isEmpty()) {
            sql.append("AND p.status = ? ");
            params.add(status);
        }

        try (PreparedStatement st = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    
    public List<ProductView> getProductWithFilter(Integer categoryId, Integer brandId, String status, int offset,
            int size) {
        List<ProductView> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.product_id, p.product_name, p.category_id, p.brand_id, "
                        + "p.picture, p.status, "
                        + "c.category_name, "
                        + "b.brand_name, "
                        + "COUNT(DISTINCT pv.variant_id) AS variant_count "
                        + "FROM products p "
                        + "LEFT JOIN categories c ON p.category_id = c.category_id "
                        + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                        + "LEFT JOIN product_variants pv ON p.product_id = pv.product_id AND pv.status = 'active' "
                        + "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (brandId != null) {
            sql.append("AND p.brand_id = ? ");
            params.add(brandId);
        }

        if (status != null && !status.isEmpty()) {
            sql.append("AND p.status = ? ");
            params.add(status);
        }

        sql.append("GROUP BY p.product_id, p.product_name, p.category_id, p.brand_id, ");
        sql.append("p.picture, p.status, c.category_name, b.brand_name ");
        sql.append("ORDER BY p.product_id DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (PreparedStatement st = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ProductView pv = new ProductView();
                pv.setProductId(rs.getInt("product_id"));
                pv.setProductName(rs.getString("product_name"));

                int catId = rs.getInt("category_id");
                pv.setCategoryId(rs.wasNull() ? null : catId);

                int brId = rs.getInt("brand_id");
                pv.setBrandId(rs.wasNull() ? null : brId);

                pv.setPicture(rs.getString("picture"));
                pv.setStatus(rs.getString("status"));
                pv.setCategoryName(rs.getString("category_name"));
                pv.setBrandName(rs.getString("brand_name"));
                pv.setVariantCount(rs.getInt("variant_count"));

                list.add(pv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    
    public List<ProductView> searchProducts(String keyword, int offset, int size) {
        List<ProductView> list = new ArrayList<>();

        String sql = "SELECT p.product_id, p.product_name, p.category_id, p.brand_id, "
                + "p.picture, p.status, "
                + "c.category_name, "
                + "b.brand_name, "
                + "COUNT(DISTINCT pv.variant_id) AS variant_count "
                + "FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                + "LEFT JOIN product_variants pv ON p.product_id = pv.product_id AND pv.status = 'active' "
                + "WHERE p.product_name LIKE ? OR c.category_name LIKE ? OR b.brand_name LIKE ? "
                + "GROUP BY p.product_id, p.product_name, p.category_id, p.brand_id, "
                + "p.picture, p.status, c.category_name, b.brand_name "
                + "ORDER BY p.product_id DESC LIMIT ? OFFSET ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, size);
            ps.setInt(5, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductView pv = new ProductView();
                pv.setProductId(rs.getInt("product_id"));
                pv.setProductName(rs.getString("product_name"));

                int catId = rs.getInt("category_id");
                pv.setCategoryId(rs.wasNull() ? null : catId);

                int brId = rs.getInt("brand_id");
                pv.setBrandId(rs.wasNull() ? null : brId);

                pv.setPicture(rs.getString("picture"));
                pv.setStatus(rs.getString("status"));
                pv.setCategoryName(rs.getString("category_name"));
                pv.setBrandName(rs.getString("brand_name"));
                pv.setVariantCount(rs.getInt("variant_count"));

                list.add(pv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public int countSearchProducts(String keyword) {
        String sql = "SELECT COUNT(DISTINCT p.product_id) FROM products p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                + "WHERE p.product_name LIKE ? OR c.category_name LIKE ? OR b.brand_name LIKE ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public List<ProductView> getProductWithSearchAndFilter(String keyword, Integer categoryId, Integer brandId,
            String status, int offset, int size) {
        List<ProductView> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.product_id, p.product_name, p.category_id, p.brand_id, "
                        + "p.picture, p.status, "
                        + "c.category_name, "
                        + "b.brand_name, "
                        + "COUNT(DISTINCT pv.variant_id) AS variant_count "
                        + "FROM products p "
                        + "LEFT JOIN categories c ON p.category_id = c.category_id "
                        + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                        + "LEFT JOIN product_variants pv ON p.product_id = pv.product_id AND pv.status = 'active' "
                        + "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Search keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.product_name LIKE ? OR c.category_name LIKE ? OR b.brand_name LIKE ?) ");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        // Filter by category
        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        // Filter by brand
        if (brandId != null) {
            sql.append("AND p.brand_id = ? ");
            params.add(brandId);
        }

        // Filter by status
        if (status != null && !status.isEmpty()) {
            sql.append("AND p.status = ? ");
            params.add(status);
        }

        sql.append("GROUP BY p.product_id, p.product_name, p.category_id, p.brand_id, ");
        sql.append("p.picture, p.status, c.category_name, b.brand_name ");
        sql.append("ORDER BY p.product_id DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (PreparedStatement st = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ProductView pv = new ProductView();
                pv.setProductId(rs.getInt("product_id"));
                pv.setProductName(rs.getString("product_name"));

                int catId = rs.getInt("category_id");
                pv.setCategoryId(rs.wasNull() ? null : catId);

                int brId = rs.getInt("brand_id");
                pv.setBrandId(rs.wasNull() ? null : brId);

                pv.setPicture(rs.getString("picture"));
                pv.setStatus(rs.getString("status"));
                pv.setCategoryName(rs.getString("category_name"));
                pv.setBrandName(rs.getString("brand_name"));
                pv.setVariantCount(rs.getInt("variant_count"));

                list.add(pv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    
    public int countProductWithSearchAndFilter(String keyword, Integer categoryId, Integer brandId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT p.product_id) FROM products p "
                        + "LEFT JOIN categories c ON p.category_id = c.category_id "
                        + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                        + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        // Search keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.product_name LIKE ? OR c.category_name LIKE ? OR b.brand_name LIKE ?) ");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        // Filter by category
        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        // Filter by brand
        if (brandId != null) {
            sql.append("AND p.brand_id = ? ");
            params.add(brandId);
        }

        // Filter by status
        if (status != null && !status.isEmpty()) {
            sql.append("AND p.status = ? ");
            params.add(status);
        }

        try (PreparedStatement st = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    
    public boolean updateProductStatus(int productId, String status) {
        String sql = "UPDATE products SET status = ? WHERE product_id = ?";
        
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, productId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
