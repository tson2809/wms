<%-- 
    Document   : reset-password
    Created on : Jan 10, 2026, 3:28:44 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Reset Password</title>
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
            <div class="container-fluid">
                <div class="row h-100 align-items-center justify-content-center" style="min-height:100vh;">
                    <div class="col-12 col-sm-8 col-md-6 col-lg-5 col-xl-4">
                        <div class="bg-light rounded p-4 p-sm-5 my-4 mx-3">
                            <div class="d-flex align-items-center justify-content-between mb-3">
                                <a href="signin.jsp"><h3 class="text-primary"><i class="fa fa-hashtag me-2"></i>DASHMIN</h3></a>
                                <h3>Reset</h3>
                            </div>
                            <form action="reset-password" method="post">
                                <input type="hidden" name="token" value="${token}">
                                <div class="form-floating mb-3">
                                    <input type="password" class="form-control" name="password" placeholder="New Password" required>
                                    <label>New Password</label>
                                </div>
                                <div class="form-floating mb-4">
                                    <input type="password" class="form-control" name="confirmPassword" placeholder="Confirm Password" required>
                                    <label>Confirm Password</label>
                                </div>
                                <button type="submit" class="btn btn-primary py-3 w-100 mb-3">Confirm</button>
                            </form>
                            <p class="text-success text-center mb-0">${message}</p>
                            <p class="text-danger text-center mb-0">${error}</p>
                            <p class="text-center mb-0"><a href="signin.jsp">Back to Sign In</a></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="js/main.js"></script>
    </body>
</html>

