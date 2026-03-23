<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Thêm danh mục</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">

        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
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
                            <h2 class="mb-4">Thêm danh mục</h2>

                            <c:if test="${not empty generalError}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <strong>Lỗi!</strong> ${generalError}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                        </div>

                        <div class="col-12">
                            <div class="bg-light rounded p-4">
                                <form method="post" action="${pageContext.request.contextPath}/category-add">
                                    <div class="row g-3">

                                        <div class="col-md-6">
                                            <label class="form-label">Tên danh mục</label>
                                            <input type="text" class="form-control" name="categoryName" value="${categoryName}">
                                            <c:if test="${not empty categoryNameError}">
                                                <small class="text-danger">${categoryNameError}</small>
                                            </c:if>
                                        </div>

                                        <div class="col-12">
                                            <label class="form-label">Mô tả</label>
                                            <input type="text" class="form-control" name="description" value="${description}">
                                        </div>

                                    </div>

                                    <div class="text-end mt-4">
                                        <a href="${pageContext.request.contextPath}/category-list" class="btn btn-secondary me-2">Hủy</a>
                                        <button type="submit" class="btn btn-primary">Tạo danh mục</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
</html>
