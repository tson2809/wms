<%-- 
    Document   : sidebar
    Created on : 7 thg 1, 2026, 19:06:03
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- Sidebar Start -->
<div class="sidebar pe-6 pb-5">
    <nav class="navbar bg-light navbar-light">
        <div class="navbar-nav w-100">

            <a href="${pageContext.request.contextPath}/indexManager" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">WMS_HA</h3>
            </a>
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
            <a href="i${pageContext.request.contextPath}/indexManager" class="nav-item nav-link "><i class="fa fa-tachometer-alt me-2"></i>Trang chủ</a>    
        </div>
    </nav>

</div>
<!-- Sidebar End -->


