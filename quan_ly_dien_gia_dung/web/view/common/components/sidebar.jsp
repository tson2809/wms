<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="roleId" value="${sessionScope.user.role.roleId}" />
<%
    String currentURI = request.getRequestURI();

        String userListActive = (currentURI.contains("/user-list")
            || currentURI.contains("/user-edit")
            || currentURI.contains("/user-add")
            || currentURI.contains("/user-detail")
            || currentURI.contains("/UpdateUser")
            || currentURI.contains("/user-toggle-status")) ? "active" : "";
        String permissionActive = (currentURI.contains("/view-permission")
            || currentURI.contains("/viewpermission")
            || currentURI.contains("/view/admin/viewpermission.jsp")) ? "active" : "";
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
            || currentURI.contains("/return_order_list.jsp")
            || currentURI.contains("/return_order_add.jsp")
            || currentURI.contains("/return_order_edit.jsp")
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
            || currentURI.contains("/sales-return-claim")
            || currentURI.contains("/sales_return_list.jsp")
            || currentURI.contains("/sales_return_add.jsp")
            || currentURI.contains("/sales_return_edit.jsp")) ? "active" : "";
    String saleOrderActive = purchaseOrderActive;
%>

<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <c:choose>
        <c:when test="${roleId == 1}">
            <a href="${pageContext.request.contextPath}/user-list" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <a href="${pageContext.request.contextPath}/user-list" class="nav-item nav-link <%= userListActive %>"><i class="fa fa-solid fa-user me-2"></i>Quản lý người dùng</a>
                <a href="${pageContext.request.contextPath}/view-permission" class="nav-item nav-link <%= permissionActive %>"><i class="fa fa-user-shield me-2"></i>Quyền hệ thống</a>
                <a href="${pageContext.request.contextPath}/ViewRole" class="nav-item nav-link <%= roleActive %>"><i class="fa fa-users-cog me-2"></i>Quản lí cấp bậc</a>
                <a href="${pageContext.request.contextPath}/audit-log-list" class="nav-item nav-link <%= auditLogActive %>"><i class="fa fa-history me-2"></i>Nhật ký hệ thống</a>
            </div>
        </c:when>
        <c:when test="${roleId == 2}">
            <a href="${pageContext.request.contextPath}/manager-report" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <a href="${pageContext.request.contextPath}/manager-report" class="nav-item nav-link <%= managerReportActive %>"><i class="fa fa-chart-pie me-2"></i>Báo cáo thống kê</a>
                <a href="${pageContext.request.contextPath}/inventory-list" class="nav-item nav-link <%= inventoryListActive %>"><i class="fa fa-boxes me-2"></i>Tồn kho</a>
                <a href="${pageContext.request.contextPath}/supplier-list" class="nav-item nav-link <%= supplierActive %>"><i class="fa fa-truck me-2"></i>Nhà cung cấp</a>
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= purchaseOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
                <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
                <a href="${pageContext.request.contextPath}/return-order-list" class="nav-item nav-link <%= returnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả về NCC</a>
                <a href="${pageContext.request.contextPath}/sales-return-list" class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả từ Sale</a>
                <a href="${pageContext.request.contextPath}/product-list" class="nav-item nav-link <%= productActive %>"><i class="fa fa-box me-2"></i>Sản phẩm</a>
                <a href="${pageContext.request.contextPath}/category-list" class="nav-item nav-link <%= categoryActive %>"><i class="fa fa-tags me-2"></i>Danh mục</a>
                <a href="${pageContext.request.contextPath}/brand-list" class="nav-item nav-link <%= brandActive %>"><i class="fa fa-copyright me-2"></i>Thương hiệu</a>
                <a href="${pageContext.request.contextPath}/unit-list" class="nav-item nav-link <%= unitActive %>"><i class="fa fa-ruler me-2"></i>Đơn vị tính</a>
                <a href="${pageContext.request.contextPath}/inventory-alert" class="nav-item nav-link <%= inventoryAlertActive %>">
                    <i class="fa fa-exclamation-triangle me-2"></i>Cảnh Báo
                    <c:if test="${alertEnabled and alertCount > 0}">
                        <span class="badge bg-danger ms-2">${alertCount}</span>
                    </c:if>
                </a>
                <a href="${pageContext.request.contextPath}/inventory-transactions" class="nav-item nav-link <%= inventoryTransactionActive %>"><i class="fa fa-clipboard-list me-2"></i>Lịch sử biến động kho</a>
                <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="nav-item nav-link <%= inventorySheetActive %>"><i class="fa fa-clipboard-check me-2"></i>Quản lý sheet</a>
            </div>
        </c:when>
        <c:when test="${roleId == 3}">
            <a href="${pageContext.request.contextPath}/inventory-list" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <a href="${pageContext.request.contextPath}/inventory-list" class="nav-item nav-link <%= inventoryListActive %>"><i class="fa fa-boxes me-2"></i>Tồn kho</a>
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= purchaseOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
                <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
                <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="nav-item nav-link <%= inventorySheetActive %>"><i class="fa fa-clipboard-check me-2"></i>Quản lý sheet</a>
                <a href="${pageContext.request.contextPath}/return-order-list" class="nav-item nav-link <%= returnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả về NCC</a>
                <a href="${pageContext.request.contextPath}/sales-return-list" class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả từ Sale</a>
            </div>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/purchase-order/list" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
            <div class="navbar-nav w-100">
                <a href="${pageContext.request.contextPath}/purchase-order/list" class="nav-item nav-link <%= saleOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
                <a href="${pageContext.request.contextPath}/sales-return-list" class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Trả hàng</a>
            </div>
        </c:otherwise>
        </c:choose>
    </nav>
</div>
