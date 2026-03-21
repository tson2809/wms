<%-- 
    Document   : inventory-detail
    Created on : Mar 4, 2026, 10:28:03 PM
    Author     : GIAKHANHPC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 3}">
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:otherwise>
            </c:choose>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="inventory-card p-4">
                                <h5 class="mb-4">Chi tiết tồn kho</h5>
                                <div class="row">
                                    <div class="col-md-4 text-center">
                                        <img width="250"
                                             class="img-fluid rounded"
                                             src="${empty p.image ? pageContext.request.contextPath.concat('/img/no-image.png') : p.image}">
                                    </div>
                                    <div class="col-md-8">
                                        <table class="table table-bordered">
                                            <tr>
                                                <th>Sản phẩm</th>
                                                <td>${p.productName}</td>
                                            </tr>
                                            <tr>
                                                <th>SKU</th>
                                                <td>${p.sku}</td>
                                            </tr>
                                            <tr>
                                                <th>Barcode</th>
                                                <td>${p.barcode}</td>
                                            </tr>
                                            <tr>
                                                <th>Danh mục</th>
                                                <td>${p.categoryName}</td>
                                            </tr>
                                            <tr>
                                                <th>Thương hiệu</th>
                                                <td>${p.brandName}</td>
                                            </tr>
                                            <tr>
                                                <th>Thuộc tính</th>
                                                <td>${p.variantName}</td>
                                            </tr>
                                            <tr>
                                                <th>Giá vốn</th>
                                                <td>
                                                    <fmt:formatNumber value="${p.costPrice}" type="number" groupingUsed="true"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <th>Giá bán</th>
                                                <td>
                                                    <fmt:formatNumber value="${p.salePrice}" type="number" groupingUsed="true"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <th>Số lượng tồn</th>
                                                <td>${p.totalQuantity}</td>
                                            </tr>
                                            <tr>
                                                <th>Trạng thái</th>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${p.status == 'Hết hàng'}">
                                                            <span class="badge bg-danger">Out of stock</span>
                                                        </c:when>
                                                        <c:when test="${p.status == 'Sắp hết'}">
                                                            <span class="badge bg-warning text-dark">Low stock</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-success">In stock</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                                <h5 class="mt-4 mb-3">Serial Number</h5>
                                <div class="row text-center mb-4">
                                    <div class="col-md-3">
                                        <div class="summary-card">
                                            <div class="title">Tổng Serial</div>
                                            <div class="value">${serialSummary.total}</div>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="summary-card">
                                            <div class="title">Trong kho</div>
                                            <div class="value">${serialSummary.inStock}</div>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="summary-card">
                                            <div class="title">Đã bán</div>
                                            <div class="value">${serialSummary.sold}</div>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="summary-card">
                                            <div class="title">Lỗi</div>
                                            <div class="value">${serialSummary.defective}</div>
                                        </div>
                                    </div>
                                </div>
                                <h6 class="mb-3">Danh sách Serial</h6>
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Serial Number</th>
                                                <th>Trạng thái</th>
                                                <th>Ngày tạo</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${empty serialList}">
                                                    <tr>
                                                        <td colspan="3" class="text-center text-muted">
                                                            Không có serial nào
                                                        </td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach items="${serialList}" var="s">
                                                        <tr>
                                                            <td>
                                                                <i class="fa fa-barcode text-primary"></i>
                                                                ${s[0]}
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${s[1]=='in_stock'}">
                                                                        <span class="badge bg-success">Trong kho</span>
                                                                    </c:when>
                                                                    <c:when test="${s[1]=='sold'}">
                                                                        <span class="badge bg-primary">Đã bán</span>
                                                                    </c:when>
                                                                    <c:when test="${s[1]=='defective'}">
                                                                        <span class="badge bg-danger">Lỗi</span>
                                                                    </c:when>
                                                                    <c:when test="${s[1]=='returned'}">
                                                                        <span class="badge bg-warning text-dark">Trả hàng</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge bg-secondary">${s[1]}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${s[2]}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="mt-4">
                                    <a href="${pageContext.request.contextPath}/inventory-list"
                                       class="btn btn-secondary">
                                        <i class="fa fa-arrow-left"></i> Quay lại
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
