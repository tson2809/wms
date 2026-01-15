<%-- 
    Document   : sidebar
    Created on : 7 thg 1, 2026, 19:06:03
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- Sidebar Start -->
<div class="sidebar pe-4 pb-3">
    <nav class="navbar bg-light navbar-light">
        <a href="index.jsp" class="navbar-brand mx-4 mb-3">
            <h3 class="text-primary">WMS_HA</h3>
        </a>
        <div class="navbar-nav w-100">
            <a href="index.jsp" class="nav-item nav-link active"><i class="fa fa-tachometer-alt me-2"></i>Trang chủ</a>
            
            <a href="products.jsp" class="nav-item nav-link"><i class="fa fa-boxes me-2"></i>Quản lý hàng hóa</a>
            
            <a href="import.jsp" class="nav-item nav-link"><i class="fa fa-arrow-down me-2"></i>Quản lý nhập kho</a>
            
            <a href="export.jsp" class="nav-item nav-link"><i class="fa fa-arrow-up me-2"></i>Quản lý xuất kho</a>
           
            <a href="inventory.jsp" class="nav-item nav-link"><i class="fa fa-clipboard-check me-2"></i>Kiểm kê</a>
            
            <a href="reports.jsp" class="nav-item nav-link"><i class="fa fa-chart-line me-2"></i>Báo cáo</a>
            
            <a href="${pageContext.request.contextPath}/viewpermission" class="nav-item nav-link"><i class="fa fa-user-shield me-2"></i>Quyền hệ thống</a>
            
            <a href="${pageContext.request.contextPath}/ViewRole" class="nav-item nav-link"><i class="fa fa-users-cog me-2"></i>Quản lí cấp bậc</a>
        </div>
    </nav>
</div>
<!-- Sidebar End -->


