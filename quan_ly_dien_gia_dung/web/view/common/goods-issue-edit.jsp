<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>${readOnly ? 'Xem' : 'Duyệt'} phiếu xuất kho</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <style>
            .tag-input-container {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                align-items: center;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                padding: 8px;
                min-height: 42px;
                background: white;
                cursor: text;
            }
            .tag {
                background: #3b82f6;
                color: white;
                padding: 4px 12px;
                border-radius: 16px;
                font-size: 14px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/sidebar.jsp" />

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <h5 class="mb-1">${readOnly ? 'Xem' : 'Duyệt'} phiếu xuất kho</h5>
                        </div>

                        <div class="col-lg-9">
                            <div class="bg-light rounded p-3">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="productTable">
                                        <thead>
                                            <tr>
                                                <th style="width: 50px;">Id</th>
                                                <th style="width: 220px;">Mã hàng</th>
                                                <th>Tên hàng</th>
                                                <th style="width: 80px;">Đơn vị</th>
                                                <th style="width: 100px;">Số lượng</th>
                                            </tr>
                                        </thead>
                                        <tbody id="productTableBody">
                                            <tr>
                                                <td colspan="5" class="text-center text-muted">
                                                    Đang tải dữ liệu...
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-3">
                            <div class="bg-light rounded p-4">
                                <div class="mb-3">
                                    <label class="form-label">Ngày xuất</label>
                                    <fmt:formatDate value="${issue.issueDate}" pattern="yyyy-MM-dd" var="issueDateFormatted"/>
                                    <input type="date" class="form-control" value="${issueDateFormatted}" readonly>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Loại xuất</label>
                                    <c:set var="issueTypeLabel" value="${issue.issueType}" />
                                    <c:choose>
                                        <c:when test="${issue.issueType eq 'sale'}">
                                            <c:set var="issueTypeLabel" value="Bán hàng" />
                                        </c:when>
                                        <c:when test="${issue.issueType eq 'return_supplier'}">
                                            <c:set var="issueTypeLabel" value="Trả nhà cung cấp" />
                                        </c:when>
                                        <c:when test="${issue.issueType eq 'other'}">
                                            <c:set var="issueTypeLabel" value="Khác" />
                                        </c:when>
                                    </c:choose>
                                    <input type="text" class="form-control" readonly value="${issueTypeLabel}">
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Người nhận</label>
                                    <input type="text" class="form-control" value="${issue.receiverName}" readonly>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Người tạo</label>
                                    <input type="text" class="form-control" value="${not empty issue.createdByUser ? issue.createdByUser.fullName : ''}" readonly>
                                </div>

                                <c:choose>
                                    <c:when test="${sessionScope.user.role.roleId == 2 and !readOnly}">
                                        <div class="mb-3">
                                            <label class="form-label">Trạng thái:</label>
                                            <select class="form-select" name="status" form="goodsIssueStatusForm">
                                                <option value="draft" ${issue.status == 'draft' ? 'selected' : ''}>Nháp</option>
                                                <option value="completed" ${issue.status == 'completed' ? 'selected' : ''}>Hoàn thành</option>
                                                <option value="cancelled" ${issue.status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                            </select>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="mb-3">
                                            <label class="form-label">Trạng thái:</label>
                                            <input type="text" class="form-control" readonly
                                                   value="${issue.status eq 'draft' ? 'Nháp' : (issue.status eq 'completed' ? 'Hoàn thành' : 'Đã hủy')}">
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="mb-4">
                                    <label class="form-label">Ghi chú:</label>
                                    <textarea class="form-control" rows="3" readonly>${issue.notes}</textarea>
                                </div>

                                <div class="d-flex gap-2">
                                    <a href="${pageContext.request.contextPath}/goods-issue-list" class="btn btn-secondary flex-fill">Đóng</a>
                                    <c:if test="${sessionScope.user.role.roleId == 2 and !readOnly}">
                                        <form id="goodsIssueStatusForm" method="POST" action="${pageContext.request.contextPath}/goods-issue-detail"
                                              class="d-inline flex-fill">
                                            <input type="hidden" name="id" value="${issue.issueId}">
                                            <button type="submit" class="btn btn-success w-100">Cập nhật trạng thái</button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
            window.GOODS_ISSUE_EDIT = {
                contextPath: '${pageContext.request.contextPath}',
                isManager: ${sessionScope.user.role.roleId == 2},
                readOnly: ${readOnly eq true}
            };
        </script>
        <c:if test="${not empty productsJson}">
            <div id="goods-issue-edit-products-json" style="display:none"><c:out value="${fn:replace(productsJson, '</', '&lt;/')}" escapeXml="false"/></div>
        </c:if>
        <script src="${pageContext.request.contextPath}/js/goods-issue-edit.js?v=1"></script>

        <!-- Modal xem serial -->
        <div class="modal fade" id="serialModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Xem Serial Number</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="serialListContent">
                            <p class="text-center text-muted">Vui lòng chọn sản phẩm</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
