<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Change Password | HA_WMS</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link href="img/favicon.ico" rel="icon">
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <c:choose>
                <c:when test="${sessionScope.user.role.roleId == 2}">
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 3}">
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:otherwise>
            </c:choose>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
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
                                    <c:if test="${param.error == 'weak'}">
                                    <div class="alert alert-danger">
                                        Password must contain uppercase, lowercase, number, special character and be at least 8 characters
                                    </div>
                                </c:if>
                                <form action="${pageContext.request.contextPath}/change-password" method="post">
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
        </div>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
</html>
