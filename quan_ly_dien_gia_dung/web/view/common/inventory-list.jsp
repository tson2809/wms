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
            <jsp:include page="/view/common/components/RoleSideBar.jsp" />
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="inventory-card p-4">

                                <div class="d-flex justify-content-between align-items-center mb-3 inventory-header">
                                    <h5 class="mb-0">Product Inventory</h5>
                                </div>
                                <div class="inventory-toolbar">
                                    <a href="#" class="btn btn-primary btn-sm">
                                        + Import
                                    </a>
                                    <a href="#" class="btn btn-warning btn-sm">
                                        + Export
                                    </a>
                                    <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="btn btn-secondary btn-sm">
                                        Inventory Check
                                    </a>
                                    <a href="${pageContext.request.contextPath}/export-inventory?keyword=${param.keyword}&categoryId=${param.categoryId}&status=${param.status}"
                                       class="btn btn-success btn-sm">
                                        Export Excel
                                    </a>


                                </div>
                                <form class="filter-bar" method="get"
                                      action="${pageContext.request.contextPath}/inventory-list">

                                    <div class="row g-3 align-items-end">

                                        <div class="col-md-3">
                                            <label>Search</label>
                                            <input class="form-control"
                                                   name="keyword"
                                                   placeholder="Product name"
                                                   value="${param.keyword}">
                                        </div>

                                        <div class="col-md-2">
                                            <label>Category</label>
                                            <select class="form-select" name="categoryId">
                                                <option value="">All</option>
                                                <c:forEach items="${categories}" var="c">
                                                    <option value="${c.categoryId}"
                                                            ${param.categoryId eq c.categoryId ? 'selected' : ''}>
                                                        ${c.categoryName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="col-md-2">
                                            <label>Stock Status</label>
                                            <select class="form-select" name="status">
                                                <option value="">All</option>
                                                <option value="In Stock" ${param.status=='In Stock'?'selected':''}>In stock</option>
                                                <option value="Low" ${param.status=='Low'?'selected':''}>Low stock</option>
                                                <option value="Out of Stock" ${param.status=='Out of Stock'?'selected':''}>Out of stock</option>
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <button class="btn btn-primary">Search</button>
                                            <a class="btn btn-outline-secondary"
                                               href="${pageContext.request.contextPath}/inventory-list">
                                                Reset
                                            </a>
                                        </div>
                                    </div>
                                </form>

                                <div class="summary-cards">
                                    <div class="summary-card">
                                        <div class="title">Total SKU</div>
                                        <div class="value">${sum.totalSku}</div>
                                    </div>

                                    <div class="summary-card">
                                        <div class="title">Total Quantity</div>
                                        <div class="value">${sum.totalQty}</div>
                                    </div>

                                    <div class="summary-card">
                                        <div class="title">Low Stock</div>
                                        <div class="value">${sum.lowStock}</div>
                                    </div>

                                    <div class="summary-card">
                                        <div class="title">Out of Stock</div>
                                        <div class="value">${sum.outStock}</div>
                                    </div>
                                </div>

                                <table class="table table-bordered inventory-table">
                                    <thead>
                                        <tr>
                                            <th>Image</th>
                                            <th>SKU</th>
                                            <th>Product</th>
                                            <th>Variant</th>
                                            <th>Category</th>
                                            <th>Brand</th>
                                            <th>Cost</th>
                                            <th>Price</th>
                                            <th>Qty</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        <c:forEach items="${list}" var="p">
                                            <tr>
                                                <td>
                                                    <img width="40"
                                                         src="${empty p.image ? pageContext.request.contextPath.concat('/img/no-image.png') : p.image}">
                                                </td>
                                                <td>${p.sku}</td>
                                                <td>${p.productName}</td>
                                                <td>${p.variantName}</td>
                                                <td>${p.categoryName}</td>
                                                <td>${p.brandName}</td>
                                                <td>${p.costPrice}</td>
                                                <td>${p.salePrice}</td>
                                                <td>${p.totalQuantity}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${p.status == 'Out of Stock'}">
                                                            <span class="badge bg-danger">Out</span>
                                                        </c:when>
                                                        <c:when test="${p.status == 'Low'}">
                                                            <span class="badge bg-warning text-dark">Low</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-success">In</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                            </tr>
                                        </c:forEach>
                                    </tbody>
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
