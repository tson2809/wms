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
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <%@ include file="components/profileSidebar.jsp" %>
            <div class="content">
                <div id="navbar-container"></div>
                <div class="container-fluid pt-4 px-4">
                    <div class="row justify-content-center">
                        <div class="col-lg-6">
                            <div class="bg-light rounded p-4">
                                <h5 class="mb-4">My Profile</h5>
                                <c:if test="${param.success != null}">
                                    <div class="alert alert-success">
                                        Profile updated successfully
                                    </div>
                                </c:if>
                                <c:if test="${param.error != null}">
                                    <div class="alert alert-danger">
                                        Update failed
                                    </div>
                                </c:if>
                                <form action="profile" method="post">
                                    <div class="mb-3">
                                        <label class="form-label">Username</label>
                                        <input class="form-control" value="${user.username}" readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Email</label>
                                        <input class="form-control" value="${user.email}" readonly>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Full Name</label>
                                        <input class="form-control"
                                               name="fullName"
                                               value="${user.fullName}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Phone</label>
                                        <input class="form-control"
                                               name="phone"
                                               value="${user.phone}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Address</label>
                                        <input class="form-control"
                                               name="address"
                                               value="${user.address}">
                                    </div>
                                    <div class="mb-4">
                                        <label class="form-label">Avatar URL</label>
                                        <input class="form-control"
                                               name="avatar"
                                               value="${user.avatar}">
                                    </div>
                                    <button class="btn btn-primary w-100">
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
        <script src="js/loadComponents.js"></script>
        <script src="js/main.js"></script>
    </body>
</html>