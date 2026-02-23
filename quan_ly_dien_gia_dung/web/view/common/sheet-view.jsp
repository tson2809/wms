<%-- 
    Document   : sheet-view
    Created on : Feb 4, 2026, 1:42:16 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Details</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/CommonSideBar.jsp"/>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp"/>

                <div class="container-fluid pt-4 px-4">
                    <div class="inventory-card p-4">

                        <h5>Inventory Sheet Details</h5>

                        <div class="mb-3">
                            <b>Sheet Code:</b> ${sheet.sheetCode}<br>
                            <b>Date:</b> ${sheet.inventoryDate}<br>
                            <b>Category:</b> ${sheet.categoryName}<br>
                            <b>Status:</b> ${sheet.status}
                        </div>

                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>SKU</th>
                                    <th>System Qty</th>
                                    <th>Counted Qty</th>
                                    <th>Difference</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${details}" var="d">
                                <tr>
                                    <td>${d.productName}</td>
                                    <td>${d.sku}</td>
                                    <td>${d.systemQuantity}</td>
                                    <td>${d.countedQuantity}</td>
                                    <td>${d.countedQuantity - d.systemQuantity}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>

                        <a href="${pageContext.request.contextPath}/inventory-sheet-list"
                           class="btn btn-secondary">
                            Back
                        </a>

                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
