<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Báo cáo thống kê Manager</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid position-relative bg-white d-flex p-0">
    <div id="spinner" class="show bg-white position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
        <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>

    <jsp:include page="/view/common/components/sidebar.jsp" />

    <div class="content">
        <jsp:include page="/view/common/components/navbar.jsp" />

        <div class="container-fluid pt-4 px-4">
            <div class="bg-light rounded p-4 mb-4">
                <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                    <div>
                        <h5 class="mb-1">Báo cáo tổng hợp kho và dòng tiền</h5>
                        <small class="text-muted">Phạm vi dữ liệu từ ${fromDate} đến ${toDate}</small>
                    </div>
                    <form method="get" action="${pageContext.request.contextPath}/manager-report" class="row g-2 align-items-end ms-md-auto justify-content-md-end">
                        <div class="col-12 d-flex justify-content-md-end">
                            <div class="btn-group" role="group" aria-label="quick-range">
                                <button type="submit" name="range" value="today" class="btn btn-sm ${selectedRange == 'today' ? 'btn-primary' : 'btn-outline-primary'}">Hôm nay</button>
                                <button type="submit" name="range" value="this_week" class="btn btn-sm ${selectedRange == 'this_week' ? 'btn-primary' : 'btn-outline-primary'}">Tuần này</button>
                                <button type="submit" name="range" value="this_month" class="btn btn-sm ${selectedRange == 'this_month' ? 'btn-primary' : 'btn-outline-primary'}">Tháng này</button>
                                <button type="submit" name="range" value="this_quarter" class="btn btn-sm ${selectedRange == 'this_quarter' ? 'btn-primary' : 'btn-outline-primary'}">Quý này</button>
                                <button type="submit" name="range" value="year" class="btn btn-sm ${selectedRange == 'year' ? 'btn-primary' : 'btn-outline-primary'}">Năm</button>
                            </div>
                        </div>
                        <div class="col-auto">
                            <label class="form-label mb-1">Từ ngày</label>
                            <input type="date" name="fromDate" class="form-control" value="${fromDate}">
                        </div>
                        <div class="col-auto">
                            <label class="form-label mb-1">Đến ngày</label>
                            <input type="date" name="toDate" class="form-control" value="${toDate}">
                        </div>
                        <div class="col-auto">
                            <button type="submit" name="range" value="custom" class="btn btn-primary"><i class="fa fa-filter me-1"></i>Lọc</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-wallet fa-2x text-primary"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Dòng tiền ròng</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.netCashFlow}" type="number" maxFractionDigits="0" /></h5>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-arrow-circle-down fa-2x text-success"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Tiền vào</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.cashIn}" type="number" maxFractionDigits="0" /></h5>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-arrow-circle-up fa-2x text-danger"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Tiền ra</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.cashOut}" type="number" maxFractionDigits="0" /></h5>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-exchange-alt fa-2x text-info"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Giao dịch kho</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.transactionCount}" type="number" maxFractionDigits="0" /></h5>
                            <small class="text-muted">Stockout SKU: <fmt:formatNumber value="${inventoryInsight.stockoutCount}" type="number" /></small>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Giá trị nhập kho</p>
                        <h5><fmt:formatNumber value="${summary.totalImportValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Số phiếu nhập: <fmt:formatNumber value="${summary.completedReceiptCount}" type="number" /></small>
                        <div class="mt-2">
                            <a href="${pageContext.request.contextPath}/goods-receipt-list?fromDate=${fromDate}&toDate=${toDate}&status=completed"
                               class="small">Xem chi tiết</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Giá trị xuất kho</p>
                        <h5><fmt:formatNumber value="${summary.totalExportValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Số phiếu xuất: <fmt:formatNumber value="${summary.completedIssueCount}" type="number" /></small>
                        <div class="mt-2">
                            <a href="${pageContext.request.contextPath}/goods-issue-list?fromDate=${fromDate}&toDate=${toDate}&status=completed"
                               class="small">Xem chi tiết</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Mua hàng (PO NCC)</p>
                        <h5><fmt:formatNumber value="${summary.totalPurchaseValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Số PO NCC hoàn tất: <fmt:formatNumber value="${summary.completedPurchaseOrderCount}" type="number" /></small>
                        <div class="mt-2"><a href="${pageContext.request.contextPath}/purchase-order/list" class="small">Xem chi tiết</a></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Bán hàng (đơn Sale)</p>
                        <h5><fmt:formatNumber value="${summary.totalSalesValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Số đơn Sale hoàn tất: <fmt:formatNumber value="${summary.completedSaleOrderCount}" type="number" /></small>
                        <div class="mt-2"><a href="${pageContext.request.contextPath}/purchase-order/list?orderType=sale" class="small">Xem chi tiết</a></div>
                    </div>
                </div>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-12">
                    <div class="bg-light rounded p-4 h-100">
                        <h6 class="mb-3">Bảng tổng hợp theo tháng</h6>
                        <div class="table-responsive">
                            <table class="table table-striped align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th>Tháng</th>
                                        <th class="text-end">Giá trị nhập kho</th>
                                        <th class="text-end">Giá trị xuất kho</th>
                                        <th class="text-end">Doanh số đơn Sale</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty monthlyFlows}">
                                            <c:forEach var="row" items="${monthlyFlows}">
                                                <tr>
                                                    <td>${row.monthLabel}</td>
                                                    <td class="text-end"><fmt:formatNumber value="${row.importValue}" type="number" maxFractionDigits="0" /></td>
                                                    <td class="text-end"><fmt:formatNumber value="${row.exportValue}" type="number" maxFractionDigits="0" /></td>
                                                    <td class="text-end"><fmt:formatNumber value="${row.salesValue}" type="number" maxFractionDigits="0" /></td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="4" class="text-center text-muted py-4">Không có dữ liệu trong khoảng thời gian đã chọn.</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <div class="col-12">
                    <div class="bg-light rounded p-4">
                        <div class="d-flex justify-content-between align-items-center mb-3 gap-3 flex-column flex-md-row">
                            <div>
                                <h6 class="mb-0">Danh sách biến thể</h6>
                                <small class="text-muted">Chọn chế độ hiển thị và phân trang</small>
                            </div>

                            <div class="btn-group" role="group" aria-label="top-variant-mode">
                                <form method="get" action="${pageContext.request.contextPath}/manager-report" class="d-inline">
                                    <input type="hidden" name="range" value="custom">
                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                    <input type="hidden" name="toDate" value="${toDate}">
                                    <input type="hidden" name="topMode" value="import">
                                    <input type="hidden" name="topPage" value="1">
                                    <input type="hidden" name="topNumberPerPage" value="${topNumberPerPage}">
                                    <button type="submit" class="btn btn-sm ${topMode == 'import' ? 'btn-primary' : 'btn-outline-primary'}">Danh sách nhập</button>
                                </form>
                                <form method="get" action="${pageContext.request.contextPath}/manager-report" class="d-inline">
                                    <input type="hidden" name="range" value="custom">
                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                    <input type="hidden" name="toDate" value="${toDate}">
                                    <input type="hidden" name="topMode" value="export">
                                    <input type="hidden" name="topPage" value="1">
                                    <input type="hidden" name="topNumberPerPage" value="${topNumberPerPage}">
                                    <button type="submit" class="btn btn-sm ${topMode == 'export' ? 'btn-primary' : 'btn-outline-primary'}">Danh sách xuất</button>
                                </form>
                                <form method="get" action="${pageContext.request.contextPath}/manager-report" class="d-inline">
                                    <input type="hidden" name="range" value="custom">
                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                    <input type="hidden" name="toDate" value="${toDate}">
                                    <input type="hidden" name="topMode" value="risk">
                                    <input type="hidden" name="topPage" value="1">
                                    <input type="hidden" name="topNumberPerPage" value="${topNumberPerPage}">
                                    <button type="submit" class="btn btn-sm ${topMode == 'risk' ? 'btn-primary' : 'btn-outline-primary'}">Danh sách tồn cao</button>
                                </form>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h6 class="mb-0">
                                <c:choose>
                                    <c:when test="${topMode == 'export'}">Danh sách xuất nhiều</c:when>
                                    <c:when test="${topMode == 'risk'}">Danh sách tồn cao</c:when>
                                    <c:otherwise>Danh sách nhập nhiều</c:otherwise>
                                </c:choose>
                            </h6>
                            <small class="text-muted">
                                <c:choose>
                                    <c:when test="${topMode == 'risk'}">Snapshot tồn tại ${toDate}</c:when>
                                    <c:when test="${topMode == 'export'}">Theo SL xuất (từ ${fromDate} đến ${toDate})</c:when>
                                    <c:otherwise>Theo SL nhập (từ ${fromDate} đến ${toDate})</c:otherwise>
                                </c:choose>
                            </small>
                        </div>

                        <div class="table-responsive">
                            <table class="table table-striped align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th>SKU</th>
                                        <th>Sản phẩm</th>
                                        <th class="text-end">
                                            <c:choose>
                                                <c:when test="${topMode == 'export'}">SL xuất</c:when>
                                                <c:when test="${topMode == 'risk'}">Tồn</c:when>
                                                <c:otherwise>SL nhập</c:otherwise>
                                            </c:choose>
                                        </th>
                                        <th class="text-center" style="width: 120px;">Chi tiết</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty topVariantRows}">
                                            <c:forEach var="row" items="${topVariantRows}">
                                                <tr>
                                                    <td>${row.sku}</td>
                                                    <td>${row.productName}</td>
                                                    <td class="text-end">
                                                        <c:choose>
                                                            <c:when test="${topMode == 'export'}"><fmt:formatNumber value="${row.exportQuantity}" type="number" /></c:when>
                                                            <c:when test="${topMode == 'risk'}"><fmt:formatNumber value="${row.stockQuantity}" type="number" /></c:when>
                                                            <c:otherwise><fmt:formatNumber value="${row.importQuantity}" type="number" /></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-center">
                                                        <form method="post" action="${pageContext.request.contextPath}/manager-report-variant-detail" class="d-inline">
                                                            <input type="hidden" name="sku" value="${row.sku}">
                                                            <input type="hidden" name="fromDate" value="${fromDate}">
                                                            <input type="hidden" name="toDate" value="${toDate}">
                                                            <input type="hidden" name="mode" value="${topMode == 'import' ? 'port' : topMode}">
                                                            <button type="submit" class="btn btn-sm btn-outline-primary">Chi tiết</button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="4" class="text-center text-muted py-4">Không có dữ liệu.</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>

                        <c:if test="${topTotalRows > 0}">
                            <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-1 border-top-0">
                                <div class="d-flex align-items-center gap-2 flex-grow-1">
                                    <c:choose>
                                        <c:when test="${topPage == 1}">
                                            <span class="page-btn disabled">‹</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="get" action="${pageContext.request.contextPath}/manager-report" class="d-inline">
                                                <input type="hidden" name="range" value="custom">
                                                <input type="hidden" name="fromDate" value="${fromDate}">
                                                <input type="hidden" name="toDate" value="${toDate}">
                                                <input type="hidden" name="topMode" value="${topMode}">
                                                <input type="hidden" name="topNumberPerPage" value="${topNumberPerPage}">
                                                <input type="hidden" name="topPage" value="${topPage - 1}">
                                                <button type="submit" class="page-btn border-0 bg-transparent p-0">‹</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>

                                    <span class="page-number">
                                        Trang
                                        <form action="${pageContext.request.contextPath}/manager-report" method="get" class="page-jump-form d-inline">
                                            <input type="hidden" name="range" value="custom">
                                            <input type="hidden" name="fromDate" value="${fromDate}">
                                            <input type="hidden" name="toDate" value="${toDate}">
                                            <input type="hidden" name="topMode" value="${topMode}">
                                            <input type="hidden" name="topNumberPerPage" value="${topNumberPerPage}">
                                            <input type="number" name="topPage" min="1" max="${topListOfPage}" value="${topPage}" onchange="this.form.submit()">
                                        </form>
                                        / ${topListOfPage}
                                    </span>

                                    <c:choose>
                                        <c:when test="${topPage == topListOfPage}">
                                            <span class="page-btn disabled">›</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="get" action="${pageContext.request.contextPath}/manager-report" class="d-inline">
                                                <input type="hidden" name="range" value="custom">
                                                <input type="hidden" name="fromDate" value="${fromDate}">
                                                <input type="hidden" name="toDate" value="${toDate}">
                                                <input type="hidden" name="topMode" value="${topMode}">
                                                <input type="hidden" name="topNumberPerPage" value="${topNumberPerPage}">
                                                <input type="hidden" name="topPage" value="${topPage + 1}">
                                                <button type="submit" class="page-btn border-0 bg-transparent p-0">›</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <form method="get" action="${pageContext.request.contextPath}/manager-report" class="d-flex align-items-center gap-2 ms-auto">
                                    <input type="hidden" name="range" value="custom">
                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                    <input type="hidden" name="toDate" value="${toDate}">
                                    <input type="hidden" name="topMode" value="${topMode}">
                                    <input type="hidden" name="topPage" value="1">
                                    <span class="text-muted small">Hiển thị</span>
                                    <select name="topNumberPerPage" class="form-select form-select-sm" onchange="this.form.submit()">
                                        <option value="5" ${topNumberPerPage == 5 ? 'selected' : ''}>5</option>
                                        <option value="10" ${topNumberPerPage == 10 ? 'selected' : ''}>10</option>
                                        <option value="20" ${topNumberPerPage == 20 ? 'selected' : ''}>20</option>
                                    </select>
                                    <span class="text-muted small">dòng/trang</span>
                                </form>
                            </div>
                        </c:if>
                    </div>
                </div>
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