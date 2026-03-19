<%-- 
    Document   : sidebar
    Created on : 7 thg 1, 2026, 19:06:03
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
    String currentURI = request.getRequestURI(); 
    String indexActive = currentURI.contains("/indexStaff") ? "active" : "";
    String goodsReceiptActive = (currentURI.contains("/goods-receipt-list") || currentURI.contains("/goods-receipt-detail") || currentURI.contains("/goods-receipt-add")) ? "active" : "";
    String goodsIssueActive = (currentURI.contains("/goods-issue-list") || currentURI.contains("/goods-issue-detail") || currentURI.contains("/goods-issue-add")) ? "active" : "";
    String returnActive = (currentURI.contains("/return-order-list") || currentURI.contains("/return-add") || currentURI.contains("/return-view") || currentURI.contains("/return-edit")) ? "active" : "";
    String salesReturnActive = currentURI.contains("/sales-return-list") ? "active" : "";
%>

<!-- Sidebar Start -->
<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <a href="${pageContext.request.contextPath}/indexStaff" class="navbar-brand mx-4 mb-3">
            <h3 class="text-primary">WMS_HA</h3>
        </a>
        <div class="navbar-nav w-100">
            <a href="${pageContext.request.contextPath}/indexStaff" class="nav-item nav-link <%= indexActive %>"><i class="fa fa-tachometer-alt me-2"></i>Trang chủ</a>
            <a href="${pageContext.request.contextPath}/goods-receipt-list" class="nav-item nav-link <%= goodsReceiptActive %>"><i class="fa fa-file-import me-2"></i>Phiếu nhập kho</a>
            <a href="${pageContext.request.contextPath}/goods-issue-list" class="nav-item nav-link <%= goodsIssueActive %>"><i class="fa fa-file-export me-2"></i>Phiếu xuất kho</a>
            <a href="${pageContext.request.contextPath}/inventory-list"
               class="nav-item nav-link ${activePage == 'inventoryList' ? 'active' : ''}">
                <i class="fa fa-boxes me-2"></i>
                Tồn kho
            </a>
            <a href="${pageContext.request.contextPath}/inventory-sheet-list"
               class="nav-item nav-link ${activePage == 'sheetList' ? 'active' : ''}">
                <i class="fa fa-clipboard-list me-2"></i>
                Quản lý sheet
            </a>
            <a href="${pageContext.request.contextPath}/return-order-list"
               class="nav-item nav-link <%= returnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả về NCC</a>
            <a href="${pageContext.request.contextPath}/sales-return-list"
               class="nav-item nav-link <%= salesReturnActive %>"><i class="fa fa-undo me-2"></i>Đơn trả từ Sale</a>
        </div>        
    </nav>
</div>
<!-- Sidebar End -->


