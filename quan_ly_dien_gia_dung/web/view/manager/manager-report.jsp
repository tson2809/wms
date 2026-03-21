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
                                <button type="submit" name="range" value="ytd" class="btn btn-sm ${selectedRange == 'ytd' ? 'btn-primary' : 'btn-outline-primary'}">YTD</button>
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
                <div class="mt-3">
                    <small class="text-muted">So sánh kỳ trước: ${prevFromDate} đến ${prevToDate}</small>
                </div>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-wallet fa-2x text-primary"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Dòng tiền ròng</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.netCashFlow}" type="number" maxFractionDigits="0" /></h5>
                            <small class="${deltaNetCashFlow >= 0 ? 'text-success' : 'text-danger'}">${deltaNetCashFlow >= 0 ? '+' : ''}<fmt:formatNumber value="${deltaNetCashFlow}" type="number" maxFractionDigits="2" />%</small>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-arrow-circle-down fa-2x text-success"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Tiền vào</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.cashIn}" type="number" maxFractionDigits="0" /></h5>
                            <small class="text-muted">Phải thu ước tính: <fmt:formatNumber value="${summary.estimatedReceivable}" type="number" maxFractionDigits="0" /></small>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3">
                    <div class="bg-light rounded d-flex align-items-center justify-content-between p-4 h-100">
                        <i class="fa fa-arrow-circle-up fa-2x text-danger"></i>
                        <div class="ms-3 text-end">
                            <p class="mb-1">Tiền ra</p>
                            <h5 class="mb-0"><fmt:formatNumber value="${summary.cashOut}" type="number" maxFractionDigits="0" /></h5>
                            <small class="text-muted">Phải trả ước tính: <fmt:formatNumber value="${summary.estimatedPayable}" type="number" maxFractionDigits="0" /></small>
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
                        <small class="text-muted">Số lượng nhập: <fmt:formatNumber value="${summary.totalImportQuantity}" type="number" /></small>
                        <div class="mt-2"><a href="${pageContext.request.contextPath}/goods-receipt-list" class="small">Xem chi tiết</a></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Giá trị xuất kho</p>
                        <h5><fmt:formatNumber value="${summary.totalExportValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Số lượng xuất: <fmt:formatNumber value="${summary.totalExportQuantity}" type="number" /></small>
                        <div class="mt-2"><a href="${pageContext.request.contextPath}/goods-issue-list" class="small">Xem chi tiết</a></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Mua hàng (PO NCC)</p>
                        <h5><fmt:formatNumber value="${summary.totalPurchaseValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Phiếu nhập hoàn tất: <fmt:formatNumber value="${summary.completedReceiptCount}" type="number" /></small>
                        <div class="mt-2"><a href="${pageContext.request.contextPath}/purchase-order/list" class="small">Xem chi tiết</a></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="bg-light rounded p-4 h-100">
                        <p class="mb-2 text-muted">Bán hàng (đơn Sale)</p>
                        <h5><fmt:formatNumber value="${summary.totalSalesValue}" type="number" maxFractionDigits="0" /></h5>
                        <small class="text-muted">Phiếu xuất xác nhận: <fmt:formatNumber value="${summary.completedIssueCount}" type="number" /></small>
                        <div class="mt-2"><a href="${pageContext.request.contextPath}/purchase-order/list?orderType=sale" class="small">Xem chi tiết</a></div>
                    </div>
                </div>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-12">
                    <div class="bg-light rounded p-4 h-100">
                        <h6 class="mb-3">Xu hướng theo tháng</h6>
                        <canvas id="monthlyFlowChart" height="110"></canvas>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <div class="col-12">
                    <div class="bg-light rounded p-4">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h6 class="mb-0">Top biến thể theo lưu chuyển nhập xuất</h6>
                            <small class="text-muted">Giá trị lớn nhất theo tổng khối lượng nhập + xuất</small>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-striped align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th>SKU</th>
                                        <th>Sản phẩm</th>
                                        <th class="text-end">SL nhập</th>
                                        <th class="text-end">SL xuất</th>
                                        <th class="text-end">SL ròng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty topVariantFlows}">
                                            <c:forEach var="row" items="${topVariantFlows}">
                                                <tr>
                                                    <td>${row.sku}</td>
                                                    <td>${row.productName}</td>
                                                    <td class="text-end"><fmt:formatNumber value="${row.importQuantity}" type="number" /></td>
                                                    <td class="text-end"><fmt:formatNumber value="${row.exportQuantity}" type="number" /></td>
                                                    <td class="text-end"><fmt:formatNumber value="${row.netQuantity}" type="number" /></td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="5" class="text-center text-muted py-4">Không có dữ liệu trong khoảng thời gian đã chọn.</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/chart/chart.min.js"></script>
<script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
    (function () {
        const monthLabels = [
            <c:forEach var="row" items="${monthlyFlows}" varStatus="st">"${row.monthLabel}"<c:if test="${!st.last}">,</c:if></c:forEach>
        ];
        const importValues = [
            <c:forEach var="row" items="${monthlyFlows}" varStatus="st">${row.importValue}<c:if test="${!st.last}">,</c:if></c:forEach>
        ];
        const exportValues = [
            <c:forEach var="row" items="${monthlyFlows}" varStatus="st">${row.exportValue}<c:if test="${!st.last}">,</c:if></c:forEach>
        ];
        const salesValues = [
            <c:forEach var="row" items="${monthlyFlows}" varStatus="st">${row.salesValue}<c:if test="${!st.last}">,</c:if></c:forEach>
        ];

        const monthlyCanvas = document.getElementById("monthlyFlowChart");
        if (monthlyCanvas) {
            new Chart(monthlyCanvas, {
                data: {
                    labels: monthLabels,
                    datasets: [
                        {
                            type: "bar",
                            label: "Nhập kho",
                            data: importValues,
                            backgroundColor: "rgba(25, 135, 84, 0.65)",
                            borderColor: "rgba(25, 135, 84, 1)",
                            borderWidth: 1
                        },
                        {
                            type: "bar",
                            label: "Xuất kho",
                            data: exportValues,
                            backgroundColor: "rgba(220, 53, 69, 0.65)",
                            borderColor: "rgba(220, 53, 69, 1)",
                            borderWidth: 1
                        },
                        {
                            type: "line",
                            label: "Doanh số đơn Sale",
                            data: salesValues,
                            borderColor: "rgba(13, 110, 253, 1)",
                            backgroundColor: "rgba(13, 110, 253, 0.15)",
                            fill: false,
                            tension: 0.3,
                            yAxisID: "y"
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: "top"
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }
    })();
</script>
</body>
</html>