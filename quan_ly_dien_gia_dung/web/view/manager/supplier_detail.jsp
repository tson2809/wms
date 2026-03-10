<%-- 
    Document   : supplier_detail
    Created on : 31 thg 1, 2026, 13:10:16
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>${mode == 'add' ? 'Thêm' : 'Chỉnh sửa'} nhà cung cấp</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/manager/components/sidebarManager.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="mb-3">
                                <h5 class="mb-0 fw-semibold">
                                    ${mode == 'add' ? 'Thêm nhà cung cấp' : 'Chỉnh sửa nhà cung cấp'}
                                </h5>
                            </div>
                            <div class="bg-light rounded p-4">
                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        ${successMessage}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>

                                <c:set var="formAction" value="${mode == 'add' ? '/supplier-add' : '/supplier-detail'}" />
                                <form action="${pageContext.request.contextPath}${formAction}" method="post">
                                    <c:if test="${mode != 'add'}">
                                        <input type="hidden" name="id" value="${supplier != null ? supplier.supplierId : supplierId}">
                                    </c:if>

                                    <c:set var="valName"    value="${supplier != null ? supplier.supplierName  : supplierName}" />
                                    <c:set var="valContact" value="${supplier != null ? supplier.contactPerson : contactPerson}" />
                                    <c:set var="valEmail"   value="${supplier != null ? supplier.email         : email}" />
                                    <c:set var="valPhone"   value="${supplier != null ? supplier.phone         : phone}" />
                                    <c:set var="valDesc"    value="${supplier != null ? supplier.description   : description}" />
                                    <c:set var="valStatus"  value="${not empty status ? status : (supplier != null ? supplier.status : 'active')}" />

                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="supplierName" class="form-label">Tên nhà cung cấp</label>
                                            <input type="text" class="form-control" id="supplierName" name="supplierName"
                                                   value="${valName}" placeholder="Nhập tên nhà cung cấp">
                                            <c:if test="${not empty errorSupplierName}">
                                                <div class="text-danger small mt-1">${errorSupplierName}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="contactPerson" class="form-label">Người liên hệ</label>
                                            <input type="text" class="form-control" id="contactPerson" name="contactPerson"
                                                   value="${valContact}" placeholder="Họ tên người liên hệ">
                                            <c:if test="${not empty errorContactPerson}">
                                                <div class="text-danger small mt-1">${errorContactPerson}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="email" class="form-label">Email</label>
                                            <input type="text" class="form-control" id="email" name="email"
                                                   value="${valEmail}" placeholder="email@gmail.com">
                                            <c:if test="${not empty errorEmail}">
                                                <div class="text-danger small mt-1">${errorEmail}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="phone" class="form-label">Số điện thoại</label>
                                            <input type="text" class="form-control" id="phone" name="phone"
                                                   value="${valPhone}" placeholder="Số điện thoại">
                                            <c:if test="${not empty errorPhone}">
                                                <div class="text-danger small mt-1">${errorPhone}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="status" class="form-label">Trạng thái</label>
                                            <select class="form-select" id="status" name="status">
                                                <option value="active"   ${valStatus == 'active'   ? 'selected' : ''}>Active</option>
                                                <option value="inactive" ${valStatus == 'inactive' ? 'selected' : ''}>Inactive</option>
                                            </select>
                                        </div>
                                        <div class="col-12">
                                            <label for="description" class="form-label">Mô tả</label>
                                            <textarea class="form-control" id="description" name="description" rows="3"
                                                      placeholder="Mô tả về nhà cung cấp">${valDesc}</textarea>
                                            <c:if test="${not empty errorDescription}">
                                                <div class="text-danger small mt-1">${errorDescription}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 d-flex justify-content-end gap-2">
                                            <a href="${pageContext.request.contextPath}/supplier-list" class="btn btn-secondary">Quay lại</a>
                                            <button type="submit" class="btn btn-primary">
                                                ${mode == 'add' ? 'Thêm mới' : 'Lưu'}
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
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
