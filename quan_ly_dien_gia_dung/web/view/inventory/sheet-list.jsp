<%-- 
    Document   : sheet-list
    Created on : Feb 1, 2026, 11:55:05 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Sheet List</title>
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
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/admin/components/sidebarAdmin.jsp" />
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="inventory-card p-4">
                        <div class="d-flex justify-content-between mb-3">
                            <h5>Inventory Sheets </h5>
                            <a href="${pageContext.request.contextPath}/inventory-sheet-create"
                               class="btn btn-success">
                                + Create New Sheet
                            </a>
                        </div>
                        <form method="get" action="${pageContext.request.contextPath}/inventory-sheet-list"
                              class="row g-2 mb-3">

                            <div class="col-auto">
                                <select name="year" class="form-select">
                                    <c:forEach begin="2023" end="2030" var="y">
                                        <option value="${y}" ${y == year ? 'selected' : ''}>
                                            ${y}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-auto">
                                <select name="month" class="form-select">
                                    <option value="">All months</option>
                                    <c:forEach begin="1" end="12" var="m">
                                        <option value="${m}" ${m == month ? 'selected' : ''}>
                                            Month ${m}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-auto">
                                <button class="btn btn-primary">Filter</button>
                            </div>

                        </form>
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Sheet Code</th>
                                    <th>Date</th>
                                    <th>Category</th>
                                    <th>Created By</th>
                                    <th>Status</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${sheets}" var="s">
                                    <tr>
                                        <td>${s.sheetCode}</td>
                                        <td>${s.inventoryDate}</td>
                                        <td>${s.categoryName}</td>
                                        <td>${s.createdByName}</td>
                                        <td>
                                            <span class="badge
                                                  ${s.status == 'draft' ? 'bg-secondary' :
                                                    s.status == 'submitted' ? 'bg-warning' : 'bg-success'}">
                                                      ${s.status}
                                                  </span>
                                            </td>
                                            <td class="text-center">
                                                <c:if test="${s.status ne 'draft'}">
                                                    <a href="${pageContext.request.contextPath}/inventory-sheet-view?id=${s.sheetId}"
                                                       class="btn btn-sm btn-outline-primary">
                                                        View
                                                    </a>
                                                </c:if>
                                                <c:if test="${s.status == 'draft'}">
                                                    <a href="inventory-sheet-edit?id=${s.sheetId}"
                                                       class="btn btn-sm btn-outline-warning">
                                                        Edit
                                                    </a>
                                                </c:if>
                                                <c:if test="${s.status == 'submitted'}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/inventory-sheet-approve"
                                                          style="display:inline;">
                                                        <input type="hidden" name="id" value="${s.sheetId}">
                                                        <input type="hidden" name="action" value="approve">
                                                        <button class="btn btn-sm btn-outline-success"
                                                                onclick="return confirm('Approve this sheet?')">
                                                            Approve
                                                        </button>
                                                    </form>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/inventory-sheet-approve"
                                                          style="display:inline;">
                                                        <input type="hidden" name="id" value="${s.sheetId}">
                                                        <input type="hidden" name="action" value="reject">
                                                        <button class="btn btn-sm btn-outline-danger"
                                                                onclick="return confirm('Reject this sheet?')">
                                                            Reject
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
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
