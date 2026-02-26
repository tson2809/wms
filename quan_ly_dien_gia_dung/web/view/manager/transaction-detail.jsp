<%-- 
    Document   : transaction-detail
    Created on : Feb 24, 2026, 4:02:46 PM
    Author     : GIAKHANHPC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Transaction Detail</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <meta content="" name="keywords">
        <meta content="" name="description">

        <!-- Favicon -->
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">

        <!-- Google Web Fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">

        <!-- Libraries Stylesheet -->
        <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet" />

        <!-- Customized Bootstrap Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">

        <!-- Template Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/inventory-transaction.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/inventory.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/manager/components/sidebarManager.jsp"/>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp"/>

                <div class="container-fluid pt-4 px-4">
                    <div class="row">
                        <div class="col-12">
                            <div class="bg-white rounded shadow-sm p-4">
                                <div class="d-flex justify-content-between mb-3">
                                    <h5 class="mb-0">Transaction Detail</h5>
                                    <a href="inventory-transactions" class="btn btn-secondary btn-sm">
                                        ← Quay lại
                                    </a>
                                </div>
                                <table class="table table-bordered align-middle">
                                    <tr>
                                        <th width="220">Transaction ID</th>
                                        <td>TRX${t.transactionId}</td>
                                    </tr>
                                    <tr>
                                        <th>Sản phẩm (SKU)</th>
                                        <td>${t.sku}</td>
                                    </tr>
                                    <tr>
                                        <th>Loại</th>
                                        <td>
                                            <span class="badge
                                                  ${t.transactionType == 'import' ? 'bg-success' :
                                                    t.transactionType == 'export' ? 'bg-danger' :
                                                    t.transactionType == 'adjustment' ? 'bg-primary' : 'bg-warning'}">
                                                      ${t.transactionType}
                                                  </span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>SL thay đổi</th>
                                            <td class="${t.quantityChange > 0 ? 'text-success fw-bold' : 'text-danger fw-bold'}">
                                                ${t.quantityChange}
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Số lượng trước</th>
                                            <td>${t.quantityBefore}</td>
                                        </tr>
                                        <tr>
                                            <th>Số lượng sau</th>
                                            <td>${t.quantityAfter}</td>
                                        </tr>
                                        <tr>
                                            <th>Reference</th>
                                            <td>${t.referenceType} - ${t.referenceId}</td>
                                        </tr>
                                        <tr>
                                            <th>Tạo ra bởi</th>
                                            <td>${t.createdBy}</td>
                                        </tr>
                                        <tr>
                                            <th>Ngày tạo</th>
                                            <td>
                                                <fmt:formatDate value="${t.transactionDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Ghi chú</th>
                                            <td>${t.notes}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </body>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </html>
