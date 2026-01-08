/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author hung
 */
public class UserDAO extends DBContext {

    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, email, full_name, phone, address, avatar, "
                + "role_id, is_active, created_at "
                + "FROM users WHERE user_id = ?";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = new DBContext().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            rs = statement.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setAvatar(rs.getString("avatar"));
                user.setRoleId(rs.getInt("role_id"));
                user.setActive(rs.getBoolean("is_active"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(statement);
            closeConnection(connection);
        }

        return null;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, address = ?, avatar = ? "
                + "WHERE user_id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = new DBContext().getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, user.getFullName());
            statement.setString(2, user.getPhone());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getAvatar());
            statement.setInt(5, user.getUserId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }

        return false;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, email, password_hash, role_id, is_active "
                + "FROM users WHERE username = ?";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = new DBContext().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            rs = statement.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRoleId(rs.getInt("role_id"));
                user.setActive(rs.getBoolean("is_active"));

                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(statement);
            closeConnection(connection);
        }

        return null;
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeStatement(PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
