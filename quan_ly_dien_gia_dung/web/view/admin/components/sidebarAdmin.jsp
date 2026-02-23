<%-- 
    Document   : sidebar
    Created on : 7 thg 1, 2026, 19:06:03
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String currentURI = request.getRequestURI();
    String indexActive = "";
    String userListActive = "";
    String permissionActive = "";
    String roleActive = "";
    
    if (currentURI.endsWith("/indexAdmin.jsp") || currentURI.endsWith("/")) {
        indexActive = "active";
    } else if (currentURI.contains("/user-list") || currentURI.contains("/user-detail") || currentURI.contains("/UpdateUser") || currentURI.contains("/user-add")) {
        userListActive = "active";
    } else if (currentURI.contains("/viewpermission")) {
        permissionActive = "active";
    } else if (currentURI.contains("/ViewRole") || currentURI.contains("/viewrole")) {
        roleActive = "active";
    }
%>
<!-- Sidebar Start -->
<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <a href="${pageContext.request.contextPath}/indexAdmin" class="navbar-brand mx-4 mb-3">
            <h3 class="text-primary">WMS_HA</h3>
        </a>
        <div class="navbar-nav w-100">
            <a href="${pageContext.request.contextPath}/indexAdmin" class="nav-item nav-link <%= indexActive %>"><i class="fa fa-tachometer-alt me-2"></i>Trang chủ</a>
            
            <a href="${pageContext.request.contextPath}/user-list" class="nav-item nav-link <%= userListActive %>"><i class="fa fa-solid fa-user me-2"></i>Quản lý người dùng</a>
            
            <a href="${pageContext.request.contextPath}/viewpermission" class="nav-item nav-link <%= permissionActive %>"><i class="fa fa-user-shield me-2"></i>Quyền hệ thống</a>
            
            <a href="${pageContext.request.contextPath}/ViewRole" class="nav-item nav-link <%= roleActive %>"><i class="fa fa-users-cog me-2"></i>Quản lí cấp bậc</a>
            
            <a href="${pageContext.request.contextPath}/inventory-list"
               class="nav-item nav-link ${activePage == 'inventoryList' ? 'active' : ''}">
                <i class="fa fa-boxes me-2"></i>
                Inventory List
            </a>
            <a href="${pageContext.request.contextPath}/inventory-sheet-list"
               class="nav-item nav-link ${activePage == 'sheetList' ? 'active' : ''}">
                <i class="fa fa-clipboard-list me-2"></i>
                Manage Sheets
            </a>       
        </div>
    </nav>
</div>
<!-- Sidebar End -->


