package controller.Manager;

import dal.ReturnOrderDAO;
import dal.SupplierDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.ReturnOrder;
import model.Supplier;

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

        request.getRequestDispatcher("/view/manager/return_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect POST to GET
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Return Order List Controller with pagination and filtering";
    }
}
