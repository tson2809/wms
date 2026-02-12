/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Staff;

import dal.SupplierDAO;
import dal.GoodsReceiptDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.GoodsReceiptDetail;
import model.Supplier;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsReceiptAddController", urlPatterns = {"/goods-receipt-add"})
public class GoodsReceiptAddController extends HttpServlet {
    private SupplierDAO supplierDAO = new SupplierDAO();
    private GoodsReceiptDAO goodsReceiptDAO = new GoodsReceiptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
        request.setAttribute("suppliers", suppliers);
        
        request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
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
            handleCreateReceipt(request, response);
        }
    }
    
    private void handleCreateReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            GoodsReceiptDAO checkDao = new GoodsReceiptDAO();
            if (checkDao.receiptCodeExists(receiptCode)) {
                request.setAttribute("receiptCodeError", "Mã phiếu nhập đã tồn tại");
                hasErrors = true;
            }
        }
        
        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("productsError", "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập");
            hasErrors = true;
        }
        
        if (hasErrors) {
            request.setAttribute("productsJson", productsJson);
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            request.setAttribute("suppliers", suppliers);
            request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
            return;
        }
        
        try {
            int supplier_Id = Integer.parseInt(supplierId);
            String notes = request.getParameter("notes");
            if (notes == null) notes = "";
            
            List<GoodsReceiptDetail> details = parseProductsJson(productsJson);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (GoodsReceiptDetail detail : details) {
                totalAmount = totalAmount.add(detail.getTotalAmount());
            }
            
            User currentUser = (User) request.getSession().getAttribute("user");
            int createdBy = currentUser.getUserId();
            
            boolean success = goodsReceiptDAO.createGoodsReceipt(
                receiptCode, supplier_Id, receiptDate, totalAmount.doubleValue(), 
                notes, createdBy, details
            );
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Tạo phiếu nhập kho thành công!");
                response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
            } else {
                request.setAttribute("generalError", "Có lỗi xảy ra khi tạo phiếu nhập kho. Vui lòng thử lại!");
                request.setAttribute("productsJson", productsJson);
                List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
                request.setAttribute("suppliers", suppliers);
                request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("generalError", "Có lỗi xảy ra: " + ex.getMessage());
            request.setAttribute("productsJson", productsJson);
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            request.setAttribute("suppliers", suppliers);
            request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
        }
    }
    
    private List<GoodsReceiptDetail> parseProductsJson(String json) {
        List<GoodsReceiptDetail> details = new ArrayList<>();
        
        // Remove [ ] brackets
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
        // Split by },{ pattern
        String[] products = json.split("\\},\\{");
        
        for (String productStr : products) {
            productStr = productStr.replace("{", "").replace("}", "");
            
            GoodsReceiptDetail detail = new GoodsReceiptDetail();
            List<String> serials = new ArrayList<>();
            
            // FIRST: Extract serials array separately (to avoid split issues)
            int serialsStartIdx = productStr.indexOf("\"serials\":");
            if (serialsStartIdx != -1) {
                int arrayStartIdx = productStr.indexOf("[", serialsStartIdx);
                int arrayEndIdx = productStr.indexOf("]", arrayStartIdx);
                
                if (arrayStartIdx != -1 && arrayEndIdx != -1) {
                    String serialsArrayStr = productStr.substring(arrayStartIdx + 1, arrayEndIdx);
                    
                    if (!serialsArrayStr.trim().isEmpty()) {
                        String[] serialArray = serialsArrayStr.split(",");
                        
                        for (String serial : serialArray) {
                            serial = serial.trim().replace("\"", "");
                            if (!serial.isEmpty()) {
                                serials.add(serial);
                            }
                        }
                    }
                    
                    // Remove serials array from productStr to avoid parsing issues
                    productStr = productStr.substring(0, serialsStartIdx) + productStr.substring(arrayEndIdx + 1);
                }
            }
            
            // SECOND: Parse other fields
            String[] fields = productStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            for (String field : fields) {
                String[] keyValue = field.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();
                    
                    switch (key) {
                        case "variantId":
                            detail.setVariantId(Integer.parseInt(value));
                            break;
                        case "quantity":
                            detail.setQuantity(Integer.parseInt(value));
                            break;
                        case "price":
                            detail.setUnitPrice(new BigDecimal(value));
                            break;
                    }
                }
            }
            
            // SECOND PASS: Calculate totalAmount after all fields are set
            if (detail.getUnitPrice() != null && detail.getQuantity() > 0) {
                BigDecimal subtotal = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                detail.setTotalAmount(subtotal);
            } else {
                detail.setTotalAmount(BigDecimal.ZERO);
            }
            
            detail.setSerials(serials);
            details.add(detail);
        }
        
        return details;
    }
    
    private void handleProductSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = goodsReceiptDAO.searchProductsForReceiptJson(search);
        response.getWriter().write(json);
    }
    
    private void handleCheckSerial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serial = request.getParameter("serial");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        boolean exists = goodsReceiptDAO.serialNumberExists(serial);
        
        response.getWriter().write("{\"exists\": " + exists + "}");
    }
    
    private void handleSearchBySKU(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sku = request.getParameter("sku");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = goodsReceiptDAO.searchProductBySKU(sku);
        response.getWriter().write(json);
    }
}
