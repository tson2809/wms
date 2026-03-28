package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Unit;

/**
 * 
 * @author thais
 */
public class UnitDAO extends DBContext {

    public List<Unit> getAllUnits() {
        List<Unit> list = new ArrayList<>();
        String sql = "SELECT * FROM units WHERE status = 'active' ORDER BY unit_name";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Unit u = new Unit();
                u.setUnitId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                u.setStatus(rs.getString("status"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Unit> getAllUnitsForManagement() {
        List<Unit> list = new ArrayList<>();
        String sql = "SELECT * FROM units ORDER BY unit_name";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Unit u = new Unit();
                u.setUnitId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                u.setStatus(rs.getString("status"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Unit getUnitById(int id) {
        String sql = "SELECT * FROM units WHERE unit_id = ?";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Unit u = new Unit();
                    u.setUnitId(rs.getInt("unit_id"));
                    u.setUnitName(rs.getString("unit_name"));
                    u.setStatus(rs.getString("status"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean unitNameExists(String name, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM units WHERE LOWER(unit_name) = LOWER(?)"
                   + (excludeId != null ? " AND unit_id <> ?" : "");
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUnit(String name) {
        String sql = "INSERT INTO units(unit_name) VALUES(?)";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUnit(int id, String name) {
        String sql = "UPDATE units SET unit_name = ? WHERE unit_id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUnitUsed(int unitId) {
        String sql = "SELECT COUNT(*) FROM products WHERE unit_id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, unitId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleUnitStatus(int id) {
        String sql = """
                UPDATE units
                SET status = CASE WHEN status = 'active' THEN 'inactive' ELSE 'active' END
                WHERE unit_id = ?
                """;
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
