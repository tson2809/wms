<%-- 
    Document : notification_edit 
    Created on : 4 thg 2, 2026, 00:32:00 
    Author : thais 
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="utf-8">
        <title>Chỉnh sửa thông báo</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
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
        <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css"
              rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/RoleSideBar.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="mb-3">
                                <h5 class="mb-0 fw-semibold">Chỉnh sửa thông báo</h5>
                            </div>
                            <div class="bg-light rounded p-4">
                                <c:if test="${not empty message}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        ${message}
                                        <button type="button" class="btn-close"
                                                data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>
                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        ${errorMessage}
                                        <button type="button" class="btn-close"
                                                data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>
                                <c:set var="canEdit"
                                       value="${sessionScope.user.role.roleName eq 'Admin'
                                                or (sessionScope.user.role.roleName eq 'Manager'
                                                and notification.notificationType ne 'System')}" />
                                <form action="${pageContext.request.contextPath}/notification-edit"
                                      method="post">
                                    <input type="hidden" name="id" value="${notification.notificationId}">
                                    <div class="row g-3">
                                        <div class="col-md-12">
                                            <label for="notificationType" class="form-label">Loại thông
                                                báo</label>
                                            <select class="form-select" id="notificationType"
                                                    name="notificationType"
                                                    ${!canEdit ? 'disabled' : ''}>
                                                <option value="">Chọn loại thông báo</option>
                                                <c:forEach items="${allTypes}" var="type">
                                                    <option value="${type}"
                                                            ${notification.notificationType==type ? 'selected' : ''}>${type}</option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty errorNotificationType}">
                                                <div class="text-danger small mt-1">${errorNotificationType}
                                                </div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-12">
                                            <label for="title" class="form-label">Tiêu đề</label>
                                            <input type="text" class="form-control" id="title" name="title"
                                                   value="${notification.title}"
                                                   placeholder="Nhập tiêu đề thông báo"
                                                   maxlength="255"
                                                   ${!canEdit ? 'disabled' : ''}>
                                            <c:if test="${not empty errorTitle}">
                                                <div class="text-danger small mt-1">${errorTitle}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12">
                                            <label for="content" class="form-label">Nội dung</label>
                                            <textarea class="form-control" id="content" name="content" rows="6"
                                                      placeholder="Nhập nội dung thông báo"
                                                      ${!canEdit ? 'disabled' : ''}>${notification.content}</textarea>
                                            <c:if test="${not empty errorContent}">
                                                <div class="text-danger small mt-1">${errorContent}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 d-flex justify-content-end gap-2">
                                            <a href="${pageContext.request.contextPath}/notification-list"
                                               class="btn btn-secondary">Quay lại</a>
                                            <c:choose>
                                                <c:when test="${sessionScope.user.role.roleName eq 'Admin'}">
                                                    <button type="submit" class="btn btn-primary">Cập nhật</button>
                                                </c:when>
                                                <c:when test="${sessionScope.user.role.roleName eq 'Manager'
                                                                and notification.notificationType ne 'System'}">
                                                        <button type="submit" class="btn btn-primary">Cập nhật</button>
                                                </c:when>
                                                <c:otherwise>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
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
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>

</html>