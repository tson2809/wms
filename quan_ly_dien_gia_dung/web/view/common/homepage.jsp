<%-- 
    Document   : homepage
    Created on : 19 thg 1, 2026, 19:47:40
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>WHS_HA - Homepage</title>

        <!-- Icon Font Stylesheet -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        
        <!-- Customized Bootstrap Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        
        <!-- Template Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        
        <style>
            body {
                background-color: #009CFF;
            }
            .header-shadow {
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            .header-dark {
                background-color: #0088DD !important;
            }
        </style>
    </head>
    <body>          
        <!-- Header Start -->
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary px-4 py-3 header-shadow header-dark">
            <div class="container-fluid">
                <a class="navbar-brand fw-bold fs-4" href="#">WHS_HA</a>
                <div class="d-flex">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-light">Đăng nhập</a>
                </div>
            </div>
        </nav>
        <!-- Header End -->

        <!-- Homepage Start -->
        <div class="container-fluid p-0 position-relative">
            <div class="row g-0">
                <div class="col-12">
                    <img src="${pageContext.request.contextPath}/img/homepage.webp" class="img-fluid w-100" alt="Homepage" style="display: block;">
                </div>
            </div>
            <div class="position-absolute start-0 px-5" style="top: 35%; max-width: 800px;">
                <h1 class="display-3 fw-bold" style="color: white; text-shadow: 2px 2px 8px rgba(0,0,0,0.7);">Quản lý kho điện gia dụng</h1>
            </div>
        </div>
        <!-- Homepage End -->

        <!-- JavaScript Libraries -->
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
