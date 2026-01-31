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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Supplier;

/**
 *
 * @author thais
 */
public class SupplierDAO extends DBContext {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM suppliers ORDER BY supplier_id desc
                     """;
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                int supplierId = rs.getInt("supplier_id");
                String supplierName = rs.getString("supplier_name");
                String contactPerson = rs.getString("contact_person");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String status = rs.getString("status");
                String description = rs.getString("description");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Supplier s = new Supplier(supplierId, supplierName, contactPerson, email, phone, status, description, createdAt);
                list.add(s);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Supplier getSupplierById(int supplierId) {
        String sql = """
                     SELECT * FROM suppliers WHERE supplier_id = ?
                     """;
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            pre.setInt(1, supplierId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("supplier_id");
                String supplierName = rs.getString("supplier_name");
                String contactPerson = rs.getString("contact_person");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String status = rs.getString("status");
                String description = rs.getString("description");
                Timestamp createdAt = rs.getTimestamp("created_at");
                return new Supplier(id, supplierName, contactPerson, email, phone, status, description, createdAt);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int insertSupplier(Supplier s) {
        int n = 0;
        String sql = """
                     INSERT INTO suppliers (supplier_name, contact_person, email, phone, status, description) 
                     VALUES (?, ?, ?, ?, ?, ?)
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pre.setString(1, s.getSupplierName());
            pre.setString(2, s.getContactPerson());
            pre.setString(3, s.getEmail());
            pre.setString(4, s.getPhone());
            pre.setString(5, s.getStatus() != null ? s.getStatus() : "active");
            pre.setString(6, s.getDescription());
            n = pre.executeUpdate();
            if (n > 0) {
                ResultSet keys = pre.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int updateSupplier(Supplier s) {
        int n = 0;
        String sql = """
                     UPDATE suppliers SET supplier_name = ?, contact_person = ?, email = ?, phone = ?, status = ?, 
                     description = ? WHERE supplier_id = ?
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, s.getSupplierName());
            pre.setString(2, s.getContactPerson());
            pre.setString(3, s.getEmail());
            pre.setString(4, s.getPhone());
            pre.setString(5, s.getStatus() != null ? s.getStatus() : "active");
            pre.setString(6, s.getDescription());
            pre.setInt(7, s.getSupplierId());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }

    public List<Supplier> getSuppliersByStatus(String st) {
        List<Supplier> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM suppliers WHERE status = ? ORDER BY supplier_id ASC
                     """;
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            pre.setString(1, st);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                int supplierId = rs.getInt("supplier_id");
                String supplierName = rs.getString("supplier_name");
                String contactPerson = rs.getString("contact_person");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String description = rs.getString("description");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Supplier s = new Supplier(supplierId, supplierName, contactPerson, email, phone, st, description, createdAt);
                list.add(s);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public boolean updateSupplierStatus(int supplierId, String status) {
        String sql = "UPDATE suppliers SET status = ? WHERE supplier_id = ?";
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, status);
            pre.setInt(2, supplierId);
            return pre.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int countSuppliers(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM suppliers WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (supplier_name LIKE ? OR contact_person LIKE ? OR email LIKE ? OR phone LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status);
        }
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<Supplier> getSuppliersByPage(int page, int pageSize, String keyword, String status, String sort, String dir) {
        List<Supplier> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        StringBuilder sql = new StringBuilder("SELECT * FROM suppliers WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (supplier_name LIKE ? OR contact_person LIKE ? OR email LIKE ? OR phone LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status);
        }
        String orderBy = "supplier_id";
        if ("supplier_name".equals(sort)) orderBy = "supplier_name";
        else if ("status".equals(sort)) orderBy = "status";
        String direction = "asc".equalsIgnoreCase(dir) ? "ASC" : "DESC";
        sql.append("ORDER BY ").append(orderBy).append(" ").append(direction).append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int supplierId = rs.getInt("supplier_id");
                    String supplierName = rs.getString("supplier_name");
                    String contactPerson = rs.getString("contact_person");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String st = rs.getString("status");
                    String description = rs.getString("description");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    Supplier s = new Supplier(supplierId, supplierName, contactPerson, email, phone, st, description, createdAt);
                    list.add(s);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static void main(String[] args) {
        SupplierDAO sp = new SupplierDAO();
        List<Supplier> sup = sp.getAllSuppliers();
        for (Supplier supplier : sup) {
            System.out.println(supplier);
        }
    }
}
