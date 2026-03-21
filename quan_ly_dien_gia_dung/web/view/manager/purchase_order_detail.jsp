<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Chi tiết đơn đặt hàng</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .bg-light {
            background-color: #f8f9fa !important;
        }
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
        
        .detail-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            border: none;
        }
        .detail-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px 10px 0 0;
        }
        .info-section {
            padding: 20px;
        }
        .info-item {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .info-item:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: 600;
            color: #666;
            min-width: 150px;
        }
        .info-value {
            color: #333;
            text-align: right;
        }
        
        .product-table {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        .product-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #495057;
            border: none;
            padding: 15px;
        }
        .product-table td {
            padding: 15px;
            vertical-align: middle;
            border-color: #f0f0f0;
        }
        .product-table tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .summary-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 10px;
            padding: 25px;
            margin-top: 20px;
        }
        .summary-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            font-size: 16px;
        }
        .summary-item:last-child {
            margin-bottom: 0;
            font-size: 20px;
            font-weight: 700;
            border-top: 2px solid rgba(255,255,255,0.3);
            padding-top: 15px;
            margin-top: 15px;
        }
        
        .action-buttons {
            margin-top: 20px;
        }
        .btn-action {
            margin: 0 5px;
            padding: 10px 20px;
            border-radius: 25px;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        
        .timeline {
            position: relative;
            padding: 20px 0;
        }
        .timeline::before {
            content: '';
            position: absolute;
            left: 20px;
            top: 0;
            bottom: 0;
            width: 2px;
            background: #e0e0e0;
        }
        .timeline-item {
            position: relative;
            padding-left: 50px;
            margin-bottom: 20px;
        }
        .timeline-item::before {
            content: '';
            position: absolute;
            left: 14px;
            top: 5px;
            width: 14px;
            height: 14px;
            border-radius: 50%;
            background: #667eea;
            border: 3px solid white;
            box-shadow: 0 0 0 3px #e0e0e0;
        }
        .timeline-content {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }
        .timeline-date {
            font-size: 12px;
            color: #666;
            margin-bottom: 5px;
        }
        
        .status-update-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-top: 20px;
        }
        
        @media print {
            .no-print { display: none !important; }
            .detail-card { box-shadow: none; }
        }
    </style>
</head>
<body>
    <div class="container-xxl position-relative bg-white d-flex p-0">
        <jsp:include page="/view/common/components/sidebar.jsp" />
        
        <div class="content">
            <jsp:include page="../common/components/navbar.jsp" />
            
            <div class="container-fluid pt-4 px-4">
                <!-- Header -->
                <div class="d-flex align-items-center justify-content-between mb-4">
                    <h4 class="mb-0">
                        <i class="fa fa-file-invoice me-2"></i>Chi tiết đơn đặt hàng
                    </h4>
                    <div class="action-buttons no-print">
                        <a href="${pageContext.request.contextPath}/purchase-order/list" 
                           class="btn btn-secondary btn-action">
                            <i class="fa fa-arrow-left me-2"></i>Quay lại
                        </a>
                        <c:if test="${purchaseOrder.status == 'draft' || purchaseOrder.status == 'submitted'}">
                            <a href="${pageContext.request.contextPath}/purchase-order/edit?id=${purchaseOrder.purchaseOrderId}" 
                               class="btn btn-primary btn-action">
                                <i class="fa fa-edit me-2"></i>Chỉnh sửa
                            </a>
                        </c:if>
                        <button class="btn btn-info btn-action" onclick="window.print()">
                            <i class="fa fa-print me-2"></i>In đơn hàng
                        </button>
                    </div>
                </div>

                <!-- Success/Error Messages -->
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

                <!-- Thông tin đơn hàng -->
                <div class="row">
                    <div class="col-lg-8">
                        <div class="detail-card">
                            <div class="detail-header">
                                <h5 class="mb-0">
                                    <i class="fa fa-shopping-cart me-2"></i>${purchaseOrder.poCode}
                                </h5>
                                <small>Đơn đặt hàng #${purchaseOrder.purchaseOrderId}</small>
                            </div>
                            <div class="info-section">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="info-item">
                                            <span class="info-label">Trạng thái:</span>
                                            <span class="info-value">
                                                <span class="status-badge status-${purchaseOrder.status}">
                                                    <c:choose>
                                                        <c:when test="${purchaseOrder.status == 'draft'}">Nháp</c:when>
                                                        <c:when test="${purchaseOrder.status == 'submitted'}">Đã gửi</c:when>
                                                        <c:when test="${purchaseOrder.status == 'approved'}">Đã duyệt</c:when>
                                                        <c:when test="${purchaseOrder.status == 'received'}">Đã nhận hàng</c:when>
                                                        <c:when test="${purchaseOrder.status == 'cancelled'}">Đã hủy</c:when>
                                                        <c:otherwise>${purchaseOrder.status}</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Ngày đặt hàng:</span>
                                            <span class="info-value">
                                                <fmt:formatDate value="${purchaseOrder.orderDate}" pattern="dd/MM/yyyy"/>
                                            </span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Ngày giao dự kiến:</span>
                                            <span class="info-value">
                                                <c:choose>
                                                    <c:when test="${not empty purchaseOrder.expectedDeliveryDate}">
                                                        <fmt:formatDate value="${purchaseOrder.expectedDeliveryDate}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="info-item">
                                            <span class="info-label">Người tạo:</span>
                                            <span class="info-value">${purchaseOrder.createdByName}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Ngày tạo:</span>
                                            <span class="info-value">
                                                <fmt:formatDate value="${purchaseOrder.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Cập nhật lần cuối:</span>
                                            <span class="info-value">
                                                <fmt:formatDate value="${purchaseOrder.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <c:if test="${not empty purchaseOrder.notes}">
                                    <div class="info-item">
                                        <span class="info-label">Ghi chú:</span>
                                        <span class="info-value">${purchaseOrder.notes}</span>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-lg-4">
                        <div class="detail-card">
                            <div class="detail-header">
                                <h5 class="mb-0">
                                    <i class="fa fa-truck me-2"></i>Thông tin nhà cung cấp
                                </h5>
                            </div>
                            <div class="info-section">
                                <div class="info-item">
                                    <span class="info-label">Tên nhà cung cấp:</span>
                                    <span class="info-value">${supplier.supplierName}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Mã nhà cung cấp:</span>
                                    <span class="info-value">${supplier.supplierCode}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Email:</span>
                                    <span class="info-value">${supplier.email}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Điện thoại:</span>
                                    <span class="info-value">${supplier.phone}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Địa chỉ:</span>
                                    <span class="info-value">${supplier.address}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Chi tiết sản phẩm -->
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="product-table">
                            <div class="detail-header">
                                <h5 class="mb-0">
                                    <i class="fa fa-box me-2"></i>Chi tiết sản phẩm
                                </h5>
                            </div>
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>STT</th>
                                            <th>ID Biến thể</th>
                                            <th>Sản phẩm</th>
                                            <th>Số lượng</th>
                                            <th>Đơn giá</th>
                                            <th>Thành tiền</th>
                                            <th>Ghi chú</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${details}" var="detail" varStatus="status">
                                            <tr>
                                                <td>${status.index + 1}</td>
                                                <td><strong>${detail.variantId}</strong></td>
                                                <td>${detail.productName}</td>
                                                <td>
                                                    <span class="badge bg-info">${detail.quantity}</span>
                                                </td>
                                                <td>
                                                    <fmt:formatNumber value="${detail.unitPrice}" pattern="#,##0"/> VNĐ
                                                </td>
                                                <td>
                                                    <strong>
                                                        <fmt:formatNumber value="${detail.unitPrice * detail.quantity}" pattern="#,##0"/> VNĐ
                                                    </strong>
                                                </td>
                                                <td>
                                                    <c:if test="${not empty detail.notes}">
                                                        <small class="text-muted">${detail.notes}</small>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Update Status Section - Chỉ hiển thị khi trạng thái còn có thể thay đổi -->
                <c:if test="${purchaseOrder.status != 'received' && purchaseOrder.status != 'cancelled'}">
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="status-update-section no-print">
                            <h5 class="mb-3">
                                <i class="fa fa-sync-alt me-2"></i>Cập nhật trạng thái
                            </h5>
                            <form method="post" action="${pageContext.request.contextPath}/purchase-order/update-status" class="row g-3 align-items-end">
                                <input type="hidden" name="id" value="${purchaseOrder.purchaseOrderId}">
                                <div class="col-md-5">
                                    <label class="form-label fw-bold">Trạng thái hiện tại</label>
                                    <div>
                                        <span class="status-badge status-${purchaseOrder.status}">
                                            <c:choose>
                                                <c:when test="${purchaseOrder.status == 'draft'}">Nháp</c:when>
                                                <c:when test="${purchaseOrder.status == 'submitted'}">Đã gửi</c:when>
                                                <c:when test="${purchaseOrder.status == 'approved'}">Đã duyệt</c:when>
                                                <c:otherwise>${purchaseOrder.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                        <i class="fa fa-arrow-right mx-2 text-muted"></i>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">Chuyển sang</label>
                                    <select class="form-select" name="status" id="statusSelect" required>
                                        <option value="">-- Chọn trạng thái --</option>
                                        <c:choose>
                                            <c:when test="${purchaseOrder.status == 'draft'}">
                                                <option value="submitted">Đã gửi</option>
                                                <option value="cancelled">Đã hủy</option>
                                            </c:when>
                                            <c:when test="${purchaseOrder.status == 'submitted'}">
                                                <option value="approved">Đã duyệt</option>
                                                <option value="cancelled">Đã hủy</option>
                                            </c:when>
                                            <c:when test="${purchaseOrder.status == 'approved'}">
                                                <option value="received">Đã nhận hàng</option>
                                                <option value="cancelled">Đã hủy</option>
                                            </c:when>
                                        </c:choose>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <button type="submit" class="btn btn-primary w-100" onclick="return confirmUpdateStatus()">
                                        <i class="fa fa-sync-alt me-2"></i>Cập nhật
                                    </button>
                                </div>
                            </form>
                            <div class="mt-3">
                                <small class="text-muted">
                                    <i class="fa fa-info-circle me-1"></i>
                                    <c:choose>
                                        <c:when test="${purchaseOrder.status == 'draft'}">
                                            Đơn nháp có thể gửi duyệt hoặc hủy.
                                        </c:when>
                                        <c:when test="${purchaseOrder.status == 'submitted'}">
                                            Đơn đã gửi có thể duyệt hoặc hủy. Khi duyệt, hệ thống sẽ ghi nhận người duyệt.
                                        </c:when>
                                        <c:when test="${purchaseOrder.status == 'approved'}">
                                            Đơn đã duyệt có thể xác nhận nhận hàng hoặc hủy.
                                        </c:when>
                                    </c:choose>
                                </small>
                            </div>
                        </div>
                    </div>
                </div>
                </c:if>
                
                <c:if test="${purchaseOrder.status == 'received' || purchaseOrder.status == 'cancelled'}">
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="alert ${purchaseOrder.status == 'received' ? 'alert-success' : 'alert-secondary'} no-print">
                            <i class="fa ${purchaseOrder.status == 'received' ? 'fa-check-circle' : 'fa-ban'} me-2"></i>
                            <c:choose>
                                <c:when test="${purchaseOrder.status == 'received'}">
                                    <strong>Đơn hàng đã hoàn thành.</strong> Không thể thay đổi trạng thái.
                                </c:when>
                                <c:when test="${purchaseOrder.status == 'cancelled'}">
                                    <strong>Đơn hàng đã bị hủy.</strong> Không thể thay đổi trạng thái.
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </div>
                </c:if>

                <!-- Tổng kết -->
                <div class="row mt-4">
                    <div class="col-lg-6">
                        <div class="detail-card">
                            <div class="detail-header">
                                <h5 class="mb-0">
                                    <i class="fa fa-history me-2"></i>Lịch sử thay đổi
                                </h5>
                            </div>
                            <div class="info-section">
                                <div class="timeline">
                                    <div class="timeline-item">
                                        <div class="timeline-content">
                                            <div class="timeline-date">
                                                <fmt:formatDate value="${purchaseOrder.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </div>
                                            <div>Đơn hàng được tạo bởi ${purchaseOrder.createdByName}</div>
                                        </div>
                                    </div>
                                    <c:if test="${purchaseOrder.status != 'draft'}">
                                        <div class="timeline-item">
                                            <div class="timeline-content">
                                                <div class="timeline-date">
                                                    <fmt:formatDate value="${purchaseOrder.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </div>
                                                <div>Đơn hàng được cập nhật trạng thái</div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-lg-6">
                        <div class="summary-card">
                            <h5 class="mb-4">
                                <i class="fa fa-calculator me-2"></i>Tổng kết đơn hàng
                            </h5>
                            <div class="summary-item">
                                <span>Tổng số sản phẩm:</span>
                                <span>${details.size()}</span>
                            </div>
                            <div class="summary-item">
                                <span>Tổng số lượng:</span>
                                <span>
                                    <c:set var="totalQuantity" value="0"/>
                                    <c:forEach items="${details}" var="detail">
                                        <c:set var="totalQuantity" value="${totalQuantity + detail.quantity}"/>
                                    </c:forEach>
                                    ${totalQuantity}
                                </span>
                            </div>
                            <div class="summary-item">
                                <span>Tổng thành tiền:</span>
                                <span>
                                    <fmt:formatNumber value="${purchaseOrder.totalAmount}" pattern="#,##0"/> VNĐ
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        function confirmUpdateStatus() {
            var statusSelect = document.getElementById('statusSelect');
            var selectedValue = statusSelect.value;
            var selectedText = statusSelect.options[statusSelect.selectedIndex].text;
            
            if (selectedValue === '') {
                alert('Vui lòng chọn trạng thái mới!');
                return false;
            }
            
            // Cảnh báo đặc biệt khi hủy đơn
            if (selectedValue === 'cancelled') {
                return confirm('⚠️ CẢNH BÁO: Hủy đơn hàng là thao tác không thể hoàn tác!\n\n'
                    + 'Bạn có chắc chắn muốn hủy đơn hàng này không?');
            }
            
            // Cảnh báo khi duyệt đơn
            if (selectedValue === 'approved') {
                return confirm('Bạn xác nhận DUYỆT đơn hàng này?\n\n'
                    + 'Hệ thống sẽ ghi nhận bạn là người duyệt đơn.');
            }
            
            return confirm('Bạn có chắc muốn chuyển trạng thái đơn hàng thành "' + selectedText + '" không?');
        }
    </script>
</body>
</html>
