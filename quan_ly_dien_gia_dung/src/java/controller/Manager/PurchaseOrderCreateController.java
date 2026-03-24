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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PurchaseOrderCreateController", urlPatterns = {"/purchase-order/create"})
public class PurchaseOrderCreateController extends HttpServlet {
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

        List<Supplier> suppliers = supplierDAO.getActiveSuppliers();

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("categories", categoryDAO.getActiveCategories());
        request.setAttribute("brands", brandDAO.getActiveBrands());

        request.getRequestDispatcher("/view/manager/purchase_order_create.jsp").forward(request, response);
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
            po.setSupplierId(supplierId);
            po.setOrderDate(orderDate);
            po.setExpectedDeliveryDate(expectedDeliveryDate);
            po.setNotes(notes);
            po.setCreatedBy(user.getUserId());

            StringBuilder errorMsg = new StringBuilder();
            if (!purchaseOrderService.validatePurchaseOrder(po, details, errorMsg)) {
                request.setAttribute("error", errorMsg.toString());
                doGet(request, response);
                return;
            }

            int poId = purchaseOrderService.createPurchaseOrder(po, details);
            if (poId > 0) {
                session.setAttribute("successMessage", "Tạo đơn đặt hàng thành công!");
                response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            } else {
                request.setAttribute("error", "Không thể tạo đơn đặt hàng");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Định dạng ngày không hợp lệ");
            doGet(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }
}
