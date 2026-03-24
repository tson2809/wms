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
    <!-- Fonts and Icons -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .product-row { margin-bottom: 15px; padding: 15px; background: #f8f9fa; border-radius: 8px; border: 1px solid #dee2e6; }
        .status-badge { padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; }
        .status-draft { background-color: #e0e7ff; color: #4338ca; }
        .status-submitted { background-color: #fef3c7; color: #92400e; }
        .status-approved { background-color: #d1fae5; color: #065f46; }
        .status-received { background-color: #dbeafe; color: #1e40af; }
        .status-cancelled { background-color: #fee2e2; color: #991b1b; }
        .status-update-section { background: #f8f9fa; border-radius: 10px; padding: 20px; margin-top: 20px; }
        /* Style form elements that are disabled to look readable */
        input:disabled, select:disabled, textarea:disabled { background-color: #e9ecef !important; opacity: 1 !important; }
        .readonly-card { background: white; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); padding: 25px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container-xxl position-relative bg-white d-flex p-0">
        <jsp:include page="/view/common/components/sidebar.jsp" />
        
        <div class="content">
            <jsp:include page="../common/components/navbar.jsp" />
            
            <div class="container-fluid pt-4 px-4">
                <div class="readonly-card">
                    <!-- Header -->
                    <div class="d-flex align-items-center justify-content-between mb-4">
                        <h4 class="mb-0">
                            Chi tiết đơn đặt hàng: ${purchaseOrder.poCode}
                            <span class="status-badge status-${purchaseOrder.status} ms-3 align-middle" style="font-size: 14px;">
                                <c:choose>
                                    <c:when test="${purchaseOrder.status == 'draft'}">Nháp</c:when>
                                    <c:when test="${purchaseOrder.status == 'submitted'}">Đã gửi</c:when>
                                    <c:when test="${purchaseOrder.status == 'approved'}">Đã duyệt</c:when>
                                    <c:when test="${purchaseOrder.status == 'received'}">Đã nhận hàng</c:when>
                                    <c:when test="${purchaseOrder.status == 'cancelled'}">Đã hủy</c:when>
                                    <c:otherwise>${purchaseOrder.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </h4>
                        <div class="action-buttons no-print">
                            <a href="${pageContext.request.contextPath}/purchase-order/list" class="btn btn-secondary">
                                <i class="fa fa-arrow-left me-2"></i>Quay lại danh sách
                            </a>
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

                    <!-- Order Detail Readonly Form Layout -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <label class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                            <select class="form-select" disabled>
                                <option selected>${supplier.supplierName}</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Ngày đặt hàng <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" value="<fmt:formatDate value='${purchaseOrder.orderDate}' pattern='yyyy-MM-dd'/>" disabled>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Ngày giao dự kiến</label>
                            <input type="date" class="form-control" value="<fmt:formatDate value='${purchaseOrder.expectedDeliveryDate}' pattern='yyyy-MM-dd'/>" disabled>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">Ghi chú</label>
                        <textarea class="form-control" rows="3" disabled>${purchaseOrder.notes}</textarea>
                    </div>

                    <hr>

                    <div class="d-flex align-items-center justify-content-between mb-3">
                        <h5>Chi tiết sản phẩm</h5>
                        <!-- "Thêm sản phẩm" is hidden since it's readonly -->
                    </div>

                    <div id="productContainer">
                        <c:forEach items="${details}" var="detail" varStatus="status">
                            <div class="product-row">
                                <div class="row align-items-end">
                                    <div class="col-md-3">
                                        <label class="form-label">Sản phẩm <span class="text-danger">*</span></label>
                                        <select class="form-select" disabled>
                                            <option selected>${detail.sku} - ${detail.productName}</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">ĐVT</label>
                                        <input type="text" class="form-control" value="${detail.unitName}" disabled>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Số lượng <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" value="${detail.quantity}" disabled>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Đơn giá <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" value="${detail.unitPrice}" disabled>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Thành tiền</label>
                                        <input type="text" class="form-control" value="<fmt:formatNumber value='${detail.unitPrice * detail.quantity}' pattern='#,##0'/> VNĐ" disabled>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="row mt-4">
                        <div class="col-md-8"></div>
                        <div class="col-md-4">
                            <div class="card bg-white border">
                                <div class="card-body">
                                    <h5 class="card-title">Tổng cộng</h5>
                                    <h3 class="text-primary mb-0" id="grandTotal">
                                        <fmt:formatNumber value="${purchaseOrder.totalAmount}" pattern="#,##0"/> VNĐ
                                    </h3>
                                </div>
                            </div>
                        </div>
                    </div>

                </div><!-- end readonly-card -->

            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>