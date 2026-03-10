<%-- 
    Document : notification_list 
    Created on : 3 thg 2, 2026, 07:56:03 
    Author : thais 
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap"
              rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css"
              rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"
              rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css"
              rel="stylesheet">
        <link
            href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css"
            rel="stylesheet" />
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
            <jsp:include page="/view/common/components/RoleSideBar.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12 notification-list-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">Danh sách thông báo</h5>
                                <c:if test="${sessionScope.user.role.roleName eq 'Admin'
                                                or sessionScope.user.role.roleName eq 'Manager'}">
                                    <a href="${pageContext.request.contextPath}/notification-add"
                                   class="btn btn-primary">Thêm thông báo</a>
                                </c:if>
                            </div>
                            <form action="${pageContext.request.contextPath}/notification-list"
                                  method="post" class="mb-3 notification-filter-form">
                                <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                <input type="hidden" name="page" value="1">
                                <div class="row g-3 align-items-end">
                                    <div class="col-md-4">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" name="search" value="${search}"
                                               class="form-control" placeholder="Tiêu đề, nội dung...">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Loại thông báo</label>
                                        <select name="notificationType" class="form-select"
                                                onchange="this.form.submit()">
                                            <option value="">Tất cả</option>
                                            <c:forEach items="${allTypes}" var="type">
                                                <option value="${type}" ${notificationType==type
                                                                 ? 'selected' : '' }>
                                                            ${type}
                                                        </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Sắp xếp</label>
                                            <select name="sort" class="form-select"
                                                    onchange="this.form.submit()">
                                                <option value="date_desc" ${(empty sort || sort=='date_desc') ? 'selected' : '' }>Mới nhất</option>
                                                <option value="date_asc" ${sort=='date_asc' ? 'selected': '' }>Cũ nhất</option>
                                                <option value="title_asc" ${sort=='title_asc' ? 'selected': '' }>Tiêu đề A-Z</option>
                                                <option value="title_desc" ${sort=='title_desc' ? 'selected': '' }>Tiêu đề Z-A</option>
                                            </select>
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label">&nbsp;</label>
                                            <div class="d-flex flex-nowrap gap-2 align-items-center">
                                                <button type="submit" class="btn btn-primary">Tìm
                                                    kiếm</button>
                                                <a href="${pageContext.request.contextPath}/notification-list"
                                                   class="btn btn-secondary">Xóa bộ lọc</a>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                                <table class="table table-hover align-middle">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tiêu đề</th>
                                            <th>Loại</th>
                                            <!--<th>Nội dung</th>-->
                                            <th>Ngày tạo</th>
                                            <th class="action-col">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${notifications}" var="n">
                                            <tr>
                                                <td>${n.notificationId}</td>
                                                <td>${n.title}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${n.notificationType == 'info'}">
                                                            <span class="badge bg-info">Info</span>
                                                        </c:when>
                                                        <c:when test="${n.notificationType == 'warning'}">
                                                            <span
                                                                class="badge bg-warning text-dark">Warning</span>
                                                        </c:when>
                                                        <c:when test="${n.notificationType == 'error'}">
                                                            <span class="badge bg-danger">Error</span>
                                                        </c:when>
                                                        <c:when test="${n.notificationType == 'success'}">
                                                            <span class="badge bg-success">Success</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span
                                                                class="badge bg-secondary">${n.notificationType}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <!-- <td>
                                    <div class="content-preview" title="${n.content}">
                                                ${fn:length(n.content) > 50 ?
                                                  fn:substring(n.content, 0, 50).concat('...') :
                                                  n.content}
                                            </div>
                                        </td>-->
                                                <td>
                                                    <fmt:formatDate value="${n.createdAt}"
                                                                    pattern="dd/MM/yyyy HH:mm" />
                                                </td>
                                                <td class="action-col">
                                                    <div class="action-btn-group">
                                                        <a href="${pageContext.request.contextPath}/notification-edit?id=${n.notificationId}"
                                                           class="action-btn action-edit"
                                                           title="Chỉnh sửa">
                                                            <iconify-icon
                                                                icon="lucide:edit-2"></iconify-icon>
                                                        </a>
                                                        <form
                                                            action="${pageContext.request.contextPath}/notification-list"
                                                            method="post" class="d-inline"
                                                            onsubmit="return confirm('Bạn có chắc chắn muốn xóa thông báo này?')">
                                                            <input type="hidden" name="id"
                                                                   value="${n.notificationId}">
                                                            <input type="hidden" name="action"
                                                                   value="delete">
                                                            <c:choose>
                                                                <c:when test="${sessionScope.user.role.roleName eq 'Admin'}">
                                                                    <button type="submit" class="action-btn"
                                                                            title="Xóa">
                                                                        <iconify-icon
                                                                            icon="lucide:trash-2"></iconify-icon>
                                                                    </button>
                                                                </c:when>
                                                                <c:when test="${sessionScope.user.role.roleName eq 'Manager'
                                                                                and n.notificationType ne 'System'}">
                                                                        <button type="submit" class="action-btn"
                                                                                title="Xóa">
                                                                            <iconify-icon
                                                                                icon="lucide:trash-2"></iconify-icon>
                                                                        </button>
                                                                </c:when>
                                                                <c:otherwise>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <c:if test="${empty notifications}">
                                    <p class="text-muted text-center py-4">Chưa có thông báo nào.</p>
                                </c:if>
                                <c:if test="${!empty notifications}">
                                    <div
                                        class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-4 border-top-0">
                                        <div class="d-flex align-items-center gap-2 flex-grow-1">
                                            <c:choose>
                                                <c:when test="${page == 1}">
                                                    <span class="page-btn disabled">‹</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/notification-list"
                                                          class="d-inline">
                                                        <input type="hidden" name="page"
                                                               value="${page - 1}"><input type="hidden"
                                                               name="search" value="${search}"><input
                                                               type="hidden" name="notificationType"
                                                               value="${notificationType}"><input type="hidden"
                                                               name="sort" value="${sort}"><input type="hidden"
                                                               name="numberPerPage" value="${numberPerPage}">
                                                        <button type="submit"
                                                                class="page-btn border-0 bg-transparent p-0">‹</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="page-number">
                                                Trang
                                                <form
                                                    action="${pageContext.request.contextPath}/notification-list"
                                                    method="post" class="page-jump-form d-inline">
                                                    <input type="hidden" name="search" value="${search}">
                                                    <input type="hidden" name="notificationType"
                                                           value="${notificationType}">
                                                    <input type="hidden" name="sort" value="${sort}">
                                                    <input type="hidden" name="numberPerPage"
                                                           value="${numberPerPage}">
                                                    <input type="number" name="page" min="1"
                                                           max="${listOfPage}" value="${page}"
                                                           onchange="this.form.submit()">
                                                </form>
                                                / ${listOfPage}
                                            </span>
                                            <c:choose>
                                                <c:when test="${page == listOfPage}">
                                                    <span class="page-btn disabled">›</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/notification-list"
                                                          class="d-inline">
                                                        <input type="hidden" name="page"
                                                               value="${page + 1}"><input type="hidden"
                                                               name="search" value="${search}"><input
                                                               type="hidden" name="notificationType"
                                                               value="${notificationType}"><input type="hidden"
                                                               name="sort" value="${sort}"><input type="hidden"
                                                               name="numberPerPage" value="${numberPerPage}">
                                                        <button type="submit"
                                                                class="page-btn border-0 bg-transparent p-0">›</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="d-flex align-items-center gap-2 ms-n5">
                                            <label class="form-label small mb-0 me-2">Hiển thị</label>
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/notification-list"
                                                  class="d-inline" id="numberPerPageForm">
                                                <input type="hidden" name="page" value="1"><input
                                                    type="hidden" name="search" value="${search}"><input
                                                    type="hidden" name="notificationType"
                                                    value="${notificationType}"><input type="hidden"
                                                    name="sort" value="${sort}">
                                                <select name="numberPerPage"
                                                        class="form-select form-select-sm w-auto"
                                                        onchange="this.form.submit()">
                                                    <option value="5" ${numberPerPage==5 ? 'selected' : ''}>5</option>
                                                    <option value="10" ${numberPerPage==10 ? 'selected' : ''}>10</option>
                                                    <option value="20" ${numberPerPage==20 ? 'selected' : ''}>20</option>
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
            <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
            <script src="${pageContext.request.contextPath}/js/main.js"></script>
        </body>

    </html>