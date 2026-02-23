/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

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
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                int notificationId = rs.getInt("notification_id");
                int creatorId = rs.getInt("creator_id");
                String notificationType = rs.getString("notification_type");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Notification n = new Notification(notificationId, notificationId,
                        notificationType, title, content, createdAt);
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
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            pre.setString(1, pattern);
            pre.setString(2, pattern);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                int notificationId = rs.getInt("notification_id");
                int creatorId = rs.getInt("creator_id");
                String notificationType = rs.getString("notification_type");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Notification n = new Notification(notificationId, creatorId,
                        notificationType, title, content, createdAt);
                list.add(n);
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
        try {
            PreparedStatement pre = this.getConnection().prepareStatement(sql);
            pre.setInt(1, notificationId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("notification_id");
                int creatorId = rs.getInt("creator_id");
                String notificationType = rs.getString("notification_type");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                return new Notification(id, creatorId, notificationType, title, content, createdAt);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NotificationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int insertNotification(Notification s) {
        int n = 0;
        String sql = """
                INSERT INTO notifications (creator_id, notification_type, title, content)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pre.setInt(1, s.getCreatorId());
            pre.setString(2, s.getNotificationType());
            pre.setString(3, s.getTitle());
            pre.setString(4, s.getContent());
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

    public int updateNotification(Notification s) {
        int n = 0;
        String sql = """
                UPDATE notifications SET creator_id = ?, notification_type = ?, title = ?, content = ?
                WHERE notification_id = ?
                """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
            pre.setInt(1, s.getCreatorId());
            pre.setString(2, s.getNotificationType());
            pre.setString(3, s.getTitle());
            pre.setString(4, s.getContent());
            pre.setInt(5, s.getNotificationId());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SupplierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }

    public List<String> getNotificationTypes() {
        List<String> types = new ArrayList<>();
        String sql = """
                 SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = 'notifications' 
                 AND COLUMN_NAME = 'notification_type'
                 """;
        try {
            PreparedStatement st = this.getConnection().prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                String columnType = rs.getString("COLUMN_TYPE");
                columnType = columnType.substring(5, columnType.length() - 1);
                String[] split = columnType.split(",");
                for (String s : split) {
                    types.add(s.replace("'", ""));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return types;
    }

    public int deleteNotification(int notificationId) {
        int n = 0;
        String sql = """
                     delete from notifications where notification_id = ?
                     """;
        try (PreparedStatement pre = this.getConnection().prepareStatement(sql)) {
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
