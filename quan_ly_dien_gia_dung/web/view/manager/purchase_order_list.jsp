<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Danh sách đơn đặt hàng</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .status-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        .status-draft { background-color: #e0e7ff; color: #4338ca; }
        .status-submitted { background-color: #fef3c7; color: #92400e; }
        .status-approved { background-color: #d1fae5; color: #065f46; }
        .status-received { background-color: #dbeafe; color: #1e40af; }
        .status-cancelled { background-color: #fee2e2; color: #991b1b; }
    </style>
</head>
<body>
    <div class="container-xxl position-relative bg-white d-flex p-0">
        <jsp:include page="/view/common/components/sidebar.jsp" />
        
        <div class="content">
            <jsp:include page="../common/components/navbar.jsp" />
            
            <div class="container-fluid pt-4 px-4">
                <div class="bg-light rounded p-4">
                    <div class="d-flex align-items-center justify-content-between mb-4">
                        <h4 class="mb-0">Quản lý đơn đặt hàng</h4>
                        <a href="${pageContext.request.contextPath}/purchase-order/create" 
                           class="btn btn-primary">
                            <i class="fa fa-plus me-2"></i>Tạo đơn đặt hàng
                        </a>
                    </div>

                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            ${sessionScope.successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>

                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${sessionScope.errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>

                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${param.error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form method="get" action="${pageContext.request.contextPath}/purchase-order/list" class="mb-4">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <input type="text" class="form-control" name="keyword" 
                                       placeholder="Tìm theo mã đơn hàng" value="${keyword}">
                            </div>
                            <div class="col-md-2">
                                <select class="form-select" name="status">
                                    <option value="">Tất cả trạng thái</option>
                                    <option value="draft" ${status == 'draft' ? 'selected' : ''}>Nháp</option>
                                    <option value="submitted" ${status == 'submitted' ? 'selected' : ''}>Đã gửi</option>
                                    <option value="approved" ${status == 'approved' ? 'selected' : ''}>Đã duyệt</option>
                                    <option value="received" ${status == 'received' ? 'selected' : ''}>Đã nhận hàng</option>
                                    <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <select class="form-select" name="supplierId">
                                    <option value="">Tất cả nhà cung cấp</option>
                                    <c:forEach items="${suppliers}" var="supplier">
                                        <option value="${supplier.supplierId}" 
                                                ${supplierId == supplier.supplierId ? 'selected' : ''}>
                                            ${supplier.supplierName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <input type="date" class="form-control" name="fromDate" 
                                       placeholder="Từ ngày" value="${fromDate}">
                            </div>
                            <div class="col-md-2">
                                <input type="date" class="form-control" name="toDate" 
                                       placeholder="Đến ngày" value="${toDate}">
                            </div>
                            <div class="col-md-1">
                                <button type="submit" class="btn btn-primary w-100">
                                    <i class="fa fa-search"></i>
                                </button>
                            </div>
                        </div>
                    </form>

                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Mã đơn hàng</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Ngày đặt hàng</th>
                                    <th>Ngày giao dự kiến</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                    <th>Người tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${purchaseOrders}" var="po">
                                    <tr>
                                        <td><strong>${po.poCode}</strong></td>
                                        <td>${po.supplierName}</td>
                                        <td><fmt:formatDate value="${po.orderDate}" pattern="dd/MM/yyyy"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty po.expectedDeliveryDate}">
                                                    <fmt:formatDate value="${po.expectedDeliveryDate}" pattern="dd/MM/yyyy"/>
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><fmt:formatNumber value="${po.totalAmount}" pattern="#,##0"/> VNĐ</td>
                                        <td>
                                            <span class="status-badge status-${po.status}">
                                                <c:choose>
                                                    <c:when test="${po.status == 'draft'}">Nháp</c:when>
                                                    <c:when test="${po.status == 'submitted'}">Đã gửi</c:when>
                                                    <c:when test="${po.status == 'approved'}">Đã duyệt</c:when>
                                                    <c:when test="${po.status == 'received'}">Đã nhận hàng</c:when>
                                                    <c:when test="${po.status == 'cancelled'}">Đã hủy</c:when>
                                                    <c:otherwise>${po.status}</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td>${po.createdByName}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/purchase-order/view?id=${po.purchaseOrderId}" 
                                               class="btn btn-sm btn-info" title="Xem chi tiết">
                                                <i class="fa fa-eye"></i>
                                            </a>
                                            <c:if test="${po.status == 'draft' || po.status == 'submitted'}">
                                                <a href="${pageContext.request.contextPath}/purchase-order/edit?id=${po.purchaseOrderId}" 
                                                   class="btn btn-sm btn-primary" title="Chỉnh sửa">
                                                    <i class="fa fa-edit"></i>
                                                </a>
                                            </c:if>
                                            <c:if test="${po.status != 'received' && po.status != 'cancelled'}">
                                                <div class="btn-group ms-1">
                                                    <button type="button" class="btn btn-sm btn-warning dropdown-toggle" 
                                                            data-bs-toggle="dropdown" title="Cập nhật trạng thái">
                                                        <i class="fa fa-sync-alt"></i>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li><h6 class="dropdown-header">Chuyển trạng thái</h6></li>
                                                        <c:if test="${po.status == 'draft'}">
                                                            <li>
                                                                <a class="dropdown-item" href="#" 
                                                                   onclick="updateStatus(${po.purchaseOrderId}, 'submitted', 'Đã gửi'); return false;">
                                                                    <i class="fa fa-paper-plane me-2 text-warning"></i>Gửi duyệt
                                                                </a>
                                                            </li>
                                                        </c:if>
                                                        <c:if test="${po.status == 'submitted'}">
                                                            <li>
                                                                <a class="dropdown-item" href="#" 
                                                                   onclick="updateStatus(${po.purchaseOrderId}, 'approved', 'Đã duyệt'); return false;">
                                                                    <i class="fa fa-check me-2 text-success"></i>Duyệt đơn
                                                                </a>
                                                            </li>
                                                        </c:if>
                                                        <c:if test="${po.status == 'approved'}">
                                                            <li>
                                                                <a class="dropdown-item" href="#" 
                                                                   onclick="updateStatus(${po.purchaseOrderId}, 'received', 'Đã nhận hàng'); return false;">
                                                                    <i class="fa fa-box me-2 text-primary"></i>Đã nhận hàng
                                                                </a>
                                                            </li>
                                                        </c:if>
                                                        <li><hr class="dropdown-divider"></li>
                                                        <li>
                                                            <a class="dropdown-item text-danger" href="#" 
                                                               onclick="cancelOrder(${po.purchaseOrderId}); return false;">
                                                                <i class="fa fa-times me-2"></i>Hủy đơn
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty purchaseOrders}">
                                    <tr>
                                        <td colspan="8" class="text-center">Không có dữ liệu</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${page == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${page - 1}&pageSize=${pageSize}&status=${status}&supplierId=${supplierId}&fromDate=${fromDate}&toDate=${toDate}&keyword=${keyword}">
                                        Trước
                                    </a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${page == i ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&pageSize=${pageSize}&status=${status}&supplierId=${supplierId}&fromDate=${fromDate}&toDate=${toDate}&keyword=${keyword}">
                                            ${i}
                                        </a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${page == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${page + 1}&pageSize=${pageSize}&status=${status}&supplierId=${supplierId}&fromDate=${fromDate}&toDate=${toDate}&keyword=${keyword}">
                                        Sau
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/chart/chart.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/easing/easing.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/waypoints/waypoints.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/owlcarousel/owl.carousel.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/moment.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/moment-timezone.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/tempusdominus-bootstrap-4.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function updateStatus(poId, newStatus, statusLabel) {
            var msg = 'Bạn có chắc muốn chuyển trạng thái đơn hàng thành "' + statusLabel + '"?';
            if (newStatus === 'approved') {
                msg = 'Bạn xác nhận DUYỆT đơn hàng này?\nHệ thống sẽ ghi nhận bạn là người duyệt đơn.';
            }
            if (confirm(msg)) {
                submitStatusForm(poId, newStatus);
            }
        }

        function cancelOrder(poId) {
            if (confirm('⚠️ CẢNH BÁO: Hủy đơn hàng là thao tác không thể hoàn tác!\n\nBạn có chắc chắn muốn hủy đơn hàng này không?')) {
                submitStatusForm(poId, 'cancelled');
            }
        }

        function submitStatusForm(poId, status) {
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/purchase-order/update-status';

            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = poId;
            form.appendChild(idInput);

            var statusInput = document.createElement('input');
            statusInput.type = 'hidden';
            statusInput.name = 'status';
            statusInput.value = status;
            form.appendChild(statusInput);

            var sourceInput = document.createElement('input');
            sourceInput.type = 'hidden';
            sourceInput.name = 'source';
            sourceInput.value = 'list';
            form.appendChild(sourceInput);

            document.body.appendChild(form);
            form.submit();
        }
    </script>
</body>
</html>
