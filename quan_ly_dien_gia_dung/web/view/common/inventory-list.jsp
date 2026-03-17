<%-- 
    Document   : inventory-list
    Created on : Feb 1, 2026, 11:40:28 AM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page import="java.util.*"%>

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
            <c:choose>
                <c:when test="${sessionScope.user.role.roleId == 2}">
                    <jsp:include page="/view/manager/components/sidebarManager.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 3}">
                    <jsp:include page="/view/staff/components/sidebarStaff.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/view/common/components/RoleSideBar.jsp" />
                </c:otherwise>
            </c:choose>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="inventory-card p-4">

                                <div class="d-flex justify-content-between align-items-center mb-3 inventory-header">
                                    <h5 class="mb-0">Tồn kho</h5>
                                </div>
                                <div class="inventory-toolbar">
                                    <a href="${pageContext.request.contextPath}/goods-receipt-list" class="btn btn-primary btn-sm">
                                        + Nhập kho
                                    </a>
                                    <a href="${pageContext.request.contextPath}/goods-issue-list" class="btn btn-warning btn-sm">
                                        + Xuất kho
                                    </a>
                                    <a href="${pageContext.request.contextPath}/inventory-sheet-list" class="btn btn-secondary btn-sm">
                                        Kiểm tra kho
                                    </a>
                                    <a href="${pageContext.request.contextPath}/export-inventory?keyword=${param.keyword}&categoryId=${param.categoryId}&status=${param.status}${queryParams}"
                                       class="btn btn-success btn-sm">
                                        Xuất Excel
                                    </a>
                                </div>
                                <form class="filter-bar"
                                      method="get"
                                      action="${pageContext.request.contextPath}/inventory-list">
                                    <div class="row g-3 align-items-end">
                                        <div class="col-md-4">
                                            <label>Tên sản phẩm</label>
                                            <input class="form-control"
                                                   name="keyword"
                                                   placeholder="Tên sản phẩm"
                                                   value="${param.keyword}">
                                        </div>
                                        <div class="col-md-3">
                                            <label>Thể loại</label>
                                            <select class="form-select" name="categoryId" onchange="this.form.submit()">
                                                <option value="">All</option>
                                                <c:forEach items="${categories}" var="c">
                                                    <option value="${c.categoryId}"
                                                            ${param.categoryId eq c.categoryId ? 'selected' : ''}>
                                                        ${c.categoryName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label>Trạng thái</label>
                                            <select class="form-select" name="status">
                                                <option value="">All</option>
                                                <option value="In Stock"
                                                        ${param.status=='In Stock'?'selected':''}>In stock</option>
                                                <option value="Low"
                                                        ${param.status=='Low'?'selected':''}>Low stock</option>
                                                <option value="Out of Stock"
                                                        ${param.status=='Out of Stock'?'selected':''}>Out of stock</option>
                                            </select>
                                        </div>
                                        <div class="col-md-2 d-flex gap-2">
                                            <button class="btn btn-primary w-100">
                                                Tìm
                                            </button>
                                            <a class="btn btn-outline-secondary w-100"
                                               href="${pageContext.request.contextPath}/inventory-list">
                                                Reset
                                            </a>
                                        </div>
                                    </div>
                                    <c:if test="${not empty param.categoryId}">
                                        <div class="mt-3">
                                            <button type="button"
                                                    class="btn btn-outline-primary btn-sm"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#filterModal">

                                                <i class="fa fa-filter"></i> Lọc
                                            </button>
                                        </div>
                                    </c:if>
                                    <div class="selected-filters">
                                        <c:if test="${not empty param.brandId}">
                                            <c:url var="removeBrandUrl" value="/inventory-list">
                                                <c:forEach var="q" items="${paramValues}">
                                                    <c:if test="${q.key != 'brandId'}">
                                                        <c:forEach var="v" items="${q.value}">
                                                            <c:param name="${q.key}" value="${v}" />
                                                        </c:forEach>
                                                    </c:if>
                                                </c:forEach>
                                            </c:url>
                                            <span class="filter-tag">
                                                Hãng :
                                                <c:forEach items="${brands}" var="b">
                                                    <c:if test="${b.brandId == param.brandId}">
                                                        ${b.brandName}
                                                    </c:if>
                                                </c:forEach>
                                                <a href="${removeBrandUrl}">✕</a>
                                            </span>
                                        </c:if>
                                        <c:forEach var="p" items="${paramValues}">
                                            <c:if test="${fn:startsWith(p.key,'attr_') && not empty p.value[0]}">
                                                <c:url var="removeUrl" value="/inventory-list">
                                                    <c:forEach var="q" items="${paramValues}">
                                                        <c:if test="${q.key != p.key}">
                                                            <c:forEach var="v" items="${q.value}">
                                                                <c:param name="${q.key}" value="${v}" />
                                                            </c:forEach>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:url>
                                                <span class="filter-tag">
                                                    ${fn:substringAfter(p.key,'attr_')} : ${p.value[0]}
                                                    <a href="${removeUrl}">✕</a>
                                                </span>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </form>

                                <div class="summary-cards">
                                    <div class="summary-card">
                                        <div class="title">Total SKU</div>
                                        <div class="value">${sum.totalSku}</div>
                                    </div>

                                    <div class="summary-card">
                                        <div class="title">Tổng số lượng</div>
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
                                            <th>Ảnh</th>
                                            <th>SKU</th>
                                            <th>Sản phẩm</th>
                                            <th>Thuộc tính</th>
                                            <th>Thể loại</th>
                                            <th>Hãng</th>
                                            <th>Chi phí</th>
                                            <th>Giá</th>
                                            <th>Số lượng</th>
                                            <th>Trạng thái</th>
                                            <th>Hành động</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        ${list.size()}
                                        <c:forEach items="${list}" var="p">
                                            <tr>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${empty p.image}">
                                                            <img width="40" src="${pageContext.request.contextPath}/img/no-image.png">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img width="40" src="${p.image}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>${p.sku}</td>
                                                <td>${p.productName}</td>
                                                <td>${p.variantName}</td>
                                                <td>${p.categoryName}</td>
                                                <td>${p.brandName}</td>

                                                <td>
                                                    <fmt:formatNumber value="${p.costPrice}" type="number" groupingUsed="true"/>
                                                </td>

                                                <td>
                                                    <fmt:formatNumber value="${p.salePrice}" type="number" groupingUsed="true"/>
                                                </td>

                                                <td>${p.totalQuantity} ${p.unitName}</td>

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
                                                <td class="text-center">

                                                    <div class="d-flex justify-content-center align-items-center gap-2">

                                                        <a href="${pageContext.request.contextPath}/inventory-detail?variantId=${p.variantId}"
                                                           class="action-btn action-view"
                                                           title="View">

                                                            <iconify-icon icon="majesticons:eye-line"></iconify-icon>

                                                        </a>

                                                       <c:if test="${currentUser.roleId == 2}">

                                                            <a class="btn btn-warning btn-sm"
                                                               href="update-price?variantId=${p.variantId}"
                                                               title="Cập nhật giá">

                                                                <iconify-icon icon="mdi:cash-edit"></iconify-icon>

                                                            </a>

                                                            <a class="btn btn-info btn-sm"
                                                               href="price-history-detail?variantId=${p.variantId}"
                                                               title="Lịch sử giá">

                                                                <iconify-icon icon="mdi:history"></iconify-icon>

                                                            </a>

                                                        </c:if>

                                                    </div>

                                                </td>
                                            </tr>
                                        </c:forEach>                                   
                                    </tbody>
                                </table>

                                <div class="pagination-wrapper">
                                    <div class="pagination-controls">
                                        <c:set var="queryParams" value="" />
                                        <c:forEach var="p" items="${paramValues}">
                                            <c:if test="${p.key ne 'page'}">
                                                <c:forEach var="v" items="${p.value}">
                                                    <c:set var="queryParams"
                                                           value="${queryParams}&${p.key}=${v}" />
                                                </c:forEach>
                                            </c:if>
                                        </c:forEach>
                                        <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                           href="${pageContext.request.contextPath}/inventory-list?page=${currentPage - 1}${queryParams}">
                                            ‹
                                        </a>
                                        <span class="page-jump-form">
                                            Page
                                            <form action="${pageContext.request.contextPath}/inventory-list"
                                                  method="get"
                                                  style="display:inline;">
                                                <c:forEach var="p" items="${paramValues}">
                                                    <c:if test="${p.key ne 'page'}">
                                                        <c:forEach var="v" items="${p.value}">
                                                            <input type="hidden" name="${p.key}" value="${v}">
                                                        </c:forEach>
                                                    </c:if>
                                                </c:forEach>

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
                                           href="${pageContext.request.contextPath}/inventory-list?page=${currentPage + 1}${queryParams}">
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
        <div class="modal fade" id="filterModal">
            <div class="modal-dialog modal-lg modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Tất cả bộ lọc</h5>
                        <button class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="filterForm"
                              method="get"
                              action="${pageContext.request.contextPath}/inventory-list">
                            <input type="hidden" name="keyword" value="${param.keyword}">
                            <input type="hidden" name="categoryId" value="${param.categoryId}">
                            <input type="hidden" name="status" value="${param.status}">
                            <h6 class="mt-3">Hãng</h6>
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach items="${brands}" var="b">
                                    <label class="filter-option brand-filter">
                                        <input type="checkbox"
                                               name="brandId"
                                               value="${b.brandId}"
                                               hidden
                                               ${param.brandId == b.brandId ? 'checked' : ''}>
                                        <span>${b.brandName}</span>
                                    </label>
                                </c:forEach>
                            </div>
                            <c:forEach items="${attributeFilters}" var="f">
                                <h6 class="mt-3">${f.key}</h6>
                                <div class="d-flex flex-wrap gap-2">
                                    <c:forEach items="${f.value}" var="v">
                                        <label class="filter-option">
                                            <input type="radio"
                                                   name="attr_${f.key}"
                                                   value="${v}"
                                                   hidden
                                                   ${param['attr_'.concat(f.key)] == v ? 'checked' : ''}>

                                            <span>${v}</span>
                                        </label>
                                    </c:forEach>
                                </div>
                            </c:forEach>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button"
                                class="btn btn-outline-secondary"
                                data-bs-dismiss="modal">
                            Bỏ chọn
                        </button>
                        <button type="submit"
                                form="filterForm"
                                class="btn btn-primary">
                            Xem kết quả
                        </button>
                    </div>
                </div>
            </div>
        </div>       
    </body>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
                                                           document.addEventListener("DOMContentLoaded", function () {
                                                               const options = document.querySelectorAll(".filter-option");
                                                               options.forEach(option => {
                                                                   const input = option.querySelector("input");
                                                                   if (input && input.checked) {
                                                                       option.classList.add("active");
                                                                   }
                                                                   option.addEventListener("click", function (e) {
                                                                       e.preventDefault();
                                                                       if (option.classList.contains("brand-filter")) {
                                                                           if (input.checked) {
                                                                               input.checked = false;
                                                                               option.classList.remove("active");
                                                                           } else {
                                                                               input.checked = true;
                                                                               option.classList.add("active");
                                                                           }
                                                                       } else {
                                                                           const group = option.parentElement.querySelectorAll(".filter-option");
                                                                           if (input.checked) {
                                                                               input.checked = false;
                                                                               option.classList.remove("active");
                                                                           } else {
                                                                               group.forEach(o => {
                                                                                   o.classList.remove("active");
                                                                                   const i = o.querySelector("input");
                                                                                   if (i)
                                                                                       i.checked = false;
                                                                               });
                                                                               input.checked = true;
                                                                               option.classList.add("active");
                                                                           }
                                                                       }
                                                                   });
                                                               });
                                                           });
    </script>
</html>
