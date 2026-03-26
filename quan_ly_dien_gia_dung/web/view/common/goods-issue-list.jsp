<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    java.util.Set<String> userPermissions = (java.util.Set<String>) session.getAttribute("userPermissions");
    boolean canCreateGoodsIssue = userPermissions != null && userPermissions.contains("create goods issue");
    boolean canEditGoodsIssue = userPermissions != null && userPermissions.contains("edit goods issue");
    boolean canApproveGoodsIssue = userPermissions != null && userPermissions.contains("approve goods issue");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Danh sách phiếu xuất kho</title>
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
            .issue-filter-form .d-flex.gap-2 .btn,
            .issue-filter-form .d-flex.gap-2 a.btn {
                height: calc(1.5em + 0.75rem + 2px);
                line-height: 1;
                display: inline-flex;
                align-items: center;
                padding: 0 0.75rem;
            }
            .issue-list-section .status-badge {
                padding: 4px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 600;
                display: inline-block;
            }
            .issue-list-section .status-draft {
                background-color: #fef3c7;
                color: #92400e;
            }
            .issue-list-section .status-completed {
                background-color: #d1fae5;
                color: #065f46;
            }
            .issue-list-section .status-cancelled {
                background-color: #fee2e2;
                color: #991b1b;
            }
            .issue-list-section .page-btn {
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
            .issue-list-section .page-btn:hover {
                background-color: #f3f4f6;
            }
            .issue-list-section .page-btn.disabled {
                pointer-events: none;
                opacity: 0.4;
            }
            .issue-list-section .page-btn[type="submit"] {
                cursor: pointer;
                background: transparent;
            }
            .issue-list-section .page-number {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                color: #374151;
            }
            .issue-list-section .page-jump-form input {
                width: 48px;
                height: 30px;
                text-align: center;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
            }
            .issue-list-section .page-jump-form input:focus {
                outline: none;
                border-color: #6366f1;
            }
            .issue-list-section .action-btn-group {
                display: flex;
                justify-content: center;
                gap: 1rem;
            }
            .issue-list-section .action-btn {
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
            .issue-list-section .action-btn.action-view:hover {
                background-color: #eef2ff;
                color: #4338ca;
            }
            .issue-list-section .action-btn.action-approve:hover {
                background-color: #dcfce7;
                color: #15803d;
            }
            .issue-list-section .action-btn.action-cancel:hover {
                background-color: #fee2e2;
                color: #991b1b;
            }
            .issue-list-section .action-col {
                width: 140px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/sidebar.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12 issue-list-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">Danh sách phiếu xuất kho</h5>
                                <% if (canCreateGoodsIssue) { %>
                                    <a href="${pageContext.request.contextPath}/goods-issue-add" class="btn btn-primary">Tạo phiếu xuất kho</a>
                                <% } %>
                            </div>

                            <form action="${pageContext.request.contextPath}/goods-issue-list" method="post" class="mb-3 issue-filter-form">
                                <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                <input type="hidden" name="page" value="1">
                                <div class="row g-3 align-items-end">
                                    <div class="col-md-4">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" name="search" value="${search}" class="form-control" placeholder="Mã phiếu, người nhận...">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Sắp xếp</label>
                                        <select name="sort" class="form-select" onchange="this.form.submit()">
                                            <option value="date_desc" ${(empty sort || sort == 'date_desc') ? 'selected' : ''}>Ngày mới nhất</option>
                                            <option value="date_asc"  ${sort == 'date_asc'  ? 'selected' : ''}>Ngày cũ nhất</option>
                                            <option value="code_asc"  ${sort == 'code_asc'  ? 'selected' : ''}>Mã A-Z</option>
                                            <option value="code_desc" ${sort == 'code_desc' ? 'selected' : ''}>Mã Z-A</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Trạng thái</label>
                                        <select name="status" class="form-select" onchange="this.form.submit()">
                                            <option value="">Tất cả</option>
                                            <option value="draft"     ${status == 'draft'     ? 'selected' : ''}>Nháp</option>
                                            <option value="completed" ${status == 'completed' ? 'selected' : ''}>Hoàn thành</option>
                                            <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">&nbsp;</label>
                                        <div class="d-flex flex-nowrap gap-2 align-items-center">
                                            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                                            <a href="${pageContext.request.contextPath}/goods-issue-list" class="btn btn-secondary">Xóa bộ lọc</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="row g-3 align-items-end mt-1">
                                    <div class="col-md-4">
                                        <label class="form-label">Từ ngày</label>
                                        <input type="date" name="fromDate" class="form-control" value="${fromDate}">
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Đến ngày</label>
                                        <input type="date"
                                               name="toDate"
                                               class="form-control"
                                               value="${toDate}"
                                               id="giToDateInput">
                                    </div>
                                </div>
                            </form>

                            <div class="table-responsive">
                                <table class="table table-hover align-middle">
                                    <thead>
                                        <tr>
                                            <th style="width: 140px;">Mã phiếu</th>
                                            <th style="width: 130px;">Loại xuất</th>
                                            <th>Người nhận</th>
                                            <th style="width: 130px;">Ngày xuất</th>
                                            <th style="width: 120px;">Trạng thái</th>
                                            <th style="width: 150px;">Người tạo</th>
                                            <th style="width: 150px;">Người duyệt</th>
                                            <th class="action-col">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${issues}" var="gi">
                                            <tr>
                                                <td><strong>${gi.issueCode}</strong></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${gi.issueType eq 'sale'}">Bán hàng</c:when>
                                                        <c:when test="${gi.issueType eq 'return_supplier'}">Trả NCC</c:when>
                                                        <c:when test="${gi.issueType eq 'other'}">Khác</c:when>
                                                        <c:otherwise>${gi.issueType}</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${gi.receiverName}</td>
                                                <td>
                                                    <fmt:formatDate value="${gi.issueDate}" pattern="dd/MM/yyyy" />
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${gi.status == 'draft'}">
                                                            <span class="status-badge status-draft">Nháp</span>
                                                        </c:when>
                                                        <c:when test="${gi.status == 'completed'}">
                                                            <span class="status-badge status-completed">Hoàn thành</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge status-cancelled">Đã hủy</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${gi.createdByUser != null ? gi.createdByUser.fullName : '-'}</td>
                                                <td>${gi.approvedByUser != null ? gi.approvedByUser.fullName : '-'}</td>
                                                <td class="action-col">
                                                    <div class="action-btn-group">
                                                        <a href="${pageContext.request.contextPath}/goods-issue-detail?id=${gi.issueId}"
                                                           class="action-btn action-view"
                                                           title="${gi.status == 'draft' ? 'Xem / Duyệt' : 'Xem chi tiết'}">
                                                            <c:choose>
                                                                <c:when test="${gi.status == 'draft'}">
                                                                    <iconify-icon icon="<%= canEditGoodsIssue ? "lucide:edit-2" : "lucide:eye" %>"></iconify-icon>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <iconify-icon icon="lucide:eye"></iconify-icon>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </a>
                                                        <% if (canApproveGoodsIssue) { %>
                                                        <c:if test="${gi.status == 'draft'}">
                                                            <form method="POST" action="${pageContext.request.contextPath}/goods-issue-list"
                                                                  style="display:inline"
                                                                  >
                                                                <input type="hidden" name="id" value="${gi.issueId}">
                                                                <input type="hidden" name="status" value="completed">
                                                                <button type="submit" class="action-btn action-approve" title="Duyệt xuất kho">
                                                                    <iconify-icon icon="lucide:check"></iconify-icon>
                                                                </button>
                                                            </form>
                                                            <form method="POST" action="${pageContext.request.contextPath}/goods-issue-list"
                                                                  style="display:inline"
                                                                  onsubmit="return confirm('Hủy phiếu xuất này?')">
                                                                <input type="hidden" name="id" value="${gi.issueId}">
                                                                <input type="hidden" name="status" value="cancelled">
                                                                <button type="submit" class="action-btn action-cancel" title="Hủy">
                                                                    <iconify-icon icon="lucide:x"></iconify-icon>
                                                                </button>
                                                            </form>
                                                        </c:if>
                                                        <% } %>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <c:if test="${empty issues}">
                                <p class="text-muted text-center py-4">Chưa có phiếu xuất kho nào.</p>
                            </c:if>
                            <c:if test="${!empty issues}">
                                <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-4 border-top-0">
                                    <div class="d-flex align-items-center gap-2 flex-grow-1">
                                        <c:choose>
                                            <c:when test="${page == 1}">
                                                <span class="page-btn disabled">‹</span>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="post" action="${pageContext.request.contextPath}/goods-issue-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page - 1}">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="status" value="${status}">
                                                    <input type="hidden" name="sort" value="${sort}">
                                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                                    <input type="hidden" name="toDate" value="${toDate}">
                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">‹</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="page-number">
                                            Trang
                                            <form action="${pageContext.request.contextPath}/goods-issue-list" method="post" class="page-jump-form d-inline">
                                                <input type="hidden" name="search" value="${search}">
                                                <input type="hidden" name="status" value="${status}">
                                                <input type="hidden" name="sort" value="${sort}">
                                                <input type="hidden" name="fromDate" value="${fromDate}">
                                                <input type="hidden" name="toDate" value="${toDate}">
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
                                                <form method="post" action="${pageContext.request.contextPath}/goods-issue-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page + 1}">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="status" value="${status}">
                                                    <input type="hidden" name="sort" value="${sort}">
                                                    <input type="hidden" name="fromDate" value="${fromDate}">
                                                    <input type="hidden" name="toDate" value="${toDate}">
                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">›</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="d-flex align-items-center gap-2 ms-n5">
                                        <label class="form-label small mb-0 me-2">Hiển thị</label>
                                        <form method="post" action="${pageContext.request.contextPath}/goods-issue-list" class="d-inline">
                                            <input type="hidden" name="page" value="1">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="status" value="${status}">
                                            <input type="hidden" name="sort" value="${sort}">
                                            <input type="hidden" name="fromDate" value="${fromDate}">
                                            <input type="hidden" name="toDate" value="${toDate}">
                                            <select name="numberPerPage" class="form-select form-select-sm w-auto" onchange="this.form.submit()">
                                                <option value="5"  ${numberPerPage == 5  ? 'selected' : ''}>5</option>
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
        <script>
            (function () {
                var toDateInput = document.getElementById('giToDateInput');
                if (!toDateInput) return;

                var today = new Date();
                var yyyy = today.getFullYear();
                var mm = String(today.getMonth() + 1).padStart(2, '0');
                var dd = String(today.getDate()).padStart(2, '0');
                var todayStr = yyyy + '-' + mm + '-' + dd;
                toDateInput.setAttribute('max', todayStr);
            })();
        </script>
    </body>
</html>
