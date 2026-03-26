<%-- 
    Document   : supplier_list
    Created on : 31 thg 1, 2026, 8:34:10
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Danh sách nhà cung cấp</title>
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
            .supplier-filter-form .d-flex.gap-2 .btn,
            .supplier-filter-form .d-flex.gap-2 a.btn {
                height: calc(1.5em + 0.75rem + 2px);
                line-height: 1;
                display: inline-flex;
                align-items: center;
                padding: 0 0.75rem;
            }
            .supplier-list-section .status-dot {
                width: 8px;
                height: 8px;
                border-radius: 50%;
                display: inline-block;
                margin-right: 6px;
            }
            .supplier-list-section .status-active {
                background-color: #22c55e;
            }
            .supplier-list-section .status-inactive {
                background-color: #ef4444;
            }
            .supplier-list-section .page-btn {
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
            .supplier-list-section .page-btn:hover {
                background-color: #f3f4f6;
            }
            .supplier-list-section .page-btn.disabled {
                pointer-events: none;
                opacity: 0.4;
            }
            .supplier-list-section .page-btn[type="submit"] {
                cursor: pointer;
                background: transparent;
            }
            .supplier-list-section .page-number {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                color: #374151;
            }
            .supplier-list-section .page-jump-form input {
                width: 48px;
                height: 30px;
                text-align: center;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
            }
            .supplier-list-section .page-jump-form input:focus {
                outline: none;
                border-color: #6366f1;
            }
            .supplier-list-section .action-btn-group {
                display: flex;
                justify-content: center;
                gap: 1.5rem;
            }
            .supplier-list-section .action-btn {
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
            .supplier-list-section .action-btn.action-edit:hover {
                background-color: #eef2ff;
                color: #4338ca;
            }
            .supplier-list-section .action-col {
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
                        <div class="col-12 supplier-list-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">Danh sách nhà cung cấp</h5>
                                <c:if test="${sessionScope.user.role.roleId == 2}">
                                <a href="${pageContext.request.contextPath}/supplier-add" class="btn btn-primary">Thêm nhà cung cấp</a>
                                </c:if>
                            </div>
                            <form action="${pageContext.request.contextPath}/supplier-list" method="post" class="mb-3 supplier-filter-form">
                                <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                <input type="hidden" name="page" value="1">
                                <div class="row g-3 align-items-end">
                                    <div class="col-md-5">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" name="search" value="${search}" class="form-control" placeholder="Tên, email, SĐT...">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Sắp xếp theo tên</label>
                                        <select name="sort" class="form-select" onchange="this.form.submit()">
                                            <option value="name_asc" ${(empty sort || sort == 'name_asc') ? 'selected' : ''}>Tên A-Z</option>
                                            <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Tên Z-A</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Trạng thái</label>
                                        <select name="status" class="form-select" onchange="this.form.submit()">
                                            <option value="">Tất cả</option>
                                            <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
                                            <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">&nbsp;</label>
                                        <div class="d-flex flex-nowrap gap-2 align-items-center">
                                            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                                            <a href="${pageContext.request.contextPath}/supplier-list" class="btn btn-secondary">Xóa bộ lọc</a>
                                        </div>
                                    </div>
                                </div>
                            </form>
                            <table class="table table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>
                                            <form method="post" action="${pageContext.request.contextPath}/supplier-list" class="d-inline">
                                                <input type="hidden" name="search" value="${search}"><input type="hidden" name="status" value="${status}"><input type="hidden" name="page" value="${page}"><input type="hidden" name="numberPerPage" value="${numberPerPage}"><input type="hidden" name="sort" value="${sort == 'name_asc' ? 'name_desc' : 'name_asc'}">
                                                <button type="submit" class="btn btn-link link-secondary text-decoration-none p-0 border-0">Tên nhà cung cấp</button>
                                            </form>
                                        </th>
                                        <th>Người liên hệ</th>
                                        <th>Email</th>
                                        <th>Số điện thoại</th>
                                        <th>Trạng thái</th>
                                        <th class="action-col">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${suppliers}" var="s">
                                        <tr>
                                            <td>${s.supplierId}</td>
                                            <td>${s.supplierName}</td>
                                            <td>${s.contactPerson}</td>
                                            <td>${s.email}</td>
                                            <td>${s.phone}</td>
                                            <td>
                                                <span class="status-dot ${s.status == 'active' ? 'status-active' : 'status-inactive'}"></span>
                                                ${s.status == 'active' ? 'Active' : 'Inactive'}
                                            </td>
                                            <td class="action-col">
                                                <div class="action-btn-group">
                                                    <c:if test="${sessionScope.user.role.roleId == 2}">
                                                    <a href="${pageContext.request.contextPath}/supplier-detail?id=${s.supplierId}"
                                                       class="action-btn action-edit" title="Chỉnh sửa">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>
                                                    </c:if>
                                                    <c:if test="${sessionScope.user.role.roleId == 2}">
                                                    <c:choose>
                                                        <c:when test="${s.status == 'active'}">
                                                            <form method="post" action="${pageContext.request.contextPath}/supplier-list" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn deactive nhà cung cấp này?')">
                                                                <input type="hidden" name="id" value="${s.supplierId}">
                                                                <input type="hidden" name="status" value="inactive">
                                                                <button type="submit" class="btn btn-sm btn-secondary" title="Deactive">
                                                                    <i class="fa fa-user-slash"></i>
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="post" action="${pageContext.request.contextPath}/supplier-list" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn active nhà cung cấp này?')">
                                                                <input type="hidden" name="id" value="${s.supplierId}">
                                                                <input type="hidden" name="status" value="active">
                                                                <button type="submit" class="btn btn-sm btn-success" title="Active">
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
                            <c:if test="${empty suppliers}">
                                <p class="text-muted text-center py-4">Chưa có nhà cung cấp nào.</p>
                            </c:if>
                            <c:if test="${!empty suppliers}">
                                <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-4 border-top-0">
                                    <div class="d-flex align-items-center gap-2 flex-grow-1">
                                        <c:choose>
                                            <c:when test="${page == 1}">
                                                <span class="page-btn disabled">‹</span>
                                            </c:when>
                                            <c:otherwise>
                                                <form method="post" action="${pageContext.request.contextPath}/supplier-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page - 1}"><input type="hidden" name="search" value="${search}"><input type="hidden" name="status" value="${status}"><input type="hidden" name="sort" value="${sort}"><input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">‹</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="page-number">
                                            Trang
                                            <form action="${pageContext.request.contextPath}/supplier-list" method="post" class="page-jump-form d-inline">
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
                                                <form method="post" action="${pageContext.request.contextPath}/supplier-list" class="d-inline">
                                                    <input type="hidden" name="page" value="${page + 1}"><input type="hidden" name="search" value="${search}"><input type="hidden" name="status" value="${status}"><input type="hidden" name="sort" value="${sort}"><input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                    <button type="submit" class="page-btn border-0 bg-transparent p-0">›</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="d-flex align-items-center gap-2 ms-n5">
                                        <label class="form-label small mb-0 me-2">Hiển thị</label>
                                        <form method="post" action="${pageContext.request.contextPath}/supplier-list" class="d-inline" id="numberPerPageForm">
                                            <input type="hidden" name="page" value="1"><input type="hidden" name="search" value="${search}"><input type="hidden" name="status" value="${status}"><input type="hidden" name="sort" value="${sort}">
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
