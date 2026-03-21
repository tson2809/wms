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

    public int countAuditLogs(String keyword) {
        String sql = """
                     SELECT COUNT(*)
                     FROM audit_logs al
                     LEFT JOIN users u ON al.user_id = u.user_id
                     WHERE (? IS NULL OR ? = ''
                            OR al.action_type LIKE CONCAT('%', ?, '%')
                            OR al.table_name LIKE CONCAT('%', ?, '%')
                            OR u.username LIKE CONCAT('%', ?, '%'))
                     """;

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            ps.setString(4, keyword);
            ps.setString(5, keyword);

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

    public List<AuditLog> getAuditLogs(int page, int pageSize, String keyword) {
        List<AuditLog> logs = new ArrayList<>();

        String sql = """
                     SELECT al.log_id, al.user_id, u.username, al.action_type,
                    al.table_name, al.record_id
                     FROM audit_logs al
                     LEFT JOIN users u ON al.user_id = u.user_id
                     WHERE (? IS NULL OR ? = ''
                            OR al.action_type LIKE CONCAT('%', ?, '%')
                            OR al.table_name LIKE CONCAT('%', ?, '%')
                            OR u.username LIKE CONCAT('%', ?, '%'))
                ORDER BY al.log_id DESC
                     LIMIT ? OFFSET ?
                     """;

        int offset = Math.max(0, (page - 1) * pageSize);

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            ps.setString(4, keyword);
            ps.setString(5, keyword);
            ps.setInt(6, pageSize);
            ps.setInt(7, offset);

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
}
