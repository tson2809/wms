<%-- 
    Document   : unit_list
    Created on : 10 thg 3, 2026, 14:34:10
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Danh sách đơn vị tính</title>
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
            .supplier-filter-form .d-flex.gap-2 .btn,
            .supplier-filter-form .d-flex.gap-2 a.btn {
                height: calc(1.5em + 0.75rem + 2px);
                line-height: 1;
                display: inline-flex;
                align-items: center;
                padding: 0 0.75rem;
            }
            .unit-list-section .status-dot {
                width: 8px;
                height: 8px;
                border-radius: 50%;
                display: inline-block;
                margin-right: 6px;
            }
            .unit-list-section .status-active {
                background-color: #22c55e;
            }
            .unit-list-section .status-inactive {
                background-color: #ef4444;
            }
            .unit-list-section .page-btn {
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
            .unit-list-section .page-btn:hover {
                background-color: #f3f4f6;
            }
            .unit-list-section .page-btn.disabled {
                pointer-events: none;
                opacity: 0.4;
            }
            .unit-list-section .page-number {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                color: #374151;
            }
            .unit-list-section .page-jump-form input {
                width: 48px;
                height: 30px;
                text-align: center;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
            }
            .unit-list-section .page-jump-form input:focus {
                outline: none;
                border-color: #6366f1;
            }
            .unit-list-section .action-btn-group {
                display: flex;
                justify-content: center;
                gap: 1.5rem;
            }
            .unit-list-section .action-btn {
                width: 38px;
                height: 38px;
                border-radius: 50%;
                border: 1px solid #e5e7eb;
                background-color: #fff;
                color: #374151;
                display: flex;
                justify-content: center;
                align-items: center;
                font-size: 18px;
                text-decoration: none;
                transition: background-color 0.2s ease, color 0.2s ease;
            }
            .unit-list-section .action-btn.action-edit:hover {
                background-color: #eef2ff;
                color: #4338ca;
            }
            .unit-list-section .action-col {
                width: 160px;
                text-align: center;
                padding-left: 8px;
                padding-right: 8px;
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
                        <div class="col-12 unit-list-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">Danh sách đơn vị tính</h5>
                                <c:if test="${sessionScope.user.role.roleId == 2}">
                                <a href="${pageContext.request.contextPath}/unit-add" class="btn btn-primary">Thêm đơn vị</a>
                                </c:if>
                            </div>

                            <c:if test="${not empty message}">
                                <div class="alert alert-${not empty messageType ? messageType : 'info'} alert-dismissible fade show" role="alert">
                                    ${message}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/unit-list" method="post" class="mb-3 supplier-filter-form">
                                <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                <input type="hidden" name="page" value="1">
                                <div class="row g-3 align-items-end">
                                    <div class="col-md-6">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" name="search" value="${search}" class="form-control" placeholder="Tên đơn vị...">
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">&nbsp;</label>
                                        <div class="d-flex gap-2">
                                            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                                            <a href="${pageContext.request.contextPath}/unit-list" class="btn btn-secondary">Xóa bộ lọc</a>
                                        </div>
                                    </div>
                                </div>
                            </form>

                            <table class="table table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th style="width: 80px;">STT</th>
                                        <th>Tên đơn vị</th>
                                        <th style="width: 200px;">Trạng thái</th>
                                        <th class="action-col">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${units}" var="u" varStatus="st">
                                        <tr>
                                            <td>${(page - 1) * numberPerPage + st.index + 1}</td>
                                            <td>${u.unitName}</td>
                                            <td>
                                                <span class="status-dot ${u.status == 'active' ? 'status-active' : 'status-inactive'}"></span>
                                                ${u.status == 'active' ? 'Đang hoạt động' : 'Ngưng hoạt động'}
                                            </td>
                                            <td class="action-col">
                                                <div class="action-btn-group">
                                                    <c:if test="${sessionScope.user.role.roleId == 2}">
                                                    <a href="${pageContext.request.contextPath}/unit-edit?id=${u.unitId}"
                                                       class="action-btn action-edit" title="Chỉnh sửa">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>
                                                    </c:if>
                                                    <c:if test="${sessionScope.user.role.roleId == 2}">
                                                        <c:choose>
                                                            <c:when test="${u.status == 'active'}">
                                                                <form method="post" action="${pageContext.request.contextPath}/unit-list" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn ngưng hoạt động đơn vị này?')">
                                                                    <input type="hidden" name="toggleId" value="${u.unitId}">
                                                                    <button type="submit" class="btn btn-sm btn-secondary" title="Ngưng hoạt động">
                                                                        <i class="fa fa-user-slash"></i>
                                                                    </button>
                                                                </form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form method="post" action="${pageContext.request.contextPath}/unit-list" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn bật hoạt động đơn vị này?')">
                                                                    <input type="hidden" name="toggleId" value="${u.unitId}">
                                                                    <button type="submit" class="btn btn-sm btn-success" title="Bật hoạt động">
                                                                        <i class="fa fa-user-check"></i>
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${sessionScope.user.role.roleId != 2}">
                                                        <span class="text-muted small">-</span>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <c:if test="${empty units}">
                                <p class="text-muted text-center py-4">Chưa có đơn vị tính nào.</p>
                            </c:if>

                            <c:if test="${not empty units}">
                                <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-4">
                                    <div class="d-flex align-items-center gap-2 flex-grow-1">
                                        <c:choose>
                                            <c:when test="${page == 1}">
                                                <span class="page-btn disabled">‹</span>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="post" action="${pageContext.request.contextPath}/unit-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page - 1}">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">‹</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="page-number">
                                            Trang
                                            <form action="${pageContext.request.contextPath}/unit-list" method="post" class="page-jump-form d-inline">
                                                <input type="hidden" name="search" value="${search}">
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
                                                <form method="post" action="${pageContext.request.contextPath}/unit-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page + 1}">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">›</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="d-flex align-items-center gap-2">
                                        <label class="form-label small mb-0 me-2">Hiển thị</label>
                                        <form method="post" action="${pageContext.request.contextPath}/unit-list" class="d-inline">
                                            <input type="hidden" name="page" value="1">
                                            <input type="hidden" name="search" value="${search}">
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
    </body>
</html>
