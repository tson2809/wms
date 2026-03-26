/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.SupplierDAO;
import dal.GoodsReceiptDAO;
import dal.PurchaseOrderDAO;
import dal.SalesReturnDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.GoodsReceipt;
import model.GoodsReceiptDetail;
import model.Supplier;
import model.User;
import modelDTO.GoodsReceiptProductDTO;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsReceiptEditController", urlPatterns = {"/goods-receipt-edit"})
public class GoodsReceiptEditController extends HttpServlet {
    private SupplierDAO supplierDAO = new SupplierDAO();
    private GoodsReceiptDAO goodsReceiptDAO = new GoodsReceiptDAO();
    private SalesReturnDAO salesReturnDAO = new SalesReturnDAO();
    private PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
            return;
        }
        
        try {
            int receiptId = Integer.parseInt(idParam);
            
            GoodsReceipt receipt = goodsReceiptDAO.getGoodsReceiptById(receiptId);
            
            if (receipt == null) {
                request.setAttribute("error", "Không tìm thấy phiếu nhập kho");
                response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
                return;
            }
            
            boolean readOnly = "completed".equals(receipt.getStatus()) || "cancelled".equals(receipt.getStatus());
            request.setAttribute("readOnly", readOnly);
            
            List<GoodsReceiptDetail> details = goodsReceiptDAO.getGoodsReceiptDetails(receiptId);
            
            // Convert details to JSON string manually
            String productsJsonStr = convertDetailsToJson(details);
            
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            
            request.setAttribute("receipt", receipt);
            request.setAttribute("productsJson", productsJsonStr);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("isEdit", true);
            
            request.getRequestDispatcher("/view/common/goods-receipt-edit.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("searchProduct".equals(action)) {
            handleProductSearch(request, response);
        } else if ("checkSerial".equals(action)) {
            handleCheckSerial(request, response);
        } else if ("searchProductBySKU".equals(action)) {
            handleSearchBySKU(request, response);
        } else {
            handleUpdateReceipt(request, response);
        }
    }
    
    private void handleUpdateReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String receiptIdParam = request.getParameter("receiptId");
        
        if (receiptIdParam == null || receiptIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
            return;
        }
        
        int receiptId = Integer.parseInt(receiptIdParam);
        GoodsReceipt receipt = goodsReceiptDAO.getGoodsReceiptById(receiptId);
        if (receipt != null && ("completed".equals(receipt.getStatus()) || "cancelled".equals(receipt.getStatus()))) {
            response.sendRedirect(request.getContextPath() + "/goods-receipt-edit?id=" + receiptId);
            return;
        }
        
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Set<String> userPermissions = getUserPermissions(request);
        boolean canApproveGoodsReceipt = userPermissions != null && userPermissions.contains("approve goods receipt");
        boolean canEditGoodsReceipt = userPermissions != null && userPermissions.contains("edit goods receipt");

        String status = request.getParameter("status");
        if (canApproveGoodsReceipt && status != null) {
            if (status.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn trạng thái");
                GoodsReceipt r = goodsReceiptDAO.getGoodsReceiptById(receiptId);
                request.setAttribute("receipt", r);
                request.setAttribute("readOnly", r != null && ("completed".equals(r.getStatus()) || "cancelled".equals(r.getStatus())));
                List<GoodsReceiptDetail> details = goodsReceiptDAO.getGoodsReceiptDetails(receiptId);
                String productsJson = convertDetailsToJson(details);
                request.setAttribute("productsJson", productsJson);
                List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
                request.setAttribute("suppliers", suppliers);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/view/common/goods-receipt-edit.jsp").forward(request, response);
                return;
            }
            
            Integer approvedBy = "completed".equals(status) ? currentUser.getUserId() : null;
            boolean success = goodsReceiptDAO.updateGoodsReceiptStatus(receiptId, status, approvedBy);
            if (success) {
                if ("completed".equals(status) || "cancelled".equals(status)) {
                    GoodsReceipt approvedReceipt = goodsReceiptDAO.getGoodsReceiptById(receiptId);
                    if (approvedReceipt != null) {
                        if ("completed".equals(status)) {
                            // Nếu receipt liên kết với sales return → complete sales return
                            if (approvedReceipt.getSalesReturnId() != null) {
                                salesReturnDAO.completeSalesReturn(approvedReceipt.getSalesReturnId());
                            }
                            // Nếu receipt liên kết với purchase order → complete PO
                            if (approvedReceipt.getPurchaseOrderId() != null) {
                                purchaseOrderDAO.completePurchaseOrder(approvedReceipt.getPurchaseOrderId());
                            }
                        } else if ("cancelled".equals(status)) {
                            if (approvedReceipt.getPurchaseOrderId() != null) {
                                purchaseOrderDAO.cancelPurchaseOrder(approvedReceipt.getPurchaseOrderId());
                            }
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi cập nhật trạng thái");
                GoodsReceipt r = goodsReceiptDAO.getGoodsReceiptById(receiptId);
                request.setAttribute("receipt", r);
                request.setAttribute("readOnly", r != null && ("completed".equals(r.getStatus()) || "cancelled".equals(r.getStatus())));
                List<GoodsReceiptDetail> details = goodsReceiptDAO.getGoodsReceiptDetails(receiptId);
                String productsJson = convertDetailsToJson(details);
                request.setAttribute("productsJson", productsJson);
                List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
                request.setAttribute("suppliers", suppliers);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/view/common/goods-receipt-edit.jsp").forward(request, response);
            }
            return;
        }

        if (!canEditGoodsReceipt) {
            response.sendRedirect(request.getContextPath() + "/goods-receipt-list?denied=true");
            return;
        }
        
        String supplierId = request.getParameter("supplierId");
        String receiptDate = request.getParameter("receiptDate");
        String receiptCode = request.getParameter("receiptCode");
        String productsJson = request.getParameter("products");
        
        boolean hasErrors = false;
        
        if (supplierId == null || supplierId.trim().isEmpty()) {
            request.setAttribute("supplierIdError", "Vui lòng chọn nhà cung cấp");
            hasErrors = true;
        }
        
        if (receiptDate == null || receiptDate.trim().isEmpty()) {
            request.setAttribute("receiptDateError", "Vui lòng chọn ngày nhập");
            hasErrors = true;
        }
        
        if (receiptCode == null || receiptCode.trim().isEmpty()) {
            request.setAttribute("receiptCodeError", "Vui lòng nhập mã phiếu nhập");
            hasErrors = true;
        } else {
            GoodsReceipt currentReceipt = goodsReceiptDAO.getGoodsReceiptById(receiptId);
            if (currentReceipt != null && !receiptCode.equals(currentReceipt.getReceiptCode())) {
                if (goodsReceiptDAO.receiptCodeExists(receiptCode)) {
                    request.setAttribute("receiptCodeError", "Mã phiếu nhập đã tồn tại");
                    hasErrors = true;
                }
            }
        }
        
        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("productsError", "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập");
            hasErrors = true;
        }
        
        if (hasErrors) {
            request.setAttribute("productsJson", productsJson);
            request.setAttribute("receiptId", receiptId);
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            request.setAttribute("suppliers", suppliers);
            GoodsReceipt r = goodsReceiptDAO.getGoodsReceiptById(receiptId);
            request.setAttribute("receipt", r);
            request.setAttribute("readOnly", r != null && ("completed".equals(r.getStatus()) || "cancelled".equals(r.getStatus())));
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/common/goods-receipt-edit.jsp").forward(request, response);
            return;
        }
        
        try {
            int supplier_Id = Integer.parseInt(supplierId);
            Date date = Date.valueOf(receiptDate);
            
            List<model.GoodsReceiptDetail> details = parseProductsJson(productsJson);
            
            boolean success = updateGoodsReceipt(receiptId, supplier_Id, receiptCode, date, details);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi cập nhật phiếu nhập kho");
                request.setAttribute("productsJson", productsJson);
                request.setAttribute("receiptId", receiptId);
                List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
                request.setAttribute("suppliers", suppliers);
                GoodsReceipt r = goodsReceiptDAO.getGoodsReceiptById(receiptId);
                request.setAttribute("receipt", r);
                request.setAttribute("readOnly", r != null && ("completed".equals(r.getStatus()) || "cancelled".equals(r.getStatus())));
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/view/common/goods-receipt-edit.jsp").forward(request, response);
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Serial")) {
                request.setAttribute("error", errorMsg);
            } else {
                request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            }
            request.setAttribute("productsJson", productsJson);
            request.setAttribute("receiptId", receiptId);
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            request.setAttribute("suppliers", suppliers);
            GoodsReceipt r = goodsReceiptDAO.getGoodsReceiptById(receiptId);
            request.setAttribute("receipt", r);
            request.setAttribute("readOnly", r != null && ("completed".equals(r.getStatus()) || "cancelled".equals(r.getStatus())));
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/view/common/goods-receipt-edit.jsp").forward(request, response);
        }
    }
    
    private boolean updateGoodsReceipt(int receiptId, int supplierId, String receiptCode, 
                                      Date receiptDate, List<model.GoodsReceiptDetail> details) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = goodsReceiptDAO.getConnection();
            conn.setAutoCommit(false);
            
            // Get current serials from this receipt
            List<GoodsReceiptDetail> currentDetails = goodsReceiptDAO.getGoodsReceiptDetails(receiptId);
            List<String> currentSerials = new ArrayList<>();
            for (GoodsReceiptDetail currentDetail : currentDetails) {
                if (currentDetail.getSerials() != null) {
                    currentSerials.addAll(currentDetail.getSerials());
                }
            }
            
            // Check for duplicate serials before proceeding (skip serials that belong to current receipt)
            for (model.GoodsReceiptDetail detail : details) {
                if (detail.getSerials() != null) {
                    for (String serial : detail.getSerials()) {
                        if (goodsReceiptDAO.serialNumberExists(serial) && !currentSerials.contains(serial)) {
                            throw new SQLException("Serial number đã tồn tại: " + serial);
                        }
                    }
                }
            }
            
            // Calculate total amount
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (model.GoodsReceiptDetail detail : details) {
                totalAmount = totalAmount.add(detail.getTotalAmount());
            }
            
            // Update goods receipt
            String updateReceiptSql = """
                                      UPDATE goods_receipts 
                                      SET supplier_id = ?, receipt_code = ?, receipt_date = ?, 
                                          total_amount = ?, updated_at = CURRENT_TIMESTAMP 
                                      WHERE receipt_id = ?
                                      """;
            ps = conn.prepareStatement(updateReceiptSql);
            ps.setInt(1, supplierId);
            ps.setString(2, receiptCode);
            ps.setDate(3, receiptDate);
            ps.setBigDecimal(4, totalAmount);
            ps.setInt(5, receiptId);
            ps.executeUpdate();
            ps.close();
            
            // Delete existing details (this will also delete related serials via ON DELETE CASCADE)
            String deleteDetailsSql = """
                                      DELETE FROM goods_receipt_details 
                                      WHERE receipt_id = ?
                                      """;
            ps = conn.prepareStatement(deleteDetailsSql);
            ps.setInt(1, receiptId);
            ps.executeUpdate();
            ps.close();
            
            // Insert new details
            String insertDetailSql = """
                                     INSERT INTO goods_receipt_details 
                                     (receipt_id, variant_id, quantity, unit_price, total_amount) 
                                     VALUES (?, ?, ?, ?, ?)
                                     """;
            ps = conn.prepareStatement(insertDetailSql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            for (model.GoodsReceiptDetail detail : details) {
                ps.setInt(1, receiptId);
                ps.setInt(2, detail.getVariantId());
                ps.setInt(3, detail.getQuantity());
                ps.setBigDecimal(4, detail.getUnitPrice());
                ps.setBigDecimal(5, detail.getTotalAmount());
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int detailId = rs.getInt(1);
                    
                    // Insert serials with receipt_detail_id
                    if (detail.getSerials() != null && !detail.getSerials().isEmpty()) {
                        String insertSerialSql = """
                                                 INSERT INTO product_serials 
                                                 (variant_id, receipt_detail_id, serial_number, status) 
                                                 VALUES (?, ?, ?, 'in_stock')
                                                 """;
                        
                        for (String serial : detail.getSerials()) {
                            try (PreparedStatement psSerial = conn.prepareStatement(insertSerialSql)) {
                                psSerial.setInt(1, detail.getVariantId());
                                psSerial.setInt(2, detailId);
                                psSerial.setString(3, serial);
                                psSerial.executeUpdate();
                            }
                        }
                    }
                }
                rs.close();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw e;
        } finally {
            if (ps != null) ps.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private String convertDetailsToJson(List<GoodsReceiptDetail> details) {
        StringBuilder json = new StringBuilder("[");
        int idCounter = 1;
        
        for (int i = 0; i < details.size(); i++) {
            GoodsReceiptDetail detail = details.get(i);
            if (i > 0) json.append(",");
            
            json.append("{");
            json.append("\"id\":").append(idCounter++).append(",");
            json.append("\"variantId\":").append(detail.getVariantId()).append(",");
            json.append("\"code\":\"").append(escapeJson(detail.getVariantSku())).append("\",");
            json.append("\"name\":\"").append(escapeJson(detail.getProductName())).append("\",");
            json.append("\"unit\":\"").append(escapeJson(detail.getUnitName())).append("\",");
            json.append("\"price\":").append(detail.getUnitPrice()).append(",");
            json.append("\"quantity\":").append(detail.getQuantity()).append(",");
            json.append("\"serials\":[");
            
            if (detail.getSerials() != null) {
                for (int j = 0; j < detail.getSerials().size(); j++) {
                    if (j > 0) json.append(",");
                    json.append("\"").append(escapeJson(detail.getSerials().get(j))).append("\"");
                }
            }
            
            json.append("]}");
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private List<GoodsReceiptDetail> parseProductsJson(String json) {
        List<GoodsReceiptDetail> details = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) {
            return details;
        }

        Type listType = new TypeToken<List<GoodsReceiptProductDTO>>() {}.getType();
        List<GoodsReceiptProductDTO> items = gson.fromJson(json, listType);
        if (items == null) {
            return details;
        }

        for (GoodsReceiptProductDTO item : items) {
            if (item == null) continue;
            if (item.getVariantId() <= 0 || item.getQuantity() <= 0 || item.getPrice() == null) continue;

            GoodsReceiptDetail detail = new GoodsReceiptDetail();
            detail.setVariantId(item.getVariantId());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());

            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            detail.setTotalAmount(subtotal);

            if (item.getSerials() != null && !item.getSerials().isEmpty()) {
                detail.setSerials(new ArrayList<>(item.getSerials()));
            }

            details.add(detail);
        }

        return details;
    }
    
    private void handleProductSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String supplierIdStr = request.getParameter("supplierId");
        Integer supplierId = null;
        if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("[]");
            return;
        }

        String safeSupplier = supplierIdStr.trim();
        if (!"SALE".equalsIgnoreCase(safeSupplier)) {
            try {
                supplierId = Integer.parseInt(safeSupplier);
            } catch (NumberFormatException ex) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("[]");
                return;
            }
        }

        String jsonResult = goodsReceiptDAO.searchProductsForReceiptJson(search, supplierId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResult);
    }
    
    private void handleCheckSerial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serial = request.getParameter("serial");
        boolean exists = goodsReceiptDAO.serialNumberExists(serial);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"exists\":" + exists + "}");
    }
    
    private void handleSearchBySKU(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sku = request.getParameter("sku");
        String jsonResult = goodsReceiptDAO.searchProductBySKU(sku);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResult);
    }

    private Set<String> getUserPermissions(HttpServletRequest request) {
        Object raw = request.getSession().getAttribute("userPermissions");
        if (!(raw instanceof Set<?> rawSet)) {
            return new HashSet<>();
        }

        Set<String> permissions = new HashSet<>();
        for (Object item : rawSet) {
            if (item instanceof String permission) {
                permissions.add(permission.toLowerCase());
            }
        }
        return permissions;
    }
}
