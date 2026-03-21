package controller.Sale;

import dal.BrandDAO;
import dal.CategoryDAO;
import dal.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.ProductVariant;
import model.User;
import service.PurchaseOrderService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SaleOrderCreateController", urlPatterns = {"/sale-order/create"})
public class SaleOrderCreateController extends HttpServlet {
    private PurchaseOrderService purchaseOrderService;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderService = new PurchaseOrderService();
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
        if (user.getRole() == null || user.getRole().getRoleId() != 4) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            return;
        }
        request.setAttribute("categories", categoryDAO.getActiveCategories());
        request.setAttribute("brands", brandDAO.getActiveBrands());
        request.getRequestDispatcher("/view/sale/sale_order_create.jsp").forward(request, response);
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
        if (user.getRole() == null || user.getRole().getRoleId() != 4) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            return;
        }

        try {
            String orderDateParam = request.getParameter("orderDate");
            String expectedDeliveryDateParam = request.getParameter("expectedDeliveryDate");
            String notes = request.getParameter("notes");

            Date orderDate = Date.valueOf(orderDateParam);
            Date expectedDeliveryDate = null;
            if (expectedDeliveryDateParam != null && !expectedDeliveryDateParam.trim().isEmpty()) {
                expectedDeliveryDate = Date.valueOf(expectedDeliveryDateParam);
            }

            String[] variantIds    = request.getParameterValues("variantIds[]");
            String[] quantities    = request.getParameterValues("quantities[]");
            String[] unitPrices    = request.getParameterValues("unitPrices[]");
            String[] detailNotes   = request.getParameterValues("detailNotes[]");

            if (variantIds == null || quantities == null || unitPrices == null || variantIds.length == 0) {
                request.setAttribute("error", "Phải có ít nhất một sản phẩm trong đơn hàng");
                doGet(request, response);
                return;
            }

            List<PurchaseOrderDetail> details = new ArrayList<>();
            for (int i = 0; i < variantIds.length; i++) {
                PurchaseOrderDetail d = new PurchaseOrderDetail();
                d.setVariantId(Integer.parseInt(variantIds[i]));
                d.setQuantity(Integer.parseInt(quantities[i]));
                d.setUnitPrice(new BigDecimal(unitPrices[i]));
                if (detailNotes != null && i < detailNotes.length) d.setNotes(detailNotes[i]);
                details.add(d);
            }

            // supplierId = 0 → insertPurchaseOrder sẽ set NULL
            PurchaseOrder po = new PurchaseOrder();
            po.setSupplierId(0);
            po.setOrderDate(orderDate);
            po.setExpectedDeliveryDate(expectedDeliveryDate);
            po.setNotes(notes);
            po.setCreatedBy(user.getUserId());

            int poId = purchaseOrderService.createPurchaseOrder(po, details);
            if (poId > 0) {
                session.setAttribute("successMessage", "Tạo đơn đặt hàng thành công!");
                response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            } else {
                request.setAttribute("error", "Không thể tạo đơn đặt hàng");
                doGet(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            doGet(request, response);
        }
    }
}
