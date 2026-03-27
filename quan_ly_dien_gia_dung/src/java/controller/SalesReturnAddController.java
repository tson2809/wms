package controller;

import com.google.gson.Gson;
import dal.SalesReturnDAO;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SalesReturnDetail;
import model.User;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "SalesReturnAddController", urlPatterns = {"/sales-return-add"})
public class SalesReturnAddController extends HttpServlet {

    private final SalesReturnDAO dao = new SalesReturnDAO();
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("searchProduct".equals(action)) {
            String keyword = request.getParameter("search");
            User user = (User) request.getSession().getAttribute("user");
            int saleUserId = user != null ? user.getUserId() : 0;
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(dao.searchProductsForSalesReturnJson(keyword, saleUserId));
            return;
        }

        String srCode = request.getParameter("srCode");
        String returnDateStr = request.getParameter("returnDate");
        String description = request.getParameter("description");
        String productsJson = request.getParameter("products");

        request.setAttribute("generalError", null);
        request.setAttribute("srCodeError", null);

        if (srCode == null || srCode.trim().isEmpty()) {
            request.setAttribute("generalError", "Vui lòng nhập mã phiếu trả hàng.");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }
        if (dao.srCodeExists(srCode.trim())) {
            request.setAttribute("srCodeError", "mã phiếu trả bị trùng");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }
        if (returnDateStr == null || returnDateStr.trim().isEmpty()) {
            request.setAttribute("generalError", "Vui lòng chọn ngày trả.");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }
        if (productsJson == null || productsJson.trim().isEmpty() || "[]".equals(productsJson.trim())) {
            request.setAttribute("generalError", "Vui lòng thêm ít nhất một sản phẩm.");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Date returnDate;
        try {
            returnDate = Date.valueOf(returnDateStr.trim());
        } catch (IllegalArgumentException ex) {
            request.setAttribute("generalError", "Ngày trả không hợp lệ.");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }

        if (returnDate.toLocalDate().isBefore(LocalDate.now())) {
            request.setAttribute("generalError", "Ngày trả không được nhỏ hơn ngày hiện tại.");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }

        if (!productsJson.trim().startsWith("[")) {
            request.setAttribute("generalError", "Dữ liệu sản phẩm không hợp lệ.");
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            return;
        }

        try {
            List<SalesReturnDetail> details = parseProductsJson(productsJson);
            if (details.isEmpty()) {
                request.setAttribute("generalError", "Không có sản phẩm hợp lệ để tạo đơn.");
                request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
                return;
            }

            int createdBy = user.getUserId();
            boolean success = dao.createSalesReturn(srCode.trim(), returnDate, description, createdBy, details);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/sales-return-list");
            } else {
                request.setAttribute("generalError", "Có lỗi khi tạo đơn trả hàng. Vui lòng kiểm tra lại.");
                request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            request.setAttribute("generalError", "Lỗi: " + ex.getMessage());
            request.getRequestDispatcher("/view/sale/sales_return_add.jsp").forward(request, response);
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
            SalesReturnDetail d = new SalesReturnDetail();

            Object variantIdObj = item.get("variantId");
            Object qtyObj = item.get("quantity");
            Object originalPriceObj = item.get("originalPrice");

            if (variantIdObj == null || qtyObj == null) continue;

            int variantId = ((Number) variantIdObj).intValue();
            int quantity = ((Number) qtyObj).intValue();

            java.math.BigDecimal originalPrice = originalPriceObj != null
                    ? new java.math.BigDecimal(originalPriceObj.toString())
                    : java.math.BigDecimal.ZERO;

            d.setVariantId(variantId);
            d.setQuantity(quantity);
            d.setOriginalPrice(originalPrice);
            d.setTotalRefund(originalPrice.multiply(java.math.BigDecimal.valueOf(quantity)));

            
            if (d.getVariantId() > 0 && d.getQuantity() > 0) {
                out.add(d);
            }
        }

        return out;
    }
}

