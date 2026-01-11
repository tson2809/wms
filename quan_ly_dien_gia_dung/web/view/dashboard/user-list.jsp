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
        <title>User Management</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/user-list.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-4">
            <div class="card user-list-card p-3 shadow-sm">

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
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary w-100">
                                Search
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
    </body>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
</html>

