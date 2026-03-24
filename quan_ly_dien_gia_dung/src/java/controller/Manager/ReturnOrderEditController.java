package controller.Manager;

import dal.ReturnOrderDAO;
import dal.SupplierDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ReturnOrder;
import model.ReturnOrderDetail;
import model.ReturnOrderSerial;
import model.Supplier;
import model.User;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "ReturnOrderEditController", urlPatterns = { "/return-edit" })
public class ReturnOrderEditController extends HttpServlet {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ReturnOrderDAO returnOrderDAO = new ReturnOrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        Integer id = parsePositiveInt(idStr);
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/return-order-list");
            return;
        }
        ReturnOrder order = returnOrderDAO.getReturnOrderById(id);
        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/return-order-list");
            return;
        }
        User user = (User) request.getSession().getAttribute("user");
        boolean staffViewOnly = (user != null && user.getRole() != null && user.getRole().getRoleId() == 3)
                || "1".equals(request.getParameter("view"));
        if (staffViewOnly) {
            request.setAttribute("viewOnly", true);
        }
        List<ReturnOrderDetail> details = returnOrderDAO.getReturnOrderDetailsByOrderId(id);
        request.setAttribute("returnOrder", order);
        request.setAttribute("returnOrderDetails", details);
        request.setAttribute("suppliers", supplierDAO.getActiveSuppliers());
        request.setAttribute("editDataJson", buildEditDataJson(details));
        request.getRequestDispatcher("/view/manager/return_edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getRole() != null && user.getRole().getRoleId() == 3) {
            response.sendRedirect(request.getContextPath() + "/return-order-list");
            return;
        }
        String action = request.getParameter("action");
        if ("searchProduct".equals(action)) {
            handleSearchProduct(request, response);
            return;
        }
        if ("getSerials".equals(action)) {
            handleGetSerials(request, response);
            return;
        }
        if ("checkSerial".equals(action)) {
            handleCheckSerial(request, response);
            return;
        }

        String returnOrderIdStr = request.getParameter("returnOrderId");
        Integer returnOrderId = parsePositiveInt(returnOrderIdStr);
        if (returnOrderId == null) {
            setErrorAndForward(request, response, returnOrderId, "Đơn không hợp lệ.");
            return;
        }
        ReturnOrder order = returnOrderDAO.getReturnOrderById(returnOrderId);
        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/return-order-list");
            return;
        }
        String status = order.getStatus() != null ? order.getStatus().trim().toLowerCase() : "";

        if ("completed".equals(status)) {
            String refundStatus = request.getParameter("refundStatus");
            if (refundStatus == null || refundStatus.trim().isEmpty()) {
                setErrorAndForward(request, response, returnOrderId, "Vui lòng chọn trạng thái hoàn tiền.");
                return;
            }
            if (!"not_refunded".equals(refundStatus.trim()) && !"refunded".equals(refundStatus.trim())) {
                setErrorAndForward(request, response, returnOrderId, "Trạng thái hoàn tiền không hợp lệ.");
                return;
            }
            boolean ok = returnOrderDAO.updateReturnOrderRefundStatusOnly(returnOrderId, refundStatus.trim());
            if (ok) {
                request.getSession().setAttribute("successMessage", "Đã cập nhật trạng thái hoàn tiền.");
                response.sendRedirect(request.getContextPath() + "/return-order-list");
            } else {
                setErrorAndForward(request, response, returnOrderId, "Không thể cập nhật. Vui lòng thử lại.");
            }
            return;
        }

        if (!"pending".equals(status)) {
            setErrorAndForward(request, response, returnOrderId,
                    "Chỉ đơn trả hàng trạng thái Chờ xử lý mới được sửa đầy đủ; đơn Hoàn tất chỉ được cập nhật trạng thái hoàn tiền.");
            return;
        }

        String supplierIdStr = request.getParameter("supplierId");
        String returnDateStr = request.getParameter("returnDate");
        String returnCode = request.getParameter("returnCode");
        String description = request.getParameter("description");
        String productsJson = request.getParameter("products");

        String safeSupplier = supplierIdStr != null ? supplierIdStr.trim() : "";
        String safeReturnDate = returnDateStr != null ? returnDateStr.trim() : "";
        String safeReturnCode = returnCode != null ? returnCode.trim() : "";
        String safeDescription = description != null ? description.trim() : "";
        boolean hasErrors = false;
        if (safeSupplier.isEmpty()) {
            request.setAttribute("supplierIdError", "Vui lòng chọn nhà cung cấp");
            hasErrors = true;
        }
        if (safeReturnDate.isEmpty()) {
            request.setAttribute("returnDateError", "Vui lòng chọn ngày trả");
            hasErrors = true;
        }
        if (safeReturnCode.isEmpty()) {
            request.setAttribute("returnCodeError", "Vui lòng nhập mã phiếu trả");
            hasErrors = true;
        } else if (returnOrderDAO.returnCodeExistsExcludingId(safeReturnCode, returnOrderId)) {
            request.setAttribute("returnCodeError", "Mã phiếu trả đã tồn tại");
            hasErrors = true;
        }
        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("productsError", "Vui lòng thêm ít nhất một sản phẩm có serial hợp lệ");
            hasErrors = true;
        }
        if (hasErrors) {
            forwardEditWithErrors(request, response, returnOrderId, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
            return;
        }
        try {
            int supplierId = Integer.parseInt(safeSupplier);
            Date returnDate = Date.valueOf(safeReturnDate);
            List<ReturnOrderDetail> details = parseProductsJson(productsJson.trim());
            if (details.isEmpty()) {
                request.setAttribute("productsError", "Không có sản phẩm nào với serial hợp lệ.");
                forwardEditWithErrors(request, response, returnOrderId, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
                return;
            }
            BigDecimal totalRefund = BigDecimal.ZERO;
            for (ReturnOrderDetail d : details) {
                totalRefund = totalRefund.add(
                        d.getOriginalPrice().multiply(BigDecimal.valueOf(d.getQuantity())));
            }
            String refundStatus = request.getParameter("refundStatus");
            if (refundStatus == null || refundStatus.trim().isEmpty()) refundStatus = "not_refunded";

            boolean success = returnOrderDAO.updateReturnOrder(
                    returnOrderId, safeReturnCode, supplierId, returnDate, totalRefund, refundStatus, safeDescription, details);
            if (success) {
                request.getSession().setAttribute("successMessage", "Cập nhật đơn trả hàng thành công.");
                response.sendRedirect(request.getContextPath() + "/return-order-list");
            } else {
                request.setAttribute("generalError", "Không thể cập nhật. Vui lòng thử lại.");
                forwardEditWithErrors(request, response, returnOrderId, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("generalError", "Lỗi: " + ex.getMessage());
            forwardEditWithErrors(request, response, returnOrderId, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
        }
    }

    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response, Integer returnOrderId,
            String generalError) throws ServletException, IOException {
        request.setAttribute("generalError", generalError);
        if (returnOrderId != null) {
            ReturnOrder order = returnOrderDAO.getReturnOrderById(returnOrderId);
            if (order != null) {
                List<ReturnOrderDetail> details = returnOrderDAO.getReturnOrderDetailsByOrderId(returnOrderId);
                request.setAttribute("returnOrder", order);
                request.setAttribute("returnOrderDetails", details);
                request.setAttribute("editDataJson", buildEditDataJson(details));
            }
        }
        request.setAttribute("suppliers", supplierDAO.getActiveSuppliers());
        request.getRequestDispatcher("/view/manager/return_edit.jsp").forward(request, response);
    }

    private void forwardEditWithErrors(HttpServletRequest request, HttpServletResponse response, int returnOrderId,
            String supplierIdValue, String returnDateValue, String returnCodeValue, String descriptionValue)
            throws ServletException, IOException {
        ReturnOrder order = returnOrderDAO.getReturnOrderById(returnOrderId);
        if (order != null) {
            request.setAttribute("returnOrder", order);
            request.setAttribute("returnOrderDetails", returnOrderDAO.getReturnOrderDetailsByOrderId(returnOrderId));
        }
        request.setAttribute("supplierIdValue", supplierIdValue);
        request.setAttribute("returnDateValue", returnDateValue);
        request.setAttribute("returnCodeValue", returnCodeValue);
        request.setAttribute("descriptionValue", descriptionValue);
        request.setAttribute("suppliers", supplierDAO.getActiveSuppliers());
        String productsParam = request.getParameter("products");
        request.setAttribute("editDataJson", (productsParam != null && !productsParam.trim().isEmpty()) ? productsParam : (order != null ? buildEditDataJson(returnOrderDAO.getReturnOrderDetailsByOrderId(returnOrderId)) : "[]"));
        request.getRequestDispatcher("/view/manager/return_edit.jsp").forward(request, response);
    }

    private String buildEditDataJson(List<ReturnOrderDetail> details) {
        if (details == null || details.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < details.size(); i++) {
            ReturnOrderDetail d = details.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"variantId\":").append(d.getVariantId()).append(",");
            sb.append("\"code\":\"").append(escapeJson(d.getVariantSku())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(d.getProductName())).append("\",");
            sb.append("\"unit\":\"").append(escapeJson(d.getUnitName())).append("\",");
            sb.append("\"originalPrice\":").append(d.getOriginalPrice() != null ? d.getOriginalPrice() : 0).append(",");
            sb.append("\"quantity\":").append(d.getQuantity()).append(",");
            sb.append("\"serialIds\":[");
            if (d.getSerials() != null) {
                for (int j = 0; j < d.getSerials().size(); j++) {
                    if (j > 0) sb.append(",");
                    sb.append(d.getSerials().get(j).getSerialId());
                }
            }
            sb.append("],\"serialNumbers\":[");
            if (d.getSerials() != null) {
                for (int j = 0; j < d.getSerials().size(); j++) {
                    if (j > 0) sb.append(",");
                    sb.append("\"").append(escapeJson(d.getSerials().get(j).getSerialNumber())).append("\"");
                }
            }
            sb.append("]}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private void handleSearchProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String search = request.getParameter("search");
        String supplierIdStr = request.getParameter("supplierId");
        Integer supplierId = parsePositiveInt(supplierIdStr);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (supplierId == null || supplierId <= 0) {
            response.getWriter().write("[]");
            return;
        }
        String json = returnOrderDAO.searchProductsForReturnJson(search != null ? search.trim() : "", supplierId);
        response.getWriter().write(json);
    }

    private void handleGetSerials(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer variantId = parsePositiveInt(request.getParameter("variantId"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (variantId == null) {
            response.getWriter().write("[]");
            return;
        }
        response.getWriter().write(returnOrderDAO.getAvailableSerialsJson(variantId));
    }

    private void handleCheckSerial(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String variantIdStr = request.getParameter("variantId");
        String serialNumber = request.getParameter("serialNumber");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Integer variantId = parsePositiveInt(variantIdStr);
        if (variantId == null || serialNumber == null || serialNumber.trim().isEmpty()) {
            response.getWriter().write("{\"valid\":false,\"message\":\"Dữ liệu không hợp lệ\"}");
            return;
        }
        Integer serialId = returnOrderDAO.getSerialIdByNumberAndVariant(variantId, serialNumber.trim());
        if (serialId != null) {
            response.getWriter().write("{\"valid\":true,\"serialId\":" + serialId + "}");
        } else {
            response.getWriter().write("{\"valid\":false,\"message\":\"Serial không tồn tại hoặc không thuộc sản phẩm này trong kho\"}");
        }
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

    private static final Pattern INT_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[([^\\]\\[]*)\\]");

    private List<ReturnOrderDetail> parseProductsJson(String json) {
        List<ReturnOrderDetail> details = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return details;
        List<String> objects = extractJsonObjects(json);
        for (String obj : objects) {
            int variantId = extractInt(obj, "variantId");
            int quantity = extractInt(obj, "quantity");
            BigDecimal originalPrice = extractBigDecimal(obj, "originalPrice");
            List<Integer> serialIds = extractIntArray(obj, "serialIds");
            List<String> serialNumbers = extractStringArray(obj, "serialNumbers");
            for (String sn : serialNumbers) {
                if (sn == null || sn.trim().isEmpty()) continue;
                Integer sid = returnOrderDAO.getSerialIdByNumberAndVariant(variantId, sn.trim());
                if (sid != null && !serialIds.contains(sid)) serialIds.add(sid);
            }
            if (originalPrice == null) originalPrice = BigDecimal.ZERO;
            if (variantId > 0 && quantity > 0 && !serialIds.isEmpty()) {
                ReturnOrderDetail detail = new ReturnOrderDetail();
                detail.setVariantId(variantId);
                detail.setQuantity(quantity);
                detail.setOriginalPrice(originalPrice);
                List<ReturnOrderSerial> serials = new ArrayList<>();
                for (int sid : serialIds) {
                    ReturnOrderSerial rs = new ReturnOrderSerial();
                    rs.setSerialId(sid);
                    serials.add(rs);
                }
                detail.setSerials(serials);
                details.add(detail);
            }
        }
        return details;
    }

    private List<String> extractJsonObjects(String json) {
        List<String> result = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') {
                i = skipJsonString(json, i);
                continue;
            }
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    result.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    private int skipJsonString(String json, int start) {
        int i = start + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) i += 2;
            else if (c == '"') return i;
            else i++;
        }
        return i;
    }

    private int extractInt(String json, String key) {
        Matcher m = INT_PATTERN.matcher(json);
        while (m.find()) {
            if (key.equals(m.group(1))) {
                try {
                    return (int) Double.parseDouble(m.group(2).trim());
                } catch (NumberFormatException ignored) { }
            }
        }
        return 0;
    }

    private BigDecimal extractBigDecimal(String json, String key) {
        Matcher m = INT_PATTERN.matcher(json);
        while (m.find()) {
            if (key.equals(m.group(1))) {
                try {
                    return new BigDecimal(m.group(2).trim());
                } catch (NumberFormatException ignored) { }
            }
        }
        return null;
    }

    private List<Integer> extractIntArray(String json, String key) {
        List<Integer> list = new ArrayList<>();
        Matcher m = ARRAY_PATTERN.matcher(json);
        while (m.find()) {
            if (key.equals(m.group(1))) {
                String content = m.group(2).trim();
                if (!content.isEmpty()) {
                    for (String s : content.split(",")) {
                        try {
                            String v = s.trim();
                            if (v.isEmpty()) continue;
                            int n = (int) Double.parseDouble(v);
                            if (n > 0) list.add(n);
                        } catch (NumberFormatException ignored) { }
                    }
                }
                break;
            }
        }
        return list;
    }

    private List<String> extractStringArray(String json, String key) {
        List<String> list = new ArrayList<>();
        Matcher m = ARRAY_PATTERN.matcher(json);
        while (m.find()) {
            if (key.equals(m.group(1))) {
                String content = m.group(2).trim();
                if (!content.isEmpty()) {
                    for (String s : content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
                        String val = s.trim().replaceAll("^\"|\"$", "").trim();
                        if (!val.isEmpty()) list.add(val);
                    }
                }
                break;
            }
        }
        return list;
    }
}
