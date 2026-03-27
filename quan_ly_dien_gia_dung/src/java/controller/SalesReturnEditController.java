package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.SalesReturnDAO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SalesReturn;
import model.SalesReturnDetail;
import model.User;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "SalesReturnEditController", urlPatterns = {"/sales-return-edit"})
public class SalesReturnEditController extends HttpServlet {

    private final SalesReturnDAO dao = new SalesReturnDAO();
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parsePositiveInt(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/sales-return-list");
            return;
        }

        SalesReturn sr = dao.getSalesReturnById(id);
        if (sr == null) {
            response.sendRedirect(request.getContextPath() + "/sales-return-list");
            return;
        }

        User user = (User) request.getSession().getAttribute("user");
        boolean roleViewOnly = (user == null || user.getRole() == null || user.getRole().getRoleId() != 4);

        List<SalesReturnDetail> details = dao.getSalesReturnDetailsByOrderId(id);
        request.setAttribute("salesReturn", sr);
        request.setAttribute("salesReturnDetails", details);
        request.setAttribute("editDataJson", buildEditDataJson(details));
        request.setAttribute("roleViewOnly", roleViewOnly);
        request.getRequestDispatcher("/view/common/sales_return_edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        Integer id = parsePositiveInt(request.getParameter("salesReturnId"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/sales-return-list");
            return;
        }

        SalesReturn existing = dao.getSalesReturnById(id);
        if (existing == null) {
            response.sendRedirect(request.getContextPath() + "/sales-return-list");
            return;
        }

        User user = (User) request.getSession().getAttribute("user");
        boolean roleViewOnly = (user == null || user.getRole() == null || user.getRole().getRoleId() != 4);
        if (roleViewOnly) {
            response.sendRedirect(request.getContextPath() + "/sales-return-edit?id=" + id + "&view=1");
            return;
        }

        String status = existing.getStatus() != null ? existing.getStatus().trim().toLowerCase() : "";
        boolean pending = "pending".equals(status);
        boolean refundOnly = "processing".equals(status) || "completed".equals(status);
        boolean viewOnly = "cancelled".equals(status);

        if ("searchProduct".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (!pending) {
                response.getWriter().write("[]");
                return;
            }
            String keyword = request.getParameter("search");
            int saleUserId = existing.getCreatedBy() != null ? existing.getCreatedBy() : 0;
            response.getWriter().write(dao.searchProductsForSalesReturnJson(keyword, saleUserId));
            return;
        }

        if (viewOnly) {
            request.setAttribute("generalError", "Đơn đã hủy, chỉ được xem.");
            forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            return;
        }

        String refundStatus = request.getParameter("refundStatus");
        if (refundStatus == null || refundStatus.trim().isEmpty()) refundStatus = "not_refunded";
        refundStatus = refundStatus.trim();
        if (!"not_refunded".equals(refundStatus) && !"refunded".equals(refundStatus)) {
            request.setAttribute("generalError", "Trạng thái hoàn tiền không hợp lệ.");
            forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            return;
        }

        if (refundOnly) {
            boolean ok = dao.updateSalesReturnRefundStatusOnly(id, refundStatus);
            if (ok) {
                response.sendRedirect(request.getContextPath() + "/sales-return-list");
            } else {
                request.setAttribute("generalError", "Không thể cập nhật trạng thái hoàn tiền.");
                forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            }
            return;
        }

        
        String srCode = request.getParameter("srCode");
        String returnDateStr = request.getParameter("returnDate");
        String description = request.getParameter("description");
        String productsJson = request.getParameter("products");

        if (srCode == null || srCode.trim().isEmpty()) {
            request.setAttribute("generalError", "Vui lòng nhập mã phiếu trả hàng.");
            forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            return;
        }
        if (returnDateStr == null || returnDateStr.trim().isEmpty()) {
            request.setAttribute("generalError", "Vui lòng chọn ngày trả.");
            forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            return;
        }
        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("generalError", "Vui lòng thêm ít nhất một sản phẩm.");
            forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            return;
        }

        try {
            Date returnDate = Date.valueOf(returnDateStr.trim());
            List<SalesReturnDetail> details = parseProductsJson(productsJson);
            if (details.isEmpty()) {
                request.setAttribute("generalError", "Không có sản phẩm hợp lệ để cập nhật.");
                forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
                return;
            }

            int createdBy = existing.getCreatedBy() != null ? existing.getCreatedBy() : 0;
            boolean success = dao.updateSalesReturn(
                    id, srCode.trim(), returnDate, description, refundStatus, createdBy, details);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/sales-return-list");
            } else {
                request.setAttribute("generalError", "Có lỗi khi cập nhật đơn trả hàng. Vui lòng kiểm tra lại.");
                forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
            }
        } catch (Exception ex) {
            request.setAttribute("generalError", "Lỗi: " + ex.getMessage());
            forwardEdit(request, response, existing, dao.getSalesReturnDetailsByOrderId(id));
        }
    }

    private void forwardEdit(HttpServletRequest request, HttpServletResponse response,
            SalesReturn sr, List<SalesReturnDetail> details) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        boolean roleViewOnly = (user == null || user.getRole() == null || user.getRole().getRoleId() != 4);
        request.setAttribute("salesReturn", sr);
        request.setAttribute("salesReturnDetails", details);
        request.setAttribute("editDataJson", buildEditDataJson(details));
        request.setAttribute("roleViewOnly", roleViewOnly);
        request.getRequestDispatcher("/view/common/sales_return_edit.jsp").forward(request, response);
    }

    private Integer parsePositiveInt(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            int n = Integer.parseInt(s.trim());
            return n > 0 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<SalesReturnDetail> parseProductsJson(String json) {
        List<SalesReturnDetail> out = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return out;

        Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        List<Map<String, Object>> items = gson.fromJson(json, listType);
        if (items == null) return out;

        for (Map<String, Object> item : items) {
            if (item == null) continue;
            Object variantIdObj = item.get("variantId");
            Object qtyObj = item.get("quantity");
            Object originalPriceObj = item.get("originalPrice");
            if (variantIdObj == null || qtyObj == null) continue;

            SalesReturnDetail d = new SalesReturnDetail();
            d.setVariantId(((Number) variantIdObj).intValue());
            d.setQuantity(((Number) qtyObj).intValue());
            java.math.BigDecimal originalPrice = originalPriceObj != null
                    ? new java.math.BigDecimal(originalPriceObj.toString())
                    : java.math.BigDecimal.ZERO;
            d.setOriginalPrice(originalPrice);
            d.setTotalRefund(originalPrice.multiply(java.math.BigDecimal.valueOf(d.getQuantity())));
            if (d.getVariantId() > 0 && d.getQuantity() > 0) out.add(d);
        }
        return out;
    }

    private String buildEditDataJson(List<SalesReturnDetail> details) {
        if (details == null || details.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < details.size(); i++) {
            SalesReturnDetail d = details.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"variantId\":").append(d.getVariantId()).append(",");
            sb.append("\"code\":\"").append(escapeJson(d.getVariantSku())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(d.getProductName())).append("\",");
            sb.append("\"unit\":\"").append(escapeJson(d.getUnitName())).append("\",");
            sb.append("\"originalPrice\":").append(d.getOriginalPrice() != null ? d.getOriginalPrice() : 0).append(",");
            sb.append("\"quantity\":").append(d.getQuantity());
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}

