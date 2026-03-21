<%-- 
    Document   : price-history-detail
    Created on : Mar 7, 2026, 8:13:05 PM
    Author     : GIAKHANHPC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Price History Detail</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <meta content="" name="keywords">
        <meta content="" name="description">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

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
        <link href="${pageContext.request.contextPath}/css/inventory.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/price.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/sidebar.jsp"/>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp"/>
                <div class="container-fluid pt-4 px-4">
                    <div class="row">
                        <div class="col-12">
                            <div class="bg-white rounded shadow-sm p-4">
                                <div class="d-flex justify-content-between mb-3">
                                    <h5 class="mb-0">Chi tiết lịch sử thay đổi giá</h5>
                                    <a href="${pageContext.request.contextPath}/inventory-list"
                                       class="btn btn-secondary btn-sm">
                                        ← Quay lại tồn kho
                                    </a>
                                </div>
                                <div class="mt-2 text-muted">
                                    <b>Sản phẩm:</b> ${product[1]}  
                                    &nbsp;&nbsp;|&nbsp;&nbsp;
                                    <b>SKU:</b> ${product[0]}

                                </div>
                                <form method="get"
                                      class="row g-2 mb-3"
                                      onsubmit="return validatePriceHistoryDate()">
                                    <input type="hidden" name="variantId" value="${param.variantId}">
                                    <div class="col-md-3">
                                        <label>Từ ngày</label>
                                        <input type="date"
                                               id="fromDate"
                                               name="fromDate"
                                               value="${param.fromDate}"
                                               class="form-control">
                                    </div>
                                    <div class="col-md-3">
                                        <label>Đến ngày</label>
                                        <input type="date"
                                               id="toDate"
                                               name="toDate"
                                               value="${param.toDate}"
                                               class="form-control">
                                    </div>
                                    <div class="col-md-2 align-self-end">
                                        <button class="btn btn-primary w-100">
                                            Lọc
                                        </button>
                                    </div>
                                </form>
                                <div class="card mb-4">
                                    <div class="card-body">
                                        <h6 class="mb-3">Biểu đồ biến động giá</h6>
                                        <canvas id="priceChart" height="90"></canvas>
                                    </div>
                                </div>
                                <table class="table table-bordered table-hover align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Ngày thay đổi</th>
                                            <th>Giá vốn cũ</th>
                                            <th>Giá vốn mới</th>
                                            <th>Giá bán cũ</th>
                                            <th>Giá bán mới</th>
                                            <th>Người chỉnh sửa</th>
                                            <th>Lý do</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="h" items="${historyList}">
                                            <tr>
                                                <td>${h[4]}</td>
                                                <td>
                                                    <fmt:formatNumber value="${h[0]}" type="number" groupingUsed="true"/>
                                                </td>
                                                <td class="text-success fw-bold">
                                                    <fmt:formatNumber value="${h[1]}" type="number" groupingUsed="true"/>
                                                </td>
                                                <td>
                                                    <fmt:formatNumber value="${h[2]}" type="number" groupingUsed="true"/>
                                                </td>
                                                <td class="text-primary fw-bold">
                                                    <fmt:formatNumber value="${h[3]}" type="number" groupingUsed="true"/>
                                                </td>
                                                <td>${h[6]}</td>
                                                <td>${h[5]}</td>
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

                                           href="${pageContext.request.contextPath}/price-history-detail?page=${currentPage - 1}${queryParams}">

                                            ‹

                                        </a>
                                        <span class="page-jump-form">
                                            Page
                                            <form action="${pageContext.request.contextPath}/price-history-detail"
                                                  method="get"
                                                  style="display:inline;">
                                                <c:forEach var="p" items="${paramValues}">
                                                    <c:if test="${p.key ne 'page'}">
                                                        <c:forEach var="v" items="${p.value}">
                                                            <input type="hidden"
                                                                   name="${p.key}"
                                                                   value="${v}">
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
                                           href="${pageContext.request.contextPath}/price-history-detail?page=${currentPage + 1}${queryParams}">
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
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const labels = ${dates};
            const costData = ${costPrices};
            const saleData = ${salePrices};
            const ctx = document.getElementById("priceChart");
            new Chart(ctx, {
                type: "line",
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: "Cost Price",
                            data: costData,
                            borderColor: "#0d6efd",
                            backgroundColor: "transparent",
                            tension: 0.3
                        },
                        {
                            label: "Sale Price",
                            data: saleData,
                            borderColor: "#198754",
                            backgroundColor: "transparent",
                            tension: 0.3
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    let value = context.parsed.y;
                                    return context.dataset.label +
                                            ": " +
                                            value.toLocaleString() +
                                            " VND";
                                }
                            }
                        }
                    }
                }
            });
        });
    </script>
    <script>
        const fromDateInput = document.getElementById("fromDate");
        const toDateInput = document.getElementById("toDate");
        const today = new Date().toISOString().split("T")[0];
        fromDateInput.max = today;
        toDateInput.max = today;
        fromDateInput.addEventListener("change", function () {
            toDateInput.min = this.value;
        });
        toDateInput.addEventListener("change", function () {
            fromDateInput.max = this.value;
        });
        function validatePriceHistoryDate() {
            const from = fromDateInput.value;
            const to = toDateInput.value;
            if (from && to && from > to) {
                alert("Từ ngày phải nhỏ hơn hoặc bằng đến ngày");
                return false;
            }
            if (to && to > today) {
                alert("Đến ngày không được lớn hơn ngày hiện tại");
                return false;
            }
            return true;
        }
    </script>

    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
