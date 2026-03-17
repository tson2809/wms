/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Staff;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dal.GoodsIssueDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import model.GoodsIssueDetail;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsIssueAddController", urlPatterns = {"/goods-issue-add"})
public class GoodsIssueAddController extends HttpServlet {
    private GoodsIssueDAO goodsIssueDAO = new GoodsIssueDAO();
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
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

        handleCreateIssue(request, response);
    }

    private void handleCreateIssue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String issueCode = request.getParameter("issueCode");
        String issueType = request.getParameter("issueType");
        String issueDate = request.getParameter("issueDate");
        String receiverName = request.getParameter("receiverName");
        String department = request.getParameter("department");
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
            request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
            return;
        }

        try {
            List<GoodsIssueDetail> details = parseProductsJson(productsJson);
            User user = (User) request.getSession().getAttribute("user");

            boolean success = goodsIssueDAO.createGoodsIssue(
                issueCode.trim(), issueType, issueDate,
                receiverName.trim(), department, notes,
                user.getUserId(), details
            );

            if (success) {
                request.getSession().setAttribute("successMessage", "Tạo phiếu xuất kho thành công!");
                response.sendRedirect(request.getContextPath() + "/goods-issue-list");
            } else {
                request.setAttribute("generalError", "Có lỗi xảy ra khi tạo phiếu xuất. Vui lòng thử lại!");
                request.setAttribute("productsJson", productsJson);
                request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            request.setAttribute("generalError", "Có lỗi xảy ra: " + ex.getMessage());
            request.setAttribute("productsJson", productsJson);
            request.getRequestDispatcher("/view/staff/goods-issue-add.jsp").forward(request, response);
        }
    }

    private List<GoodsIssueDetail> parseProductsJson(String json) {
        List<GoodsIssueDetail> details = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return details;

        Type listType = new TypeToken<List<java.util.Map<String, Object>>>() {}.getType();
        List<java.util.Map<String, Object>> items = gson.fromJson(json, listType);
        if (items == null) return details;

        for (java.util.Map<String, Object> item : items) {
            if (item == null) continue;
            GoodsIssueDetail d = new GoodsIssueDetail();
            d.setVariantId(((Number) item.get("variantId")).intValue());
            d.setQuantity(((Number) item.get("quantity")).intValue());

            Object serialsObj = item.get("serials");
            if (serialsObj instanceof List) {
                List<String> serials = new ArrayList<>();
                for (Object s : (List<?>) serialsObj) {
                    if (s != null) serials.add(s.toString());
                }
                d.setSerials(serials);
            }
            details.add(d);
        }
        return details;
    }
}
