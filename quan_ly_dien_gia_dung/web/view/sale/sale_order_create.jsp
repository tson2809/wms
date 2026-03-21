<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Tạo đơn đặt hàng</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .product-row { margin-bottom: 10px; padding: 15px; background: #f8f9fa; border-radius: 8px; }
        #pf-search-wrap { position: relative; }
        #pf-dropdown {
            position: absolute; z-index: 1000; width: 100%;
            background: #fff; border: 1px solid #dee2e6; border-radius: 6px;
            max-height: 280px; overflow-y: auto; box-shadow: 0 4px 12px rgba(0,0,0,.08);
            display: none;
        }
        .pf-dd-item { padding: 8px 14px; cursor: pointer; font-size: .9rem; }
        .pf-dd-item:hover { background: #e8f4ff; }
    </style>
</head>
<body>
    <div class="container-fluid position-relative d-flex p-0">
        <jsp:include page="/view/common/components/sidebar.jsp"/>

        <div class="content">
            <jsp:include page="/view/common/components/navbar.jsp"/>

            <div class="container-fluid pt-4 px-4">
                <div class="bg-light rounded p-4">
                    <div class="d-flex align-items-center justify-content-between mb-4">
                        <h4 class="mb-0">Tạo đơn đặt hàng mới</h4>
                        <a href="${pageContext.request.contextPath}/purchase-order/list" class="btn btn-secondary">
                            <i class="fa fa-arrow-left me-2"></i>Quay lại
                        </a>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/sale-order/create" id="poForm">
                        <div class="row mb-4">
                            <div class="col-md-4">
                                <label class="form-label">Ngày đặt hàng <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" name="orderDate" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Ngày giao dự kiến</label>
                                <input type="date" class="form-control" name="expectedDeliveryDate">
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Ghi chú</label>
                            <textarea class="form-control" name="notes" rows="2"></textarea>
                        </div>

                        <hr>

                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <h5>Chi tiết sản phẩm</h5>
                        </div>

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

                        <%-- Product rows (added dynamically via JS) --%>
                        <div id="productContainer"></div>

                        <div class="row mt-4">
                            <div class="col-md-8"></div>
                            <div class="col-md-4">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">Tổng cộng</h5>
                                        <h3 class="text-primary" id="grandTotal">0 VNĐ</h3>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4">
                            <button type="submit" class="btn btn-primary">
                                <i class="fa fa-save me-2"></i>Tạo đơn đặt hàng
                            </button>
                            <a href="${pageContext.request.contextPath}/purchase-order/list" class="btn btn-secondary">Hủy</a>
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
        window.PO_FILTER = { contextPath: '${pageContext.request.contextPath}' };
    </script>
    <script src="${pageContext.request.contextPath}/js/purchase-order-product-filter.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelector('input[name="orderDate"]').value = new Date().toISOString().split('T')[0];
        });
        document.getElementById('poForm').addEventListener('submit', function(e) {
            if (!document.querySelector('#productContainer .product-row')) {
                e.preventDefault();
                alert('Vui lòng thêm ít nhất một sản phẩm!');
            }
        });
    </script>
</body>
</html>
