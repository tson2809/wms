/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.*;
import java.util.*;
import model.InventoryTransaction;

/**
 *
 * @author GIAKHANHPC
 */
public class InventoryTransactionDAO extends DBContext {

    public List<InventoryTransaction> getTransactions(
            Integer trxId,
            Integer refId,
            Integer variantId,
            String keyword,
            String type,
            String qtyType,
            String refType,
            String createdBy,
            String dateFrom,
            String dateTo,
            int page,
            int pageSize,
            String sort,
            String dir
    ) {

        List<InventoryTransaction> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String orderBy = "it.transaction_id";
        if ("date".equals(sort)) {
            orderBy = "it.transaction_date";
        }
        if ("qty".equals(sort)) {
            orderBy = "it.quantity_change";
        }

        String direction = "DESC";
        if ("asc".equalsIgnoreCase(dir)) {
            direction = "ASC";
        }

        String sql
                = "SELECT it.transaction_id, pv.sku, it.transaction_type, "
                + "it.quantity_change, it.reference_type, it.reference_id, "
            + "CASE "
            + "  WHEN it.reference_type = 'goods_receipt' THEN COALESCE(gr.receipt_code, CONCAT('GR-', it.reference_id)) "
            + "  WHEN it.reference_type = 'goods_issue' THEN COALESCE(gi.issue_code, CONCAT('GI-', it.reference_id)) "
            + "  WHEN it.reference_type = 'inventory_sheet' THEN COALESCE(isf.sheet_code, CONCAT('IS-', it.reference_id)) "
            + "  ELSE CONCAT(COALESCE(it.reference_type, 'N/A'), '-', COALESCE(CAST(it.reference_id AS CHAR), 'N/A')) "
            + "END AS reference_display, "
                + "u.full_name, it.transaction_date, it.notes "
                + "FROM inventory_transactions it "
                + "JOIN product_variants pv ON it.variant_id = pv.variant_id "
            + "LEFT JOIN goods_receipts gr ON it.reference_type = 'goods_receipt' AND it.reference_id = gr.receipt_id "
            + "LEFT JOIN goods_issues gi ON it.reference_type = 'goods_issue' AND it.reference_id = gi.issue_id "
            + "LEFT JOIN inventory_sheets isf ON it.reference_type = 'inventory_sheet' AND it.reference_id = isf.sheet_id "
                + "LEFT JOIN users u ON it.created_by = u.user_id "
                + "WHERE 1=1 "
                + "AND (? IS NULL OR it.transaction_id = ?) "
                + "AND (? IS NULL OR it.reference_id = ?) "
                + "AND (? IS NULL OR it.variant_id = ?) "
                + "AND (? IS NULL OR pv.sku LIKE ?) "
                + "AND (? IS NULL OR it.transaction_type = ?) "
                + "AND (? IS NULL OR it.reference_type = ?) "
                + "AND (? IS NULL OR u.full_name LIKE ?) "
                + "AND (? IS NULL OR DATE(it.transaction_date) >= ?) "
                + "AND (? IS NULL OR DATE(it.transaction_date) <= ?) ";

        if ("increase".equals(qtyType)) {
            sql += "AND it.quantity_change > 0 ";
        } else if ("decrease".equals(qtyType)) {
            sql += "AND it.quantity_change < 0 ";
        }

        sql += "ORDER BY " + orderBy + " " + direction + " LIMIT ? OFFSET ?";

        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setObject(1, trxId);
            ps.setObject(2, trxId);

            ps.setObject(3, refId);
            ps.setObject(4, refId);

            ps.setObject(5, variantId);
            ps.setObject(6, variantId);

            ps.setObject(7, keyword);
            ps.setString(8, keyword == null ? null : "%" + keyword + "%");

            ps.setObject(9, type);
            ps.setObject(10, type);

            ps.setObject(11, refType);
            ps.setObject(12, refType);

            ps.setObject(13, createdBy);
            ps.setString(14, createdBy == null ? null : "%" + createdBy + "%");

            if (dateFrom == null) {
                ps.setNull(15, Types.DATE);
                ps.setNull(16, Types.DATE);
            } else {
                ps.setString(15, dateFrom);
                ps.setString(16, dateFrom);
            }

            if (dateTo == null) {
                ps.setNull(17, Types.DATE);
                ps.setNull(18, Types.DATE);
            } else {
                ps.setString(17, dateTo);
                ps.setString(18, dateTo);
            }

            ps.setInt(19, pageSize);
            ps.setInt(20, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InventoryTransaction t = new InventoryTransaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setSku(rs.getString("sku"));
                t.setTransactionType(rs.getString("transaction_type"));
                t.setQuantityChange(rs.getInt("quantity_change"));
                t.setReferenceType(rs.getString("reference_type"));
                t.setReferenceId((Integer) rs.getObject("reference_id"));
                t.setReferenceDisplay(rs.getString("reference_display"));
                t.setCreatedBy(rs.getString("full_name"));
                t.setTransactionDate(rs.getTimestamp("transaction_date"));
                t.setNotes(rs.getString("notes"));
                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countTransactions(
            Integer trxId,
            Integer refId,
            Integer variantId,
            String keyword,
            String type,
            String qtyType,
            String refType,
            String createdBy,
            String dateFrom,
            String dateTo
    ) {

        String sql
                = "SELECT COUNT(*) "
                + "FROM inventory_transactions it "
                + "JOIN product_variants pv ON it.variant_id = pv.variant_id "
                + "LEFT JOIN users u ON it.created_by = u.user_id "
                + "WHERE 1=1 "
                + "AND (? IS NULL OR it.transaction_id = ?) "
                + "AND (? IS NULL OR it.reference_id = ?) "
                + "AND (? IS NULL OR it.variant_id = ?) "
                + "AND (? IS NULL OR pv.sku LIKE ?) "
                + "AND (? IS NULL OR it.transaction_type = ?) "
                + "AND (? IS NULL OR it.reference_type = ?) "
                + "AND (? IS NULL OR u.full_name LIKE ?) "
                + "AND (? IS NULL OR DATE(it.transaction_date) >= ?) "
                + "AND (? IS NULL OR DATE(it.transaction_date) <= ?) ";

        if ("increase".equals(qtyType)) {
            sql += "AND it.quantity_change > 0 ";
        } else if ("decrease".equals(qtyType)) {
            sql += "AND it.quantity_change < 0 ";
        }

        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setObject(1, trxId);
            ps.setObject(2, trxId);

            ps.setObject(3, refId);
            ps.setObject(4, refId);

            ps.setObject(5, variantId);
            ps.setObject(6, variantId);

            ps.setObject(7, keyword);
            ps.setString(8, keyword == null ? null : "%" + keyword + "%");

            ps.setObject(9, type);
            ps.setObject(10, type);

            ps.setObject(11, refType);
            ps.setObject(12, refType);

            ps.setObject(13, createdBy);
            ps.setString(14, createdBy == null ? null : "%" + createdBy + "%");

            if (dateFrom == null) {
                ps.setNull(15, Types.DATE);
                ps.setNull(16, Types.DATE);
            } else {
                ps.setString(15, dateFrom);
                ps.setString(16, dateFrom);
            }

            if (dateTo == null) {
                ps.setNull(17, Types.DATE);
                ps.setNull(18, Types.DATE);
            } else {
                ps.setString(17, dateTo);
                ps.setString(18, dateTo);
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

    public InventoryTransaction getTransactionById(int id) {
        String sql
                = "SELECT it.transaction_id, pv.sku, it.transaction_type, "
                + "it.quantity_change, it.quantity_before, it.quantity_after, "
                + "it.reference_type, it.reference_id, "
            + "CASE "
            + "  WHEN it.reference_type = 'goods_receipt' THEN COALESCE(gr.receipt_code, CONCAT('GR-', it.reference_id)) "
            + "  WHEN it.reference_type = 'goods_issue' THEN COALESCE(gi.issue_code, CONCAT('GI-', it.reference_id)) "
            + "  WHEN it.reference_type = 'inventory_sheet' THEN COALESCE(isf.sheet_code, CONCAT('IS-', it.reference_id)) "
            + "  ELSE CONCAT(COALESCE(it.reference_type, 'N/A'), '-', COALESCE(CAST(it.reference_id AS CHAR), 'N/A')) "
            + "END AS reference_display, "
                + "u.full_name, it.transaction_date, it.notes "
                + "FROM inventory_transactions it "
                + "JOIN product_variants pv ON it.variant_id = pv.variant_id "
            + "LEFT JOIN goods_receipts gr ON it.reference_type = 'goods_receipt' AND it.reference_id = gr.receipt_id "
            + "LEFT JOIN goods_issues gi ON it.reference_type = 'goods_issue' AND it.reference_id = gi.issue_id "
            + "LEFT JOIN inventory_sheets isf ON it.reference_type = 'inventory_sheet' AND it.reference_id = isf.sheet_id "
                + "LEFT JOIN users u ON it.created_by = u.user_id "
                + "WHERE it.transaction_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                InventoryTransaction t = new InventoryTransaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setSku(rs.getString("sku"));
                t.setTransactionType(rs.getString("transaction_type"));
                t.setQuantityChange(rs.getInt("quantity_change"));
                t.setQuantityBefore(rs.getInt("quantity_before"));
                t.setQuantityAfter(rs.getInt("quantity_after"));
                t.setReferenceType(rs.getString("reference_type"));
                t.setReferenceId((Integer) rs.getObject("reference_id"));
                t.setReferenceDisplay(rs.getString("reference_display"));
                t.setCreatedBy(rs.getString("full_name"));
                t.setTransactionDate(rs.getTimestamp("transaction_date"));
                t.setNotes(rs.getString("notes"));
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
