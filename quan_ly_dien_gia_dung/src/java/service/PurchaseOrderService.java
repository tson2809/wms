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
        if (!"draft".equalsIgnoreCase(status)) {
            throw new SQLException("Chỉ có thể chỉnh sửa đơn hàng ở trạng thái chờ xử lý (pending).");
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

    public boolean claimPurchaseOrder(int purchaseOrderId, int staffId) {
        return purchaseOrderDAO.claimPurchaseOrder(purchaseOrderId, staffId);
    }

    public boolean cancelPurchaseOrder(int purchaseOrderId) {
        return purchaseOrderDAO.cancelPurchaseOrder(purchaseOrderId);
    }

    public boolean completePurchaseOrder(int purchaseOrderId) {
        return purchaseOrderDAO.completePurchaseOrder(purchaseOrderId);
    }

    private String getStatusLabel(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "processing": return "Đang xử lý";
            case "completed": return "Hoàn tất";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}
