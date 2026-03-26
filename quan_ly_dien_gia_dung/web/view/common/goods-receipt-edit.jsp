<%-- 
    Document   : goods-receipt-edit
    Created on : Feb 09, 2026
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>${readOnly ? 'Xem' : 'Sửa'} phiếu nhập kho</title>
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

            .tag .remove-tag {
                cursor: pointer;
                font-weight: bold;
                font-size: 16px;
            }

            .tag .remove-tag:hover {
                color: #fee;
            }

            .tag-input {
                border: none;
                outline: none;
                flex: 1;
                min-width: 150px;
                padding: 4px;
                font-size: 14px;
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
                            <h5 class="mb-1">${readOnly ? 'Xem' : 'Sửa'} phiếu nhập kho</h5>
                        </div>

                        <div class="col-lg-9">
                            <c:if test="${!readOnly}">
                            <c:if test="${sessionScope.user.role.roleId == 3}">
                                <div class="bg-light rounded p-2 mb-3">
                                    <div class="d-flex gap-2 align-items-stretch position-relative">
                                        <div class="flex-grow-1 position-relative">
                                            <input type="text" id="searchProduct" class="form-control" style="height: 40px;" placeholder="Mã hàng, tên sản phẩm...">
                                            <div id="searchDropdown" class="dropdown-menu w-100" style="display: none; max-height: 300px; overflow-y: auto;">
                                            </div>
                                        </div>
                                        <button class="btn btn-primary d-flex align-items-center justify-content-center px-4" style="height: 40px; white-space: nowrap; min-width: 100px;" type="button" onclick="searchProduct()">
                                            Tìm kiếm
                                        </button>
                                        <button class="btn btn-success d-flex align-items-center justify-content-center px-4" style="height: 40px; white-space: nowrap; min-width: 120px;" type="button" id="importFileBtn">
                                            Import File
                                        </button>
                                    </div>
                                </div>
                            </c:if>
                            </c:if>

                            <div class="bg-light rounded p-3">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="productTable">
                                        <thead>
                                            <tr>
                                                <th style="width: 50px;">Id</th>
                                                <th style="width: 220px;">Mã hàng</th>
                                                <th>Tên hàng</th>
                                                <th style="width: 80px;">Đơn vị</th>
                                                <th style="width: 120px;">Giá</th>
                                                <th style="width: 100px;">Số lượng</th>
                                                <c:if test="${!readOnly}">
                                                <c:if test="${sessionScope.user.role.roleId == 3}">
                                                    <th style="width: 80px;">Thao tác</th>
                                                </c:if>
                                                </c:if>
                                            </tr>
                                        </thead>
                                        <tbody id="productTableBody">
                                            <c:choose>
                                                <c:when test="${sessionScope.user.role.roleId == 3 and !readOnly}">
                                                    <tr>
                                                        <td colspan="7" class="text-center text-muted">
                                                            Chưa có sản phẩm nào. Tìm kiếm để thêm sản phẩm.
                                                        </td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="6" class="text-center text-muted">
                                                            Chưa có sản phẩm nào. Tìm kiếm để thêm sản phẩm.
                                                        </td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-3">
                            <div class="bg-light rounded p-4">
                                <form id="receiptForm" method="post" action="${pageContext.request.contextPath}/goods-receipt-edit">
                                    <input type="hidden" name="receiptId" value="${receipt.receiptId}">

                                    <div class="mb-3">
                                        <label class="form-label">Nguồn cung cấp</label>
                                        <c:choose>
                                            <c:when test="${empty receipt.supplier}">
                                                <input type="text" class="form-control" value="Nhập từ sale" readonly>
                                                <input type="hidden" name="supplierId" value="SALE">
                                            </c:when>
                                            <c:otherwise>
                                                <select class="form-select" name="supplierId" ${sessionScope.user.role.roleId != 3 or readOnly ? 'disabled' : ''}>
                                                    <c:forEach items="${suppliers}" var="s">
                                                        <option value="${s.supplierId}" ${receipt.supplier.supplierId eq s.supplierId ? 'selected' : ''}>${s.supplierName}</option>
                                                    </c:forEach>
                                                </select>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${not empty supplierIdError}">
                                            <small class="text-danger">${supplierIdError}</small>
                                        </c:if>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Ngày nhập</label>
                                        <fmt:formatDate value="${receipt.receiptDate}" pattern="yyyy-MM-dd" var="formattedDate" />
                                        <input type="date" class="form-control" name="receiptDate" id="receiptDate" value="${formattedDate}" ${sessionScope.user.role.roleId != 3 or readOnly ? 'readonly' : ''} required>
                                        <c:if test="${not empty receiptDateError}">
                                            <small class="text-danger">${receiptDateError}</small>
                                        </c:if>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Mã đơn hàng</label>
                                        <input type="text" class="form-control" name="purchaseOrderCode" placeholder="Nhập mã đơn hàng (nếu có)" value="${receipt.purchaseOrderId}" readonly disabled>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Tổng tiền:</label>
                                        <input type="text" class="form-control" id="totalAmount" readonly value="0 ₫">
                                        <input type="hidden" name="totalAmount" id="totalAmountValue" value="0">
                                    </div>

                                    <c:if test="${!readOnly}">
                                    <c:if test="${sessionScope.user.role.roleId == 2}">
                                        <div class="mb-3">
                                            <label class="form-label">Trạng thái:</label>
                                            <select class="form-select" name="status">
                                                <option value="draft" ${receipt.status == 'draft' ? 'selected' : ''}>Nháp</option>
                                                <option value="completed" ${receipt.status == 'completed' ? 'selected' : ''}>Hoàn thành</option>
                                                <option value="cancelled" ${receipt.status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                            </select>
                                        </div>
                                    </c:if>
                                    </c:if>

                                    <c:if test="${readOnly}">
                                        <div class="mb-3">
                                            <label class="form-label">Trạng thái:</label>
                                            <input type="text" class="form-control" readonly value="${receipt.status == 'draft' ? 'Nháp' : (receipt.status == 'completed' ? 'Hoàn thành' : 'Đã hủy')}">
                                        </div>
                                    </c:if>
                                    <div class="mb-4">
                                        <label class="form-label">Ghi chú:</label>
                                        <textarea class="form-control" name="notes" rows="4" placeholder="Nhập ghi chú..." ${sessionScope.user.role.roleId != 3 or readOnly ? 'readonly' : ''}>${receipt.notes}</textarea>
                                    </div>

                                    <c:if test="${not empty productsError}">
                                        <div class="alert alert-danger">
                                            ${productsError}
                                        </div>
                                    </c:if>

                                    <c:if test="${not empty generalError}">
                                        <div class="alert alert-danger">
                                            ${generalError}
                                        </div>
                                    </c:if>

                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/goods-receipt-list" class="btn btn-secondary flex-fill">Đóng</a>
                                        <c:if test="${!readOnly}">
                                            <c:if test="${sessionScope.user.role.roleId == 2}">
                                                <button type="submit" class="btn btn-success flex-fill">Cập nhật trạng thái</button>
                                            </c:if>
                                            <c:if test="${sessionScope.user.role.roleId != 2 and sessionScope.user.role.roleId == 3}">
                                                <button type="submit" class="btn btn-primary flex-fill">Cập nhật</button>
                                            </c:if>
                                        </c:if>
                                    </div>

                                    <input type="hidden" name="products" id="productsData">
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
        <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
        <script>
            window.GOODS_RECEIPT_EDIT = {
                contextPath: '${pageContext.request.contextPath}',
                editUrl: '${pageContext.request.contextPath}/goods-receipt-edit',
                isManager: ${sessionScope.user.role.roleId == 2},
                readOnly: ${readOnly eq true}
            };
        </script>
        <c:if test="${not empty productsJson}">
            <div id="goods-receipt-edit-products-json" style="display:none"><c:out value="${fn:replace(productsJson, '</', '&lt;/')}" escapeXml="false"/></div>
        </c:if>
        <script src="${pageContext.request.contextPath}/js/goods-receipt-edit.js?v=2"></script>

        <!-- Modal for Serial Numbers -->
        <div class="modal fade" id="serialModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="serialModalTitle">
                            <c:choose>
                                <c:when test="${sessionScope.user.role.roleId == 3 and not readOnly}">Nhập Serial Number</c:when>
                                <c:otherwise>Xem Serial Number</c:otherwise>
                            </c:choose>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="serialListContent">
                            <p class="text-center text-muted">Vui lòng chọn sản phẩm</p>
                        </div>
                    </div>
                    <c:if test="${sessionScope.user.role.roleId == 3 and not readOnly}">
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" id="saveSerials">Lưu</button>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Modal for Import Excel -->
        <div class="modal fade" id="importModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Import File Excel</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Chọn file Excel (.xlsx, .xls)</label>
                            <input type="file" class="form-control" id="excelFile" accept=".xlsx,.xls">
                            <small class="text-muted">
                                File Excel cần có các cột: <strong>SKU, Quantity, Serial Numbers</strong>
                            </small>
                        </div>
                        <div class="alert alert-info" role="alert">
                            <strong>Hướng dẫn:</strong>
                            <ul class="mb-0">
                                <li>Cột <strong>SKU</strong>: Mã sản phẩm (bắt buộc)</li>
                                <li>Cột <strong>Quantity</strong>: Số lượng (bắt buộc)</li>
                                <li>Cột <strong>Serial Numbers</strong>: Các serial cách nhau bằng dấu phẩy (,)</li>
                            </ul>
                        </div>
                        <div id="importError" class="alert alert-danger" style="display: none;"></div>
                        <div id="importSuccess" class="alert alert-success" style="display: none;"></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" id="processImport">Import</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
