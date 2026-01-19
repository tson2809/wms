<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>User Profile | WMS_HA</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/ProfileSideBar.jsp" />
            <div class="content">
                <div class="container-fluid pt-4 px-4">
                    <div class="row">
                        <div class="col-lg-4">
                            <div class="bg-light rounded p-4 text-center">
                                <img id="avatarPreview"
                                     src="${user.avatar != null && !user.avatar.isEmpty() ? pageContext.request.contextPath.concat('/').concat(user.avatar) : ''}"
                                     class="rounded-circle"
                                     style="width:120px;height:120px;object-fit:cover;">
                                <br>
                                <form action="profile"
                                      method="post"
                                      enctype="multipart/form-data"
                                      id="avatarForm">
                                    <input type="file"
                                           id="avatarInput"
                                           name="avatar"
                                           class="d-none"
                                           accept="image/*">
                                    <button type="button"
                                            class="btn btn-outline-primary btn-sm"
                                            onclick="selectAvatar()">
                                        Change Avatar
                                    </button>
                                </form>
                                <h5 class="mt-3 mb-0">${user.username}</h5>
                            </div>
                        </div>
                        <div class="col-lg-8">
                            <div class="bg-light rounded p-4">
                                <form action="profile"
                                      method="post"
                                      enctype="multipart/form-data"
                                      id="profileForm">
                                    <div class="mb-3">
                                        <label class="form-label">Username</label>
                                        <input class="form-control"
                                               value="${user.username}"
                                               readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Full Name</label>
                                        <input class="form-control"
                                               value="${user.fullName}"
                                               readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Email</label>
                                        <input class="form-control"
                                               value="${user.email}"
                                               readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Phone</label>
                                        <input class="form-control editable"
                                               name="phone"
                                               value="${user.phone}"
                                               readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Address</label>
                                        <input class="form-control editable"
                                               name="address"
                                               value="${user.address}"
                                               readonly>
                                    </div>
                                    <button type="button"
                                            id="editBtn"
                                            class="btn btn-primary"
                                            onclick="enableEdit()">
                                        Edit
                                    </button>
                                    <button type="submit"
                                            id="saveBtn"
                                            class="btn btn-success d-none">
                                        Save Changes
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
                                                function enableEdit() {
                                                    document.querySelectorAll('.editable').forEach(i => {
                                                        i.removeAttribute('readonly');
                                                    });
                                                    document.getElementById('editBtn').classList.add('d-none');
                                                    document.getElementById('saveBtn').classList.remove('d-none');
                                                }

                                                function selectAvatar() {
                                                    document.getElementById('avatarInput').click();
                                                }

                                                document.getElementById('avatarInput').addEventListener('change', function () {
    if (this.files.length > 0) {
        document.getElementById('avatarPreview').src =
            URL.createObjectURL(this.files[0]);
        document.getElementById('avatarForm').submit();
    }
});
        </script>
    </body>
</html>