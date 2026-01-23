<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="sidebar pe-4 pb-3">
    <nav class="navbar bg-light navbar-light">
        <a href="profile" class="navbar-brand mx-4 mb-3">
            <h4 class="text-primary">
                <i class="fa fa-user me-2"></i>My Account
            </h4>
        </a>

        <div class="navbar-nav w-100">

            <a href="profile"
               class="nav-item nav-link ${activePage == 'profile' ? 'active' : ''}">
                <i class="fa fa-id-card me-2"></i>Profile
            </a>

            <a href="change-password"
               class="nav-item nav-link ${activePage == 'password' ? 'active' : ''}">
                <i class="fa fa-key me-2"></i>Change Password
            </a>
                
                <a href="${pageContext.request.contextPath}/indexAdmin"
               class="nav-item nav-link ${activePage == 'password' ? 'active' : ''}">
                <i class="fa fa-tachometer-alt me-2"></i>Trang chủ
            </a>

            <hr>
        </div>
    </nav>
</div>
