/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Permission;

/**
 *
 * @author thais
 */
public class PermissionDAO extends DBContext {
    public List<Permission> getAllPermission() {
        List<Permission> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM permissions order by permission_id desc
                     """;       
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql);
             ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                int permissionId = rs.getInt("permission_id");
                String permissionName = rs.getString("permission_name");
                String permissionDescription = rs.getString("permission_description");
                Date createdAt = rs.getDate("created_at");
                
                Permission permission = new Permission(permissionId, permissionName, 
                                                      permissionDescription, createdAt);
                list.add(permission);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PermissionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public int updatePermission(Permission p) {
        int n = 0;
        String sql = """
                     UPDATE permissions SET permission_name = ?, permission_description = ? 
                     WHERE permission_id = ?
                     """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setString(1, p.getPermissionName());
            pre.setString(2, p.getPermissionDescription());
            pre.setInt(3, p.getPermissionId());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PermissionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return n;
    }

    public void ensurePermissionExists(String permissionName, String permissionDescription) {
        String checkSql = "SELECT permission_id FROM permissions WHERE LOWER(permission_name) = LOWER(?)";
        String insertSql = "INSERT INTO permissions (permission_name, permission_description) VALUES (?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, permissionName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, permissionName);
                insertStmt.setString(2, permissionDescription);
                insertStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PermissionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
