package controller.Manager;

import dal.ReturnOrderDAO;
import dal.SupplierDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import model.ReturnOrder;
import model.Supplier;
import model.User;

/**
 * Controller for return order list page with pagination and filtering
 * 
 * @author laptop368
 */
@WebServlet(name = "ReturnOrderListController", urlPatterns = { "/return-order-list" })
public class ReturnOrderListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get filter parameters
        String search = request.getParameter("search");
        String searchNormalized = (search != null && !search.trim().isEmpty())
                ? search.trim().replaceAll("\\s+", " ")
                : null;

        String supplierIdStr = request.getParameter("supplierId");
        String orderStatus = request.getParameter("orderStatus");
        String refundStatus = request.getParameter("refundStatus");

        // Parse supplier ID
        Integer supplierId = null;
        try {
            if (supplierIdStr != null && !supplierIdStr.trim().isEmpty()) {
                supplierId = Integer.parseInt(supplierIdStr.trim());
            }
        } catch (NumberFormatException e) {
            // Invalid supplierId, ignore
        }

        // Pagination parameters
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("numberPerPage");

        int page = 1;
        int size = 10;

        try {
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                page = Integer.parseInt(pageStr.trim());
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        try {
            if (sizeStr != null && !sizeStr.trim().isEmpty()) {
                size = Integer.parseInt(sizeStr.trim());
            }
        } catch (NumberFormatException e) {
            size = 10;
        }

        // Validate size
        if (size != 5 && size != 10 && size != 20) {
            size = 10;
        }

        int offset = (page - 1) * size;
        ReturnOrderDAO dao = new ReturnOrderDAO();

        // Get filtered return orders
        List<ReturnOrder> returnOrders = dao.getReturnOrderWithSearchAndFilter(
                searchNormalized, supplierId, orderStatus, refundStatus, offset, size);
        int totalOrders = dao.countReturnOrderWithSearchAndFilter(
                searchNormalized, supplierId, orderStatus, refundStatus);
        int totalPages = (int) Math.ceil(totalOrders * 1.0 / size);

        // Ensure page doesn't exceed totalPages
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
            offset = (page - 1) * size;
            returnOrders = dao.getReturnOrderWithSearchAndFilter(
                    searchNormalized, supplierId, orderStatus, refundStatus, offset, size);
        }

        // Get suppliers for dropdown
        SupplierDAO supplierDAO = new SupplierDAO();
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();

        // Set return order data
        request.setAttribute("returnOrders", returnOrders);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("numberPerPage", size);
        request.setAttribute("totalOrders", totalOrders);

        // Set filter values for maintaining state
        request.setAttribute("search", search);
        request.setAttribute("supplierId", supplierIdStr);
        request.setAttribute("orderStatus", orderStatus);
        request.setAttribute("refundStatus", refundStatus);

        // Set suppliers for dropdown
        request.setAttribute("suppliers", suppliers);

        request.getRequestDispatcher("/view/common/return_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("cancel".equals(action)) {
            User user = (User) request.getSession().getAttribute("user");
            if (user != null && user.getRole() != null && user.getRole().getRoleId() == 2) {
                String idStr = request.getParameter("id");
                Integer returnOrderId = parsePositiveInt(idStr);
                if (returnOrderId != null) {
                    ReturnOrderDAO dao = new ReturnOrderDAO();
                    dao.cancelReturnOrder(returnOrderId);
                }
            }
            response.sendRedirect(buildRedirectUrl(request));
            return;
        }
        doGet(request, response);
    }

    private String buildRedirectUrl(HttpServletRequest request) {
        StringBuilder q = new StringBuilder(request.getContextPath() + "/return-order-list");
        String[] params = {"search", "supplierId", "orderStatus", "refundStatus", "page", "numberPerPage"};
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
            // UTF-8 is always supported
        }
        return q.toString();
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

    @Override
    public String getServletInfo() {
        return "Return Order List Controller with pagination and filtering";
    }
}
