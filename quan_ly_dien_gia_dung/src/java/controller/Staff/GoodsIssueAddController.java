/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Staff;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.GoodsIssueDAO;
import dal.PurchaseOrderDetailDAO;
import dal.PurchaseOrderDAO;
import dal.ReturnOrderDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.PurchaseOrderDetail;
import model.ReturnOrder;
import model.ReturnOrderDetail;
import model.ReturnOrderSerial;
import model.GoodsIssueDetail;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsIssueAddController", urlPatterns = { "/goods-issue-add" })
public class GoodsIssueAddController extends HttpServlet {
    private GoodsIssueDAO goodsIssueDAO = new GoodsIssueDAO();
    private PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private PurchaseOrderDetailDAO purchaseOrderDetailDAO = new PurchaseOrderDetailDAO();
    private ReturnOrderDAO returnOrderDAO = new ReturnOrderDAO();
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pre-fill từ return order
        String returnOrderIdStr = request.getParameter("returnOrderId");
        if (returnOrderIdStr != null && !returnOrderIdStr.trim().isEmpty()) {
            try {
                int returnOrderId = Integer.parseInt(returnOrderIdStr.trim());
                ReturnOrder ro = returnOrderDAO.getReturnOrderById(returnOrderId);
                User user = (User) request.getSession().getAttribute("user");
                if (ro != null && user != null && user.getRole() != null && user.getRole().getRoleId() == 3
                        && "processing".equalsIgnoreCase(ro.getStatus() != null ? ro.getStatus().trim() : "")
                        && ro.getReceivedBy() != null && ro.getReceivedBy().equals(user.getUserId())) {
                    List<Map<String, Object>> products = buildProductsFromReturnOrder(returnOrderId);
                    if (!products.isEmpty()) {
                        request.setAttribute("productsJson", gson.toJson(products));
                        request.setAttribute("issueType", "return_supplier");
                        request.setAttribute("returnOrderCode", ro.getReturnCode());
                        request.setAttribute("returnOrderId", returnOrderId);
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // Pre-fill từ purchase order (Sale orders - supplier_id IS NULL)
        String purchaseOrderIdStr = request.getParameter("purchaseOrderId");
        if (purchaseOrderIdStr != null && !purchaseOrderIdStr.trim().isEmpty()) {
            try {
                int purchaseOrderId = Integer.parseInt(purchaseOrderIdStr.trim());
                User user = (User) request.getSession().getAttribute("user");
                if (user != null && user.getRole() != null && user.getRole().getRoleId() == 3) {
                    var po = purchaseOrderDAO.getPurchaseOrderById(purchaseOrderId);
                    // Fix: dùng intValue() thay vì == để so sánh Integer với int
                    boolean isStaffOwner = po != null && po.getApprovedBy() != null
                            && po.getApprovedBy().intValue() == user.getUserId();
                    if (po != null && "submitted".equals(po.getStatus()) && isStaffOwner) {
                        List<Map<String, Object>> products = buildProductsFromPurchaseOrder(purchaseOrderId);
                        if (!products.isEmpty()) {
                            request.setAttribute("productsJson", gson.toJson(products));
                        }
                        request.setAttribute("issueType", "sale");
                        request.setAttribute("purchaseOrderId", purchaseOrderId);
                        request.setAttribute("purchaseOrderCode", po.getPoCode());
                        if (po.getExpectedDeliveryDate() != null) {
                            request.setAttribute("poExpectedDate", po.getExpectedDeliveryDate().toString());
                        }
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
    }

    private List<Map<String, Object>> buildProductsFromPurchaseOrder(int purchaseOrderId) {
        List<Map<String, Object>> out = new java.util.ArrayList<>();
        List<PurchaseOrderDetail> details = purchaseOrderDetailDAO.getDetailsByPurchaseOrderId(purchaseOrderId);
        List<Integer> variantIds = new java.util.ArrayList<>();
        for (PurchaseOrderDetail d : details) {
            if (d != null && d.getVariantId() > 0 && !variantIds.contains(d.getVariantId())) {
                variantIds.add(d.getVariantId());
            }
        }
        var infoMap = goodsIssueDAO.getVariantInfoByIds(variantIds);
        int id = 1;
        for (PurchaseOrderDetail d : details) {
            if (d == null)
                continue;
            GoodsIssueDAO.VariantInfo vi = infoMap.get(d.getVariantId());
            if (vi == null)
                continue;
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", id++);
            m.put("variantId", vi.getVariantId());
            m.put("code", vi.getSku());
            m.put("name", vi.getProductName());
            m.put("unit", vi.getUnitName());
            m.put("stock", vi.getStock());
            m.put("serials", new java.util.ArrayList<>());
            m.put("quantity", d.getQuantity());
            out.add(m);
        }
        return out;
    }

    private List<Map<String, Object>> buildProductsFromReturnOrder(int returnOrderId) {
        List<Map<String, Object>> out = new ArrayList<>();
        List<ReturnOrderDetail> details = returnOrderDAO.getReturnOrderDetailsByOrderId(returnOrderId);
        List<Integer> variantIds = new ArrayList<>();
        for (ReturnOrderDetail d : details) {
            if (d != null && d.getVariantId() > 0 && !variantIds.contains(d.getVariantId())) {
                variantIds.add(d.getVariantId());
            }
        }
        var infoMap = goodsIssueDAO.getVariantInfoByIds(variantIds);
        int id = 1;
        for (ReturnOrderDetail d : details) {
            if (d == null)
                continue;
            GoodsIssueDAO.VariantInfo vi = infoMap.get(d.getVariantId());
            if (vi == null)
                continue;
            List<String> serialNumbers = new ArrayList<>();
            if (d.getSerials() != null) {
                for (ReturnOrderSerial s : d.getSerials()) {
                    if (s != null && s.getSerialNumber() != null && !s.getSerialNumber().trim().isEmpty()) {
                        serialNumbers.add(s.getSerialNumber().trim());
                    }
                }
            }
            List<String> inStockSerials = goodsIssueDAO.filterInStockSerialNumbers(vi.getVariantId(), serialNumbers);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id++);
            m.put("variantId", vi.getVariantId());
            m.put("code", vi.getSku());
            m.put("name", vi.getProductName());
            m.put("unit", vi.getUnitName());
            m.put("stock", vi.getStock());
            m.put("serials", inStockSerials);
            m.put("quantity", inStockSerials.size());
            out.add(m);
        }
        return out;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("searchProduct".equals(action)) {
            String keyword = request.getParameter("search");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(goodsIssueDAO.searchProductsForIssueJson(keyword));
            return;
        }

        if ("getSerials".equals(action)) {
            String variantIdStr = request.getParameter("variantId");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                int variantId = Integer.parseInt(variantIdStr);
                response.getWriter().write(goodsIssueDAO.getAvailableSerialsJson(variantId));
            } catch (NumberFormatException e) {
                response.getWriter().write("[]");
            }
            return;
        }

        if ("searchProductBySKU".equals(action)) {
            String sku = request.getParameter("sku");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(goodsIssueDAO.searchProductBySKUJson(sku));
            return;
        }

        if ("listSourceOrders".equals(action)) {
            handleListSourceOrders(request, response);
            return;
        }

        if ("loadFromSource".equals(action)) {
            handleLoadFromSource(request, response);
            return;
        }

        handleCreateIssue(request, response);
    }

    private void handleListSourceOrders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sourceType = request.getParameter("sourceType");
        String keyword = request.getParameter("search");
        List<Map<String, Object>> result = new ArrayList<>();

        String pattern = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : "%";

        if ("purchase_order".equals(sourceType)) {
            // Exclude completed flows (received) and cancelled orders from selectable list
            String sql = """
                    SELECT purchase_order_id, po_code, status
                    FROM purchase_orders
                    WHERE po_code LIKE ?
                      AND COALESCE(status, '') NOT IN ('received', 'completed', 'cancelled')
                    ORDER BY created_at DESC
                    LIMIT 20
                    """;
            try (var ps = purchaseOrderDAO.getConnection().prepareStatement(sql)) {
                ps.setString(1, pattern);
                var rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getInt("purchase_order_id"));
                    m.put("code", rs.getString("po_code"));
                    String st = rs.getString("status");
                    m.put("sub", st != null ? ("Trạng thái: " + st) : "");
                    result.add(m);
                }
            } catch (Exception ignored) {
            }
        } else if ("return_order".equals(sourceType)) {
            // Exclude completed/cancelled return orders from selectable list
            String sql = """
                    SELECT return_order_id, return_code, status
                    FROM return_orders
                    WHERE return_code LIKE ?
                      AND COALESCE(status, '') NOT IN ('completed', 'cancelled')
                    ORDER BY created_at DESC
                    LIMIT 20
                    """;
            try (var ps = returnOrderDAO.getConnection().prepareStatement(sql)) {
                ps.setString(1, pattern);
                var rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getInt("return_order_id"));
                    m.put("code", rs.getString("return_code"));
                    String st = rs.getString("status");
                    m.put("sub", st != null ? ("Trạng thái: " + st) : "");
                    result.add(m);
                }
            } catch (Exception ignored) {
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(result));
    }

    private void handleLoadFromSource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sourceType = request.getParameter("sourceType");
        String sourceIdRaw = request.getParameter("sourceId");

        int sourceId;
        try {
            sourceId = Integer.parseInt(sourceIdRaw);
        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().write("Invalid sourceId");
            return;
        }

        List<Map<String, Object>> out = new ArrayList<>();

        if ("purchase_order".equals(sourceType)) {
            List<PurchaseOrderDetail> details = purchaseOrderDetailDAO.getDetailsByPurchaseOrderId(sourceId);
            List<Integer> variantIds = new ArrayList<>();
            for (PurchaseOrderDetail d : details) {
                if (d != null && d.getVariantId() > 0 && !variantIds.contains(d.getVariantId())) {
                    variantIds.add(d.getVariantId());
                }
            }

            var infoMap = goodsIssueDAO.getVariantInfoByIds(variantIds);
            for (PurchaseOrderDetail d : details) {
                if (d == null)
                    continue;
                GoodsIssueDAO.VariantInfo vi = infoMap.get(d.getVariantId());
                if (vi == null)
                    continue;

                Map<String, Object> m = new LinkedHashMap<>();
                m.put("variantId", vi.getVariantId());
                m.put("code", vi.getSku());
                m.put("name", vi.getProductName());
                m.put("unit", vi.getUnitName());
                m.put("stock", vi.getStock());
                m.put("quantity", 0);
                m.put("serials", new ArrayList<>());
                out.add(m);
            }
        } else if ("return_order".equals(sourceType)) {
            List<ReturnOrderDetail> details = returnOrderDAO.getReturnOrderDetailsByOrderId(sourceId);
            List<Integer> variantIds = new ArrayList<>();
            for (ReturnOrderDetail d : details) {
                if (d != null && d.getVariantId() > 0 && !variantIds.contains(d.getVariantId())) {
                    variantIds.add(d.getVariantId());
                }
            }

            var infoMap = goodsIssueDAO.getVariantInfoByIds(variantIds);
            for (ReturnOrderDetail d : details) {
                if (d == null)
                    continue;
                GoodsIssueDAO.VariantInfo vi = infoMap.get(d.getVariantId());
                if (vi == null)
                    continue;

                List<String> serialNumbers = new ArrayList<>();
                if (d.getSerials() != null) {
                    for (ReturnOrderSerial s : d.getSerials()) {
                        if (s != null && s.getSerialNumber() != null && !s.getSerialNumber().trim().isEmpty()) {
                            serialNumbers.add(s.getSerialNumber().trim());
                        }
                    }
                }

                List<String> inStockSerials = goodsIssueDAO.filterInStockSerialNumbers(vi.getVariantId(),
                        serialNumbers);

                Map<String, Object> m = new LinkedHashMap<>();
                m.put("variantId", vi.getVariantId());
                m.put("code", vi.getSku());
                m.put("name", vi.getProductName());
                m.put("unit", vi.getUnitName());
                m.put("stock", vi.getStock());
                m.put("serials", inStockSerials);
                m.put("quantity", inStockSerials.size());
                out.add(m);
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(out));
    }

    private void handleCreateIssue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String issueCode = request.getParameter("issueCode");
        String issueType = normalizeIssueType(request.getParameter("issueType"));
        String issueDate = request.getParameter("issueDate");
        String receiverName = request.getParameter("receiverName");
        String notes = request.getParameter("notes");
        String productsJson = request.getParameter("products");

        boolean hasErrors = false;

        if (issueCode == null || issueCode.trim().isEmpty()) {
            request.setAttribute("issueCodeError", "Vui lòng nhập mã phiếu xuất");
            hasErrors = true;
        } else if (goodsIssueDAO.issueCodeExists(issueCode.trim())) {
            request.setAttribute("issueCodeError", "Mã phiếu xuất đã tồn tại");
            hasErrors = true;
        }

        if (issueDate == null || issueDate.trim().isEmpty()) {
            request.setAttribute("issueDateError", "Vui lòng chọn ngày xuất");
            hasErrors = true;
        }

        if (receiverName == null || receiverName.trim().isEmpty()) {
            request.setAttribute("receiverNameError", "Vui lòng nhập tên người nhận");
            hasErrors = true;
        }

        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("productsError", "Vui lòng thêm ít nhất một sản phẩm vào phiếu xuất");
            hasErrors = true;
        }

        if (hasErrors) {
            request.setAttribute("productsJson", productsJson);
            String roIdParam = request.getParameter("returnOrderId");
            if (roIdParam != null && !roIdParam.trim().isEmpty()) {
                request.setAttribute("returnOrderId", roIdParam.trim());
            }
            String poIdParam = request.getParameter("purchaseOrderId");
            if (poIdParam != null && !poIdParam.trim().isEmpty()) {
                request.setAttribute("purchaseOrderId", poIdParam.trim());
            }
            request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
            return;
        }

        try {
            List<GoodsIssueDetail> details = parseProductsJson(productsJson);
            User user = (User) request.getSession().getAttribute("user");
            Integer returnOrderId = null;
            String roIdStr = request.getParameter("returnOrderId");
            if (roIdStr != null && !roIdStr.trim().isEmpty()) {
                try {
                    returnOrderId = Integer.parseInt(roIdStr.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            Integer purchaseOrderId = null;
            String poIdStr = request.getParameter("purchaseOrderId");
            if (poIdStr != null && !poIdStr.trim().isEmpty()) {
                try {
                    purchaseOrderId = Integer.parseInt(poIdStr.trim());
                } catch (NumberFormatException ignored) {
                }
            }

            String notesWithPo = notes != null ? notes : "";
            if (purchaseOrderId != null) {
                notesWithPo = notesWithPo + "\n[PO_ID:" + purchaseOrderId + "]";
            }

            boolean success = goodsIssueDAO.createGoodsIssue(
                    issueCode.trim(), issueType, issueDate,
                    receiverName.trim(), notesWithPo.trim(),
                    user.getUserId(), details, returnOrderId);

            if (success) {
                request.getSession().setAttribute("successMessage", "Tạo phiếu xuất kho thành công!");
                response.sendRedirect(request.getContextPath() + "/goods-issue-list");
            } else {
                request.setAttribute("generalError", "Có lỗi xảy ra khi tạo phiếu xuất. Vui lòng thử lại!");
                request.setAttribute("productsJson", productsJson);
                if (returnOrderId != null) request.setAttribute("returnOrderId", returnOrderId);
                if (purchaseOrderId != null) request.setAttribute("purchaseOrderId", purchaseOrderId);
                request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            request.setAttribute("generalError", "Có lỗi xảy ra: " + ex.getMessage());
            request.setAttribute("productsJson", request.getParameter("products"));
            String roIdParam = request.getParameter("returnOrderId");
            if (roIdParam != null && !roIdParam.trim().isEmpty()) {
                request.setAttribute("returnOrderId", roIdParam.trim());
            }
            String poIdParam = request.getParameter("purchaseOrderId");
            if (poIdParam != null && !poIdParam.trim().isEmpty()) {
                request.setAttribute("purchaseOrderId", poIdParam.trim());
            }
            request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
        }
    }

    private List<GoodsIssueDetail> parseProductsJson(String json) {
        List<GoodsIssueDetail> details = new ArrayList<>();
        if (json == null || json.trim().isEmpty())
            return details;

        Type listType = new TypeToken<List<java.util.Map<String, Object>>>() {
        }.getType();
        List<java.util.Map<String, Object>> items = gson.fromJson(json, listType);
        if (items == null)
            return details;

        for (java.util.Map<String, Object> item : items) {
            if (item == null)
                continue;
            GoodsIssueDetail d = new GoodsIssueDetail();
            d.setVariantId(((Number) item.get("variantId")).intValue());
            d.setQuantity(((Number) item.get("quantity")).intValue());

            Object serialsObj = item.get("serials");
            if (serialsObj instanceof List) {
                List<String> serials = new ArrayList<>();
                for (Object s : (List<?>) serialsObj) {
                    if (s != null)
                        serials.add(s.toString());
                }
                d.setSerials(serials);
            }
            details.add(d);
        }
        return details;
    }

    private String normalizeIssueType(String issueType) {
        if (issueType == null) {
            return "sale";
        }
        String normalized = issueType.trim().toLowerCase();
        if ("return_supplier".equals(normalized) || "other".equals(normalized)) {
            return normalized;
        }
        return "sale";
    }
}
