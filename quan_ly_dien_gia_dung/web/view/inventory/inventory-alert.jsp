<%-- 
    Document   : inventory-alert
    Created on : Feb 1, 2026, 11:40:58 AM
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
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">

            <jsp:include page="/view/admin/components/sidebarAdmin.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="d-flex justify-content-between align-items-center mb-3 inventory-header">
                                <h5 class="mb-0">Low Inventory Alert</h5>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <form method="get"
                                      action="${pageContext.request.contextPath}/inventory-alert">
                                    <select name="status"
                                            class="form-select w-auto"
                                            onchange="this.form.submit()">
                                        <option value="">Status</option>
                                        <option value="low"  ${selectedStatus == 'low' ? 'selected' : ''}>
                                            Low Stock
                                        </option>
                                        <option value="high" ${selectedStatus == 'high' ? 'selected' : ''}>
                                            High Stock
                                        </option>
                                    </select>
                                </form>
                                <div class="form-check form-switch">
                                    <input class="form-check-input"
                                           type="checkbox"
                                           id="alertSwitch"
                                           ${alertEnabled ? "checked" : ""}
                                           onchange="toggleAlert(this)">
                                    <label class="form-check-label" for="alertSwitch">
                                        ${alertEnabled ? "ON" : "OFF"}
                                    </label>
                                </div>
                                <script>
                                    function toggleAlert(el) {
                                        el.nextElementSibling.innerText = el.checked ? "ON" : "OFF";
                                    }
                                </script>
                            </div>

                            <table class="table table-bordered">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>name</th>
                                        <th>quantity</th>
                                        <th>status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${list}" var="p">
                                        <tr>
                                            <td>${p.productId}</td>
                                            <td>${p.productName}</td>
                                            <td>
                                                ${p.totalQuantity}
                                                <c:if test="${not empty p.unitName}">
                                                    ${p.unitName}
                                                </c:if>
                                            </td>
                                            <td>${p.status}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination-wrapper">
                                <div class="pagination-controls">

                                    <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                       href="inventory-alert?page=${currentPage - 1}&status=${selectedStatus}">
                                        ‹
                                    </a>

                                    <span class="page-number">
                                        Page
                                        <form action="inventory-alert" method="get" class="page-jump-form">
                                            <input type="hidden" name="status" value="${selectedStatus}">
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
                                       href="inventory-alert?page=${currentPage + 1}&status=${selectedStatus}">
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
