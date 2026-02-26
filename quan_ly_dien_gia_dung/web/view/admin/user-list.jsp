<%-- 
    Document   : user-list
    Created on : Jan 10, 2026, 8:14:27 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
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
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">

        <!-- Libraries Stylesheet -->
        <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet" />

        <!-- Customized Bootstrap Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">

        <!-- Template Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/user-list.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/admin/components/sidebarAdmin.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="d-flex justify-content-between align-items-center mb-3 user-list-header">
                                <h5 class="mb-0">Members List</h5>
                                <button class="btn add-member-btn">
                                    <a  href="${pageContext.request.contextPath}/user-add">
                                        Thêm người dùng
                                    </a>
                                </button>
                            </div>
                            <form action="user-list" method="get" class="user-filter-form mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-4">
                                        <label class="form-label">Search</label>
                                        <input type="text"
                                               name="keyword"
                                               value="${param.keyword}"
                                               class="form-control"
                                               placeholder="Search by name">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Role</label>
                                        <select name="role" class="form-select">
                                            <option value="">All roles</option>
                                            <option value="Admin" ${param.role == 'Admin' ? 'selected' : ''}>Admin</option>
                                            <option value="Manager" ${param.role == 'Manager' ? 'selected' : ''}>Manager</option>
                                            <option value="Staff" ${param.role == 'Staff' ? 'selected' : ''}>Staff</option>                                          
                                        </select>
                                    </div>

                                    <div class="col-md-3">
                                        <label class="form-label">Status</label>
                                        <select name="active" class="form-select">
                                            <option value="">All</option>
                                            <option value="1" ${param.active == '1' ? 'selected' : ''}>Active</option>
                                            <option value="0" ${param.active == '0' ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2 d-flex gap-2">
                                        <button type="submit" class="btn btn-primary w-100">
                                            Tìm 
                                        </button>
                                        <button type="reset" class="btn btn-secondary w-100" onclick="window.location.href = 'user-list'">
                                            Clear
                                        </button>
                                    </div>
                                </div>
                            </form>
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>
                                            <a href="user-list?sort=full_name&dir=${param.sort == 'full_name' && param.dir == 'asc' ? 'desc' : 'asc'}">
                                                Tên
                                                <c:if test="${param.sort == 'full_name'}">
                                                    ${param.dir == 'asc' ? '▲' : '▼'}
                                                </c:if>
                                            </a>
                                        </th>
                                        <th>Email</th>
                                        <th>SĐT</th>
                                        <th>
                                            <a href="user-list?sort=role&dir=${param.sort == 'role' && param.dir == 'asc' ? 'desc' : 'asc'}">
                                                Vai trò
                                                <c:if test="${param.sort == 'role'}">
                                                    ${param.dir == 'asc' ? '▲' : '▼'}
                                                </c:if>
                                            </a>
                                        </th>
                                        <th>
                                            <a href="user-list?sort=status&dir=${param.sort == 'status' && param.dir == 'asc' ? 'desc' : 'asc'}">
                                                Trạng Thái
                                                <c:if test="${param.sort == 'status'}">
                                                    ${param.dir == 'asc' ? '▲' : '▼'}
                                                </c:if>
                                            </a>
                                        </th>
                                        <th class="action-col">Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${users}" var="u">
                                        <tr>
                                            <td>${u.fullName}</td>
                                            <td>${u.email}</td>
                                            <td>${u.phone}</td>
                                            <td>${u.roleName}</td>
                                            <td>
                                                <span class="status-dot ${u.active ? 'status-active' : 'status-inactive'}"></span>
                                                ${u.active ? 'Active' : 'Inactive'}
                                            </td>
                                            <td class="action-col">
                                                <div class="action-btn-group">
                                                    <a href="${pageContext.request.contextPath}/user-detail?id=${u.userId}"
                                                       class="action-btn action-view"
                                                       title="View user">
                                                        <iconify-icon icon="majesticons:eye-line"></iconify-icon>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/UpdateUser?id=${u.userId}"
                                                       class="action-btn action-edit"
                                                       title="Edit user">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>
                                                    <c:choose>
                                                        <c:when test="${u.active}">
                                                            <form method="post" action="${pageContext.request.contextPath}/user-toggle-status" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn deactive user này?')">
                                                                <input type="hidden" name="id" value="${u.userId}">
                                                                <input type="hidden" name="active" value="false">
                                                                <button type="submit" class="btn btn-sm btn-secondary" title="Deactive">
                                                                    <i class="fa fa-user-slash"></i>
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="post" action="${pageContext.request.contextPath}/user-toggle-status" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn active user này?')">
                                                                <input type="hidden" name="id" value="${u.userId}">
                                                                <input type="hidden" name="active" value="true">
                                                                <button type="submit" class="btn btn-sm btn-success" title="Active">
                                                                    <i class="fa fa-user-check"></i>
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>                          
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination-wrapper">
                                <div class="pagination-controls">
                                    <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                       href="user-list?page=${currentPage - 1}&keyword=${param.keyword}&role=${param.role}&active=${param.active}&sort=${param.sort}&dir=${param.dir}">
                                        ‹
                                    </a>
                                    <span class="page-number">
                                        Page
                                        <form action="user-list" method="get" class="page-jump-form">
                                            <input type="hidden" name="keyword" value="${param.keyword}">
                                            <input type="hidden" name="role" value="${param.role}">
                                            <input type="hidden" name="active" value="${param.active}">
                                            <input type="hidden" name="sort" value="${param.sort}">
                                            <input type="hidden" name="dir" value="${param.dir}">
                                            <input type="number"
                                                   name="page"
                                                   min="1"
                                                   max="${totalPages}"
                                                   value="${currentPage}"
                                                   onchange="this.form.submit()">
                                        </form>
                                        of ${totalPages}
                                    </span>
                                    <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                       href="user-list?page=${currentPage + 1}&keyword=${param.keyword}&role=${param.role}&active=${param.active}&sort=${param.sort}&dir=${param.dir}">
                                        ›
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</html>
