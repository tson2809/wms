package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AuditLog;

public class AuditLogDAO extends DBContext {

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public void insertAuditLog(Integer userId, String actionType, String tableName, Integer recordId) {
        String sql = """
                     INSERT INTO audit_logs (user_id, action_type, table_name, record_id)
                     VALUES (?, ?, ?, ?)
                     """;
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, userId);
            }
            ps.setString(2, actionType);
            ps.setString(3, tableName);
            if (recordId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, recordId);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AuditLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int countAuditLogs(String keyword, String actionType, String tableName) {
        StringBuilder sql = new StringBuilder("""
                     SELECT COUNT(*)
                     FROM audit_logs al
                     LEFT JOIN users u ON al.user_id = u.user_id
                     WHERE 1=1
                     """);

        if (hasText(keyword)) {
            sql.append(" AND (al.action_type LIKE ? OR al.table_name LIKE ? OR u.username LIKE ?)");
        }
        if (hasText(actionType)) {
            sql.append(" AND al.action_type = ?");
        }
        if (hasText(tableName)) {
            sql.append(" AND al.table_name = ?");
        }

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasText(keyword)) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            if (hasText(actionType)) {
                ps.setString(idx++, actionType.trim());
            }
            if (hasText(tableName)) {
                ps.setString(idx++, tableName.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuditLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<AuditLog> getAuditLogs(int page, int pageSize, String keyword, String actionType, String tableName) {
        List<AuditLog> logs = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                     SELECT al.log_id, al.user_id, u.username, al.action_type,
                    al.table_name, al.record_id
                     FROM audit_logs al
                     LEFT JOIN users u ON al.user_id = u.user_id
                     WHERE 1=1
                     """);

        if (hasText(keyword)) {
            sql.append(" AND (al.action_type LIKE ? OR al.table_name LIKE ? OR u.username LIKE ?)");
        }
        if (hasText(actionType)) {
            sql.append(" AND al.action_type = ?");
        }
        if (hasText(tableName)) {
            sql.append(" AND al.table_name = ?");
        }

        sql.append(" ORDER BY al.log_id DESC LIMIT ? OFFSET ?");

        int offset = Math.max(0, (page - 1) * pageSize);

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasText(keyword)) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            if (hasText(actionType)) {
                ps.setString(idx++, actionType.trim());
            }
            if (hasText(tableName)) {
                ps.setString(idx++, tableName.trim());
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setLogId(rs.getLong("log_id"));
                    int userId = rs.getInt("user_id");
                    log.setUserId(rs.wasNull() ? null : userId);
                    log.setUsername(rs.getString("username"));
                    log.setActionType(rs.getString("action_type"));
                    log.setTableName(rs.getString("table_name"));
                    int recordId = rs.getInt("record_id");
                    log.setRecordId(rs.wasNull() ? null : recordId);
                    logs.add(log);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuditLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return logs;
    }

    public List<String> getDistinctActionTypes() {
        List<String> items = new ArrayList<>();
        String sql = "SELECT DISTINCT action_type FROM audit_logs WHERE action_type IS NOT NULL AND action_type <> '' ORDER BY action_type";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuditLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }

    public List<String> getDistinctTableNames() {
        List<String> items = new ArrayList<>();
        String sql = "SELECT DISTINCT table_name FROM audit_logs WHERE table_name IS NOT NULL AND table_name <> '' ORDER BY table_name";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuditLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items;
    }
}
