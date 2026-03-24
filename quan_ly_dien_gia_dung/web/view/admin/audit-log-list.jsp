<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Nhật ký hệ thống</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/user-list.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/sidebar.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="d-flex justify-content-between align-items-center mb-3 user-list-header">
                                <h5 class="mb-0">Nhật ký hệ thống</h5>
                            </div>

                            <form method="get" action="${pageContext.request.contextPath}/audit-log-list" class="user-filter-form mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-4">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Tìm theo hành động, bảng hoặc người dùng">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Hành động</label>
                                        <select class="form-select" name="actionType">
                                            <option value="">Tất cả</option>
                                            <c:forEach var="item" items="${actionTypes}">
                                                <option value="${item}" ${item == actionType ? 'selected' : ''}>${item}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Bảng</label>
                                        <select class="form-select" name="tableName">
                                            <option value="">Tất cả</option>
                                            <c:forEach var="item" items="${tableNames}">
                                                <option value="${item}" ${item == tableName ? 'selected' : ''}>${item}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-2 d-flex gap-2">
                                        <button type="submit" class="btn btn-primary w-100">Tìm</button>
                                        <a href="${pageContext.request.contextPath}/audit-log-list" class="btn btn-secondary w-100">Xóa lọc</a>
                                    </div>
                                </div>
                            </form>

                            <c:url var="prevPageUrl" value="/audit-log-list">
                                <c:param name="page" value="${currentPage - 1}"/>
                                <c:param name="keyword" value="${keyword}"/>
                                <c:param name="actionType" value="${actionType}"/>
                                <c:param name="tableName" value="${tableName}"/>
                            </c:url>

                            <c:url var="nextPageUrl" value="/audit-log-list">
                                <c:param name="page" value="${currentPage + 1}"/>
                                <c:param name="keyword" value="${keyword}"/>
                                <c:param name="actionType" value="${actionType}"/>
                                <c:param name="tableName" value="${tableName}"/>
                            </c:url>

                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Người dùng</th>
                                        <th>Hành động</th>
                                        <th>Bảng</th>
                                        <th>Record ID</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty logs}">
                                            <tr>
                                                <td colspan="5" class="text-center text-muted">Không có dữ liệu nhật ký.</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="log" items="${logs}">
                                                <tr>
                                                    <td>${log.logId}</td>
                                                    <td>${empty log.username ? '-' : log.username}</td>
                                                    <td>${log.actionType}</td>
                                                    <td>${empty log.tableName ? '-' : log.tableName}</td>
                                                    <td>${empty log.recordId ? '-' : log.recordId}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>

                            <div class="pagination-wrapper">
                                <div class="pagination-info">Tổng bản ghi: ${totalRecords}</div>
                                <div class="pagination-controls">
                                    <a class="page-btn ${currentPage <= 1 ? 'disabled' : ''}"
                                       href="${pageContext.request.contextPath}${prevPageUrl}">‹</a>
                                    <span class="page-number">Trang ${currentPage}/${totalPages}</span>
                                    <a class="page-btn ${currentPage >= totalPages ? 'disabled' : ''}"
                                       href="${pageContext.request.contextPath}${nextPageUrl}">›</a>
                                </div>
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
