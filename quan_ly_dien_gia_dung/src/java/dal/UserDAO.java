/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.User;
import java.sql.*;
import model.Role;

/**
 *
 * @author laptop368
 */
public class UserDAO extends DBContext {

    public User getUserById(int id) {
        String sql = """
        SELECT 
            u.user_id,
            u.username,
            u.email,
            u.password_hash,
            u.full_name,
            u.address,
            u.avatar,
            u.is_active,
            u.created_at,
            r.role_id,
            r.role_name
        FROM users u
        INNER JOIN roles r ON u.role_id = r.role_id
        WHERE u.user_id = ?
    """;

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setAddress(rs.getString("address"));
                user.setAvatar(rs.getString("avatar"));
                user.setIsActive(rs.getBoolean("is_active"));
                user.setCreateAt(rs.getTimestamp("created_at"));

                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                user.setRole(role);

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(User user) {

        String sql = """
        UPDATE users
        SET username = ?,
            email = ?,
            password_hash = ?,
            full_name = ?,
            address = ?,
            avatar = ?,
            role_id = ?,
            is_active = ?
        WHERE user_id = ?
    """;

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getAvatar());
            ps.setInt(7, user.getRole().getRoleId());
            ps.setBoolean(8, user.isIsActive());
            ps.setInt(9, user.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        Role role = new Role();
        role.setRoleId(2);
        User user = new User();
        user.setUserId(3);
        user.setUserName("admin_updated");
        user.setEmail("admin_updated@gmail.com");
        user.setPassword("123456");
        user.setFullName("Admin Updated");
        user.setAddress("Ha Noi");
        user.setAvatar("avatar_updated.png");
        user.setRole(role);
        user.setIsActive(true);

        boolean success = dao.update(user);
    }
}
