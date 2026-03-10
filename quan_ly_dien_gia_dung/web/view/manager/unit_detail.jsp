<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>${mode == 'edit' ? 'Sửa' : 'Thêm'} đơn vị tính</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/manager/components/sidebarManager.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12 col-md-6">
                            <div class="d-flex align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">
                                    ${mode == 'edit' ? 'Sửa' : 'Thêm'} đơn vị tính
                                </h5>
                            </div>

                            <div class="bg-light rounded p-4">
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        ${error}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>

                                <c:choose>
                                    <c:when test="${mode == 'edit'}">
                                        <form action="${pageContext.request.contextPath}/unit-edit" method="post">
                                            <input type="hidden" name="unitId" value="${unit.unitId}">
                                            <div class="mb-3">
                                                <label for="unitName" class="form-label fw-semibold">Tên đơn vị</label>
                                                <input type="text" class="form-control" id="unitName" name="unitName"
                                                       value="${unit.unitName}" placeholder="VD: Chiếc, Bộ, Cái..." maxlength="50" autofocus>
                                            </div>
                                            <div class="d-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/unit-list" class="btn btn-secondary flex-fill">Hủy</a>
                                                <button type="submit" class="btn btn-primary flex-fill">Cập nhật</button>
                                            </div>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/unit-add" method="post">
                                            <div class="mb-3">
                                                <label for="unitName" class="form-label fw-semibold">Tên đơn vị</label>
                                                <input type="text" class="form-control" id="unitName" name="unitName"
                                                       value="${unitName}" placeholder="VD: Chiếc, Bộ, Cái..." maxlength="50" autofocus>
                                            </div>
                                            <div class="d-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/unit-list" class="btn btn-secondary flex-fill">Hủy</a>
                                                <button type="submit" class="btn btn-primary flex-fill">Thêm mới</button>
                                            </div>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
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
