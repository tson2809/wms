<%-- 
    Document : sidebar 
    Created on : 7 thg 1, 2026, 19:06:03 
    Author : thais 
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<% String currentURI=request.getRequestURI(); String
    indexActive=currentURI.contains("/indexManager") ? "active" : "" ; String
    supplierActive=(currentURI.contains("/supplier-list") || currentURI.contains("/supplier-add") ||
    currentURI.contains("/supplier-detail")) ? "active" : "" ; String
    goodsReceiptActive=(currentURI.contains("/goods-receipt-list") ||
    currentURI.contains("/goods-receipt-detail") || currentURI.contains("/goods-receipt-add"))
    ? "active" : "" ; String returnActive=(currentURI.contains("/return-list") ||
    currentURI.contains("/return-add") || currentURI.contains("/return-view")) ? "active" : "" ;
    String unitActive=(currentURI.contains("/unit-list") || currentURI.contains("/unit-add") ||
    currentURI.contains("/unit-edit")) ? "active" : "" ; %>
<!-- Sidebar Start -->
<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <a href="${pageContext.request.contextPath}/indexManager"
           class="navbar-brand mx-4 mb-3">
            <h3 class="text-primary">WMS_HA</h3>
        </a>
        <div class="navbar-nav w-100">
            <a href="${pageContext.request.contextPath}/indexManager"
               class="nav-item nav-link <%= indexActive %>"><i
                    class="fa fa-tachometer-alt me-2"></i>Trang
                chủ</a>
            <a href="${pageContext.request.contextPath}/supplier-list"
               class="nav-item nav-link <%= supplierActive %>"><i
                    class="fa fa-truck me-2"></i>Nhà cung
                cấp</a>
            <a href="${pageContext.request.contextPath}/goods-receipt-list"
               class="nav-item nav-link <%= goodsReceiptActive %>"><i
                    class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
            <a href="${pageContext.request.contextPath}/return-order-list"
               class="nav-item nav-link <%= returnActive %>"><i class="fa fa-undo me-2"></i>Đơn
                trả hàng</a>
            <a href="/quan_ly_dien_gia_dung/product-list" class="nav-item nav-link"><i
                    class="fa fa-box me-2"></i>Sản phẩm</a>
            <a href="${pageContext.request.contextPath}/unit-list"
               class="nav-item nav-link <%= unitActive %>"><i
                    class="fa fa-ruler me-2"></i>Đơn vị tính</a>                                 
            <a href="${pageContext.request.contextPath}/inventory-alert"
               class="nav-item nav-link ${activePage == 'alert' ? 'active' : ''}">
                <i class="fa fa-exclamation-triangle me-2"></i>
                Alerts
                <c:if test="${alertEnabled and alertCount > 0}">
                    <span class="badge bg-danger ms-2">${alertCount}</span>
                </c:if>
            </a>
            <a href="${pageContext.request.contextPath}/inventory-transactions"
               class="nav-item nav-link ${activePage == 'inventoryTransaction' ? 'active' : ''}">
                <i class="fa fa-clipboard-list me-2"></i>
                Inventory Transactions
            </a>
        </div>
    </nav>
</div>
<!-- Sidebar End -->

