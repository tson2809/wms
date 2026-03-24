package controller.Manager;

import dal.ReturnOrderDAO;
import dal.SupplierDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ReturnOrderDetail;
import model.ReturnOrderSerial;
import model.Supplier;
import model.User;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "ReturnOrderAddController", urlPatterns = {"/return-add"})
public class ReturnOrderAddController extends HttpServlet {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ReturnOrderDAO returnOrderDAO = new ReturnOrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("suppliers", supplierDAO.getActiveSuppliers());
        request.getRequestDispatcher("/view/manager/return_add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("searchProduct".equals(action)) {
            handleSearchProduct(request, response);
        } else if ("getSerials".equals(action)) {
            handleGetSerials(request, response);
        } else if ("checkSerial".equals(action)) {
            handleCheckSerial(request, response);
        } else {
            handleCreateReturnOrder(request, response);
        }
    }

    private void handleSearchProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String search = request.getParameter("search");
        String supplierIdStr = request.getParameter("supplierId");
        Integer supplierId = parsePositiveInt(supplierIdStr);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (supplierId == null || supplierId <= 0) {
            response.getWriter().write("[]");
            return;
        }

        String json = returnOrderDAO.searchProductsForReturnJson(
                search != null ? search.trim() : "", supplierId);
        response.getWriter().write(json);
    }

    private void handleGetSerials(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String variantIdStr = request.getParameter("variantId");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer variantId = parsePositiveInt(variantIdStr);
        if (variantId == null) {
            response.getWriter().write("[]");
            return;
        }

        response.getWriter().write(returnOrderDAO.getAvailableSerialsJson(variantId));
    }

    private void handleCheckSerial(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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

    private void handleCreateReturnOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        } else if (returnOrderDAO.returnCodeExists(safeReturnCode)) {
            request.setAttribute("returnCodeError", "Mã phiếu trả đã tồn tại");
            hasErrors = true;
        }

        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("productsError", "Vui lòng thêm ít nhất một sản phẩm có serial hợp lệ");
            hasErrors = true;
        }

        if (hasErrors) {
            forwardWithError(request, response, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
            return;
        }

        try {
            int supplierId = Integer.parseInt(safeSupplier);
            Date returnDate = Date.valueOf(safeReturnDate);
            if (returnDate.toLocalDate().isBefore(LocalDate.now())) {
                request.setAttribute("returnDateError", "Ngày trả không được nhỏ hơn ngày hiện tại");
                forwardWithError(request, response, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
                return;
            }

            List<ReturnOrderDetail> details = parseProductsJson(productsJson.trim());
            if (details.isEmpty()) {
                request.setAttribute("productsError",
                        "Không có sản phẩm nào với serial hợp lệ. Vui lòng dùng 'Load serial trong kho' hoặc nhập serial đúng có trong kho.");
                forwardWithError(request, response, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
                return;
            }

            BigDecimal totalRefund = BigDecimal.ZERO;
            for (ReturnOrderDetail d : details) {
                totalRefund = totalRefund.add(
                        d.getOriginalPrice().multiply(BigDecimal.valueOf(d.getQuantity())));
            }

            User currentUser = (User) request.getSession().getAttribute("user");
            int createdBy = currentUser != null ? currentUser.getUserId() : 0;

            boolean success = returnOrderDAO.createReturnOrder(
                    safeReturnCode, supplierId, returnDate, totalRefund, safeDescription, createdBy, details);

            if (success) {
                request.getSession().setAttribute("successMessage", "Tạo đơn trả hàng thành công!");
                response.sendRedirect(request.getContextPath() + "/return-order-list");
            } else {
                request.setAttribute("generalError", "Có lỗi khi tạo đơn trả hàng. Vui lòng thử lại.");
                forwardWithError(request, response, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("generalError", "Ngày trả không hợp lệ.");
            forwardWithError(request, response, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("generalError", "Lỗi: " + ex.getMessage());
            forwardWithError(request, response, safeSupplier, safeReturnDate, safeReturnCode, safeDescription);
        }
    }

    /**
     * Forward về form kèm giá trị đã nhập và thông báo lỗi.
     * Dùng request attribute (không dùng param vì param là từ request parameter).
     */
    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            String supplierIdValue, String returnDateValue, String returnCodeValue, String descriptionValue)
            throws ServletException, IOException {
        request.setAttribute("suppliers", supplierDAO.getActiveSuppliers());
        request.setAttribute("supplierIdValue", supplierIdValue != null ? supplierIdValue : "");
        request.setAttribute("returnDateValue", returnDateValue != null ? returnDateValue : "");
        request.setAttribute("returnCodeValue", returnCodeValue != null ? returnCodeValue : "");
        request.setAttribute("descriptionValue", descriptionValue != null ? descriptionValue : "");
        request.getRequestDispatcher("/view/manager/return_add.jsp").forward(request, response);
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

    /**
     * Parse JSON mảng sản phẩm từ frontend.
     * Mỗi object cần: variantId, quantity, originalPrice, serialIds[], serialNumbers[]
     */
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

    private static final Pattern INT_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[([^\\]\\[]*)\\]");

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
