<%-- 
    Document   : sheet-edit
    Created on : Feb 4, 2026, 1:43:59 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Edit Inventory Sheet</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/admin/components/sidebarAdmin.jsp"/>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp"/>

                <div class="container-fluid pt-4 px-4">
                    <div class="inventory-card p-4">

                        <h5>Edit Inventory Sheet</h5>

                        <form method="post"
                              action="${pageContext.request.contextPath}/inventory-sheet-edit">

                            <input type="hidden" name="sheetId" value="${sheet.sheetId}">

                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>SKU</th>
                                        <th>System Qty</th>
                                        <th>Counted Qty</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${details}" var="d">
                                        <tr>
                                            <td>${d.productName}</td>
                                            <td>${d.sku}</td>
                                            <td>${d.systemQuantity}</td>
                                            <td>
                                                <input type="hidden"
                                                       name="detailId"
                                                       value="${d.productId}">
                                                <input type="number"
                                                       name="countedQty"
                                                       value="${d.countedQuantity}"
                                                       class="form-control"
                                                       required>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <button class="btn btn-primary">
                                Save Changes
                            </button>

                            <a href="${pageContext.request.contextPath}/inventory-sheet-list"
                               class="btn btn-secondary">
                                Cancel
                            </a>

                        </form>

                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
