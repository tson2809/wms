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
import model.Notification;

/**
 *
 * @author thais
 */
public class NotificationDAO extends DBContext {

    public List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        String sql = """
                SELECT * FROM notifications ORDER BY notification_id desc
                """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql);
             ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                int notificationId = rs.getInt("notification_id");
                int creatorId = rs.getInt("creator_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Notification n = new Notification(notificationId, notificationId,
                        title, content, createdAt);
                list.add(n);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<Notification> searchNotifications(String keyword) {
        List<Notification> list = new ArrayList<>();
        String sql = """
                SELECT * FROM notifications
                WHERE title LIKE ? OR content LIKE ?
                ORDER BY notification_id DESC
                """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            pre.setString(1, pattern);
            pre.setString(2, pattern);
            try (ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                int notificationId = rs.getInt("notification_id");
                int creatorId = rs.getInt("creator_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Notification n = new Notification(notificationId, creatorId,
                        title, content, createdAt);
                list.add(n);
            }
            }
        } catch (SQLException ex) {
            Logger.getLogger(NotificationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Notification getNotificationById(int notificationId) {
        String sql = """
                SELECT * FROM notifications WHERE notification_id = ?
                """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setInt(1, notificationId);
            try (ResultSet rs = pre.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("notification_id");
                    int creatorId = rs.getInt("creator_id");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    return new Notification(id, creatorId, title, content, createdAt);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(NotificationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int insertNotification(Notification s) {
        int n = 0;
        String sql = """
                INSERT INTO notifications (creator_id, title, content)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pre.setInt(1, s.getCreatorId());
            pre.setString(2, s.getTitle());
            pre.setString(3, s.getContent());
            n = pre.executeUpdate();
            if (n > 0) {
                try (ResultSet keys = pre.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int updateNotification(Notification s) {
        int n = 0;
        String sql = """
                UPDATE notifications SET creator_id = ?, title = ?, content = ?
                WHERE notification_id = ?
                """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setInt(1, s.getCreatorId());
            pre.setString(2, s.getTitle());
            pre.setString(3, s.getContent());
            pre.setInt(4, s.getNotificationId());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }

    public int deleteNotification(int notificationId) {
        int n = 0;
        String sql = """
                     delete from notifications where notification_id = ?
                     """;
        try (Connection conn = this.getConnection();
             PreparedStatement pre = conn.prepareStatement(sql)) {
            pre.setInt(1, notificationId);
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(NotificationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }

    public static void main(String[] args) {
        NotificationDAO nd = new NotificationDAO();
        // List<Notification> list = nd.getAllNotifications();
        // for (Notification notification : list) {
        // System.out.println(notification);
        // }
        // Timestamp time = new Timestamp(System.currentTimeMillis());
        // Notification n = new Notification(0, 1, "1", "1", "1", time);
        // nd.insertNotification(n);
        // nd.updateSupplier(n);
    }
}
