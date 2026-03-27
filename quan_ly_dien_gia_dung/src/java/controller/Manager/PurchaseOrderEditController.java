package controller.Manager;

import dal.BrandDAO;
import dal.CategoryDAO;
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
import java.util.Set;

@WebServlet(name = "PurchaseOrderEditController", urlPatterns = {"/purchase-order/edit"})
public class PurchaseOrderEditController extends HttpServlet {
    private PurchaseOrderService purchaseOrderService;
    private SupplierDAO supplierDAO;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderService = new PurchaseOrderService();
        this.supplierDAO = new SupplierDAO();
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.brandDAO = new BrandDAO();
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

            @SuppressWarnings("unchecked")
            Set<String> permissions = (Set<String>) session.getAttribute("userPermissions");
            boolean canEditPurchaseOrder = permissions != null && permissions.contains("edit purchase order");

            request.setAttribute("categories", categoryDAO.getActiveCategories());
            request.setAttribute("brands", brandDAO.getActiveBrands());

            boolean viewOnly;
            String targetJsp;

            if (roleId == 4) { // Sale role
                // Security check: only creator can edit their own order
                if (po.getCreatedBy() != user.getUserId()) {
                    response.sendRedirect(request.getContextPath() + "/purchase-order/list");
                    return;
                }
                viewOnly = !"draft".equalsIgnoreCase(po.getStatus());
                targetJsp = "/view/sale/sale_order_edit.jsp";
            } else {
                // Manager/Staff: Cho phép sửa khi có quyền edit và đơn đang draft; còn lại chỉ xem.
                viewOnly = !(canEditPurchaseOrder && "draft".equalsIgnoreCase(po.getStatus()));
                targetJsp = "/view/manager/purchase_order_edit.jsp";
            }

            request.setAttribute("purchaseOrder", po);
            request.setAttribute("details", details);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("variants", variants);
            request.setAttribute("viewOnly", viewOnly);

            request.getRequestDispatcher(targetJsp).forward(request, response);

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

        try {
            String idParam = request.getParameter("id");
            int purchaseOrderId = Integer.parseInt(idParam);

            String supplierIdParam = request.getParameter("supplierId");
            String orderDateParam = request.getParameter("orderDate");
            String expectedDeliveryDateParam = request.getParameter("expectedDeliveryDate");
            String notes = request.getParameter("notes");

            int supplierId = 0;
            if (supplierIdParam != null && !supplierIdParam.trim().isEmpty()) {
                supplierId = Integer.parseInt(supplierIdParam);
            }
            
            Date orderDate = Date.valueOf(orderDateParam);
            Date expectedDeliveryDate = null;
            if (expectedDeliveryDateParam != null && !expectedDeliveryDateParam.trim().isEmpty()) {
                expectedDeliveryDate = Date.valueOf(expectedDeliveryDateParam);
            }

            String[] variantIds = request.getParameterValues("variantIds[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] unitPrices = request.getParameterValues("unitPrices[]");

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
                details.add(detail);
            }

            PurchaseOrder po = new PurchaseOrder();
            po.setPurchaseOrderId(purchaseOrderId);
            po.setSupplierId(supplierId);
            po.setOrderDate(orderDate);
            po.setExpectedDeliveryDate(expectedDeliveryDate);
            po.setNotes(notes);

            // Security check for Sale
            int roleId = (user.getRole() != null) ? user.getRole().getRoleId() : 0;
            if (roleId == 4) {
                PurchaseOrder existingPo = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
                if (existingPo == null || existingPo.getCreatedBy() != user.getUserId()) {
                    request.setAttribute("error", "Bạn không có quyền chỉnh sửa đơn hàng này");
                    doGet(request, response);
                    return;
                }
                if (!"draft".equalsIgnoreCase(existingPo.getStatus())) {
                    request.setAttribute("error", "Đơn hàng đã được xử lý, không thể chỉnh sửa");
                    doGet(request, response);
                    return;
                }
            }

            // Chỉ cho sửa khi status = 'pending' (Actually status is checked inside service/DAO usually, but controller might check too)
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
