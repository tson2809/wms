package service;

import dal.PurchaseOrderDAO;
import dal.PurchaseOrderDetailDAO;
import dal.SupplierDAO;
import dal.DBContext;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.Supplier;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseOrderService {
    private PurchaseOrderDAO purchaseOrderDAO;
    private PurchaseOrderDetailDAO detailDAO;
    private SupplierDAO supplierDAO;
    private DBContext dbContext;

    public PurchaseOrderService() {
        this.purchaseOrderDAO = new PurchaseOrderDAO();
        this.detailDAO = new PurchaseOrderDetailDAO();
        this.supplierDAO = new SupplierDAO();
        this.dbContext = new DBContext();
    }

    public boolean validatePurchaseOrder(PurchaseOrder po, List<PurchaseOrderDetail> details, 
            StringBuilder errorMsg) {
        
        Supplier supplier = supplierDAO.getSupplierById(po.getSupplierId());
        if (supplier == null) {
            errorMsg.append("Nhà cung cấp không tồn tại. ");
            return false;
        }
        
        if (!"active".equalsIgnoreCase(supplier.getStatus())) {
            errorMsg.append("Nhà cung cấp không còn hoạt động. ");
            return false;
        }

        if (po.getExpectedDeliveryDate() != null && po.getOrderDate() != null) {
            if (po.getExpectedDeliveryDate().before(po.getOrderDate())) {
                errorMsg.append("Ngày giao hàng dự kiến không được nhỏ hơn ngày đặt hàng. ");
                return false;
            }
        }

        if (details == null || details.isEmpty()) {
            errorMsg.append("Phải có ít nhất một sản phẩm trong đơn hàng. ");
            return false;
        }

        for (int i = 0; i < details.size(); i++) {
            PurchaseOrderDetail detail = details.get(i);
            
            if (detail.getQuantity() <= 0) {
                errorMsg.append("Số lượng sản phẩm dòng ").append(i + 1)
                         .append(" phải lớn hơn 0. ");
                return false;
            }
            
            if (detail.getUnitPrice() == null || detail.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                errorMsg.append("Đơn giá sản phẩm dòng ").append(i + 1)
                         .append(" phải lớn hơn 0. ");
                return false;
            }

            for (int j = i + 1; j < details.size(); j++) {
                if (details.get(j).getVariantId() == detail.getVariantId()) {
                    errorMsg.append("Sản phẩm bị trùng lặp trong đơn hàng. ");
                    return false;
                }
            }
        }

        return true;
    }

    public int createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderDetail> details) 
            throws SQLException {
        Connection conn = null;
        try {
            conn = dbContext.getConnection();
            conn.setAutoCommit(false);

            String poCode = purchaseOrderDAO.generatePoCode();
            po.setPoCode(poCode);
            po.setStatus("draft");

            BigDecimal grandTotal = BigDecimal.ZERO;
            for (PurchaseOrderDetail detail : details) {
                BigDecimal lineTotal = detail.getUnitPrice()
                        .multiply(new BigDecimal(detail.getQuantity()));
                detail.setTotalAmount(lineTotal);
                grandTotal = grandTotal.add(lineTotal);
            }
            po.setTotalAmount(grandTotal);

            int poId = purchaseOrderDAO.insertPurchaseOrder(po, conn);
            if (poId <= 0) {
                throw new SQLException("Failed to insert purchase order");
            }

            for (PurchaseOrderDetail detail : details) {
                detail.setPurchaseOrderId(poId);
                if (!detailDAO.insertDetail(detail, conn)) {
                    throw new SQLException("Failed to insert purchase order detail");
                }
            }

            conn.commit();
            return poId;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    Logger.getLogger(PurchaseOrderService.class.getName())
                          .log(Level.SEVERE, "Rollback failed", e);
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    Logger.getLogger(PurchaseOrderService.class.getName())
                          .log(Level.SEVERE, "Failed to reset autocommit", e);
                }
            }
        }
    }

    public boolean updatePurchaseOrder(PurchaseOrder po, List<PurchaseOrderDetail> details) 
            throws SQLException {
        
        PurchaseOrder existingPO = purchaseOrderDAO.getPurchaseOrderById(po.getPurchaseOrderId());
        if (existingPO == null) {
            throw new SQLException("Purchase order not found");
        }

        String status = existingPO.getStatus();
        if ("approved".equalsIgnoreCase(status) || "received".equalsIgnoreCase(status) 
                || "cancelled".equalsIgnoreCase(status)) {
            throw new SQLException("Không thể chỉnh sửa đơn hàng có trạng thái: " + status);
        }

        Connection conn = null;
        try {
            conn = dbContext.getConnection();
            conn.setAutoCommit(false);

            BigDecimal grandTotal = BigDecimal.ZERO;
            for (PurchaseOrderDetail detail : details) {
                BigDecimal lineTotal = detail.getUnitPrice()
                        .multiply(new BigDecimal(detail.getQuantity()));
                detail.setTotalAmount(lineTotal);
                grandTotal = grandTotal.add(lineTotal);
            }
            po.setTotalAmount(grandTotal);

            detailDAO.deleteDetailsByPurchaseOrderId(po.getPurchaseOrderId(), conn);

            if (!purchaseOrderDAO.updatePurchaseOrder(po, conn)) {
                throw new SQLException("Failed to update purchase order");
            }

            for (PurchaseOrderDetail detail : details) {
                detail.setPurchaseOrderId(po.getPurchaseOrderId());
                if (!detailDAO.insertDetail(detail, conn)) {
                    throw new SQLException("Failed to insert purchase order detail");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    Logger.getLogger(PurchaseOrderService.class.getName())
                          .log(Level.SEVERE, "Rollback failed", e);
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    Logger.getLogger(PurchaseOrderService.class.getName())
                          .log(Level.SEVERE, "Failed to reset autocommit", e);
                }
            }
        }
    }

    public List<PurchaseOrder> getPurchaseOrdersWithPagination(String status, Integer supplierId,
            Date fromDate, Date toDate, String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return purchaseOrderDAO.getPurchaseOrdersWithFilter(status, supplierId, 
                fromDate, toDate, keyword, offset, pageSize);
    }

    public int countPurchaseOrders(String status, Integer supplierId, 
            Date fromDate, Date toDate, String keyword) {
        return purchaseOrderDAO.countPurchaseOrdersWithFilter(status, supplierId, 
                fromDate, toDate, keyword);
    }

    public PurchaseOrder getPurchaseOrderById(int id) {
        return purchaseOrderDAO.getPurchaseOrderById(id);
    }

    public List<PurchaseOrderDetail> getPurchaseOrderDetails(int purchaseOrderId) {
        return detailDAO.getDetailsByPurchaseOrderId(purchaseOrderId);
    }

    /**
     * Cập nhật trạng thái đơn đặt hàng với validation nghiệp vụ chặt chẽ.
     * 
     * Quy tắc chuyển trạng thái cho Manager:
     *   draft      → submitted, cancelled
     *   submitted  → approved (ghi approved_by), cancelled
     *   approved   → received, cancelled
     *   received   → KHÔNG được thay đổi (đã hoàn thành)
     *   cancelled  → KHÔNG được thay đổi (đã hủy vĩnh viễn)
     *
     * @param purchaseOrderId ID đơn đặt hàng
     * @param newStatus       Trạng thái mới
     * @param userId          ID người thực hiện (Manager)
     * @return "SUCCESS" nếu thành công, hoặc chuỗi thông báo lỗi nghiệp vụ
     */
    public String updatePurchaseOrderStatus(int purchaseOrderId, String newStatus, int userId) {
        // 1. Validate trạng thái mới có hợp lệ trong ENUM không
        String[] validStatuses = {"draft", "submitted", "approved", "received", "cancelled"};
        boolean isValidStatus = false;
        for (String s : validStatuses) {
            if (s.equalsIgnoreCase(newStatus)) {
                isValidStatus = true;
                newStatus = s; // chuẩn hóa lowercase
                break;
            }
        }
        if (!isValidStatus) {
            return "Trạng thái \"" + newStatus + "\" không hợp lệ.";
        }

        // 2. Lấy đơn hàng hiện tại
        PurchaseOrder currentPO = purchaseOrderDAO.getPurchaseOrderById(purchaseOrderId);
        if (currentPO == null) {
            return "Không tìm thấy đơn đặt hàng #" + purchaseOrderId + ".";
        }

        String currentStatus = currentPO.getStatus();

        // 3. Không cho phép cập nhật nếu trạng thái không đổi
        if (currentStatus.equalsIgnoreCase(newStatus)) {
            return "Đơn hàng đã ở trạng thái này rồi.";
        }

        // 4. Validate quy tắc chuyển trạng thái (transition rules)
        if (!isValidTransition(currentStatus, newStatus)) {
            String currentLabel = getStatusLabel(currentStatus);
            String newLabel = getStatusLabel(newStatus);
            return "Không thể chuyển từ \"" + currentLabel + "\" sang \"" + newLabel + "\". "
                    + getTransitionHint(currentStatus);
        }

        // 5. Xác định approved_by: chỉ ghi khi chuyển sang approved
        Integer approvedBy = null;
        if ("approved".equals(newStatus)) {
            approvedBy = userId;
        }

        // 6. Thực hiện cập nhật
        try {
            boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(purchaseOrderId, newStatus, approvedBy);
            if (success) {
                return "SUCCESS";
            } else {
                return "Không thể cập nhật trạng thái. Vui lòng thử lại.";
            }
        } catch (Exception e) {
            Logger.getLogger(PurchaseOrderService.class.getName()).log(Level.SEVERE,
                    "Error updating purchase order status", e);
            return "Lỗi hệ thống khi cập nhật trạng thái: " + e.getMessage();
        }
    }

    /**
     * Kiểm tra chuyển trạng thái có hợp lệ không.
     * Chỉ cho phép chuyển tiến, không nhảy cóc, không quay lại.
     */
    private boolean isValidTransition(String currentStatus, String newStatus) {
        switch (currentStatus.toLowerCase()) {
            case "draft":
                return "submitted".equals(newStatus) || "cancelled".equals(newStatus);
            case "submitted":
                return "approved".equals(newStatus) || "cancelled".equals(newStatus);
            case "approved":
                return "received".equals(newStatus) || "cancelled".equals(newStatus);
            case "received":
                return false; // Đã hoàn thành, không thể thay đổi
            case "cancelled":
                return false; // Đã hủy, không thể thay đổi
            default:
                return false;
        }
    }

    /**
     * Trả về gợi ý chuyển trạng thái hợp lệ cho trạng thái hiện tại.
     */
    private String getTransitionHint(String currentStatus) {
        switch (currentStatus.toLowerCase()) {
            case "draft":
                return "Đơn nháp chỉ có thể chuyển sang \"Đã gửi\" hoặc \"Đã hủy\".";
            case "submitted":
                return "Đơn đã gửi chỉ có thể chuyển sang \"Đã duyệt\" hoặc \"Đã hủy\".";
            case "approved":
                return "Đơn đã duyệt chỉ có thể chuyển sang \"Đã nhận hàng\" hoặc \"Đã hủy\".";
            case "received":
                return "Đơn đã nhận hàng không thể thay đổi trạng thái.";
            case "cancelled":
                return "Đơn đã hủy không thể thay đổi trạng thái.";
            default:
                return "";
        }
    }

    private String getStatusLabel(String status) {
        switch (status) {
            case "draft": return "Nháp";
            case "submitted": return "Đã gửi";
            case "approved": return "Đã duyệt";
            case "received": return "Đã nhận hàng";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}
