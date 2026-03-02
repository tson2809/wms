/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Staff;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.SupplierDAO;
import dal.GoodsReceiptDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.GoodsReceiptDetail;
import model.Supplier;
import model.User;
import modelDTO.GoodsReceiptProductDTO;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsReceiptAddController", urlPatterns = {"/goods-receipt-add"})
public class GoodsReceiptAddController extends HttpServlet {
    private SupplierDAO supplierDAO = new SupplierDAO();
    private GoodsReceiptDAO goodsReceiptDAO = new GoodsReceiptDAO();
    private static final Gson gson = new Gson();

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
