<%-- 
    Document   : transaction-list
    Created on : Feb 17, 2026, 9:52:47 PM
    Author     : GIAKHANHPC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Transaction List</title>
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

            <jsp:include page="/view/common/components/sidebar.jsp"/>

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp"/>

                <div class="container-fluid pt-4 px-4">
                    <div class="inventory-card">

                        <div class="inventory-header">
                            <h5>Inventory Transactions</h5>
                        </div>

                        <form action="inventory-transactions"
                              method="get"
                              class="inventory-filter-form mb-3"
                              onsubmit="return validateDateFilter()">
                            <div class="row g-2">

                                <div class="col-md-3">
                                    <label>Transaction ID</label>
                                    <input type="text" name="trxId" value="${param.trxId}" class="form-control">
                                </div>

                                <div class="col-md-3">
                                    <label>Reference ID</label>
                                    <input type="text" name="refId" value="${param.refId}" class="form-control">
                                </div>

                                <div class="col-md-3">
                                    <label>Tìm SKU</label>
                                    <input type="text" name="keyword" value="${param.keyword}" class="form-control">
                                </div>

                                <div class="col-md-3">
                                    <label>Loại biến động</label>
                                    <select name="type" class="form-select">
                                        <option value="">All</option>
                                        <option value="import" ${param.type=='import'?'selected':''}>Import</option>
                                        <option value="export" ${param.type=='export'?'selected':''}>Export</option>
                                        <option value="adjustment" ${param.type=='adjustment'?'selected':''}>Adjustment</option>
                                        <option value="inventory_check" ${param.type=='inventory_check'?'selected':''}>Inventory Check</option>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <label>Loại số lượng</label>
                                    <select name="qtyType" class="form-select">
                                        <option value="">All</option>
                                        <option value="increase" ${param.qtyType=='increase'?'selected':''}>Increase</option>
                                        <option value="decrease" ${param.qtyType=='decrease'?'selected':''}>Decrease</option>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <label>Reference</label>
                                    <select name="refType" class="form-select">
                                        <option value="">All</option>
                                        <option value="goods_receipt" ${param.refType=='goods_receipt'?'selected':''}>Receipt</option>
                                        <option value="goods_issue" ${param.refType=='goods_issue'?'selected':''}>Issue</option>
                                        <option value="inventory_sheet" ${param.refType=='inventory_sheet'?'selected':''}>Sheet</option>
                                    </select>
                                </div>

                                <div class="col-md-3 position-relative">
                                    <label>Tạo ra bởi</label>

                                    <input type="text"
                                           name="createdBy"
                                           id="createdByInput"
                                           value="${param.createdBy}"
                                           class="form-control"
                                           placeholder="Created by...">

                                    <div id="suggestBox"
                                         class="list-group position-absolute w-100"
                                         style="z-index:1000;"></div>
                                </div>

                                <div class="col-md-3">
                                    <label>Từ ngày</label>
                                    <input type="date" id="dateFrom" name="dateFrom" value="${param.dateFrom}" class="form-control">
                                </div>

                                <div class="col-md-3">
                                    <label>Đến ngày</label>
                                    <input type="date" id="dateTo" name="dateTo" value="${param.dateTo}" class="form-control">
                                </div>

                                <div class="col-md-2 d-flex align-items-end">
                                    <button class="btn btn-primary w-100">Tìm</button>
                                </div>

                                <div class="col-md-2 d-flex align-items-end">
                                    <a href="inventory-transactions" class="btn btn-secondary w-100">Clear</a>
                                </div>

                            </div>
                        </form>

                        <div class="inventory-table">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>No</th>
                                        <th>TRX ID</th>
                                        <th>SKU</th>
                                        <th>Loại</th>
                                        <th>Số lượng</th>
                                        <th>Reference</th>
                                        <th>Tạo ra bởi</th>     
                                        <th>Ngày tạo</th> 
                                        <th>Hành động</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach items="${transactions}" var="t" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td>TRX${t.transactionId}</td>
                                            <td>${t.sku}</td>
                                            <td>
                                                <span class="badge-type type-${t.transactionType}">
                                                    ${t.transactionType}
                                                </span>
                                            </td>
                                            <td class="${t.quantityChange > 0 ? 'qty-positive' : 'qty-negative'}">
                                                ${t.quantityChange}
                                            </td>
                                            <td>${t.referenceType}-${t.referenceId}</td>
                                            <td>${t.createdBy}</td>  
                                            <td>
                                                <fmt:formatDate value="${t.transactionDate}" pattern="dd/MM/yyyy"/>
                                            </td>                                        
                                            <td class="text-center">
                                                <a href="transaction-detail?id=${t.transactionId}"
                                                   class="action-btn action-view"
                                                   title="View">
                                                    <iconify-icon icon="majesticons:eye-line"></iconify-icon>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination-wrapper">
                                <div class="pagination-controls">
                                    <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                       href="inventory-transactions?page=${currentPage - 1}
                                       &trxId=${param.trxId}
                                       &refId=${param.refId}
                                       &keyword=${param.keyword}
                                       &type=${param.type}
                                       &qtyType=${param.qtyType}
                                       &refType=${param.refType}
                                       &createdBy=${param.createdBy}
                                       &variantId=${param.variantId}
                                       &dateFrom=${param.dateFrom}
                                       &dateTo=${param.dateTo}
                                       &sort=${param.sort}
                                       &dir=${param.dir}">
                                        ‹
                                    </a>
                                    <span class="page-number">
                                        Page
                                        <form action="inventory-transactions" method="get" class="page-jump-form">
                                            <input type="hidden" name="trxId" value="${param.trxId}">
                                            <input type="hidden" name="refId" value="${param.refId}">
                                            <input type="hidden" name="keyword" value="${param.keyword}">
                                            <input type="hidden" name="type" value="${param.type}">
                                            <input type="hidden" name="qtyType" value="${param.qtyType}">
                                            <input type="hidden" name="refType" value="${param.refType}">
                                            <input type="hidden" name="createdBy" value="${param.createdBy}">
                                            <input type="hidden" name="variantId" value="${param.variantId}">
                                            <input type="hidden" name="dateFrom" value="${param.dateFrom}">
                                            <input type="hidden" name="dateTo" value="${param.dateTo}">
                                            <input type="hidden" name="sort" value="${param.sort}">
                                            <input type="hidden" name="dir" value="${param.dir}">

                                            <input type="number"
                                                   name="page"
                                                   min="1"
                                                   max="${totalPages}"
                                                   value="${currentPage}"
                                                   onchange="this.form.submit()">
                                        </form>
                                        of ${totalPages}
                                    </span>
                                    <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                       href="inventory-transactions?page=${currentPage + 1}
                                       &trxId=${param.trxId}
                                       &refId=${param.refId}
                                       &keyword=${param.keyword}
                                       &type=${param.type}
                                       &qtyType=${param.qtyType}
                                       &refType=${param.refType}
                                       &createdBy=${param.createdBy}
                                       &variantId=${param.variantId}
                                       &dateFrom=${param.dateFrom}
                                       &dateTo=${param.dateTo}
                                       &sort=${param.sort}
                                       &dir=${param.dir}">
                                        ›
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <script>
        const input = document.getElementById("createdByInput");
        const box = document.getElementById("suggestBox");

        if (input) {
            input.addEventListener("keyup", function () {
                let val = this.value;
                if (val.length === 0) {
                    box.innerHTML = "";
                    return;
                }

                fetch("${pageContext.request.contextPath}/search-user?q=" + val)
                        .then(res => res.json())
                        .then(data => {
                            box.innerHTML = "";
                            data.forEach(name => {
                                let item = document.createElement("a");
                                item.className = "list-group-item list-group-item-action";
                                item.innerText = name;
                                item.onclick = () => {
                                    input.value = name;
                                    box.innerHTML = "";
                                };
                                box.appendChild(item);
                            });
                        });
            });
        }
    </script>
    <script>
        const dateFromInput = document.getElementById("dateFrom");
        const dateToInput = document.getElementById("dateTo");
        const today = new Date().toISOString().split("T")[0];
        dateFromInput.max = today;
        dateToInput.max = today;
        dateFromInput.addEventListener("change", function () {
            dateToInput.min = this.value;
        });
        dateToInput.addEventListener("change", function () {
            dateFromInput.max = this.value;
        });
        function validateDateFilter() {
            const from = dateFromInput.value;
            const to = dateToInput.value;
            if (from && to && from > to) {
                alert("Từ ngày phải nhỏ hơn hoặc bằng đến ngày");
                return false;
            }
            if (to && to > today) {
                alert("Đến ngày không được ở tương lai");
                return false;
            }
            return true;
        }
    </script>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</html>
