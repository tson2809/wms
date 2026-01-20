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
        <link href="img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
        <link href="lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet" />
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <link href="css/user-list.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <div id="sidebar-container"></div>

            <div class="content">
                <div id="navbar-container"></div>
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="d-flex justify-content-between align-items-center mb-3 user-list-header">
                                <h5 class="mb-0">Members List</h5>
                                <button class="btn add-member-btn">
                                    + Add new member
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
                                            <option value="Staff" ${param.role == 'Sale' ? 'selected' : ''}>Sale</option>
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
                                            Search
                                        </button>
                                        <button type="reset" class="btn btn-secondary w-100" onclick="window.location.href='user-list'">
                                            Clear Filter
                                        </button>
                                    </div>
                                </div>
                            </form>
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Full name</th>
                                        <th>Email</th>
                                        <th>Phone</th>
                                        <th>Role</th>
                                        <th>Status</th>
                                        <th class="action-col">Action</th>
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
                                                    <a href=""
                                                       class="action-btn action-view"
                                                       title="View user">
                                                        <iconify-icon icon="majesticons:eye-line"></iconify-icon>
                                                    </a>
                                                    <a href=""
                                                       class="action-btn action-edit"
                                                       title="Edit user">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>
                                                    <a href=""
                                                       class="action-btn action-deactivate"
                                                       title="Deactivate account">
                                                        <iconify-icon icon="mdi:account-off-outline"></iconify-icon>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>                          
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination-wrapper">
                                <div class="pagination-controls">
                                    <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                       href="user-list?page=${currentPage - 1}">
                                        ‹
                                    </a>
                                    <span class="page-number">
                                        Page
                                        <form action="user-list" method="get" class="page-jump-form">
                                            <input type="number"
                                                   name="page"
                                                   min="1"
                                                   max="${totalPages}"
                                                   value="${currentPage}">
                                        </form>
                                        of ${totalPages}
                                    </span>
                                    <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                       href="user-list?page=${currentPage + 1}">
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
    <script src="js/loadComponents.js"></script>
    <script src="js/main.js"></script>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
</html>
