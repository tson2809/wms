<%-- 
    Document   : sidebar
    Created on : 7 thg 1, 2026, 19:06:03
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String currentURI = request.getRequestURI();
    String salesReturnActive = currentURI.contains("/sales-return-list") ? "active" : "";
    String saleOrderActive = currentURI.contains("/purchase-order") || currentURI.contains("/sale-order") ? "active" : "";

%>

<!-- Sidebar Start -->
<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <a href="${pageContext.request.contextPath}/indexSale" class="navbar-brand mx-4 mb-3">
            <h3 class="text-primary">WMS_HA</h3>
        </a>
        <div class="navbar-nav w-100">
            <a href="${pageContext.request.contextPath}/indexSale" class="nav-item nav-link "><i class="fa fa-tachometer-alt me-2"></i>Trang chủ</a>
            <a href="${pageContext.request.contextPath}/sales-return-list"
               class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Trả hàng</a>
            <a href="${pageContext.request.contextPath}/purchase-order/list"
               class="nav-item nav-link <%= saleOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Đơn đặt hàng</a>
        </div>
    </nav>
</div>
<!-- Sidebar End -->


