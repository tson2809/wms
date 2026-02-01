<%-- 
    Document   : inventory-list
    Created on : Feb 1, 2026, 11:40:28 AM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>View Product Inventory</title>
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
        <link href="${pageContext.request.contextPath}/css/inventory.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/admin/components/sidebarAdmin.jsp" />
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="inventory-card p-4">

                                <div class="d-flex justify-content-between align-items-center mb-3 inventory-header">
                                    <h5 class="mb-0">Product Inventory</h5>
                                </div>
                                <form method="get" action="${pageContext.request.contextPath}/inventory-list">
                                    Product Name:
                                    <input type="text" name="keyword" value="${param.keyword}" />

                                    Category:
                                    <select name="categoryId">
                                        <option value="">All</option>
                                        <c:forEach items="${categories}" var="c">
                                            <option value="${c.categoryId}"
                                                    <c:if test="${param.categoryId == c.categoryId.toString()}">
                                                        selected
                                                    </c:if>
                                                    >
                                                ${c.categoryName}
                                            </option>
                                        </c:forEach>
                                    </select>

                                    Status:
                                    <select name="status">
                                        <option value="">All</option>
                                        <option value="In Stock" ${param.status == 'In Stock' ? 'selected' : ''}>
                                            In Stock
                                        </option>
                                        <option value="Out of Stock" ${param.status == 'Out of Stock' ? 'selected' : ''}>
                                            Out of Stock
                                        </option>
                                    </select>


                                    <button type="submit" class="btn btn-primary">
                                        Search
                                    </button>

                                    <a href="${pageContext.request.contextPath}/inventory-list"
                                       class="btn btn-success">
                                        Clear
                                    </a>
                                </form>

                                <table border="1" width="100%" cellpadding="8" cellspacing="0">
                                    <tr style="background-color:#f0f0f0;">
                                        <th>ID</th>
                                        <th>
                                            <a href="inventory-list?sort=name
                                               &dir=${param.sort == 'name' && param.dir == 'asc' ? 'desc' : 'asc'}
                                               &keyword=${param.keyword}
                                               &categoryId=${param.categoryId}
                                               &status=${param.status}">
                                                Product Name
                                                <c:if test="${param.sort == 'name'}">
                                                    ${param.dir == 'asc' ? '▲' : '▼'}
                                                </c:if>
                                            </a>
                                        </th>
                                        <th>Category</th>
                                        <th>
                                            <a href="inventory-list?sort=quantity
                                               &dir=${param.sort == 'quantity' && param.dir == 'asc' ? 'desc' : 'asc'}
                                               &keyword=${param.keyword}
                                               &categoryId=${param.categoryId}
                                               &status=${param.status}">
                                                Quantity
                                                <c:if test="${param.sort == 'quantity'}">
                                                    ${param.dir == 'asc' ? '▲' : '▼'}
                                                </c:if>
                                            </a>
                                        </th>

                                        <th>Status</th>
                                    </tr>

                                    <c:forEach items="${list}" var="p">
                                        <tr>
                                            <td>${p.productId}</td>
                                            <td>${p.productName}</td>
                                            <td>${p.categoryName}</td>
                                            <td>${p.totalQuantity} 
                                                <c:if test="${not empty p.unitName}">
                                                    ${p.unitName}
                                                </c:if></td>
                                            <td>${p.status}</td>
                                        </tr>
                                    </c:forEach>
                                </table>
                                <div class="pagination-wrapper">
                                    <div class="pagination-controls">

                                        <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                           href="${pageContext.request.contextPath}/inventory-list?page=${currentPage - 1}&keyword=${param.keyword}&categoryId=${param.categoryId}&status=${param.status}&sort=${param.sort}&dir=${param.dir}">
                                            ‹
                                        </a>
                                        <span class="page-jump-form">
                                            Page
                                            <form action="${pageContext.request.contextPath}/inventory-list"
                                                  method="get"
                                                  style="display:inline;">      
                                                <input type="hidden" name="keyword" value="${param.keyword}">
                                                <input type="hidden" name="categoryId" value="${param.categoryId}">
                                                <input type="hidden" name="status" value="${param.status}">
                                                <input type="hidden" name="sort" value="${param.sort}">
                                                <input type="hidden" name="dir" value="${param.dir}">
                                                <input type="number"
                                                       name="page"
                                                       min="1"
                                                       max="${totalPages}"
                                                       value="${currentPage}"
                                                       style="width:60px;"
                                                       onchange="this.form.submit()">
                                            </form>
                                            of ${totalPages}
                                        </span>


                                        <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                           href="${pageContext.request.contextPath}/inventory-list?page=${currentPage + 1}&keyword=${param.keyword}&categoryId=${param.categoryId}&status=${param.status}&sort=${param.sort}&dir=${param.dir}">
                                            ›
                                        </a>

                                    </div>
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
