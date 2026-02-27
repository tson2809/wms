package controller.Manager;

import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.Supplier;
import model.User;
import service.PurchaseOrderService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "PurchaseOrderDetailController", urlPatterns = {"/purchase-order/view"})
public class PurchaseOrderDetailController extends HttpServlet {
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

        User user = (User) session.getAttribute("user");
        if (user.getRole() == null || !"Manager".equalsIgnoreCase(user.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/indexManager");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            return;
        }

        try {
            int purchaseOrderId = Integer.parseInt(idParam);
            PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
            
            if (po == null) {
                request.setAttribute("error", "Không tìm thấy đơn đặt hàng");
                response.sendRedirect(request.getContextPath() + "/purchase-order/list");
                return;
            }

            List<PurchaseOrderDetail> details = purchaseOrderService.getPurchaseOrderDetails(purchaseOrderId);
            Supplier supplier = supplierDAO.getSupplierById(po.getSupplierId());

            request.setAttribute("purchaseOrder", po);
            request.setAttribute("details", details);
            request.setAttribute("supplier", supplier);

            request.getRequestDispatcher("/view/manager/purchase_order_detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi
            response.sendRedirect(request.getContextPath() + "/purchase-order/list?error=" + e.getMessage());
        }
    }
}
