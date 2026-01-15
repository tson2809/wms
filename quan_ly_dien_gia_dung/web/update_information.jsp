<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit User</title>

        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap & Icons -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid pt-4 px-4">
            <div class="row justify-content-center">
                <div class="col-lg-10 col-xl-9">

                    <div class="bg-light rounded p-4">
                        <h5 class="mb-4">
                            <i class="fa fa-user-edit me-2"></i>
                            Edit User Information
                        </h5>

                        <form method="post" enctype="multipart/form-data">

                            <!-- hidden id -->
                            <input type="hidden" name="userId" value="${user.userId}">

                            <div class="row">
                                <!-- Avatar -->
                                <div class="col-md-4 text-center">
                                    <img id="avatarPreview"
                                         src="img/${user.avatar}"
                                         class="rounded-circle mb-3"
                                         style="width:150px;height:150px;object-fit:cover">

                                    <div class="mb-3">
                                        <input class="form-control"
                                               type="file"
                                               name="avatar"
                                               accept="image/*"
                                               onchange="previewAvatar(this)">
                                    </div>
                                </div>

                                <!-- User Info -->
                                <div class="col-md-8">
                                    <div class="row g-3">

                                        <div class="col-md-6">
                                            <label class="form-label">Username</label>
                                            <input type="text" class="form-control"
                                                   name="username" value="${user.userName}" >
                                            <c:if test="${not empty usernameError}">
                                                <small class="text-danger">${usernameError}</small>
                                            </c:if>

                                        </div>

                                        <div class="col-md-6">
                                            <label class="form-label">Email</label>
                                            <input type="email" class="form-control"
                                                   name="email"
                                                   value="${user.email}">
                                            <c:if test="${not empty emailError}">
                                                <small class="text-danger">${emailError}</small>
                                            </c:if>
                                        </div>

                                        <div class="col-md-6">
                                            <label class="form-label">Full Name</label>
                                            <input type="text" class="form-control"
                                                   name="fullName"
                                                   value="${user.fullName}">
                                            <c:if test="${not empty fullNameError}">
                                                <small class="text-danger" >${fullNameError}</small>
                                            </c:if>
                                        </div>

                                        <div class="col-12">
                                            <label class="form-label">Address</label>
                                            <input type="text" class="form-control"
                                                   name="address"
                                                   value="${user.address}">
                                            <c:if test="${not empty addressError}">
                                                <small class="text-danger" >${addressError}</small>
                                            </c:if>
                                        </div>

                                        <div class="col-md-6">
                                            <label class="form-label">Role</label>
                                            <select class="form-select" name="roleId">
                                                <c:forEach items="${roles}" var="r">
                                                    <option value="${r.roleId}"
                                                            ${r.roleId == user.role.roleId ? "selected" : ""}>
                                                        ${r.roleName}
                                                    </option>
                                                </c:forEach>
                                            </select>

                                        </div>

                                        <div class="col-md-6">
                                            <label class="form-label">Status</label>
                                            <select class="form-select" name="isActive">
                                                <option value="true"
                                                        ${user.isActive ? "selected" : ""}>Active</option>
                                                <option value="false"
                                                        ${!user.isActive ? "selected" : ""}>Inactive</option>
                                            </select>
                                        </div>

                                        <div class="col-12">
                                            <label class="form-label">New Password</label>
                                            <input type="password" class="form-control"
                                                   name="password"
                                                   placeholder="Leave blank to keep old password">
                                        </div>

                                    </div>
                                </div>
                            </div>

                            <div class="text-end mt-4">
                                <a href="user-list" class="btn btn-secondary me-2">Cancel</a>
                                <button type="submit" class="btn btn-primary">
                                    Save Changes
                                </button>
                            </div>

                        </form>
                    </div>

                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
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
