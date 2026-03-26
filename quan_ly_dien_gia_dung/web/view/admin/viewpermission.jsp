<%-- 
    Document   : viewpermissions
    Created on : 8 thg 1, 2026, 21:29:56
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="utf-8">
        <title>View Permission</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <meta content="" name="keywords">
        <meta content="" name="description">

        <!-- Favicon -->
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">

        <!-- Google Web Fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">

        <!-- Icon Font Stylesheet -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">

        <!-- Libraries Stylesheet -->
        <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet" />

        <!-- Customized Bootstrap Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">

        <!-- Template Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <!-- Spinner Start -->
            <div id="spinner" class="show bg-white position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
                <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status">
                    <span class="sr-only">Loading...</span>
                </div>
            </div>
            <!-- Spinner End -->

            <!-- Sidebar Container -->
            <jsp:include page="/view/common/components/sidebar.jsp" />

            <!-- Content Start -->
            <div class="content">
                <!-- Navbar Container -->
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <h1 class="display-5 mb-4">Danh Sách Quyền Hệ Thống</h1>
                        </div>

                        <div class="col-12">
                            <div class="bg-light rounded h-100 p-4">
                                <c:if test="${empty allPermissions}">
                                    <div class="alert alert-warning">
                                        Không có permission nào trong hệ thống!
                                    </div>
                                </c:if>

                                <c:if test="${not empty allPermissions}">
                                    <div class="table-responsive">
                                        <table class="table table-bordered table-hover">
                                            <thead class="table-primary">
                                                <tr>
                                                    <th style="width: 25%">Danh sách</th>
                                                    <th style="width: 35%">Mô tả</th>
                                                    <c:forEach var="role" items="${allRoles}">
                                                        <th class="text-center role-column" data-role-id="${role.roleId}" style="width: 11%">${role.roleName}</th>
                                                    </c:forEach>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="permission" items="${allPermissions}">
                                                    <tr>
                                                        <td class="align-middle">
                                                            <strong>${permission.permissionName}</strong>
                                                        </td>

                                                        <td class="align-middle">
                                                            <small class="text-muted">${permission.permissionDescription}</small>
                                                        </td>

                                                        <c:forEach var="role" items="${allRoles}">
                                                            <td class="text-center align-middle role-column" data-role-id="${role.roleId}">
                                                                <input class="form-check-input"
                                                                       type="checkbox"
                                                                       disabled
                                                                       ${allRolePermissions[role.roleId].contains(permission.permissionId) ? 'checked' : ''}>
                                                            </td>
                                                        </c:forEach>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                    </div>
                </div>

                <div class="container-fluid pt-4 px-4">
                </div>
            </div>
            <!-- Content End -->
        </div>

        <!-- JavaScript Libraries -->
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/chart/chart.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/easing/easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/waypoints/waypoints.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/owlcarousel/owl.carousel.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/moment.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/moment-timezone.min.js"></script>
        <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/tempusdominus-bootstrap-4.min.js"></script>

        <!-- Component Loader -->
        <!-- Template Javascript -->
        <script src="${pageContext.request.contextPath}/js/main.js"></script>

    </body>
</html>
