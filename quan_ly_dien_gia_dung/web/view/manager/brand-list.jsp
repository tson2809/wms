<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Danh sách Brand</title>
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
            String sidebarPage = "/view/admin/components/sidebarAdmin.jsp";
            if ("manager".equals(roleName)) {
                sidebarPage = "/view/manager/components/sidebarManager.jsp";
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
                                <h5 class="mb-0">Danh sách Brand</h5>
                                <button class="btn add-member-btn">
                                    <a href="${pageContext.request.contextPath}/brand-add">Add Brand</a>
                                </button>
                            </div>

                            <form action="brand-list" method="get" class="user-filter-form mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-6">
                                        <label class="form-label">Keyword</label>
                                        <input type="text" name="keyword" value="${param.keyword}" class="form-control" placeholder="Search name/description">
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Status</label>
                                        <select name="status" class="form-select">
                                            <option value="all" ${(empty param.status || param.status == 'all') ? 'selected' : ''}>All</option>
                                            <option value="active" ${param.status == 'active' ? 'selected' : ''}>Active</option>
                                            <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Sort</label>
                                        <select name="sortBy" class="form-select">
                                            <option value="brand_id" ${(empty param.sortBy || param.sortBy == 'brand_id') ? 'selected' : ''}>Id</option>
                                            <option value="brand_name" ${param.sortBy == 'brand_name' ? 'selected' : ''}>Name</option>
                                            <option value="status" ${param.sortBy == 'status' ? 'selected' : ''}>Status</option>
                                            <option value="created_at" ${param.sortBy == 'created_at' ? 'selected' : ''}>Created at</option>
                                        </select>
                                    </div>
                                    <div class="col-md-1">
                                        <label class="form-label">Dir</label>
                                        <select name="sortDir" class="form-select">
                                            <option value="asc" ${(empty param.sortDir || param.sortDir == 'asc') ? 'selected' : ''}>Asc</option>
                                            <option value="desc" ${param.sortDir == 'desc' ? 'selected' : ''}>Desc</option>
                                        </select>
                                    </div>
                                    <div class="col-md-1">
                                        <label class="form-label">Size</label>
                                        <select name="size" class="form-select">
                                            <option value="5" ${param.size == '5' ? 'selected' : ''}>5</option>
                                            <option value="10" ${(empty param.size || param.size == '10') ? 'selected' : ''}>10</option>
                                            <option value="20" ${param.size == '20' ? 'selected' : ''}>20</option>
                                        </select>
                                    </div>
                                    <div class="col-md-12 d-flex gap-2 mt-2">
                                        <button type="submit" class="btn btn-primary">Apply</button>
                                        <button type="reset" class="btn btn-secondary" onclick="window.location.href = 'brand-list'">Clear</button>
                                    </div>
                                </div>
                            </form>

                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Id</th>
                                        <th>Name</th>
                                        <th>Description</th>
                                        <th>Status</th>
                                        <th class="action-col">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${brands}" var="b">
                                        <tr>
                                            <td>${b.brandId}</td>
                                            <td>${b.brandName}</td>
                                            <td>${b.description}</td>
                                            <td>
                                                <span class="status-dot ${b.status == 'active' ? 'status-active' : 'status-inactive'}"></span>
                                                ${b.status == 'active' ? 'Active' : 'Inactive'}
                                            </td>
                                            <td class="action-col">
                                                <div class="action-btn-group">
                                                    <a href="${pageContext.request.contextPath}/brand-edit?id=${b.brandId}" class="action-btn action-edit" title="Edit brand">
                                                        <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                    </a>

                                                    <c:choose>
                                                        <c:when test="${b.status == 'active'}">
                                                            <form method="post" action="${pageContext.request.contextPath}/brand-toggle-status" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn chuyển thương hiệu này sang Inactive?')">
                                                                <input type="hidden" name="id" value="${b.brandId}">
                                                                <input type="hidden" name="status" value="inactive">
                                                                <button type="submit" class="btn btn-sm btn-secondary" title="Deactivate">
                                                                    <i class="fa fa-trash"></i>
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="post" action="${pageContext.request.contextPath}/brand-toggle-status" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn kích hoạt lại thương hiệu này?')">
                                                                <input type="hidden" name="id" value="${b.brandId}">
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
                                       href="brand-list?page=${currentPage - 1}&keyword=${param.keyword}&status=${param.status}&sortBy=${param.sortBy}&sortDir=${param.sortDir}&size=${param.size}">‹</a>

                                    <span class="page-number">
                                        Page
                                        <form action="brand-list" method="get" class="page-jump-form">
                                            <input type="hidden" name="keyword" value="${param.keyword}">
                                            <input type="hidden" name="status" value="${param.status}">
                                            <input type="hidden" name="sortBy" value="${param.sortBy}">
                                            <input type="hidden" name="sortDir" value="${param.sortDir}">
                                            <input type="hidden" name="size" value="${param.size}">
                                            <input type="number" name="page" min="1" max="${totalPages}" value="${currentPage}" onchange="this.form.submit()">
                                        </form>
                                        of ${totalPages}
                                    </span>

                                    <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                       href="brand-list?page=${currentPage + 1}&keyword=${param.keyword}&status=${param.status}&sortBy=${param.sortBy}&sortDir=${param.sortDir}&size=${param.size}">›</a>
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
