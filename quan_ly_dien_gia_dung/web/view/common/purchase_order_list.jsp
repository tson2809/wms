<%@page contentType="text/html" pageEncoding="UTF-8" %>
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
    <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .po-list-section .status-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
            display: inline-block;
        }
        .po-list-section .status-pending    { background-color:#fef3c7; color:#92400e; }
        .po-list-section .status-processing { background-color:#dbeafe; color:#1e40af; }
        .po-list-section .status-completed  { background-color:#d1fae5; color:#065f46; }
        .po-list-section .status-cancelled  { background-color:#fee2e2; color:#991b1b; }
        .po-list-section .page-btn {
            width:32px; height:32px; border:1px solid #d1d5db; border-radius:6px;
            display:flex; align-items:center; justify-content:center;
            text-decoration:none; color:#374151; font-weight:600;
        }
        .po-list-section .page-btn:hover    { background-color:#f3f4f6; }
        .po-list-section .page-btn.disabled { pointer-events:none; color:#9ca3af; border-color:#e5e7eb; }
        .po-list-section .page-btn.active   { background-color:#4f46e5; color:#fff; border-color:#4f46e5; }
        .po-list-section .action-btn-group {
            display: flex;
            justify-content: center;
            gap: 0.5rem;
        }
        .po-list-section .action-btn {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            border: 1px solid #e5e7eb;
            background-color: #fff;
            color: #374151;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 16px;
            text-decoration: none;
            transition: background-color 0.2s ease, color 0.2s ease;
            cursor: pointer;
            padding: 0;
        }
        .po-list-section .action-btn.action-view:hover { background-color: #eef2ff; color: #4338ca; }
        .po-list-section .action-btn.action-edit:hover { background-color: #eff6ff; color: #1d4ed8; }
        .po-list-section .action-btn.action-cancel:hover { background-color: #fee2e2; color: #991b1b; }
        .po-list-section .action-btn.action-approve:hover { background-color: #dcfce7; color: #15803d; }
        .po-list-section .action-btn.action-receipt:hover { background-color: #e0f2fe; color: #0369a1; }
        .po-list-section .action-btn.action-issue:hover { background-color: #fef3c7; color: #b45309; }
    </style>
    <!-- Add Iconify -->
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
</head>
<body>
    <div class="container-fluid position-relative d-flex p-0">
        <!-- Sidebar -->
        <c:choose>
            <c:when test="${roleId == 2}">
                <jsp:include page="/view/manager/components/sidebarManager.jsp"/>
            </c:when>
            <c:when test="${roleId == 4}">
                <jsp:include page="/view/sale/components/sidebarSale.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="/view/staff/components/sidebarStaff.jsp"/>
            </c:otherwise>
        </c:choose>

        <div class="content">
            <!-- Navbar -->
            <jsp:include page="/view/common/components/navbar.jsp"/>

            <div class="container-fluid pt-4 px-4 po-list-section">

                <!-- Alert messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${sessionScope.successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="successMessage" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>${sessionScope.errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <div class="d-flex align-items-center justify-content-between mb-4">
                    <h5 class="mb-0">
                        <i class="fas fa-shopping-cart me-2 text-primary"></i>
                        Danh sách đơn đặt hàng
                    </h5>
                    <div class="d-flex gap-2">
                        <c:if test="${roleId == 2}">
                            <a href="${pageContext.request.contextPath}/purchase-order/create" class="btn btn-primary">
                                <i class="fas fa-plus me-1"></i> Tạo đơn đặt hàng
                            </a>
                        </c:if>
                        <c:if test="${roleId == 4}">
                            <a href="${pageContext.request.contextPath}/sale-order/create" class="btn btn-primary">
                                <i class="fas fa-plus me-1"></i> Tạo đơn đặt hàng
                            </a>
                        </c:if>
                    </div>
                </div>



                <!-- Filter form -->
                <!-- Filter form -->
                <div class="bg-white rounded p-4 mb-4 shadow-sm">
                    <form method="GET" action="${pageContext.request.contextPath}/purchase-order/list" class="product-filter-form">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <label class="form-label fw-semibold">Trạng thái</label>
                                <select name="status" class="form-select">
                                    <option value="">-- Tất cả --</option>
                                    <option value="draft"     ${status == 'draft'     ? 'selected' : ''}>Chờ xử lý</option>
                                    <option value="submitted" ${status == 'submitted' ? 'selected' : ''}>Đang xử lý</option>
                                    <option value="received"  ${status == 'received'  ? 'selected' : ''}>Hoàn tất</option>
                                    <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                </select>
                            </div>
                            <c:if test="${roleId == 2 and not isSaleOrderView}">
                            <div class="col-md-3">
                                <label class="form-label fw-semibold">Nhà cung cấp</label>
                                <select name="supplierId" class="form-select">
                                    <option value="">-- Tất cả --</option>
                                    <c:forEach var="s" items="${suppliers}">
                                        <option value="${s.supplierId}" ${supplierId == s.supplierId ? 'selected' : ''}>${s.supplierName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-semibold">Từ ngày</label>
                                <input type="date" name="fromDate" class="form-control" value="${fromDate}">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-semibold">Đến ngày</label>
                                <input type="date" name="toDate" class="form-control" value="${toDate}">
                            </div>
                            </c:if>
                            <div class="col-md-2">
                                <label class="form-label fw-semibold">Từ khóa</label>
                                <input type="text" name="keyword" class="form-control" placeholder="Mã PO..." value="${keyword}">
                            </div>
                        </div>
                        <div class="d-flex gap-2 mt-3">
                            <button type="submit" class="btn btn-primary"><i class="fas fa-search me-1"></i>Tìm kiếm</button>
                            <a href="${pageContext.request.contextPath}/purchase-order/list${roleId == 3 and orderType == 'sale' ? '?orderType=sale' : ''}" class="btn btn-outline-secondary"><i class="fas fa-redo me-1"></i>Đặt lại</a>
                        </div>
                    </form>

                </div>

                <!-- Table -->
                <div class="bg-white rounded shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0 align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Mã đơn</th>
                                    <c:if test="${roleId == 3}">
                                        <th>Loại</th>
                                    </c:if>
                                    <c:if test="${roleId != 4}">
                                        <th>Nhà cung cấp</th>
                                    </c:if>
                                    <th>Ngày đặt</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                    <c:choose>
                                        <c:when test="${roleId == 3}">
                                            <th>Người tạo đơn</th>
                                        </c:when>
                                        <c:otherwise>
                                            <th>NV phụ trách</th>
                                        </c:otherwise>
                                    </c:choose>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty purchaseOrders}">
                                    <tr>
                                        <td colspan="${roleId == 3 ? '9' : '8'}" class="text-center py-4 text-muted">
                                            <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                            Không có đơn đặt hàng nào.
                                        </td>
                                    </tr>
                                </c:if>
                                <c:forEach var="po" items="${purchaseOrders}" varStatus="loop">
                                    <tr>
                                        <td>${(page - 1) * pageSize + loop.index + 1}</td>
                                        <td><strong>${po.poCode}</strong></td>
                                        <%-- Cột Loại chỉ hiện với Staff --%>
                                        <c:if test="${roleId == 3}">
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty po.supplierName}">
                                                        <span class="badge" style="background:#dbeafe;color:#1e40af;font-weight:500">Nhập kho</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge" style="background:#fef3c7;color:#92400e;font-weight:500">Xuất hàng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:if>
                                        <c:if test="${roleId != 4}">
                                            <td>${po.supplierName}</td>
                                        </c:if>
                                        <td>
                                            <fmt:formatDate value="${po.orderDate}" pattern="dd/MM/yyyy"/>
                                        </td>
                                        <td><fmt:formatNumber value="${po.totalAmount}" type="number" groupingUsed="true"/> đ</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${po.status == 'draft'}"><span class="status-badge status-pending">Chờ xử lý</span></c:when>
                                                <c:when test="${po.status == 'submitted'}"><span class="status-badge status-processing">Đang xử lý</span></c:when>
                                                <c:when test="${po.status == 'received'}"><span class="status-badge status-completed">Hoàn tất</span></c:when>
                                                <c:when test="${po.status == 'cancelled'}"><span class="status-badge status-cancelled">Đã hủy</span></c:when>
                                                <c:otherwise><span class="status-badge">${po.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${roleId == 3}">
                                                    ${not empty po.createdByName ? po.createdByName : '-'}
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${empty po.supplierName and not empty po.goodsIssueApprovedByName}">
                                                            ${po.goodsIssueApprovedByName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${not empty po.approvedByName ? po.approvedByName : '-'}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <div class="action-btn-group">

                                                <!-- Xem chi tiết (luôn hiện) -->
                                                <a href="${pageContext.request.contextPath}/purchase-order/edit?id=${po.purchaseOrderId}"
                                                   class="action-btn action-view" title="Xem chi tiết">
                                                    <iconify-icon icon="lucide:eye"></iconify-icon>
                                                </a>

                                                <!-- Manager: Sửa (chỉ khi draft) -->
                                                <c:if test="${roleId == 2 and po.status == 'draft'}">
                                                    <a href="${pageContext.request.contextPath}/purchase-order/edit?id=${po.purchaseOrderId}"
                                                       class="action-btn action-edit" title="Chỉnh sửa">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>
                                                </c:if>

                                                <!-- Manager: Hủy (draft hoặc submitted) -->
                                                <c:if test="${roleId == 2 and (po.status == 'draft' or po.status == 'submitted')}">
                                                    <form method="POST" action="${pageContext.request.contextPath}/purchase-order/list"
                                                          onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?');" style="display:inline">
                                                        <input type="hidden" name="action" value="cancel">
                                                        <input type="hidden" name="id" value="${po.purchaseOrderId}">
                                                        <button type="submit" class="action-btn action-cancel" title="Hủy đơn">
                                                            <iconify-icon icon="lucide:x"></iconify-icon>
                                                        </button>
                                                    </form>
                                                </c:if>

                                                <%-- Staff: Nhận đơn (chỉ draft, chưa có người nhận) --%>
                                                <c:if test="${roleId == 3 and po.status == 'draft' and empty po.approvedByName}">
                                                    <form method="POST" action="${pageContext.request.contextPath}/purchase-order/claim"
                                                          onsubmit="return confirm('Bạn muốn nhận đơn đặt hàng này?');" style="display:inline">
                                                        <input type="hidden" name="id" value="${po.purchaseOrderId}">
                                                        <button type="submit" class="action-btn action-approve" title="Nhận đơn">
                                                            <iconify-icon icon="lucide:check"></iconify-icon>
                                                        </button>
                                                    </form>
                                                </c:if>

                                                <%-- Staff: Tạo phiếu nhập kho (PO có NCC, submitted, là người phụ trách) --%>
                                                <c:if test="${roleId == 3 and not empty po.supplierName and po.status == 'submitted' and po.approvedBy == currentUserId}">
                                                    <a href="${pageContext.request.contextPath}/goods-receipt-add?purchaseOrderId=${po.purchaseOrderId}"
                                                       class="action-btn action-receipt" title="Tạo phiếu nhập kho">
                                                        <iconify-icon icon="lucide:package-plus"></iconify-icon>
                                                    </a>
                                                </c:if>

                                                <%-- Staff: Tạo phiếu xuất (Sale order, submitted, là người phụ trách) --%>
                                                <c:if test="${roleId == 3 and empty po.supplierName and po.status == 'submitted' and po.approvedBy == currentUserId}">
                                                    <a href="${pageContext.request.contextPath}/goods-issue-add?purchaseOrderId=${po.purchaseOrderId}"
                                                       class="action-btn action-issue" title="Tạo phiếu xuất hàng">
                                                        <iconify-icon icon="lucide:package-minus"></iconify-icon>
                                                    </a>
                                                </c:if>

                                                <%-- Sale: Hủy đơn (chỉ khi draft) --%>
                                                <c:if test="${roleId == 4 and po.status == 'draft'}">
                                                    <form method="POST" action="${pageContext.request.contextPath}/purchase-order/list"
                                                          onsubmit="return confirm('Bạn có chắc muốn hủy đơn này?');" style="display:inline">
                                                        <input type="hidden" name="action" value="cancel">
                                                        <input type="hidden" name="id" value="${po.purchaseOrderId}">
                                                        <button type="submit" class="action-btn action-cancel" title="Hủy đơn">
                                                            <iconify-icon icon="lucide:x"></iconify-icon>
                                                        </button>
                                                    </form>
                                                </c:if>

                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="d-flex align-items-center justify-content-between px-4 py-3 border-top">
                            <span class="text-muted small">
                                Hiển thị ${(page-1)*pageSize + 1} – ${page*pageSize < totalRecords ? page*pageSize : totalRecords}
                                trong tổng số ${totalRecords} đơn hàng
                            </span>
                            <div class="d-flex gap-1">
                                <a href="?page=${page-1}&pageSize=${pageSize}&status=${status}&supplierId=${supplierId}&fromDate=${fromDate}&toDate=${toDate}&keyword=${keyword}"
                                   class="page-btn ${page <= 1 ? 'disabled' : ''}">
                                    <i class="fas fa-chevron-left fa-xs"></i>
                                </a>
                                <c:forEach begin="1" end="${totalPages}" var="p">
                                    <a href="?page=${p}&pageSize=${pageSize}&status=${status}&supplierId=${supplierId}&fromDate=${fromDate}&toDate=${toDate}&keyword=${keyword}"
                                       class="page-btn ${p == page ? 'active' : ''}">${p}</a>
                                </c:forEach>
                                <a href="?page=${page+1}&pageSize=${pageSize}&status=${status}&supplierId=${supplierId}&fromDate=${fromDate}&toDate=${toDate}&keyword=${keyword}"
                                   class="page-btn ${page >= totalPages ? 'disabled' : ''}">
                                    <i class="fas fa-chevron-right fa-xs"></i>
                                </a>
                            </div>
                        </div>
                    </c:if>
                </div>

            </div><!-- /.container-fluid -->
        </div><!-- /.content -->
    </div><!-- /.container-fluid -->

    <script src="${pageContext.request.contextPath}/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/chart/chart.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/easing/easing.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/waypoints/waypoints.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/owlcarousel/owl.carousel.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/moment.min.js"></script>
    <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/moment-timezone.js"></script>
    <script src="${pageContext.request.contextPath}/lib/tempusdominus/js/tempusdominus-bootstrap-4.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
