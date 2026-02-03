<%-- 
    Document   : navbar
    Created on : 37 thg 1, 2026, 19:05:37
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- Navbar Start -->
<head>
    <meta charset="utf-8">
    <title>Danh sách thông báo</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta content="" name="keywords">
    <meta content="" name="description">
    <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap"
          rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css"
          rel="stylesheet">
    <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css"
          rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .notification-filter-form .d-flex.gap-2 .btn,
        .notification-filter-form .d-flex.gap-2 a.btn {
            height: calc(1.5em + 0.75rem + 2px);
            line-height: 1;
            display: inline-flex;
            align-items: center;
            padding: 0 0.75rem;
        }

        .notification-list-section .badge {
            font-size: 0.75rem;
            padding: 0.35em 0.65em;
        }

        .notification-list-section .page-btn {
            width: 32px;
            height: 32px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: #374151;
            font-weight: 600;
        }

        .notification-list-section .page-btn:hover {
            background-color: #f3f4f6;
        }

        .notification-list-section .page-btn.disabled {
            pointer-events: none;
            opacity: 0.4;
        }

        .notification-list-section .page-btn[type="submit"] {
            cursor: pointer;
            background: transparent;
        }

        .notification-list-section .page-number {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: #374151;
        }

        .notification-list-section .page-jump-form input {
            width: 48px;
            height: 30px;
            text-align: center;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 14px;
        }

        .notification-list-section .page-jump-form input:focus {
            outline: none;
            border-color: #6366f1;
        }

        .notification-list-section .action-btn-group {
            display: flex;
            justify-content: center;
            gap: 1.5rem;
        }

        .notification-list-section .action-btn {
            width: 38px;
            height: 38px;
            border-radius: 50%;
            border: 1px solid #e5e7eb;
            background-color: #fff;
            color: #374151;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 18px;
            text-decoration: none;
            transition: background-color 0.2s ease, color 0.2s ease;
        }

        .notification-list-section .action-btn.action-view:hover {
            background-color: #eef2ff;
            color: #4338ca;
        }

        .notification-list-section .action-col {
            width: 100px;
            text-align: center;
            padding-left: 8px;
            padding-right: 8px;
        }

        .notification-list-section .content-preview {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
    </style>
</head>
<nav class="navbar navbar-expand bg-light navbar-light sticky-top px-4 py-0">
    <a href="index.jsp" class="navbar-brand d-flex d-lg-none me-4">
        <h2 class="text-primary mb-0"><i class="fa fa-hashtag"></i></h2>
    </a>
    <a href="#" class="sidebar-toggler flex-shrink-0">
        <i class="fa fa-bars"></i>
    </a>
    <form class="d-none d-md-flex ms-4">
        <input class="form-control border-0" type="search" placeholder="Search">
    </form>
    <div class="navbar-nav align-items-center ms-auto">
        <%@page import="java.util.List" %>
        <%@page import="model.Notification" %>
        <%@page import="dal.NotificationDAO" %>
        <%@page import="java.util.Collections" %>

        <div class="nav-item dropdown">
            <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                <i class="fa fa-bell me-lg-2"></i>
                <span class="d-none d-lg-inline-flex"></span>
            </a>
            <div class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0"
                 style="min-width: 250px;">
                <% NotificationDAO navDAO=new NotificationDAO(); List<Notification> navList =
                    navDAO.getAllNotifications();

                    if (navList != null && !navList.isEmpty()) {
                    int count = 0;
                    for (Notification n : navList) {
                    if (count >= 3) break;
                %>
                <a href="${pageContext.request.contextPath}/notification-edit?id=<%= n.getNotificationId() %>"
                   class="dropdown-item">
                    <h6 class="fw-normal mb-0">
                        <%= n.getTitle() %>
                    </h6>
                    <small class="text-muted">
                        <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(n.getCreatedAt()) %>
                    </small>
                </a>
                <hr class="dropdown-divider">
                <% count++; } } else { %>
                <div class="dropdown-item text-center">
                    <small class="text-muted">Chưa có thông báo nào</small>
                </div>
                <% } %>
                <a href="${pageContext.request.contextPath}/notification-list"
                   class="dropdown-item text-center">Xem tất cả</a>
            </div>
        </div>
        <div class="nav-item dropdown">
            <% model.User currentUser=(model.User) session.getAttribute("user"); String
                userAvatar="" ; String userName="User" ; if (currentUser !=null) { String
                avatar=currentUser.getAvatar(); if (avatar !=null && !avatar.isEmpty()) {
                userAvatar=request.getContextPath() + "/" + avatar; }
                userName=currentUser.getFullName() !=null ? currentUser.getFullName() : "User" ;
                } %>
            <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                <% if (!userAvatar.isEmpty()) { %>
                <img class="rounded-circle me-lg-2" src="<%= userAvatar %>" alt=""
                     style="width: 40px; height: 40px;">
                <% } %>
                <span class="d-none d-lg-inline-flex">
                    <%= userName %>
                </span>
            </a>
            <div
                class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0">
                <a href="${pageContext.request.contextPath}/profile"
                   class="dropdown-item">My Profile</a>
                <a href="#" class="dropdown-item">Settings</a>
                <a href="${pageContext.request.contextPath}/logout"
                   class="dropdown-item">Log Out</a>
            </div>
        </div>
    </div>
</nav>
<!-- Navbar End -->