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
}
