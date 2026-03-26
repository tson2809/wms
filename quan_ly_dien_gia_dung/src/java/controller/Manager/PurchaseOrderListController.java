package controller.Manager;

import dal.PurchaseOrderDAO;
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

@WebServlet(name = "PurchaseOrderListController", urlPatterns = { "/purchase-order/list" })
public class PurchaseOrderListController extends HttpServlet {
    private PurchaseOrderService purchaseOrderService;
    private SupplierDAO supplierDAO;
    private PurchaseOrderDAO purchaseOrderDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderService = new PurchaseOrderService();
        this.supplierDAO = new SupplierDAO();
        this.purchaseOrderDAO = new PurchaseOrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = (user.getRole() != null) ? user.getRole().getRoleId() : 0;

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
                if (page < 1)
                    page = 1;
            } catch (NumberFormatException e) {
            }
        }

        int pageSize = 10;
        if (pageSizeParam != null && !pageSizeParam.trim().isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize < 5 || pageSize > 100)
                    pageSize = 10;
            } catch (NumberFormatException e) {
            }
        }

        List<PurchaseOrder> purchaseOrders;
        int totalRecords;
        boolean isSaleOrderView = false;

        if (roleId == 4) {
            // Sale: chỉ thấy đơn do mình tạo (supplier_id IS NULL)
            isSaleOrderView = true;
            purchaseOrders = purchaseOrderDAO.getSaleOrdersByCreator(
                    user.getUserId(), status,
                    (page - 1) * pageSize, pageSize);
            totalRecords = purchaseOrderDAO.countSaleOrdersByCreator(user.getUserId(), status);
        } else if (roleId == 3) {
            // Staff: xem tất cả đơn (cả PO có NCC lẫn Sale orders) trong một bảng
            purchaseOrders = purchaseOrderDAO.getAllOrdersForStaff(status, keyword, (page - 1) * pageSize, pageSize);
            totalRecords = purchaseOrderDAO.countAllOrdersForStaff(status, keyword);
        } else {
            // Manager: chỉ xem PO có NCC
            purchaseOrders = purchaseOrderService.getPurchaseOrdersWithPagination(
                    status, supplierId, fromDate, toDate, keyword, page, pageSize);
            totalRecords = purchaseOrderService.countPurchaseOrders(
                    status, supplierId, fromDate, toDate, keyword);
        }

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
        request.setAttribute("roleId", roleId);
        request.setAttribute("currentUserId", user.getUserId());
        request.setAttribute("isSaleOrderView", isSaleOrderView);

        request.getRequestDispatcher("/view/common/purchase_order_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String action = request.getParameter("action");

        if ("cancel".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    int poId = Integer.parseInt(idParam);
                    int roleId = (user.getRole() != null) ? user.getRole().getRoleId() : 0;

                    PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
                    boolean canCancel = false;
                    if (po != null) {
                        if (roleId == 2 || roleId == 3) {
                            canCancel = true;
                        } else if (roleId == 4) {
                            canCancel = po.getSupplierId() == 0
                                    && po.getCreatedBy() == user.getUserId()
                                    && "draft".equalsIgnoreCase(po.getStatus());
                        }
                    }
                    
                    if (canCancel) {
                        boolean ok = purchaseOrderDAO.cancelPurchaseOrder(poId);
                        if (ok) {
                            session.setAttribute("successMessage", "Đã hủy đơn đặt hàng thành công.");
                        } else {
                            session.setAttribute("errorMessage",
                                    "Không thể hủy đơn. Đơn hàng chỉ có thể hủy khi đang chờ xử lý hoặc đang xử lý.");
                        }
                    } else {
                        session.setAttribute("errorMessage", "Bạn không có quyền hủy đơn hàng này.");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("errorMessage", "Mã đơn hàng không hợp lệ.");
                }
            }
        }

        // Redirect về list, giữ lại các filter params
        String redirectUrl = buildRedirectUrl(request);
        response.sendRedirect(redirectUrl);
    }

    private String buildRedirectUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/purchase-order/list");
        boolean first = true;
        String[] params = { "status", "supplierId", "fromDate", "toDate", "keyword", "page", "pageSize" };
        for (String p : params) {
            String v = request.getParameter(p);
            if (v != null && !v.trim().isEmpty()) {
                url.append(first ? "?" : "&").append(p).append("=").append(v);
                first = false;
            }
        }
        return url.toString();
    }
}
