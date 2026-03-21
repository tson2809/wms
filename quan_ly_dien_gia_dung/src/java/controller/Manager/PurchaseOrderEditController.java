package controller.Manager;

import dal.ProductDAO;
import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.ProductVariant;
import model.Supplier;
import model.User;
import service.PurchaseOrderService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PurchaseOrderEditController", urlPatterns = {"/purchase-order/edit"})
public class PurchaseOrderEditController extends HttpServlet {
    private PurchaseOrderService purchaseOrderService;
    private SupplierDAO supplierDAO;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderService = new PurchaseOrderService();
        this.supplierDAO = new SupplierDAO();
        this.productDAO = new ProductDAO();
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
        if (user.getRole() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int roleId = user.getRole().getRoleId();
        if (roleId != 2 && roleId != 3 && roleId != 4) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
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
            List<Supplier> suppliers = supplierDAO.getActiveSuppliers();
            List<ProductVariant> variants = productDAO.getAllActiveProductVariants();

            // Chỉ Manager mới được sửa khi draft, các role khác chỉ được xem
            boolean viewOnly = true;
            if (roleId == 2) {
                viewOnly = !"draft".equalsIgnoreCase(po.getStatus());
            }
            request.setAttribute("purchaseOrder", po);
            request.setAttribute("details", details);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("variants", variants);
            request.setAttribute("viewOnly", viewOnly);

            request.getRequestDispatcher("/view/manager/purchase_order_edit.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
        }
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
        if (user.getRole() == null || user.getRole().getRoleId() != 2) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            return;
        }

        try {
            String idParam = request.getParameter("id");
            int purchaseOrderId = Integer.parseInt(idParam);

            String supplierIdParam = request.getParameter("supplierId");
            String orderDateParam = request.getParameter("orderDate");
            String expectedDeliveryDateParam = request.getParameter("expectedDeliveryDate");
            String notes = request.getParameter("notes");

            int supplierId = Integer.parseInt(supplierIdParam);
            Date orderDate = Date.valueOf(orderDateParam);
            Date expectedDeliveryDate = null;
            if (expectedDeliveryDateParam != null && !expectedDeliveryDateParam.trim().isEmpty()) {
                expectedDeliveryDate = Date.valueOf(expectedDeliveryDateParam);
            }

            String[] variantIds = request.getParameterValues("variantIds[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] unitPrices = request.getParameterValues("unitPrices[]");
            String[] detailNotes = request.getParameterValues("detailNotes[]");

            if (variantIds == null || quantities == null || unitPrices == null 
                    || variantIds.length == 0) {
                request.setAttribute("error", "Phải có ít nhất một sản phẩm trong đơn hàng");
                doGet(request, response);
                return;
            }

            List<PurchaseOrderDetail> details = new ArrayList<>();
            for (int i = 0; i < variantIds.length; i++) {
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setVariantId(Integer.parseInt(variantIds[i]));
                detail.setQuantity(Integer.parseInt(quantities[i]));
                detail.setUnitPrice(new BigDecimal(unitPrices[i]));
                if (detailNotes != null && i < detailNotes.length) {
                    detail.setNotes(detailNotes[i]);
                }
                details.add(detail);
            }

            PurchaseOrder po = new PurchaseOrder();
            po.setPurchaseOrderId(purchaseOrderId);
            po.setSupplierId(supplierId);
            po.setOrderDate(orderDate);
            po.setExpectedDeliveryDate(expectedDeliveryDate);
            po.setNotes(notes);

            // Chỉ cho sửa khi status = 'pending'
            try {
                boolean success = purchaseOrderService.updatePurchaseOrder(po, details);
                if (success) {
                    session.setAttribute("successMessage", "Cập nhật đơn đặt hàng thành công!");
                    response.sendRedirect(request.getContextPath() + "/purchase-order/list");
                } else {
                    request.setAttribute("error", "Không thể cập nhật đơn đặt hàng");
                    doGet(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Định dạng ngày không hợp lệ");
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }
}
