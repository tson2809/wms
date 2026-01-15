/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Role;

/**
 *
 * @author thais
 */
public class RoleDAO extends DBContext{
    public List<Role> getAllRole() {
        List<Role> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM roles ORDER BY role_id ASC
                     """;
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                int roleId = rs.getInt(1);
                String roleName = rs.getString(2);
                String roleDescription = rs.getString(3);
                boolean isActive = rs.getBoolean(4);
                Date createdAt = rs.getDate(5);
                Role role = new Role(roleId, roleName, roleDescription, isActive, createdAt);
                list.add(role);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public int updateRole(Role r) {
        int n = 0;
        String sql = """
                     UPDATE roles SET role_name = ?, role_description = ?, is_active = ? WHERE role_id = ?
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, r.getRoleName());
            pre.setString(2, r.getRoleDescription());
            pre.setBoolean(3, r.isIsActive());
            pre.setInt(4, r.getRoleId());  
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }
    
    // Lấy role theo ID
    public Role getRoleById(int roleId) {
        String sql = """
                     SELECT * FROM roles WHERE role_id = ?
                     """;
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            pre.setInt(1, roleId);
            ResultSet rs = pre.executeQuery();
            
            if (rs.next()) {
                return new Role(
                    rs.getInt("role_id"),
                    rs.getString("role_name"),
                    rs.getString("role_description"),
                    rs.getBoolean("is_active"),
                    rs.getDate("created_at")
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    // Lấy danh sách permission IDs của một role
    public List<Integer> getRolePermissionIds(int roleId) {
        List<Integer> permissionIds = new ArrayList<>();
        String sql = """
                     SELECT permission_id FROM role_permissions WHERE role_id = ?
                     """;
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            pre.setInt(1, roleId);
            ResultSet rs = pre.executeQuery();
            
            while (rs.next()) {
                permissionIds.add(rs.getInt("permission_id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return permissionIds;
    }
    
    // Cập nhật permissions cho một role
    public boolean updateRolePermissions(int roleId, List<Integer> permissionIds) {
        try {
            String deleteSql = "DELETE FROM role_permissions WHERE role_id = ?";
            PreparedStatement deleteStmt = this.getConnection().prepareStatement(deleteSql);
            deleteStmt.setInt(1, roleId);
            deleteStmt.executeUpdate();
            
            if (permissionIds != null && !permissionIds.isEmpty()) {
                String insertSql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
                PreparedStatement insertStmt = this.getConnection().prepareStatement(insertSql);
                
                for (Integer permissionId : permissionIds) {
                    insertStmt.setInt(1, roleId);
                    insertStmt.setInt(2, permissionId);
                    insertStmt.addBatch();
                }
                
                insertStmt.executeBatch();
            }
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(RoleDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
     
    public static void main(String[] args) {
//        RoleDAO role = new RoleDAO();
//        List<Role> roles = role.getAllRole();
//        for (Role role1 : roles) {
//            System.out.println(role1);
//        }
//        Role r = new Role(1, "12", "1", false, new Date());
//        role.updateRole(r);
    }
}
