<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="utf-8">
        <title>Thêm Người Dùng</title>
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
                            <h2 class="mb-4">Thêm Người Dùng</h2>

                            <c:if test="${not empty generalError}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <strong>Lỗi!</strong> ${generalError}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                        </div>

                        <div class="col-12">
                            <div class="bg-light rounded p-4">
                                <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/user-add">
                                    <div class="row">
                                        <div class="col-md-4 text-center">
                                            <img id="avatarPreview" src="${pageContext.request.contextPath}/img/avatar/avt_1.jpg" class="rounded-circle mb-3" style="width:150px;height:150px;object-fit:cover" alt="avatar">
                                            <div class="mb-3">
                                                <input class="form-control" type="file" name="avatar" accept="image/*" onchange="previewAvatar(this)">
                                            </div>
                                        </div>

                                        <div class="col-md-8">
                                            <div class="row g-3">

                                                <div class="col-md-6">
                                                    <label class="form-label">Username</label>
                                                    <input type="text" class="form-control" name="username" value="${username}">
                                                    <c:if test="${not empty usernameError}">
                                                        <small class="text-danger">${usernameError}</small>
                                                    </c:if>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Email</label>
                                                    <input type="email" class="form-control" name="email" value="${email}">
                                                    <c:if test="${not empty emailError}">
                                                        <small class="text-danger">${emailError}</small>
                                                    </c:if>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Full Name</label>
                                                    <input type="text" class="form-control" name="fullName" value="${fullName}">
                                                    <c:if test="${not empty fullNameError}">
                                                        <small class="text-danger">${fullNameError}</small>
                                                    </c:if>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Phone</label>
                                                    <input type="text" class="form-control" name="phone" value="${phone}">
                                                    <c:if test="${not empty phoneError}">
                                                        <small class="text-danger">${phoneError}</small>
                                                    </c:if>
                                                </div>

                                                <div class="col-12">
                                                    <label class="form-label">Address</label>
                                                    <input type="text" class="form-control" name="address" value="${address}">
                                                    <c:if test="${not empty addressError}">
                                                        <small class="text-danger">${addressError}</small>
                                                    </c:if>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Role</label>
                                                    <select class="form-select" name="roleId">
                                                        <c:forEach items="${roles}" var="r">
                                                            <option value="${r.roleId}" ${
                                                                      (roleId != null && r.roleId == roleId)
                                                                      || (roleId == null && r.roleName != null && r.roleName.equalsIgnoreCase('Admin'))
                                                                      ? "selected" : ""
                                                                      }>
                                                                ${r.roleName}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Status</label>
                                                    <select class="form-select" name="isActive">
                                                        <option value="true" ${(empty isActive || isActive) ? "selected" : ""}>Active</option>
                                                        <option value="false" ${(!empty isActive && !isActive) ? "selected" : ""}>Inactive</option>
                                                    </select>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Password</label>
                                                    <input type="password" class="form-control" name="password">
                                                    <c:if test="${not empty passwordError}">
                                                        <small class="text-danger">${passwordError}</small>
                                                    </c:if>
                                                </div>

                                                <div class="col-md-6">
                                                    <label class="form-label">Confirm Password</label>
                                                    <input type="password" class="form-control" name="confirmPassword">
                                                    <c:if test="${not empty confirmPasswordError}">
                                                        <small class="text-danger">${confirmPasswordError}</small>
                                                    </c:if>
                                                </div>

                                            </div>

                                            <div class="text-end mt-4">
                                                <a href="${pageContext.request.contextPath}/user-list" class="btn btn-secondary me-2">Cancel</a>
                                                <button type="submit" class="btn btn-primary">Create User</button>
                                            </div>
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
        <script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
            function previewAvatar(input) {
                if (input.files && input.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        document.getElementById("avatarPreview").src = e.target.result;
                    };
                    reader.readAsDataURL(input.files[0]);
                }
            }
        </script>
    </body>
</html>
