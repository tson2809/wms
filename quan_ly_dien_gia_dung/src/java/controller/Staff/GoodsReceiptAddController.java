/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Staff;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.GoodsReceipt;
import model.GoodsReceiptDetail;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.SalesReturn;
import model.SalesReturnDetail;
import model.Supplier;
import model.User;
import modelDTO.GoodsReceiptProductDTO;
import service.PurchaseOrderService;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsReceiptAddController", urlPatterns = {"/goods-receipt-add"})
public class GoodsReceiptAddController extends HttpServlet {
    private SupplierDAO supplierDAO = new SupplierDAO();
    private GoodsReceiptDAO goodsReceiptDAO = new GoodsReceiptDAO();
    private SalesReturnDAO salesReturnDAO = new SalesReturnDAO();
    private PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private PurchaseOrderService purchaseOrderService = new PurchaseOrderService();
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String salesReturnIdStr = request.getParameter("salesReturnId");
        if (salesReturnIdStr != null && !salesReturnIdStr.trim().isEmpty()) {
            try {
                int salesReturnId = Integer.parseInt(salesReturnIdStr.trim());
                SalesReturn sr = salesReturnDAO.getSalesReturnById(salesReturnId);
                User user = (User) request.getSession().getAttribute("user");

                if (sr != null && user != null && user.getRole() != null && user.getRole().getRoleId() == 3
                        && "processing".equalsIgnoreCase(sr.getStatus() != null ? sr.getStatus().trim() : "")
                        && sr.getReceivedBy() != null && sr.getReceivedBy().equals(user.getUserId())) {

                    List<SalesReturnDetail> details = salesReturnDAO.getSalesReturnDetailsByOrderId(salesReturnId);
                    List<Map<String, Object>> products = buildProductsFromSalesReturn(details);

                    if (!products.isEmpty()) {
                        request.setAttribute("productsJson", gson.toJson(products));
                        request.setAttribute("purchaseOrderCodeValue", sr.getSrCode());
                        request.setAttribute("supplierIdValue", "SALE");
                        request.setAttribute("salesReturnId", salesReturnId);
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // Pre-fill từ PO khi Staff nhấn "Tạo phiếu nhập kho"
        String purchaseOrderIdStr = request.getParameter("purchaseOrderId");
        if (purchaseOrderIdStr != null && !purchaseOrderIdStr.trim().isEmpty()) {
            try {
                int poId = Integer.parseInt(purchaseOrderIdStr.trim());
                // Luôn set purchaseOrderId để hidden input trong form được render
                request.setAttribute("purchaseOrderId", poId);

                User user = (User) request.getSession().getAttribute("user");
                PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);

                if (po != null && user != null && user.getRole() != null && user.getRole().getRoleId() == 3
                        && "submitted".equalsIgnoreCase(po.getStatus())
                        && po.getApprovedBy() != null && po.getApprovedBy().equals(user.getUserId())) {

                    List<PurchaseOrderDetail> poDetails = purchaseOrderService.getPurchaseOrderDetails(poId);
                    List<Map<String, Object>> products = buildProductsFromPO(poDetails);

                    request.setAttribute("poSupplierIdValue", po.getSupplierId());
                    request.setAttribute("poExpectedDeliveryDate", po.getExpectedDeliveryDate());
                    if (!products.isEmpty()) {
                        request.setAttribute("productsJson", gson.toJson(products));
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("generatedReceiptCode", goodsReceiptDAO.generateNextReceiptCode());
        
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
        if (receiptCode == null || receiptCode.trim().isEmpty()) {
            receiptCode = goodsReceiptDAO.generateNextReceiptCode();
        } else {
            receiptCode = receiptCode.trim();
        }
        String productsJson = request.getParameter("products");

        String salesReturnIdRaw = request.getParameter("salesReturnId");
        boolean isSalesReturnSource = salesReturnIdRaw != null && !salesReturnIdRaw.trim().isEmpty();
        // Có salesReturnId thì luôn xem là nguồn nhập từ Sale, không phụ thuộc dữ liệu client gửi lên.
        if (isSalesReturnSource) {
            supplierId = "SALE";
        }
        
        boolean hasErrors = false;
        
        // Nguồn cung cấp:
        // - Giá trị "SALE": Nhập từ sale -> supplier_id = NULL trong DB.
        // - Giá trị số khác: ID nhà cung cấp.
        // - Rỗng: không hợp lệ.
        boolean isFromSale = isSalesReturnSource || "SALE".equalsIgnoreCase(supplierId != null ? supplierId.trim() : "");
        if (!isFromSale) {
            if (supplierId == null || supplierId.trim().isEmpty()) {
                request.setAttribute("supplierIdError", "Vui lòng chọn nguồn cung cấp");
                hasErrors = true;
            }
        }
        
        if (receiptDate == null || receiptDate.trim().isEmpty()) {
            request.setAttribute("receiptDateError", "Vui lòng chọn ngày nhập");
            hasErrors = true;
        }
        
        GoodsReceiptDAO checkDao = new GoodsReceiptDAO();
        if (checkDao.receiptCodeExists(receiptCode)) {
            receiptCode = goodsReceiptDAO.generateNextReceiptCode();
            if (checkDao.receiptCodeExists(receiptCode)) {
                request.setAttribute("generalError", "Không thể tạo mã phiếu nhập tự động. Vui lòng thử lại.");
                hasErrors = true;
            }
        }
        
        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("productsError", "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập");
            hasErrors = true;
        }
        
        if (hasErrors) {
            request.setAttribute("productsJson", productsJson);
            request.setAttribute("generatedReceiptCode", receiptCode);
            String purchaseOrderCode = request.getParameter("purchaseOrderCode");
            if (purchaseOrderCode != null && !purchaseOrderCode.trim().isEmpty()) {
                request.setAttribute("purchaseOrderCodeValue", purchaseOrderCode.trim());
            }
            String salesReturnId = request.getParameter("salesReturnId");
            if (salesReturnId != null && !salesReturnId.trim().isEmpty()) {
                request.setAttribute("salesReturnId", salesReturnId.trim());
            }
            String purchaseOrderIdParam = request.getParameter("purchaseOrderId");
            if (purchaseOrderIdParam != null && !purchaseOrderIdParam.trim().isEmpty()) {
                request.setAttribute("purchaseOrderId", purchaseOrderIdParam.trim());
            }
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            request.setAttribute("suppliers", suppliers);
            request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
            return;
        }
        
        try {
            Integer supplier_Id = null;
            if (!isFromSale) {
                supplier_Id = Integer.parseInt(supplierId);
            }
            Integer salesReturnId = null;
            if (salesReturnIdRaw != null && !salesReturnIdRaw.trim().isEmpty()) {
                try {
                    salesReturnId = Integer.parseInt(salesReturnIdRaw.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            Integer purchaseOrderId = null;
            String purchaseOrderIdRaw = request.getParameter("purchaseOrderId");
            if (purchaseOrderIdRaw != null && !purchaseOrderIdRaw.trim().isEmpty()) {
                try {
                    purchaseOrderId = Integer.parseInt(purchaseOrderIdRaw.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            String notes = request.getParameter("notes");
            if (notes == null) notes = "";
            
            List<GoodsReceiptDetail> details = parseProductsJson(productsJson);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (GoodsReceiptDetail detail : details) {
                totalAmount = totalAmount.add(detail.getTotalAmount());
            }
            
            User currentUser = (User) request.getSession().getAttribute("user");
            int createdBy = currentUser.getUserId();
            
            boolean success = false;
            String createdReceiptCode = receiptCode;
            for (int attempt = 0; attempt < 3; attempt++) {
                if (attempt > 0) {
                    createdReceiptCode = goodsReceiptDAO.generateNextReceiptCode();
                }
                success = goodsReceiptDAO.createGoodsReceipt(
                    createdReceiptCode, supplier_Id, receiptDate, totalAmount.doubleValue(),
                    notes, createdBy, details, salesReturnId, purchaseOrderId
                );
                if (success) {
                    break;
                }
                if (!goodsReceiptDAO.receiptCodeExists(createdReceiptCode)) {
                    break;
                }
            }

            if (success) {
                Integer receiptId = goodsReceiptDAO.getReceiptIdByReceiptCode(createdReceiptCode);
                boolean completed = receiptId != null
                        && goodsReceiptDAO.updateGoodsReceiptStatus(receiptId, "completed", createdBy);
                if (!completed) {
                    request.setAttribute("generalError",
                            "Phiếu đã được tạo nhưng không hoàn tất nhập kho tự động. Vui lòng báo quản lý duyệt phiếu trong danh sách.");
                    request.setAttribute("productsJson", productsJson);
                    if (salesReturnId != null) {
                        request.setAttribute("salesReturnId", salesReturnId);
                    }
                    if (purchaseOrderId != null) {
                        request.setAttribute("purchaseOrderId", purchaseOrderId);
                    }
                    List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
                    request.setAttribute("suppliers", suppliers);
                    request.setAttribute("generatedReceiptCode", goodsReceiptDAO.generateNextReceiptCode());
                    request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
                    return;
                }
                GoodsReceipt justReceipt = goodsReceiptDAO.getGoodsReceiptById(receiptId);
                if (justReceipt != null) {
                    if (justReceipt.getSalesReturnId() != null) {
                        salesReturnDAO.completeSalesReturn(justReceipt.getSalesReturnId());
                    }
                    if (justReceipt.getPurchaseOrderId() != null) {
                        purchaseOrderDAO.completePurchaseOrder(justReceipt.getPurchaseOrderId());
                    }
                }
                request.getSession().setAttribute("successMessage", "Tạo phiếu nhập kho và nhập kho thành công!");
                response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
            } else {
                request.setAttribute("generalError", "Có lỗi xảy ra khi tạo phiếu nhập kho. Vui lòng thử lại!");
                request.setAttribute("productsJson", productsJson);
                if (salesReturnId != null) request.setAttribute("salesReturnId", salesReturnId);
                if (purchaseOrderId != null) request.setAttribute("purchaseOrderId", purchaseOrderId);
                List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
                request.setAttribute("suppliers", suppliers);
                request.setAttribute("generatedReceiptCode", goodsReceiptDAO.generateNextReceiptCode());
                request.getRequestDispatcher("/view/staff/goods-receipt-add.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("generalError", "Có lỗi xảy ra: " + ex.getMessage());
            request.setAttribute("productsJson", productsJson);
            String srIdParam = request.getParameter("salesReturnId");
            if (srIdParam != null && !srIdParam.trim().isEmpty()) {
                request.setAttribute("salesReturnId", srIdParam.trim());
            }
            String poIdParam = request.getParameter("purchaseOrderId");
            if (poIdParam != null && !poIdParam.trim().isEmpty()) {
                request.setAttribute("purchaseOrderId", poIdParam.trim());
            }
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("generatedReceiptCode", goodsReceiptDAO.generateNextReceiptCode());
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
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = goodsReceiptDAO.searchProductsForReceiptJson(search, supplierId);
        response.getWriter().write(json);
    }
    
    private void handleCheckSerial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serial = request.getParameter("serial");
        String supplierId = request.getParameter("supplierId");
        String salesReturnId = request.getParameter("salesReturnId");
        boolean hasSalesReturnId = salesReturnId != null && !salesReturnId.trim().isEmpty();
        boolean hasSupplierParam = supplierId != null && !supplierId.trim().isEmpty();
        boolean isFromSale = hasSalesReturnId
                || "SALE".equalsIgnoreCase(supplierId != null ? supplierId.trim() : "");
        // Fallback for stale cached JS that may only send "serial" without source params.
        // In that case, allow SOLD serial to pass pre-check; createReceipt still enforces final rule.
        if (!hasSalesReturnId && !hasSupplierParam) {
            isFromSale = true;
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        boolean exists = goodsReceiptDAO.isSerialBlockedForReceipt(serial, isFromSale);
        
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

    private List<Map<String, Object>> buildProductsFromSalesReturn(List<SalesReturnDetail> details) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (details == null || details.isEmpty()) return out;

        int id = 1;
        for (SalesReturnDetail d : details) {
            if (d == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id++);
            m.put("variantId", d.getVariantId());
            m.put("code", d.getVariantSku());
            m.put("name", d.getProductName());
            m.put("unit", d.getUnitName());
            m.put("price", d.getOriginalPrice() != null ? d.getOriginalPrice() : BigDecimal.ZERO);
            // Không tự fill số lượng khi load từ sales return; staff sẽ nhập/scan sau.
            m.put("quantity", 0);
            m.put("serials", new ArrayList<>());
            out.add(m);
        }
        return out;
    }

    private List<Map<String, Object>> buildProductsFromPO(List<PurchaseOrderDetail> details) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (details == null || details.isEmpty()) return out;

        int id = 1;
        for (PurchaseOrderDetail d : details) {
            if (d == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id++);
            m.put("variantId", d.getVariantId());
            m.put("code", d.getSku() != null ? d.getSku() : "");
            m.put("name", d.getProductName() != null ? d.getProductName() : "");
            m.put("unit", "");
            m.put("price", d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO);
            m.put("quantity", d.getQuantity());
            m.put("serials", new ArrayList<>());
            out.add(m);
        }
        return out;
    }
}
