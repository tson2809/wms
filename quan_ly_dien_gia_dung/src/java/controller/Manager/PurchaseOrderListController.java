package controller.Manager;

import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PurchaseOrder;
import model.Supplier;
import model.User;
import service.PurchaseOrderService;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "PurchaseOrderListController", urlPatterns = {"/purchase-order/list"})
public class PurchaseOrderListController extends HttpServlet {
    private PurchaseOrderService purchaseOrderService;
    private SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderService = new PurchaseOrderService();
        this.supplierDAO = new SupplierDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

//        User user = (User) session.getAttribute("user");
//        if (!"Manager".equalsIgnoreCase(user.getRoleName())) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
//            return;
//        }

        String status = request.getParameter("status");
        String supplierIdParam = request.getParameter("supplierId");
        String fromDateParam = request.getParameter("fromDate");
        String toDateParam = request.getParameter("toDate");
        String keyword = request.getParameter("keyword");
        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");

        Integer supplierId = null;
        if (supplierIdParam != null && !supplierIdParam.trim().isEmpty()) {
            try {
                supplierId = Integer.parseInt(supplierIdParam);
            } catch (NumberFormatException e) {
            }
        }

        Date fromDate = null;
        if (fromDateParam != null && !fromDateParam.trim().isEmpty()) {
            try {
                fromDate = Date.valueOf(fromDateParam);
            } catch (IllegalArgumentException e) {
            }
        }

        Date toDate = null;
        if (toDateParam != null && !toDateParam.trim().isEmpty()) {
            try {
                toDate = Date.valueOf(toDateParam);
            } catch (IllegalArgumentException e) {
            }
        }

        int page = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
            }
        }

        int pageSize = 10;
        if (pageSizeParam != null && !pageSizeParam.trim().isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize < 5 || pageSize > 100) pageSize = 10;
            } catch (NumberFormatException e) {
            }
        }

        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrdersWithPagination(
                status, supplierId, fromDate, toDate, keyword, page, pageSize);

        int totalRecords = purchaseOrderService.countPurchaseOrders(
                status, supplierId, fromDate, toDate, keyword);

        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        List<Supplier> suppliers = supplierDAO.getActiveSuppliers();

        request.setAttribute("purchaseOrders", purchaseOrders);
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("supplierId", supplierId);
        request.setAttribute("fromDate", fromDateParam != null ? fromDateParam : "");
        request.setAttribute("toDate", toDateParam != null ? toDateParam : "");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/view/manager/purchase_order_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
