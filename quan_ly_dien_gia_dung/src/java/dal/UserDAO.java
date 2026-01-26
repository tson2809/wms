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
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
            u.phone,
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
                user.setPhone(rs.getString("phone"));
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

    public User getUserByIdH(int userId) {
        String sql = "SELECT user_id, username, email, password_hash, full_name, phone, address, "
                + "avatar, role_id, is_active, created_at FROM users WHERE user_id = ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setFullName(rs.getString("full_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setAvatar(rs.getString("avatar"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, email, full_name, phone, address, avatar, role_id, is_active, created_at FROM users";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
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
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET email=?, phone = ?, address = ?, avatar = ? WHERE user_id = ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAddress());
            ps.setString(4, user.getAvatar());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkCurrentPassword(int userId, String currentPassword) {
        String sql = "SELECT password_hash FROM users WHERE user_id = ?";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return encoder.matches(currentPassword, rs.getString("password_hash"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, encoder.encode(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User findByEmail(String email) {
        String sql = "SELECT user_id, email FROM users WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username FROM users WHERE username = ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    return user;
                }
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
            
            full_name = ?,
            phone = ?,
            address = ?,
            avatar = ?,
            role_id = ?,
            is_active = ?
        WHERE user_id = ?
    """;

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());

            ps.setString(3, user.getFullName());
            ps.setString(4, user.getPhone());
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

        user.setFullName("Admin Updated");
        user.setPhone("0123456789");
        user.setAddress("Ha Noi");
        user.setAvatar("avatar_updated.png");
        user.setRole(role);
        user.setIsActive(true);

        boolean success = dao.update(user);
    }

    public int countUsers(String keyword, String role, Boolean active) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND u.full_name LIKE ? ");
            params.add("%" + keyword + "%");
        }
        if (role != null && !role.isEmpty()) {
            sql.append("AND r.role_name = ? ");
            params.add(role);
        }
        if (active != null) {
            sql.append("AND u.is_active = ? ");
            params.add(active);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<User> getUsersByPage(int page,int pageSize,String keyword,String role,Boolean active,String sort,String dir) {
        List<User> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.username, u.email, u.full_name, u.phone, "
                + "u.is_active, r.role_name "
                + "FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id "
                + "WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND u.full_name LIKE ? ");
            params.add("%" + keyword + "%");
        }
        if (role != null && !role.isEmpty()) {
            sql.append("AND r.role_name = ? ");
            params.add(role);
        }
        if (active != null) {
            sql.append("AND u.is_active = ? ");
            params.add(active);
        }
        String orderBy = "u.user_id";
        if ("full_name".equals(sort)) {
            orderBy = "u.full_name";
        } else if ("role".equals(sort)) {
            orderBy = "r.role_name";
        } else if ("status".equals(sort)) {
            orderBy = "u.is_active";
        }
        String direction = "asc".equalsIgnoreCase(dir) ? "ASC" : "DESC";
        sql.append("ORDER BY ").append(orderBy).append(" ").append(direction).append(" ");
        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setFullName(rs.getString("full_name"));
                    u.setPhone(rs.getString("phone"));
                    u.setActive(rs.getBoolean("is_active"));
                    u.setRoleName(rs.getString("role_name"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String resolveSortColumn(String sortBy) {
        if (sortBy == null) {
            return "u.user_id";
        }
        return switch (sortBy) {
            case "id" ->
                "u.user_id";
            case "username" ->
                "u.username";
            case "email" ->
                "u.email";
            case "fullName" ->
                "u.full_name";
            case "phone" ->
                "u.phone";
            case "role" ->
                "r.role_name";
            case "status" ->
                "u.is_active";
            case "createdAt" ->
                "u.created_at";
            default ->
                "u.user_id";
        };
    }

    private String resolveSortDir(String sortDir) {
        if (sortDir == null) {
            return "ASC";
        }
        return "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
    }

    public int countUsers(String keyword, Integer roleId, Boolean isActive) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM users u
        LEFT JOIN roles r ON u.role_id = r.role_id
        WHERE 1 = 1
        """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (")
                    .append("u.username LIKE ? ")
                    .append("OR u.email LIKE ? ")
                    .append("OR u.full_name LIKE ? ")
                    .append("OR u.phone LIKE ?")
                    .append(") ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (roleId != null) {
            sql.append(" AND u.role_id = ? ");
            params.add(roleId);
        }

        if (isActive != null) {
            sql.append(" AND u.is_active = ? ");
            params.add(isActive);
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

    public List<User> getUsers(String keyword, Integer roleId, Boolean isActive,
            String sortBy, String sortDir, int page, int pageSize) {
        List<User> users = new ArrayList<>();

        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        int offset = (safePage - 1) * safePageSize;

        String sortColumn = resolveSortColumn(sortBy);
        String sortDirection = resolveSortDir(sortDir);

        StringBuilder sql = new StringBuilder("""
        SELECT 
            u.user_id,
            u.username,
            u.email,
            u.password_hash,
            u.full_name,
            u.phone,
            u.address,
            u.avatar,
            u.is_active,
            u.created_at,
            r.role_id,
            r.role_name
        FROM users u
        LEFT JOIN roles r ON u.role_id = r.role_id
        WHERE 1 = 1
        """);

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (")
                    .append("u.username LIKE ? ")
                    .append("OR u.email LIKE ? ")
                    .append("OR u.full_name LIKE ? ")
                    .append("OR u.phone LIKE ?")
                    .append(") ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (roleId != null) {
            sql.append(" AND u.role_id = ? ");
            params.add(roleId);
        }

        if (isActive != null) {
            sql.append(" AND u.is_active = ? ");
            params.add(isActive);
        }

        sql.append(" ORDER BY ").append(sortColumn).append(" ").append(sortDirection);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(safePageSize);
        params.add(offset);

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setAvatar(rs.getString("avatar"));
                user.setIsActive(rs.getBoolean("is_active"));
                user.setCreateAt(rs.getTimestamp("created_at"));

                if (rs.getObject("role_id") != null) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleName(rs.getString("role_name"));
                    user.setRole(role);
                }

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean existsUsername(String username) {
        if (username == null) {
            return false;
        }
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsEmail(String email) {
        if (email == null) {
            return false;
        }
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int insert(User user) {
        String sql = """
        INSERT INTO users (username, email, password_hash, full_name, phone, address, avatar, role_id, is_active)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getAvatar());

            if (user.getRole() != null) {
                ps.setInt(8, user.getRole().getRoleId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setBoolean(9, user.isIsActive());

            int affected = ps.executeUpdate();
            if (affected <= 0) {
                return -1;
            }

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateUserStatus(int userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
