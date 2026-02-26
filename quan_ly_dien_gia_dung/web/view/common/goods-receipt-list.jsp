<%-- 
    Document   : goods-receipt-list
    Created on : Feb 09, 2026
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Danh sách phiếu nhập kho</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <meta content="" name="keywords">
        <meta content="" name="description">
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
            .receipt-filter-form .d-flex.gap-2 .btn,
            .receipt-filter-form .d-flex.gap-2 a.btn {
                height: calc(1.5em + 0.75rem + 2px);
                line-height: 1;
                display: inline-flex;
                align-items: center;
                padding: 0 0.75rem;
            }
            .receipt-list-section .status-badge {
                padding: 4px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 600;
                display: inline-block;
            }
            .receipt-list-section .status-draft {
                background-color: #fef3c7;
                color: #92400e;
            }
            .receipt-list-section .status-completed {
                background-color: #d1fae5;
                color: #065f46;
            }
            .receipt-list-section .status-cancelled {
                background-color: #fee2e2;
                color: #991b1b;
            }
            .receipt-list-section .page-btn {
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
            .receipt-list-section .page-btn:hover {
                background-color: #f3f4f6;
            }
            .receipt-list-section .page-btn.disabled {
                pointer-events: none;
                opacity: 0.4;
            }
            .receipt-list-section .page-btn[type="submit"] {
                cursor: pointer;
                background: transparent;
            }
            .receipt-list-section .page-number {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                color: #374151;
            }
            .receipt-list-section .page-jump-form input {
                width: 48px;
                height: 30px;
                text-align: center;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
            }
            .receipt-list-section .page-jump-form input:focus {
                outline: none;
                border-color: #6366f1;
            }
            .receipt-list-section .action-btn-group {
                display: flex;
                justify-content: center;
                gap: 1rem;
            }
            .receipt-list-section .action-btn {
                width: 36px;
                height: 36px;
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
            }
            .receipt-list-section .action-btn.action-view:hover {
                background-color: #eef2ff;
                color: #4338ca;
            }
            .receipt-list-section .action-col {
                width: 120px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <c:choose>
                <c:when test="${sessionScope.user.role.roleId == 2}">
                    <jsp:include page="/view/manager/components/sidebarManager.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 3}">
                    <jsp:include page="/view/staff/components/sidebarStaff.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/view/common/components/RoleSideBar.jsp" />
                </c:otherwise>
            </c:choose>

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12 receipt-list-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">Danh sách phiếu nhập kho</h5>
                                <c:if test="${sessionScope.user.role.roleId == 3}">
                                    <a href="${pageContext.request.contextPath}/goods-receipt-add" class="btn btn-primary">Tạo phiếu nhập kho</a>
                                </c:if>
                            </div>
                            <form action="${pageContext.request.contextPath}/goods-receipt-list" method="post" class="mb-3 receipt-filter-form">
                                <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                <input type="hidden" name="page" value="1">
                                <div class="row g-3 align-items-end">
                                    <div class="col-md-4">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" name="search" value="${search}" class="form-control" placeholder="Mã phiếu, nhà cung cấp...">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Sắp xếp</label>
                                        <select name="sort" class="form-select" onchange="this.form.submit()">
                                            <option value="date_desc" ${(empty sort || sort == 'date_desc') ? 'selected' : ''}>Ngày mới nhất</option>
                                            <option value="date_asc" ${sort == 'date_asc' ? 'selected' : ''}>Ngày cũ nhất</option>
                                            <option value="code_asc" ${sort == 'code_asc' ? 'selected' : ''}>Mã A-Z</option>
                                            <option value="code_desc" ${sort == 'code_desc' ? 'selected' : ''}>Mã Z-A</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Trạng thái</label>
                                        <select name="status" class="form-select" onchange="this.form.submit()">
                                            <option value="">Tất cả</option>
                                            <option value="draft" ${status == 'draft' ? 'selected' : ''}>Nháp</option>
                                            <option value="completed" ${status == 'completed' ? 'selected' : ''}>Hoàn thành</option>
                                            <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">&nbsp;</label>
                                        <div class="d-flex flex-nowrap gap-2 align-items-center">
                                            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                                            <a href="${pageContext.request.contextPath}/goods-receipt-list" class="btn btn-secondary">Xóa bộ lọc</a>
                                        </div>
                                    </div>
                                </div>
                            </form>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle">
                                    <thead>
                                        <tr>
                                            <th style="width: 80px;">ID</th>
                                            <th style="width: 140px;">Mã phiếu</th>
                                            <th>Nhà cung cấp</th>
                                            <th style="width: 130px;">Ngày nhập</th>
                                            <th style="width: 140px;" class="text-end">Tổng tiền</th>
                                            <th style="width: 120px;">Trạng thái</th>
                                            <th style="width: 150px;">Người tạo</th>
                                            <th class="action-col">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${receipts}" var="gr">
                                            <tr>
                                                <td>${gr.receiptId}</td>
                                                <td><strong>${gr.receiptCode}</strong></td>
                                                <td>${gr.supplier.supplierName}</td>
                                                <td>
                                                    <fmt:formatDate value="${gr.receiptDate}" pattern="dd/MM/yyyy" />
                                                </td>
                                                <td class="text-end">
                                                    <fmt:formatNumber value="${gr.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${gr.status == 'draft'}">
                                                            <span class="status-badge status-draft">Nháp</span>
                                                        </c:when>
                                                        <c:when test="${gr.status == 'completed'}">
                                                            <span class="status-badge status-completed">Hoàn thành</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge status-cancelled">Đã hủy</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${gr.createdByUser != null ? gr.createdByUser.fullName : '-'}</td>
                                                <td class="action-col">
                                                    <div class="action-btn-group">
                                                        <c:choose>
                                                            <c:when test="${gr.status == 'draft'}">
                                                                <a href="${pageContext.request.contextPath}/goods-receipt-edit?id=${gr.receiptId}"
                                                                   class="action-btn action-view" title="Sửa phiếu nhập">
                                                                    <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                                </a>
                                                            </c:when>
                                                            <c:when test="${gr.status == 'completed' || gr.status == 'cancelled'}">
                                                                <a href="${pageContext.request.contextPath}/goods-receipt-edit?id=${gr.receiptId}"
                                                                   class="action-btn action-view" title="Xem chi tiết">
                                                                    <iconify-icon icon="lucide:eye"></iconify-icon>
                                                                </a>
                                                            </c:when>
                                                        </c:choose>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <c:if test="${empty receipts}">
                                <p class="text-muted text-center py-4">Chưa có phiếu nhập kho nào.</p>
                            </c:if>
                            <c:if test="${!empty receipts}">
                                <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-4 border-top-0">
                                    <div class="d-flex align-items-center gap-2 flex-grow-1">
                                        <c:choose>
                                            <c:when test="${page == 1}">
                                                <span class="page-btn disabled">‹</span>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="post" action="${pageContext.request.contextPath}/goods-receipt-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page - 1}">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="status" value="${status}">
                                                    <input type="hidden" name="sort" value="${sort}">
                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">‹</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="page-number">
                                            Trang
                                            <form action="${pageContext.request.contextPath}/goods-receipt-list" method="post" class="page-jump-form d-inline">
                                                <input type="hidden" name="search" value="${search}">
                                                <input type="hidden" name="status" value="${status}">
                                                <input type="hidden" name="sort" value="${sort}">
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
                                                <form method="post" action="${pageContext.request.contextPath}/goods-receipt-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page + 1}">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="status" value="${status}">
                                                    <input type="hidden" name="sort" value="${sort}">
                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">›</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="d-flex align-items-center gap-2 ms-n5">
                                        <label class="form-label small mb-0 me-2">Hiển thị</label>
                                        <form method="post" action="${pageContext.request.contextPath}/goods-receipt-list" class="d-inline">
                                            <input type="hidden" name="page" value="1">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="status" value="${status}">
                                            <input type="hidden" name="sort" value="${sort}">
                                            <select name="numberPerPage" class="form-select form-select-sm w-auto" onchange="this.form.submit()">
                                                <option value="5" ${numberPerPage == 5 ? 'selected' : ''}>5</option>
                                                <option value="10" ${numberPerPage == 10 ? 'selected' : ''}>10</option>
                                                <option value="20" ${numberPerPage == 20 ? 'selected' : ''}>20</option>
                                            </select>
                                        </form>
                                        <span class="small text-muted">kết quả</span>
                                    </div>
                                    <div class="flex-grow-1"></div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
</html>
