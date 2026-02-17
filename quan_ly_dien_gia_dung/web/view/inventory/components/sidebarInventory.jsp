<%-- 
    Document   : sidebarInventory
    Created on : Feb 5, 2026, 4:40:51 PM
    Author     : hung
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="sidebar pe-4 pb-3">
    <nav class="navbar bg-light navbar-light">
        <a href="${pageContext.request.contextPath}/inventory-list"
           class="navbar-brand mx-4 mb-3">
            <h4 class="text-primary">
                <i class="fa fa-warehouse me-2"></i>Inventory
            </h4>
        </a>
        <div class="navbar-nav w-100">
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
            <a href="${pageContext.request.contextPath}/inventory-alert"
               class="nav-item nav-link ${activePage == 'alert' ? 'active' : ''}">
                <i class="fa fa-exclamation-triangle me-2"></i>
                Alerts
                <c:if test="${alertCount > 0}">
                    <span class="badge bg-danger ms-2">${alertCount}</span>
                </c:if>
            </a>
            <hr>
            <a href="${pageContext.request.contextPath}/home"
               class="nav-item nav-link">
                <i class="fa fa-tachometer-alt me-2"></i>
                Home
            </a>

        </div>
    </nav>
</div>
