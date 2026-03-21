<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    model.User currentUser = (model.User) session.getAttribute("user");
    String roleName = "";
    if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().getRoleName() != null) {
        roleName = currentUser.getRole().getRoleName().toLowerCase();
    }

    java.util.Set<String> userPermissions = (java.util.Set<String>) session.getAttribute("userPermissions");
    if (currentUser != null && userPermissions == null) {
        try {
            dal.RoleDAO roleDAO = new dal.RoleDAO();
            java.util.List<String> permissionNames = roleDAO.getRolePermissionNames(currentUser.getRoleId());
            userPermissions = new java.util.HashSet<>(permissionNames);
            session.setAttribute("userPermissions", userPermissions);
        } catch (Exception e) {
            // Keep last known permission set if DB refresh fails.
        }
    }
    boolean canViewGoodsReceipt = userPermissions != null && userPermissions.contains("view goods receipt");
    boolean canViewGoodsIssue = userPermissions != null && userPermissions.contains("view goods issue");
    boolean canViewInventory = userPermissions != null && userPermissions.contains("view inventory");
    boolean canViewSupplier = userPermissions != null && userPermissions.contains("view supplier");
    boolean canViewCategory = userPermissions != null && userPermissions.contains("view category");
    boolean canViewBrand = userPermissions != null && userPermissions.contains("view brand");
    boolean canViewUnit = userPermissions != null && userPermissions.contains("view unit");
    boolean canViewProduct = userPermissions != null && userPermissions.contains("view product");
    boolean canViewPurchaseOrder = userPermissions != null && userPermissions.contains("view purchase order");

    String currentURI = request.getRequestURI();

        String userListActive = (currentURI.contains("/user-list")
            || currentURI.contains("/user-edit")
            || currentURI.contains("/user-add")
            || currentURI.contains("/user-detail")
            || currentURI.contains("/UpdateUser")
            || currentURI.contains("/user-toggle-status")) ? "active" : "";
    String permissionActive = currentURI.contains("/viewpermission") ? "active" : "";
    String roleActive = (currentURI.contains("/ViewRole") || currentURI.contains("/viewrole")) ? "active" : "";
    String auditLogActive = currentURI.contains("/audit-log-list") ? "active" : "";

    String supplierActive = (currentURI.contains("/supplier-list")
            || currentURI.contains("/supplier-add")
            || currentURI.contains("/supplier-detail")
            || currentURI.contains("/supplier_list.jsp")
            || currentURI.contains("/supplier_detail.jsp")) ? "active" : "";
        String purchaseOrderActive = (currentURI.contains("/purchase-order")
            || currentURI.contains("/sale-order")
            || currentURI.contains("/purchase_order_list.jsp")
            || currentURI.contains("/purchase_order_create.jsp")
            || currentURI.contains("/purchase_order_edit.jsp")
            || currentURI.contains("/purchase_order_detail.jsp")) ? "active" : "";
    String goodsReceiptActive = (currentURI.contains("/goods-receipt-list")
            || currentURI.contains("/goods-receipt-detail")
            || currentURI.contains("/goods-receipt-add")
            || currentURI.contains("/goods-receipt-edit")) ? "active" : "";
        String goodsIssueActive = (currentURI.contains("/goods-issue-list")
            || currentURI.contains("/goods-issue-detail")
            || currentURI.contains("/goods-issue-add")
            || currentURI.contains("/goods-issue-edit")) ? "active" : "";
    String returnActive = (currentURI.contains("/return-order-list")
            || currentURI.contains("/return-add")
            || currentURI.contains("/return-view")
            || currentURI.contains("/return-edit")
            || currentURI.contains("/return-claim")
            || currentURI.contains("/return_edit.jsp")) ? "active" : "";
    String productActive = (currentURI.contains("/product-list")
            || currentURI.contains("/product-add")
            || currentURI.contains("/product-edit")
            || currentURI.contains("/product-detail")
            || currentURI.contains("/product_list.jsp")
            || currentURI.contains("/product_add.jsp")
            || currentURI.contains("/product_edit.jsp")) ? "active" : "";
    String categoryActive = (currentURI.contains("/category-list")
            || currentURI.contains("/category-add")
            || currentURI.contains("/category-edit")) ? "active" : "";
    String brandActive = (currentURI.contains("/brand-list")
            || currentURI.contains("/brand-add")
            || currentURI.contains("/brand-edit")) ? "active" : "";
    String unitActive = (currentURI.contains("/unit-list")
            || currentURI.contains("/unit-add")
            || currentURI.contains("/unit-edit")
            || currentURI.contains("/unit-detail")
            || currentURI.contains("/unit_list.jsp")
            || currentURI.contains("/unit_detail.jsp")) ? "active" : "";
    String inventoryAlertActive = currentURI.contains("/inventory-alert") ? "active" : "";
    String inventoryTransactionActive = (currentURI.contains("/inventory-transactions")
            || currentURI.contains("/transaction-detail")
            || currentURI.contains("/transaction-list.jsp")
            || currentURI.contains("/transaction-detail.jsp")) ? "active" : "";
    String inventoryListActive = (currentURI.contains("/inventory-list") || currentURI.contains("/inventory-detail")) ? "active" : "";
    String inventorySheetActive = (currentURI.contains("/inventory-sheet-list")
            || currentURI.contains("/inventory-sheet-create")
            || currentURI.contains("/inventory-sheet-view")
            || currentURI.contains("/inventory-sheet-approve")
            || currentURI.contains("/inventory-sheet-edit")
            || currentURI.contains("/sheet-list.jsp")
            || currentURI.contains("/sheet-create.jsp")
            || currentURI.contains("/sheet-view.jsp")
            || currentURI.contains("/sheet-edit.jsp")) ? "active" : "";
    String managerReportActive = currentURI.contains("/manager-report") ? "active" : "";

        String salesReturnActive = (currentURI.contains("/sales-return-list")
            || currentURI.contains("/sales-return-add")
            || currentURI.contains("/sales-return-edit")
            || currentURI.contains("/sales-return-claim")) ? "active" : "";
    String saleOrderActive = (currentURI.contains("/purchase-order") || currentURI.contains("/sale-order")) ? "active" : "";
%>

<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <% if ("admin".equals(roleName)) { %>
            <a href="${pageContext.request.contextPath}/user-list" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <a href="${pageContext.request.contextPath}/user-list" class="nav-item nav-link <%= userListActive %>"><i class="fa fa-solid fa-user me-2"></i>Quản lý người dùng</a>
                <a href="${pageContext.request.contextPath}/viewpermission" class="nav-item nav-link <%= permissionActive %>"><i class="fa fa-user-shield me-2"></i>Quyền hệ thống</a>
                <a href="${pageContext.request.contextPath}/ViewRole" class="nav-item nav-link <%= roleActive %>"><i class="fa fa-users-cog me-2"></i>Quản lí cấp bậc</a>
                <a href="${pageContext.request.contextPath}/audit-log-list" class="nav-item nav-link <%= auditLogActive %>"><i class="fa fa-history me-2"></i>Nhật ký hệ thống</a>
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-list" class="nav-item nav-link <%= inventoryListActive %>"><i class="fa fa-boxes me-2"></i>Tồn kho</a>
                <% } %>
                <% if (canViewSupplier) { %>
                <a href="${pageContext.request.contextPath}/supplier-list" class="nav-item nav-link <%= supplierActive %>"><i class="fa fa-truck me-2"></i>Nhà cung cấp</a>
                <% } %>
                <% if (canViewPurchaseOrder) { %>
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= purchaseOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <% } %>
                <% if (canViewGoodsReceipt) { %>
                <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
                <% } %>
                <% if (canViewGoodsIssue) { %>
                <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
                <% } %>
                <% if (canViewProduct) { %>
                <a href="${pageContext.request.contextPath}/product-list" class="nav-item nav-link <%= productActive %>"><i class="fa fa-box me-2"></i>Sản phẩm</a>
                <% } %>
                <% if (canViewCategory) { %>
                <a href="${pageContext.request.contextPath}/category-list" class="nav-item nav-link <%= categoryActive %>"><i class="fa fa-box me-2"></i>Danh mục</a>
                <% } %>
                <% if (canViewBrand) { %>
                <a href="${pageContext.request.contextPath}/brand-list" class="nav-item nav-link <%= brandActive %>"><i class="fa fa-box me-2"></i>Thương hiệu</a>
                <% } %>
                <% if (canViewUnit) { %>
                <a href="${pageContext.request.contextPath}/unit-list" class="nav-item nav-link <%= unitActive %>"><i class="fa fa-ruler me-2"></i>Đơn vị tính</a>
                <% } %>
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-alert" class="nav-item nav-link <%= inventoryAlertActive %>">
                    <i class="fa fa-exclamation-triangle me-2"></i>Cảnh Báo
                    <c:if test="${alertEnabled and alertCount > 0}">
                        <span class="badge bg-danger ms-2">${alertCount}</span>
                    </c:if>
                </a>
                <a href="${pageContext.request.contextPath}/inventory-transactions" class="nav-item nav-link <%= inventoryTransactionActive %>"><i class="fa fa-clipboard-list me-2"></i>Lịch sử biến động kho</a>
                <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="nav-item nav-link <%= inventorySheetActive %>"><i class="fa fa-clipboard-list me-2"></i>Quản lý sheet</a>
                <% } %>
            </div>
        <% } else if ("manager".equals(roleName)) { %>
            <a href="${pageContext.request.contextPath}/manager-report" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <a href="${pageContext.request.contextPath}/manager-report" class="nav-item nav-link <%= managerReportActive %>"><i class="fa fa-chart-pie me-2"></i>Báo cáo thống kê</a>
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-list" class="nav-item nav-link <%= inventoryListActive %>"><i class="fa fa-boxes me-2"></i>Tồn kho</a>
                <% } %>
                <% if (canViewSupplier) { %>
                <a href="${pageContext.request.contextPath}/supplier-list" class="nav-item nav-link <%= supplierActive %>"><i class="fa fa-truck me-2"></i>Nhà cung cấp</a>
                <% } %>
                <% if (canViewPurchaseOrder) { %>
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= purchaseOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <% } %>
                <% if (canViewGoodsReceipt) { %>
                <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
                <% } %>
                <% if (canViewGoodsIssue) { %>
                <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/return-order-list" class="nav-item nav-link <%= returnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả về NCC</a>
                <% if (canViewProduct) { %>
                <a href="${pageContext.request.contextPath}/product-list" class="nav-item nav-link <%= productActive %>"><i class="fa fa-box me-2"></i>Sản phẩm</a>
                <% } %>
                <% if (canViewCategory) { %>
                <a href="${pageContext.request.contextPath}/category-list" class="nav-item nav-link <%= categoryActive %>"><i class="fa fa-box me-2"></i>Danh mục</a>
                <% } %>
                <% if (canViewBrand) { %>
                <a href="${pageContext.request.contextPath}/brand-list" class="nav-item nav-link <%= brandActive %>"><i class="fa fa-box me-2"></i>Thương hiệu</a>
                <% } %>
                <% if (canViewUnit) { %>
                <a href="${pageContext.request.contextPath}/unit-list" class="nav-item nav-link <%= unitActive %>"><i class="fa fa-ruler me-2"></i>Đơn vị tính</a>
                <% } %>
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-alert" class="nav-item nav-link <%= inventoryAlertActive %>">
                    <i class="fa fa-exclamation-triangle me-2"></i>Cảnh Báo
                    <c:if test="${alertEnabled and alertCount > 0}">
                        <span class="badge bg-danger ms-2">${alertCount}</span>
                    </c:if>
                </a>
                <a href="${pageContext.request.contextPath}/inventory-transactions" class="nav-item nav-link <%= inventoryTransactionActive %>"><i class="fa fa-clipboard-list me-2"></i>Lịch sử biến động kho</a>
                <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="nav-item nav-link <%= inventorySheetActive %>"><i class="fa fa-clipboard-list me-2"></i>Quản lý sheet</a>
                <% } %>
            </div>
        <% } else if ("staff".equals(roleName)) { %>
            <a href="${pageContext.request.contextPath}/inventory-list" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-list" class="nav-item nav-link <%= inventoryListActive %>"><i class="fa fa-boxes me-2"></i>Tồn kho</a>
                <% } %>
                <% if (canViewSupplier) { %>
                <a href="${pageContext.request.contextPath}/supplier-list" class="nav-item nav-link <%= supplierActive %>"><i class="fa fa-truck me-2"></i>Nhà cung cấp</a>
                <% } %>
                <% if (canViewCategory) { %>
                <a href="${pageContext.request.contextPath}/category-list" class="nav-item nav-link <%= categoryActive %>"><i class="fa fa-box me-2"></i>Danh mục</a>
                <% } %>
                <% if (canViewBrand) { %>
                <a href="${pageContext.request.contextPath}/brand-list" class="nav-item nav-link <%= brandActive %>"><i class="fa fa-box me-2"></i>Thương hiệu</a>
                <% } %>
                <% if (canViewUnit) { %>
                <a href="${pageContext.request.contextPath}/unit-list" class="nav-item nav-link <%= unitActive %>"><i class="fa fa-ruler me-2"></i>Đơn vị tính</a>
                <% } %>
                <% if (canViewProduct) { %>
                <a href="${pageContext.request.contextPath}/product-list" class="nav-item nav-link <%= productActive %>"><i class="fa fa-box me-2"></i>Sản phẩm</a>
                <% } %>
                <% if (canViewGoodsReceipt) { %>
                <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
                <% } %>
                <% if (canViewGoodsIssue) { %>
                <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
                <% } %>
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="nav-item nav-link <%= inventorySheetActive %>"><i class="fa fa-clipboard-list me-2"></i>Quản lý sheet</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/return-order-list" class="nav-item nav-link <%= returnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả về NCC</a>
                <a href="${pageContext.request.contextPath}/sales-return-list" class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả từ Sale</a>
                <% if (canViewPurchaseOrder) { %>
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= purchaseOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <% } %>
            </div>
        <% } else { %>
            <a href="${pageContext.request.contextPath}/purchase-order/list" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <% if (canViewInventory) { %>
                <a href="${pageContext.request.contextPath}/inventory-list" class="nav-item nav-link <%= inventoryListActive %>"><i class="fa fa-boxes me-2"></i>Tồn kho</a>
                <% } %>
                <% if (canViewSupplier) { %>
                <a href="${pageContext.request.contextPath}/supplier-list" class="nav-item nav-link <%= supplierActive %>"><i class="fa fa-truck me-2"></i>Nhà cung cấp</a>
                <% } %>
                <% if (canViewCategory) { %>
                <a href="${pageContext.request.contextPath}/category-list" class="nav-item nav-link <%= categoryActive %>"><i class="fa fa-box me-2"></i>Danh mục</a>
                <% } %>
                <% if (canViewBrand) { %>
                <a href="${pageContext.request.contextPath}/brand-list" class="nav-item nav-link <%= brandActive %>"><i class="fa fa-box me-2"></i>Thương hiệu</a>
                <% } %>
                <% if (canViewUnit) { %>
                <a href="${pageContext.request.contextPath}/unit-list" class="nav-item nav-link <%= unitActive %>"><i class="fa fa-ruler me-2"></i>Đơn vị tính</a>
                <% } %>
                <% if (canViewProduct) { %>
                <a href="${pageContext.request.contextPath}/product-list" class="nav-item nav-link <%= productActive %>"><i class="fa fa-box me-2"></i>Sản phẩm</a>
                <% } %>
                <% if (canViewGoodsReceipt) { %>
                <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
                <% } %>
                <% if (canViewGoodsIssue) { %>
                <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/sales-return-list" class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Trả hàng</a>
                <% if (canViewPurchaseOrder) { %>
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= saleOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <% } %>
            </div>
        <% } %>
    </nav>
</div>
