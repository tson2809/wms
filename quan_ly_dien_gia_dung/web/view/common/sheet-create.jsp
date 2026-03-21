<%-- 
    Document   : inventory-sheet
    Created on : Feb 1, 2026, 11:41:12 AM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>

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
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">and 
</head>
<body>
    <div class="container-fluid position-relative bg-white d-flex p-0">
        <jsp:include page="/view/common/components/sidebar.jsp" />
        <div class="content">
            <jsp:include page="/view/common/components/navbar.jsp" />

            <div class="container-fluid pt-4 px-4">
                <div class="inventory-card p-4">

                    <h5>Tạo Sheet</h5>
                    <form method="get" action="${pageContext.request.contextPath}/inventory-sheet-create">
                        <select name="categoryId"
                                class="form-select w-auto"
                                onchange="this.form.submit()">
                            <option value="">Select category</option>
                            <c:forEach items="${categories}" var="c">
                                <option value="${c.categoryId}"
                                        <c:if test="${param.categoryId == c.categoryId.toString()}">
                                            selected
                                        </c:if>>
                                    ${c.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </form>

                    <c:if test="${not empty inventory}">
                        <form method="post">

                            <input type="hidden" name="categoryId" value="${param.categoryId}">

                            <table class="table align-middle mt-3">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>SKU</th>
                                        <th>Số lượng hệ thống</th>
                                        <th>Số lượng thực tế</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${inventory}" var="i">
                                        <tr>
                                            <td>${i.productName}</td>
                                            <td>${i.sku}</td>
                                            <td>${i.systemQuantity}</td>
                                            <td>
                                                <input type="hidden" name="variantId" value="${i.variantId}">
                                                <input type="hidden" name="systemQty" value="${i.systemQuantity}">
                                                <input type="number" name="countedQty"
                                                       class="form-control"
                                                       required>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <button name="action" value="save"
                                    class="btn btn-secondary">
                                Lưu bản nháp
                            </button>

                            <button name="action" value="submit"
                                    class="btn btn-primary">
                                Nộp 
                            </button>
                        </form>
                    </c:if>
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
