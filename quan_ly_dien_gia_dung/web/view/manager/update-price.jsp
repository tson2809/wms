<%-- 
    Document   : update-price
    Created on : Mar 7, 2026, 8:13:17 PM
    Author     : GIAKHANHPC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Price</title>
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
        <link href="${pageContext.request.contextPath}/css/price.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">

            <jsp:include page="/view/manager/components/sidebarManager.jsp"/>

            <div class="content">

                <jsp:include page="/view/common/components/navbar.jsp"/>

                <div class="container-fluid pt-4 px-4">

                    <div class="row justify-content-center">

                        <div class="col-md-6">

                            <div class="bg-white rounded shadow-sm p-4">
                                <h5 class="mb-3">Cập nhật giá sản phẩm</h5>                            
                                <form method="post" action="update-price">
                                    <input type="hidden" name="variantId" value="${param.variantId}">
                                    <div class="mb-3">
                                        <label class="form-label">SKU</label>
                                        <input class="form-control" value="${variant[0]}" readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Giá vốn hiện tại</label>
                                        <input class="form-control"
                                               value="<fmt:formatNumber value='${variant[1]}' type='number' groupingUsed='true'/>"
                                               readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Giá bán hiện tại</label>
                                        <input class="form-control"
                                               value="<fmt:formatNumber value='${variant[2]}' type='number' groupingUsed='true'/>"
                                               readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Giá vốn mới</label>
                                        <input class="form-control"
                                               type="number"
                                               name="newCost"
                                               min="0"
                                               step="0.01"
                                               required>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Giá bán mới</label>
                                        <input class="form-control"
                                               type="number"
                                               name="newSale"
                                               min="0"
                                               step="0.01"
                                               required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Lý do thay đổi</label>
                                        <textarea class="form-control" name="reason"></textarea>
                                    </div>
                                    <button class="btn btn-success">
                                        Cập nhật giá
                                    </button>
                                </form>
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
