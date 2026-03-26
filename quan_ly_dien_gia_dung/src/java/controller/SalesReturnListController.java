package controller;

import dal.SalesReturnDAO;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.SalesReturn;
import model.User;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "SalesReturnListController", urlPatterns = {"/sales-return-list"})
public class SalesReturnListController extends HttpServlet {

    private final SalesReturnDAO dao = new SalesReturnDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String searchNormalized = (search != null && !search.trim().isEmpty())
                ? search.trim().replaceAll("\\s+", " ")
                : null;

        String orderStatus = request.getParameter("orderStatus");
        String refundStatus = request.getParameter("refundStatus");

        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("numberPerPage");

        int page = 1;
        int size = 10;
        try {
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                page = Integer.parseInt(pageStr.trim());
            }
        } catch (NumberFormatException ignored) {
            page = 1;
        }

        try {
            if (sizeStr != null && !sizeStr.trim().isEmpty()) {
                size = Integer.parseInt(sizeStr.trim());
            }
        } catch (NumberFormatException ignored) {
            size = 10;
        }

        if (size != 5 && size != 10 && size != 20) {
            size = 10;
        }
        if (page < 1) page = 1;

        User user = (User) request.getSession().getAttribute("user");
        Integer createdBy = null;
        if (user != null && user.getRole() != null && user.getRole().getRoleId() == 4) {
            // Sale chỉ thấy đơn do chính mình tạo
            createdBy = user.getUserId();
        }

        int offset = (page - 1) * size;

        List<SalesReturn> salesReturns = dao.getSalesReturnWithSearchAndFilter(
                searchNormalized, orderStatus, refundStatus, createdBy, offset, size);
        int totalOrders = dao.countSalesReturnWithSearchAndFilter(
                searchNormalized, orderStatus, refundStatus, createdBy);

        int totalPages = (int) Math.ceil(totalOrders * 1.0 / size);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
            offset = (page - 1) * size;
            salesReturns = dao.getSalesReturnWithSearchAndFilter(
                    searchNormalized, orderStatus, refundStatus, createdBy, offset, size);
        }

        request.setAttribute("salesReturns", salesReturns);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("numberPerPage", size);
        request.setAttribute("totalOrders", totalOrders);

        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("orderStatus", orderStatus != null ? orderStatus : "");
        request.setAttribute("refundStatus", refundStatus != null ? refundStatus : "");

        request.getRequestDispatcher("/view/common/sales_return_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("cancel".equalsIgnoreCase(action)) {
            // Sale được hủy đơn khi đơn đang ở trạng thái pending (chờ xử lý)
            if (user.getRole().getRoleId() == 4) {
                Integer salesReturnId = parsePositiveInt(request.getParameter("id"));
                if (salesReturnId != null) {
                    dao.cancelPendingSalesReturn(salesReturnId, user.getUserId());
                }
            }
            response.sendRedirect(buildRedirectUrl(request));
            return;
        }

        doGet(request, response);
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

    private String buildRedirectUrl(HttpServletRequest request) {
        StringBuilder q = new StringBuilder(request.getContextPath() + "/sales-return-list");
        String[] params = {"search", "orderStatus", "refundStatus", "page", "numberPerPage"};
        try {
            boolean first = true;
            for (String p : params) {
                String v = request.getParameter(p);
                if (v != null && !v.trim().isEmpty()) {
                    q.append(first ? "?" : "&").append(p).append("=").append(URLEncoder.encode(v.trim(), "UTF-8"));
                    first = false;
                }
            }
        } catch (UnsupportedEncodingException e) {
            // UTF-8 luôn được hỗ trợ
        }
        return q.toString();
    }
}

