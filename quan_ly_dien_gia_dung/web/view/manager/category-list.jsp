<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Danh sách danh mục</title>
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
        <link href="${pageContext.request.contextPath}/css/user-list.css" rel="stylesheet">
    </head>
    <body>
        <%
            model.User currentUser = (model.User) session.getAttribute("user");
            String roleName = "";
            if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().getRoleName() != null) {
                roleName = currentUser.getRole().getRoleName().toLowerCase();
            }
            String sidebarPage = "/view/common/components/sidebar.jsp";
            if ("manager".equals(roleName)) {
                sidebarPage = "/view/common/components/sidebar.jsp";
            }
        %>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="<%= sidebarPage %>" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="d-flex justify-content-between align-items-center mb-3 user-list-header">
                                <h5 class="mb-0">Danh sách danh mục</h5>
                                <a href="${pageContext.request.contextPath}/category-add" class="btn btn-primary">Thêm danh mục</a>
                            </div>

                            <form action="category-list" method="get" class="user-filter-form mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-6">
                                        <label class="form-label">Tên</label>
                                        <input type="text" name="keyword" value="${param.keyword}" class="form-control" placeholder="Tìm kiếm theo tên">
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Trạng thái</label>
                                        <select name="status" class="form-select">
                                            <option value="all" ${(empty param.status || param.status == 'all') ? 'selected' : ''}>Tất cả</option>
                                            <option value="active" ${param.status == 'active' ? 'selected' : ''}>Hoạt động</option>
                                            <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Ngừng hoạt động</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2 d-flex gap-2">
                                        <button type="submit" class="btn btn-primary w-100">Tìm kiếm</button>
                                        <button type="reset" class="btn btn-secondary w-100" onclick="window.location.href = 'category-list'">Xóa lọc</button>
                                    </div>
                                </div>
                            </form>

                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Tên</th>
                                        <th>Mô tả</th>
                                        <th>Trạng thái</th>
                                        <th class="action-col">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${categories}" var="c">
                                        <tr>
                                            <td>${c.categoryId}</td>
                                            <td>${c.categoryName}</td>
                                            <td>${c.description}</td>
                                            <td>
                                                <span class="status-dot ${c.status == 'active' ? 'status-active' : 'status-inactive'}"></span>
                                                ${c.status == 'active' ? 'Hoạt động' : 'Ngừng hoạt động'}
                                            </td>
                                            <td class="action-col">
                                                <div class="action-btn-group">
                                                    <a href="${pageContext.request.contextPath}/category-edit?id=${c.categoryId}" class="action-btn action-edit" title="Sửa danh mục">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>

                                                    <c:choose>
                                                        <c:when test="${c.status == 'active'}">
                                                            <form method="post" action="${pageContext.request.contextPath}/category-toggle-status" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn chuyển danh mục này sang Inactive?')">
                                                                <input type="hidden" name="id" value="${c.categoryId}">
                                                                <input type="hidden" name="status" value="inactive">
                                                                <button type="submit" class="btn btn-sm btn-secondary" title="Delete (soft)">
                                                                    <i class="fa fa-trash"></i>
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="post" action="${pageContext.request.contextPath}/category-toggle-status" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn kích hoạt lại danh mục này?')">
                                                                <input type="hidden" name="id" value="${c.categoryId}">
                                                                <input type="hidden" name="status" value="active">
                                                                <button type="submit" class="btn btn-sm btn-success" title="Activate">
                                                                    <i class="fa fa-check"></i>
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <div class="pagination-wrapper">
                                <div class="pagination-controls">
                                    <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                       href="category-list?page=${currentPage - 1}&keyword=${param.keyword}&status=${param.status}">‹</a>

                                    <span class="page-number">
                                        Trang
                                        <form action="category-list" method="get" class="page-jump-form">
                                            <input type="hidden" name="keyword" value="${param.keyword}">
                                            <input type="hidden" name="status" value="${param.status}">
                                            <input type="number" name="page" min="1" max="${totalPages}" value="${currentPage}" onchange="this.form.submit()">
                                        </form>
                                        / ${totalPages}
                                    </span>

                                    <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                       href="category-list?page=${currentPage + 1}&keyword=${param.keyword}&status=${param.status}">›</a>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>

            </div>
        </div>
    </body>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</html>
