<%-- 
    Document   : viewrole
    Created on : Jan 11, 2026, 3:35:33 AM
    Author     : laptop368
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="utf-8">
        <title>Quản Lý Cấp Bậc</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <!-- Google Web Fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">

        <!-- Icon Font Stylesheet -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">

        <!-- Customized Bootstrap Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <style>
            .role-table {
                table-layout: fixed;
            }
            .role-table th:nth-child(1),
            .role-table td:nth-child(1) {
                width: 25%;
            }
            .role-table th:nth-child(2),
            .role-table td:nth-child(2) {
                width: 35%;
            }
            .role-table th:nth-child(3),
            .role-table td:nth-child(3) {
                width: 15%;
            }
            .role-table th:nth-child(4),
            .role-table td:nth-child(4) {
                width: 10%;
                text-align: center;
            }
            .role-table td > div {
                min-height: 30px;
            }
            .role-table input[type="text"],
            .role-table textarea,
            .role-table select {
                width: 100%;
            }
        </style>
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <!-- Sidebar Container -->
            <jsp:include page="/view/common/components/sidebar.jsp" />

            <!-- Content Start -->
            <div class="content">
                <!-- Navbar Container -->
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <h2 class="mb-4">Danh Sách Cấp Bậc</h2>

                            <!-- Thông báo -->
                            <c:if test="${param.success == 'true'}">
                                <div class="alert alert-success alert-dismissible fade show" role="alert">
                                    <strong>Thành công!</strong> Đã cập nhật thông tin cấp bậc.
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>

                            <c:if test="${param.error == 'true'}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <strong>Lỗi!</strong> Không thể cập nhật thông tin. Vui lòng thử lại.
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                        </div>

                        <div class="col-12">
                            <div class="bg-light rounded p-4">
                                <c:if test="${empty roles}">
                                    <div class="alert alert-warning">Không có cấp bậc nào trong hệ thống!</div>
                                </c:if>

                                <c:if test="${not empty roles}">
                                    <div class="table-responsive">
                                        <table class="table table-bordered role-table">
                                            <thead class="table-primary">
                                                <tr>
                                                    <th>Tên Cấp Bậc</th>
                                                    <th>Mô Tả</th>
                                                    <th>Trạng Thái</th>
                                                    <th>Thao Tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="role" items="${roles}">
                                                    <tr>
                                                        <td>
                                                            <div class="view-mode-${role.roleId}">
                                                                ${role.roleName}
                                                            </div>
                                                            <div class="edit-mode-${role.roleId}" style="display:none;">
                                                                <input type="text" class="form-control form-control-sm" 
                                                                       id="roleName_${role.roleId}" 
                                                                       value="${role.roleName}" required>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="view-mode-${role.roleId}">
                                                                ${role.roleDescription != null ? role.roleDescription : ''}
                                                            </div>
                                                            <div class="edit-mode-${role.roleId}" style="display:none;">
                                                                <textarea class="form-control form-control-sm" 
                                                                          id="roleDescription_${role.roleId}" 
                                                                          rows="2">${role.roleDescription != null ? role.roleDescription : ''}</textarea>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="view-mode-${role.roleId}">
                                                                <c:choose>
                                                                    <c:when test="${role.isActive}">
                                                                        <span class="badge bg-success">Hoạt động</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge bg-secondary">Không hoạt động</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <div class="edit-mode-${role.roleId}" style="display:none;">
                                                                <select class="form-select form-select-sm" id="isActive_${role.roleId}">
                                                                    <option value="true" <c:if test="${role.isActive}">selected</c:if>>Hoạt động</option>
                                                                    <option value="false" <c:if test="${!role.isActive}">selected</c:if>>Không hoạt động</option>
                                                                </select>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="view-mode-${role.roleId}">
                                                                <button type="button" class="btn btn-sm btn-warning edit-btn" 
                                                                        data-role-id="${role.roleId}"
                                                                        title="Sửa">
                                                                    <i class="fa fa-edit"></i>
                                                                </button>
                                                            </div>
                                                            <div class="edit-mode-${role.roleId}" style="display:none;">
                                                                <form method="POST" action="ViewRole" class="update-form" style="display:inline;">
                                                                    <input type="hidden" name="roleId" value="${role.roleId}">
                                                                    <input type="hidden" name="roleName" class="hidden-role-name">
                                                                    <input type="hidden" name="roleDescription" class="hidden-role-description">
                                                                    <input type="hidden" name="isActive" class="hidden-is-active">
                                                                    <button type="submit" class="btn btn-sm btn-success save-btn" title="Lưu">
                                                                        <i class="fa fa-save"></i>
                                                                    </button>
                                                                </form>
                                                                <button type="button" class="btn btn-sm btn-secondary cancel-btn" title="Hủy">
                                                                    <i class="fa fa-times"></i>
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Content End -->
        </div>

        <!-- JavaScript Libraries -->
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/loadComponents.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>

        <script>
            $(document).ready(function() {
                // Xử lý khi click nút Sửa
                $('.edit-btn').click(function() {
                    var roleId = $(this).data('role-id');
                    // Ẩn view mode, hiện edit mode
                    $('.view-mode-' + roleId).hide();
                    $('.edit-mode-' + roleId).show();
                });
                
                // Xử lý khi click nút Hủy
                $('.cancel-btn').click(function() {
                    // Reload để lấy giá trị gốc
                    location.reload();
                });
                
                // Xử lý khi submit form
                $('.update-form').submit(function(e) {
                    var form = $(this);
                    var roleId = form.find('input[name="roleId"]').val();
                    
                    // Lấy giá trị từ inputs và set vào hidden inputs
                    var roleName = $('#roleName_' + roleId).val();
                    var roleDescription = $('#roleDescription_' + roleId).val();
                    var isActive = $('#isActive_' + roleId).val();
                    
                    form.find('.hidden-role-name').val(roleName);
                    form.find('.hidden-role-description').val(roleDescription);
                    form.find('.hidden-is-active').val(isActive);
                });
            });
        </script>
    </body>
</html>
