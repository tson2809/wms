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
import model.Brand;

/**
 * DAO để lấy danh sách Brand
 * 
 * @author laptop368
 */
public class BrandDAO extends DBContext {

    private String normalizeSortBy(String sortBy) {
        if (sortBy == null) {
            return "brand_id";
        }
        String s = sortBy.trim().toLowerCase();
        switch (s) {
            case "brand_id":
            case "brand_name":
            case "status":
            case "created_at":
                return s;
            default:
                return "brand_id";
        }
    }

    private String normalizeSortDir(String sortDir) {
        if (sortDir == null) {
            return "asc";
        }
        String d = sortDir.trim().toLowerCase();
        return "desc".equals(d) ? "desc" : "asc";
    }

    /**
     * Lấy tất cả brands
     */
    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT * FROM brands ORDER BY brand_id";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Brand brand = new Brand();
                brand.setBrandId(rs.getInt("brand_id"));
                brand.setBrandName(rs.getString("brand_name"));
                brand.setDescription(rs.getString("description"));
                brand.setStatus(rs.getString("status"));
                brand.setCreatedAt(rs.getTimestamp("created_at"));
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    /**
     * Lấy brands đang active
     */
    public List<Brand> getActiveBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT * FROM brands WHERE status = 'active' ORDER BY brand_name";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Brand brand = new Brand();
                brand.setBrandId(rs.getInt("brand_id"));
                brand.setBrandName(rs.getString("brand_name"));
                brand.setDescription(rs.getString("description"));
                brand.setStatus(rs.getString("status"));
                brand.setCreatedAt(rs.getTimestamp("created_at"));
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    /**
     * Lấy brand theo ID
     */
    public Brand getBrandById(int id) {
        String sql = "SELECT * FROM brands WHERE brand_id = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Brand brand = new Brand();
                    brand.setBrandId(rs.getInt("brand_id"));
                    brand.setBrandName(rs.getString("brand_name"));
                    brand.setDescription(rs.getString("description"));
                    brand.setStatus(rs.getString("status"));
                    brand.setCreatedAt(rs.getTimestamp("created_at"));
                    return brand;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertBrand(Brand brand) {
        String sql = "INSERT INTO brands (brand_name, description, status) VALUES (?, ?, ?)";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, brand.getBrandName());
            ps.setString(2, brand.getDescription());
            ps.setString(3, brand.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBrand(Brand brand) {
        String sql = "UPDATE brands SET brand_name = ?, description = ?, status = ? WHERE brand_id = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, brand.getBrandName());
            ps.setString(2, brand.getDescription());
            ps.setString(3, brand.getStatus());
            ps.setInt(4, brand.getBrandId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBrandStatus(int id, String status) {
        String sql = "UPDATE brands SET status = ? WHERE brand_id = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isBrandNameExists(String brandName) {
        String sql = "SELECT COUNT(*) FROM brands WHERE brand_name = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, brandName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBrandNameExists(String brandName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM brands WHERE brand_name = ? AND brand_id != ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, brandName);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countBrands(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM brands WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (brand_name LIKE ? OR description LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND status = ? ");
            params.add(status);
        }

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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

    public List<Brand> getBrandsByPage(int page, int pageSize, String keyword, String status, String sortBy, String sortDir) {
        List<Brand> brands = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String sortBySafe = normalizeSortBy(sortBy);
        String sortDirSafe = normalizeSortDir(sortDir);

        StringBuilder sql = new StringBuilder("SELECT * FROM brands WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (brand_name LIKE ? OR description LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND status = ? ");
            params.add(status);
        }

        sql.append(" ORDER BY ").append(sortBySafe).append(" ").append(sortDirSafe);
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add(offset);

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Brand brand = new Brand();
                brand.setBrandId(rs.getInt("brand_id"));
                brand.setBrandName(rs.getString("brand_name"));
                brand.setDescription(rs.getString("description"));
                brand.setStatus(rs.getString("status"));
                brand.setCreatedAt(rs.getTimestamp("created_at"));
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }
}
