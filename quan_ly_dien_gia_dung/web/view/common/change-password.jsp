<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Change Password | WMS_HA</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link href="img/favicon.ico" rel="icon">
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <%@ include file="components/ProfileSideBar.jsp" %>
            <div class="content">
                <div id="navbar-container"></div>
                <div class="container-fluid pt-4 px-4">
                    <div class="row justify-content-center">
                        <div class="col-lg-5">
                            <div class="bg-light rounded p-4">
                                <h5 class="mb-4">Change Password</h5>
                                <c:if test="${param.error == 'wrong'}">
                                    <div class="alert alert-danger">Current password is incorrect</div>
                                </c:if>
                                <c:if test="${param.error == 'confirm'}">
                                    <div class="alert alert-danger">Password confirmation does not match</div>
                                </c:if>
                                <form action="change-password" method="post" onsubmit="return validatePassword()">
                                    <div class="mb-3">
                                        <label class="form-label">Current Password</label>
                                        <input type="password" name="currentPassword" class="form-control" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">New Password</label>
                                        <input type="password" id="newPassword" name="newPassword"
                                               class="form-control" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Confirm New Password</label>
                                        <input type="password" id="confirmPassword" name="confirmPassword"
                                               class="form-control" required>
                                    </div>
                                    <div id="passwordError" class="text-danger mb-3"></div>
                                    <button type="submit" class="btn btn-primary w-100">
                                        Update Password
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <a href="#" class="btn btn-lg btn-primary btn-lg-square back-to-top">
                <i class="bi bi-arrow-up"></i>
            </a>
        </div>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="js/loadComponents.js"></script>
        <script src="js/main.js"></script>

        <script>
                                    function validatePassword() {
                                        const pwd = document.getElementById("newPassword").value;
                                        const confirm = document.getElementById("confirmPassword").value;
                                        const error = document.getElementById("passwordError");
                                        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
                                        if (!regex.test(pwd)) {
                                            error.innerText = "Password must be at least 8 characters and include uppercase, lowercase, number and special character";
                                            return false;
                                        }
                                        if (pwd !== confirm) {
                                            error.innerText = "Password confirmation does not match";
                                            return false;
                                        }
                                        error.innerText = "";
                                        return true;
                                    }
        </script>
    </body>
</html>
