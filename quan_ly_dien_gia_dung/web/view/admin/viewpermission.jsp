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
            <jsp:include page="/view/admin/components/sidebarAdmin.jsp" />

            <!-- Content Start -->
            <div class="content">
                <!-- Navbar Container -->
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <h1 class="display-5 mb-4">Danh Sách Quyền Hệ Thống</h1>

                            <!-- Success/Error Messages -->
                            <c:if test="${param.success == 'true'}">
                                <div class="alert alert-success alert-dismissible fade show" role="alert">
                                    <i class="fa fa-check-circle me-2"></i>
                                    <strong>Thành công!</strong> Đã cập nhật permissions.
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>

                            <c:if test="${param.error == 'true'}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <i class="fa fa-exclamation-circle me-2"></i>
                                    <strong>Lỗi!</strong> Không thể cập nhật permissions. Vui lòng thử lại.
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                        </div>

                        <div class="col-12">
                            <div class="bg-light rounded h-100 p-4">
                                <form id="permissionForm" method="POST" action="viewpermission">

                                    <c:if test="${empty allPermissions}">
                                        <div class="alert alert-warning">
                                            Không có permission nào trong hệ thống!
                                        </div>
                                    </c:if>

                                    <c:if test="${not empty allPermissions}">
                                        <div class="mb-3">
                                            <div class="btn-group" role="group">
                                                <c:forEach var="role" items="${allRoles}">
                                                    <button type="button" 
                                                            class="btn btn-sm btn-outline-primary select-all-role-btn" 
                                                            data-role="${role.roleId}"
                                                            data-role-id="${role.roleId}">
                                                        Select All ${role.roleName}
                                                    </button>
                                                </c:forEach>
                                            </div>
                                        </div>

                                        <div class="table-responsive">
                                            <table class="table table-bordered table-hover">
                                                <thead class="table-primary">
                                                    <tr>
                                                        <th style="width: 25%">Permission</th>
                                                        <th style="width: 35%">Description</th>
                                                        <c:forEach var="role" items="${allRoles}">
                                                            <th class="text-center role-column" data-role-id="${role.roleId}" style="width: 13%">${role.roleName}</th>
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
                                                                    <input class="form-check-input role-checkbox" 
                                                                           type="checkbox" 
                                                                           name="permissions_${role.roleId}"
                                                                           value="${permission.permissionId}" 
                                                                           data-role="${role.roleId}"
                                                                           ${allRolePermissions[role.roleId].contains(permission.permissionId) ? 'checked' : ''}>
                                                                </td>
                                                            </c:forEach>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:if>
                                    <div class="mt-4 text-center">
                                        <button type="submit" class="btn btn-success">
                                            Save Changes
                                        </button>
                                        <button type="button" class="btn btn-secondary" onclick="location.reload()">
                                            Reset
                                        </button>
                                    </div>
                                </form>
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

        <script>
                                            $(document).ready(function () {
                                                // Confirm
                                                $('#permissionForm').submit(function (e) {
                                                    if (!confirm('Bạn có chắc muốn lưu thay đổi cho TẤT CẢ các roles?')) {
                                                        e.preventDefault();
                                                        return false;
                                                    }
                                                });

                                                // Xử lý khi click nút "Select All <RoleName>"
                                                $('.select-all-role-btn').click(function () {
                                                    var roleId = $(this).data('role');
                                                    var btn = $(this);

                                                    // Tìm tất cả checkboxes của role này
                                                    var checkboxes = $('.role-checkbox[data-role="' + roleId + '"]');

                                                    // Kiểm tra xem tất cả đã được check chưa
                                                    var allChecked = checkboxes.filter(':checked').length === checkboxes.length;

                                                    if (allChecked) {
                                                        // Nếu đã check hết → Uncheck all
                                                        checkboxes.prop('checked', false);
                                                        btn.removeClass('btn-primary').addClass('btn-outline-primary');
                                                    } else {
                                                        // Nếu chưa check hết → Check all
                                                        checkboxes.prop('checked', true);
                                                        btn.removeClass('btn-outline-primary').addClass('btn-primary');
                                                    }
                                                });

                                                // Cập nhật style của button khi user manually check/uncheck
                                                $('.role-checkbox').change(function () {
                                                    var roleId = $(this).data('role');

                                                    var checkboxes = $('.role-checkbox[data-role="' + roleId + '"]');
                                                    var btn = $('.select-all-role-btn[data-role="' + roleId + '"]');

                                                    var allChecked = checkboxes.filter(':checked').length === checkboxes.length;

                                                    if (allChecked && checkboxes.length > 0) {
                                                        btn.removeClass('btn-outline-primary').addClass('btn-primary');
                                                    } else {
                                                        btn.removeClass('btn-primary').addClass('btn-outline-primary');
                                                    }
                                                });

                                                // Khởi tạo style của buttons lúc load trang
                                                $('.select-all-role-btn').each(function () {
                                                    var roleId = $(this).data('role');
                                                    var checkboxes = $('.role-checkbox[data-role="' + roleId + '"]');
                                                    var allChecked = checkboxes.filter(':checked').length === checkboxes.length;

                                                    if (allChecked && checkboxes.length > 0) {
                                                        $(this).removeClass('btn-outline-primary').addClass('btn-primary');
                                                    }
                                                });
                                            });
        </script>
    </body>
</html>
