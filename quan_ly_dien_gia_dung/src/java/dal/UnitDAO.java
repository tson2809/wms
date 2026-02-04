package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Unit;

/**
 * DAO cho bảng units (đơn vị tính)
 */
public class UnitDAO extends DBContext {

    public List<Unit> getAllUnits() {
        List<Unit> list = new ArrayList<>();
        String sql = "SELECT * FROM units ORDER BY unit_name";

        try (PreparedStatement ps = this.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Unit u = new Unit();
                u.setUnitId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
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
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
