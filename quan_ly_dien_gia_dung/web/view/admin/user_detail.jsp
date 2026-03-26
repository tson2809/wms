<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="utf-8">
        <title>Chi Tiết Người Dùng</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">

        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/sidebar.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
             
                    <div class="container-fluid pt-4 px-4">
                        <div class="row g-4">
                            <div class="col-12">
                                <h2 class="mb-4">Chi Tiết Người Dùng</h2>
                            </div>

                            <div class="col-12">
                                <div class="bg-light rounded p-4">
                                    <div class="row">
                                        <div class="col-md-4 text-center">
                                            <img src="${pageContext.request.contextPath}/${user.avatar}" class="rounded-circle mb-3" style="width:150px;height:150px;object-fit:cover" alt="avatar">
                                        </div>

                                        <div class="col-md-8">
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label class="form-label">Tên đăng nhập</label>
                                                    <input type="text" class="form-control" value="${user.userName}" disabled>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Email</label>
                                                    <input type="text" class="form-control" value="${user.email}" disabled>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Họ và tên</label>
                                                    <input type="text" class="form-control" value="${user.fullName}" disabled>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Số điện thoại</label>
                                                    <input type="text" class="form-control" value="${user.phone}" disabled>
                                                </div>

                                                <div class="col-12">
                                                    <label class="form-label">Địa chỉ</label>
                                                    <input type="text" class="form-control" value="${user.address}" disabled>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Vai trò</label>
                                                    <input type="text" class="form-control" value="${user.role != null ? user.role.roleName : 'Không có'}" disabled>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Trạng thái</label>
                                                    <input type="text" class="form-control" value="${user.isActive ? 'Đang hoạt động' : 'Ngưng hoạt động'}" disabled>
                                                </div>

                                                <div class="col-12">
                                                    <label class="form-label">Ngày tạo</label>
                                                    <input type="text" class="form-control" value="${user.createAt}" disabled>
                                                </div>
                                            </div>

                                            <div class="text-end mt-4">
                                                <a href="${pageContext.request.contextPath}/user-list" class="btn btn-secondary me-2">
                                                    Quay lại
                                                </a>
                                                <a href="${pageContext.request.contextPath}/UpdateUser?id=${user.userId}" class="btn btn-warning">
                                                    Chỉnh sửa
                                                </a>
                                            </div>
                                        </div>
                                    </div>

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
