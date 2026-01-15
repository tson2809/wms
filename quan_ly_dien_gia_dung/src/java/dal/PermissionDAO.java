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
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                int permissionId = rs.getInt("permission_id");
                String permissionName = rs.getString("permission_name");
                String permissionDescription = rs.getString("permission_description");
                String module = rs.getString("module");
                Date createdAt = rs.getDate("created_at");
                
                Permission permission = new Permission(permissionId, permissionName, 
                                                      permissionDescription, module, createdAt);
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
                     UPDATE permissions SET permission_name = ?, permission_description = ?, 
                         module = ? 
                     WHERE permission_id = ?
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setString(1, p.getPermissionName());
            pre.setString(2, p.getPermissionDescription());
            pre.setString(3, p.getModule());
            pre.setInt(4, p.getPermissionId());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PermissionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return n;
    }
}
