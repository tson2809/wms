<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Chi tiết luồng xuất nhập biến thể</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .page-btn {
            width: 32px;
            height: 32px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: #374151;
            font-weight: 600;
        }
        .page-btn:hover { background-color: #f3f4f6; }
        .page-btn.disabled { pointer-events: none; opacity: 0.4; }
        .page-number { display: flex; align-items: center; gap: 6px; font-size: 14px; color: #374151; }
        .page-jump-form input {
            width: 48px;
            height: 30px;
            text-align: center;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="container-fluid position-relative bg-white d-flex p-0">
    <jsp:include page="/view/common/components/sidebar.jsp" />

    <div class="content">
        <jsp:include page="/view/common/components/navbar.jsp" />

        <div class="container-fluid pt-4 px-4">
            <div class="bg-light rounded p-4 mb-4">
                <div class="d-flex flex-column flex-md-row justify-content-between gap-3 align-items-md-center">
                    <div>
                        <h5 class="mb-1">Chi tiết biến thể: ${sku}</h5>
                        <small class="text-muted">
                            ${empty productName ? '-' : productName}
                            | Từ ${fromDate} đến ${toDate}
                            | Chế độ:
                            <c:choose>
                                <c:when test="${detailMode == 'export'}">Danh sách xuất</c:when>
                                <c:when test="${detailMode == 'import'}">Danh sách nhập</c:when>
                                <c:otherwise>Tất cả xuất/nhập</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                    <a href="${pageContext.request.contextPath}/manager-report?range=custom&fromDate=${fromDate}&toDate=${toDate}&topMode=${detailMode == 'all' ? 'import' : detailMode}" class="btn btn-secondary">
                        <i class="fa fa-arrow-left me-2"></i>Quay lại báo cáo
                    </a>
                </div>
            </div>

            <div class="bg-light rounded p-4">
                <div class="table-responsive">
                    <table class="table table-striped align-middle mb-0">
                        <thead>
                            <tr>
                                <th>Loại</th>
                                <th>Mã phiếu</th>
                                <th>Ngày</th>
                                <th class="text-end">Số lượng</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty rows}">
                                    <c:forEach var="row" items="${rows}">
                                        <tr>
                                            <td>
                                                <span class="badge ${row.flowType eq 'Nhập' ? 'bg-success' : 'bg-primary'}">${row.flowType}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty row.documentId}">
                                                        <a href="${pageContext.request.contextPath}/${row.flowType eq 'Nhập' ? 'goods-receipt-edit' : 'goods-issue-detail'}?id=${row.documentId}">
                                                            ${row.documentCode}
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>${row.documentCode}</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><fmt:formatDate value="${row.flowDate}" pattern="dd/MM/yyyy HH:mm" /></td>
                                            <td class="text-end"><fmt:formatNumber value="${row.quantity}" type="number" /></td>
                                            <td>${row.status}</td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">Không có dữ liệu xuất/nhập cho biến thể này trong khoảng thời gian đã chọn.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <c:if test="${totalRows > 0}">
                    <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-1 border-top-0">
                        <div class="d-flex align-items-center gap-2 flex-grow-1">
                            <c:choose>
                                <c:when test="${page == 1}">
                                    <span class="page-btn disabled">‹</span>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/manager-report-variant-detail" class="d-inline">
                                        <input type="hidden" name="sku" value="${sku}">
                                        <input type="hidden" name="fromDate" value="${fromDate}">
                                        <input type="hidden" name="toDate" value="${toDate}">
                                        <input type="hidden" name="mode" value="${detailMode}">
                                        <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                        <input type="hidden" name="page" value="${page - 1}">
                                        <button type="submit" class="page-btn border-0 bg-transparent p-0">‹</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>

                            <span class="page-number">
                                Trang
                                <form action="${pageContext.request.contextPath}/manager-report-variant-detail" method="post" class="page-jump-form d-inline">
                                    <input type="hidden" name="sku" value="${sku}">
                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                    <input type="hidden" name="toDate" value="${toDate}">
                                    <input type="hidden" name="mode" value="${detailMode}">
                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                    <input type="number" name="page" min="1" max="${listOfPage}" value="${page}" onchange="this.form.submit()">
                                </form>
                                / ${listOfPage}
                            </span>

                            <c:choose>
                                <c:when test="${page == listOfPage}">
                                    <span class="page-btn disabled">›</span>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/manager-report-variant-detail" class="d-inline">
                                        <input type="hidden" name="sku" value="${sku}">
                                        <input type="hidden" name="fromDate" value="${fromDate}">
                                        <input type="hidden" name="toDate" value="${toDate}">
                                        <input type="hidden" name="mode" value="${detailMode}">
                                        <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                        <input type="hidden" name="page" value="${page + 1}">
                                        <button type="submit" class="page-btn border-0 bg-transparent p-0">›</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/manager-report-variant-detail" class="d-flex align-items-center gap-2 ms-auto">
                            <input type="hidden" name="sku" value="${sku}">
                            <input type="hidden" name="fromDate" value="${fromDate}">
                            <input type="hidden" name="toDate" value="${toDate}">
                            <input type="hidden" name="mode" value="${detailMode}">
                            <input type="hidden" name="page" value="1">
                            <span class="text-muted small">Hiển thị</span>
                            <select name="numberPerPage" class="form-select form-select-sm" onchange="this.form.submit()">
                                <option value="5" ${numberPerPage == 5 ? 'selected' : ''}>5</option>
                                <option value="10" ${numberPerPage == 10 ? 'selected' : ''}>10</option>
                                <option value="20" ${numberPerPage == 20 ? 'selected' : ''}>20</option>
                            </select>
                            <span class="text-muted small">dòng/trang</span>
                        </form>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
