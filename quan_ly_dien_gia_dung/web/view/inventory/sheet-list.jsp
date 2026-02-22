<%-- 
    Document   : sheet-list
    Created on : Feb 1, 2026, 11:55:05 PM
    Author     : hung
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Sheet List</title>
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
        <link href="${pageContext.request.contextPath}/css/user-list.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/inventory.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/inventory/components/sidebarInventory.jsp"/>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="inventory-card p-4">
                        <div class="d-flex justify-content-between mb-3">
                            <h5>Inventory Sheets </h5>
                            <a href="${pageContext.request.contextPath}/inventory-sheet-create"
                               class="btn btn-success">
                                + Create New Sheet
                            </a>
                        </div>
                        <form method="get" action="${pageContext.request.contextPath}/inventory-sheet-list" 
                              class="row g-2 mb-3">

                            <div class="col-auto">
                                <select name="year" class="form-select" onchange="this.form.submit()">
                                    <option value="">All year</option>
                                    <c:forEach items="${years}" var="y">
                                        <option value="${y}" ${param.year==y?'selected':''}>${y}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-auto">
                                <select name="month" class="form-select">
                                    <option value="">All month</option>
                                    <c:forEach items="${months}" var="m">
                                        <option value="${m}" ${param.month==m?'selected':''}>
                                            <c:choose>
                                                <c:when test="${m==1}">January</c:when>
                                                <c:when test="${m==2}">February</c:when>
                                                <c:when test="${m==3}">March</c:when>
                                                <c:when test="${m==4}">April</c:when>
                                                <c:when test="${m==5}">May</c:when>
                                                <c:when test="${m==6}">June</c:when>
                                                <c:when test="${m==7}">July</c:when>
                                                <c:when test="${m==8}">August</c:when>
                                                <c:when test="${m==9}">September</c:when>
                                                <c:when test="${m==10}">October</c:when>
                                                <c:when test="${m==11}">November</c:when>
                                                <c:when test="${m==12}">December</c:when>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-auto position-relative">
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

                            <div class="col-auto">
                                <button class="btn btn-primary">Search</button>
                                <a href="${pageContext.request.contextPath}/inventory-sheet-list"
                                   class="btn btn-success">
                                    Clear
                                </a>
                            </div>

                        </form>

                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Sheet Code</th>
                                    <th>
                                        <a href="?sort=date&dir=${param.dir=='asc'?'desc':'asc'}">
                                            Date
                                        </a>
                                    </th>
                                    <th>Category</th>
                                    <th>Created By</th>
                                    <th>Status</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${sheets}" var="s">
                                    <tr>
                                        <td>${s.sheetCode}</td>
                                        <td>${s.inventoryDate}</td>
                                        <td>${s.categoryName}</td>
                                        <td>${s.createdByName}</td>
                                        <td>
                                            <span class="badge
                                                  ${s.status == 'draft' ? 'bg-secondary' :
                                                    s.status == 'submitted' ? 'bg-warning' :
                                                    s.status == 'approved' ? 'bg-success' :
                                                    'bg-danger'}">
                                                      ${s.status}
                                                  </span>
                                            </td>
                                            <td class="text-center">
                                                <c:if test="${s.status ne 'draft'}">
                                                    <a href="${pageContext.request.contextPath}/inventory-sheet-view?id=${s.sheetId}"
                                                       class="btn btn-sm btn-outline-primary">
                                                        View
                                                    </a>
                                                </c:if>
                                                <c:if test="${s.status == 'draft' && currentUser.userId == s.createdBy}">
                                                    <a href="${pageContext.request.contextPath}/inventory-sheet-edit?id=${s.sheetId}"
                                                       class="btn btn-sm btn-outline-warning">
                                                        Edit
                                                    </a>
                                                </c:if>
                                                <c:if test="${s.status == 'submitted' && currentUser.roleId == 2}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/inventory-sheet-approve"
                                                          style="display:inline;">
                                                        <input type="hidden" name="id" value="${s.sheetId}">
                                                        <input type="hidden" name="action" value="approve">
                                                        <button type="submit"
                                                                class="btn btn-sm btn-outline-success"
                                                                onclick="return confirm('Approve this sheet?')">
                                                            Approve
                                                        </button>
                                                    </form>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/inventory-sheet-approve"
                                                          style="display:inline;">
                                                        <input type="hidden" name="id" value="${s.sheetId}">
                                                        <input type="hidden" name="action" value="reject">
                                                        <button type="submit"
                                                                class="btn btn-sm btn-outline-danger"
                                                                onclick="return confirm('Reject this sheet?')">
                                                            Reject
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination-wrapper">
                                <div class="pagination-controls">

                                    <a class="page-btn ${currentPage == 1 ? 'disabled' : ''}"
                                       href="${pageContext.request.contextPath}/inventory-sheet-list
                                       ?page=${currentPage - 1}
                                       &year=${param.year}
                                       &month=${param.month}
                                       &createdBy=${param.createdBy}
                                       &sort=${param.sort}
                                       &dir=${param.dir}">
                                        ‹
                                    </a>

                                    <span class="page-number">
                                        Page ${currentPage} of ${totalPages}
                                    </span>

                                    <a class="page-btn ${currentPage == totalPages ? 'disabled' : ''}"
                                       href="${pageContext.request.contextPath}/inventory-sheet-list
                                       ?page=${currentPage + 1}
                                       &year=${param.year}
                                       &month=${param.month}
                                       &createdBy=${param.createdBy}
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
        </body>
        <script>
            const input = document.getElementById("createdByInput");
            const box = document.getElementById("suggestBox");

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
        </script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </html>
