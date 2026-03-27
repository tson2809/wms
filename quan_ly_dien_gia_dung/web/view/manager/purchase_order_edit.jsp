<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Chỉnh sửa đơn đặt hàng</title>
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
        .product-row { margin-bottom: 10px; padding: 15px; background: #f8f9fa; border-radius: 8px; }
        .btn-remove { color: #dc3545; cursor: pointer; }
        input:disabled, select:disabled, textarea:disabled { background-color: #e9ecef !important; opacity: 1 !important; }
        
        #pf-search-wrap { position: relative; }
        #pf-dropdown {
            position: absolute; z-index: 1000; width: 100%;
            background: #fff; border: 1px solid #dee2e6; border-radius: 6px;
            max-height: 280px; overflow-y: auto; box-shadow: 0 4px 12px rgba(0,0,0,.08);
            display: none;
        }
        .pf-dd-item { padding: 8px 14px; cursor: pointer; font-size: .9rem; }
        .pf-dd-item:hover { background: #e8f4ff; }
        #productContainer:empty::after { content: 'Chưa có sản phẩm. Dùng bộ lọc bên trên để thêm.'; color:#aaa; font-size:.9rem; }
    </style>
</head>
<body>
    <div class="container-fluid position-relative d-flex p-0">
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
                <div class="bg-light rounded p-4">
                    <div class="d-flex align-items-center justify-content-between mb-4">
                        <h4 class="mb-0">
                            <c:choose>
                                <c:when test="${viewOnly}">Xem đơn đặt hàng: ${purchaseOrder.poCode}</c:when>
                                <c:otherwise>Chỉnh sửa đơn đặt hàng: ${purchaseOrder.poCode}</c:otherwise>
                            </c:choose>
                        </h4>
                        <a href="${pageContext.request.contextPath}/purchase-order/list" 
                           class="btn btn-secondary">
                            <i class="fa fa-arrow-left me-2"></i>Quay lại
                        </a>
                    </div>

                    <c:if test="${viewOnly}">
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            Đơn hàng đang ở trạng thái <strong>${purchaseOrder.status}</strong> — chỉ xem, không thể chỉnh sửa.
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/purchase-order/edit?id=${purchaseOrder.purchaseOrderId}" id="poForm">
                        <input type="hidden" name="id" value="${purchaseOrder.purchaseOrderId}">
                        
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <label class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                                <select class="form-select" name="supplierId" required ${viewOnly ? 'disabled' : ''}>
                                    <option value="">-- Chọn nhà cung cấp --</option>
                                    <c:forEach items="${suppliers}" var="supplier">
                                        <option value="${supplier.supplierId}" 
                                                ${supplier.supplierId == purchaseOrder.supplierId ? 'selected' : ''}>
                                            ${supplier.supplierName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Ngày đặt hàng <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" name="orderDate" 
                                       value="<fmt:formatDate value='${purchaseOrder.orderDate}' pattern='yyyy-MM-dd'/>" 
                                       required ${viewOnly ? 'disabled' : ''}>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Ngày giao dự kiến</label>
                                <input type="date" class="form-control" name="expectedDeliveryDate"
                                       value="<fmt:formatDate value='${purchaseOrder.expectedDeliveryDate}' pattern='yyyy-MM-dd'/>"
                                       ${viewOnly ? 'disabled' : ''}>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Ghi chú</label>
                        <textarea class="form-control" name="notes" rows="3" ${viewOnly ? 'disabled' : ''}>${purchaseOrder.notes}</textarea>
                        </div>

                        <hr>

                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <h5>Chi tiết sản phẩm</h5>
                        </div>

                        <c:if test="${not viewOnly}">
                            <%-- Filter bar --%>
                            <div class="row g-2 mb-3">
                                <div class="col-md-3">
                                    <label class="form-label mb-1">Danh mục</label>
                                    <select id="pf-category" class="form-select">
                                        <option value="">-- Tất cả danh mục --</option>
                                        <c:forEach items="${categories}" var="cat">
                                            <option value="${cat.categoryId}">${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label mb-1">Thương hiệu</label>
                                    <select id="pf-brand" class="form-select">
                                        <option value="">-- Tất cả thương hiệu --</option>
                                        <c:forEach items="${brands}" var="br">
                                            <option value="${br.brandId}">${br.brandName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6" id="pf-search-wrap">
                                    <label class="form-label mb-1">Tìm sản phẩm (SKU / tên)</label>
                                    <input type="text" id="pf-keyword" class="form-control" placeholder="Gõ để tìm kiếm...">
                                    <div id="pf-dropdown"></div>
                                </div>
                            </div>
                        </c:if>

                        <div id="productContainer">
                            <c:forEach items="${details}" var="detail" varStatus="status">
                                <div class="product-row" data-index="${status.index}">
                                    <div class="row align-items-end">
                                        <div class="col-md-3">
                                            <label class="form-label">Sản phẩm <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" readonly value="${detail.sku} – ${detail.productName}">
                                            <input type="hidden" class="variant-id-input" name="variantIds[]" value="${detail.variantId}">
                                        </div>
                                        <div class="col-md-1">
                                            <label class="form-label">ĐVT</label>
                                            <input type="text" class="form-control unit-input" value="${detail.unitName}" readonly>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Số lượng <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control quantity-input" name="quantities[]" 
                                                   min="1" value="${detail.quantity}" required onchange="poCalcTotal(this)" ${viewOnly ? 'disabled' : ''}>
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label">Đơn giá <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control price-input" name="unitPrices[]" 
                                                   min="0" step="0.01" value="${detail.unitPrice}" required onchange="poCalcTotal(this)" ${viewOnly ? 'disabled' : ''}>
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label">Thành tiền</label>
                                            <input type="text" class="form-control total-input" 
                                                   value="<fmt:formatNumber value='${detail.totalAmount}' pattern='#,##0'/> VNĐ" readonly>
                                        </div>
                                        <div class="col-md-1 d-flex align-items-end">
                                            <c:if test="${not viewOnly}">
                                                <button type="button" class="btn btn-danger btn-sm w-100" onclick="poRemoveRow(this)">
                                                    <i class="fa fa-trash"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <div class="row mt-4">
                            <div class="col-md-8"></div>
                            <div class="col-md-4">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">Tổng cộng</h5>
                                        <h3 class="text-primary" id="grandTotal">
                                            <fmt:formatNumber value="${purchaseOrder.totalAmount}" pattern="#,##0"/> VNĐ
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4">
                            <c:choose>
                                <c:when test="${not viewOnly}">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fa fa-save me-2"></i>Cập nhật đơn đặt hàng
                                    </button>
                                </c:when>
                            </c:choose>
                            <a href="${pageContext.request.contextPath}/purchase-order/list" 
                               class="btn btn-secondary">
                                Quay lại danh sách
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        window.PO_FILTER = {
            contextPath: '${pageContext.request.contextPath}',
            showDetailNote: false
        };
    </script>
    <script src="${pageContext.request.contextPath}/js/purchase-order-product-filter.js?v=2"></script>
    <script>
        document.getElementById('poForm').addEventListener('submit', function(e) {
            if (!document.querySelector('#productContainer .product-row')) {
                e.preventDefault();
                alert('Vui lòng thêm ít nhất một sản phẩm!');
            }
        });
    </script>
</body>
</html>
