<%-- 
    Document   : forgot-password
    Created on : Jan 10, 2026, 3:27:20 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Forgot Password</title>
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
            <div class="container-fluid">
                <div class="row h-100 align-items-center justify-content-center" style="min-height:100vh;">
                    <div class="col-12 col-sm-8 col-md-6 col-lg-5 col-xl-4">
                        <div class="bg-light rounded p-4 p-sm-5 my-4 mx-3">
                            <div class="d-flex align-items-center justify-content-between mb-3">
                                <h3>Forgot Password</h3>
                            </div>
                            <form action="forgot-password" method="post" onsubmit="this.querySelector('button').disabled=true;">
                                <div class="form-floating mb-3">
                                    <input type="email" class="form-control" name="email" placeholder="name@example.com" required>
                                    <label>Email address</label>
                                </div>
                                <button type="submit" class="btn btn-primary py-3 w-100 mb-3">OK</button>
                            </form>
                            <p class="text-success text-center mb-0">${message}</p>
                            <p class="text-danger text-center mb-0">${error}</p>
                            <p class="text-center mb-0"><a href="${pageContext.request.contextPath}/login">Back to Log In</a></p>
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
