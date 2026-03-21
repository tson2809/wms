<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Tạo phiếu xuất kho</title>
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
                            <h5 class="mb-1">Tạo phiếu xuất kho</h5>
                            <c:if test="${not empty returnOrderCode}">
                                <div class="alert alert-info py-2 mb-0 small">
                                    <i class="fa fa-info-circle me-1"></i>Đang tạo phiếu xuất từ đơn trả hàng: <strong>${returnOrderCode}</strong>
                                </div>
                            </c:if>
                            <c:if test="${not empty purchaseOrderCode}">
                                <div class="alert alert-warning py-2 mb-0 small mt-1">
                                    <i class="fa fa-shopping-cart me-1"></i>Đang tạo phiếu xuất từ đơn Sale: <strong>${purchaseOrderCode}</strong>
                                </div>
                            </c:if>
                        </div>

                        <div class="col-lg-9">
<!--                            <div class="bg-light rounded p-2 mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-4">
                                        <label class="form-label mb-1">Nguồn đơn</label>
                                        <select id="sourceType" class="form-select" style="height:40px">
                                            <option value="">Chọn</option>
                                            <option value="purchase_order">Đơn đặt hàng</option>
                                            <option value="return_order">Đơn trả hàng</option>
                                        </select>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label mb-1">Mã đơn</label>
                                        <div class="position-relative">
                                            <input type="text" id="sourceCodeSearch" class="form-control" style="height:40px" placeholder="Nhập để tìm mã đơn..." disabled>
                                            <div id="sourceCodeDropdown" class="dropdown-menu w-100" style="display:none;max-height:300px;overflow-y:auto"></div>
                                            <input type="hidden" id="sourceId">
                                        </div>
                                    </div>
                                    <div class="col-md-2 d-flex gap-2">
                                        <button type="button" class="btn btn-outline-secondary w-100" style="height:40px" id="clearSourceBtn" disabled>Xóa</button>
                                        <button type="button" class="btn btn-success w-100" style="height:40px" id="loadSourceBtn" disabled>Load</button>
                                    </div>
                                </div>
                            </div>-->

                            <div class="bg-light rounded p-2 mb-3">
                                <div class="d-flex gap-2 align-items-stretch position-relative">
                                    <div class="flex-grow-1 position-relative">
                                        <input type="text" id="searchProduct" class="form-control" style="height: 40px;" placeholder="Mã hàng, tên sản phẩm...">
                                        <div id="searchDropdown" class="dropdown-menu w-100" style="display: none; max-height: 300px; overflow-y: auto;"></div>
                                    </div>
                                    <button class="btn btn-primary d-flex align-items-center justify-content-center px-4"
                                            style="height: 40px; white-space: nowrap; min-width: 100px;"
                                            type="button" onclick="searchProduct()">
                                        Tìm kiếm
                                    </button>
                                    <button class="btn btn-success d-flex align-items-center justify-content-center px-4"
                                            style="height: 40px; white-space: nowrap; min-width: 120px;"
                                            type="button" id="importFileBtn">
                                        Import File
                                    </button>
                                </div>
                            </div>

                            <div class="bg-light rounded p-3">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="productTable">
                                        <thead>
                                            <tr>
                                                <th style="width: 50px;">Id</th>
                                                <th style="width: 220px;">Mã hàng</th>
                                                <th>Tên hàng</th>
                                                <th style="width: 80px;">Đơn vị</th>
                                                <th style="width: 110px;">Tồn kho</th>
                                                <th style="width: 100px;">Số lượng</th>
                                                <th style="width: 80px;">Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody id="productTableBody">
                                            <tr>
                                                <td colspan="7" class="text-center text-muted">
                                                    Chưa có sản phẩm nào. Tìm kiếm để thêm sản phẩm.
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-3">
                            <div class="bg-light rounded p-4">
                                <form id="issueForm" method="post" action="${pageContext.request.contextPath}/goods-issue-add">

                                    <div class="mb-3">
                                        <label class="form-label">Mã phiếu xuất</label>
                                        <input type="text" class="form-control" name="issueCode"
                                               placeholder="VD: PX001" value="${param.issueCode}">
                                        <c:if test="${not empty issueCodeError}">
                                            <small class="text-danger">${issueCodeError}</small>
                                        </c:if>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Ngày xuất</label>
                                        <input type="date" class="form-control" name="issueDate" id="issueDate"
                                               value="${param.issueDate}" required>
                                        <c:if test="${not empty issueDateError}">
                                            <small class="text-danger">${issueDateError}</small>
                                        </c:if>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Loại xuất</label>
                                        <select class="form-select" name="issueType" id="issueType">
                                            <option value="sale"            ${(issueType ne 'return_supplier' || empty issueType) && (param.issueType ne 'return_supplier' || empty param.issueType) ? 'selected' : ''}>Bán hàng</option>
                                            <option value="return_supplier" ${(issueType eq 'return_supplier' || param.issueType eq 'return_supplier') ? 'selected' : ''}>Trả nhà cung cấp</option>
                                            <option value="transfer"        ${param.issueType eq 'transfer'        ? 'selected' : ''}>Điều chuyển kho</option>
                                            <option value="disposal"        ${param.issueType eq 'disposal'        ? 'selected' : ''}>Hủy hàng</option>
                                            <option value="internal_use"    ${param.issueType eq 'internal_use'    ? 'selected' : ''}>Sử dụng nội bộ</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Người nhận</label>
                                        <input type="text" class="form-control" name="receiverName"
                                               placeholder="Tên người nhận" value="${param.receiverName}">
                                        <c:if test="${not empty receiverNameError}">
                                            <small class="text-danger">${receiverNameError}</small>
                                        </c:if>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Bộ phận</label>
                                        <input type="text" class="form-control" name="department"
                                               placeholder="VD: Kinh doanh, Kho vận..." value="${param.department}">
                                    </div>

                                    <div class="mb-4">
                                        <label class="form-label">Ghi chú:</label>
                                        <textarea class="form-control" name="notes" rows="4" placeholder="Nhập ghi chú..."></textarea>
                                    </div>

                                    <c:if test="${not empty productsError}">
                                        <div class="alert alert-danger">${productsError}</div>
                                    </c:if>
                                    <c:if test="${not empty generalError}">
                                        <div class="alert alert-danger">${generalError}</div>
                                    </c:if>

                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/goods-issue-list" class="btn btn-secondary flex-fill">Hủy</a>
                                        <button type="submit" class="btn btn-primary flex-fill">Tạo</button>
                                    </div>

                                    <input type="hidden" name="products" id="productsData">
                                    <c:if test="${not empty returnOrderId}">
                                        <input type="hidden" name="returnOrderId" value="${returnOrderId}">
                                    </c:if>
                                    <c:if test="${not empty purchaseOrderId}">
                                        <input type="hidden" name="purchaseOrderId" value="${purchaseOrderId}">
                                    </c:if>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js?v=1"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
        <script>
            window.GOODS_ISSUE_ADD = {
                contextPath: '${pageContext.request.contextPath}',
                addUrl: '${pageContext.request.contextPath}/goods-issue-add'
            };
        </script>
        <c:if test="${not empty productsJson}">
            <div id="goods-issue-add-products-json" style="display:none"><c:out value="${fn:replace(productsJson, '</', '&lt;/')}" escapeXml="false"/></div>
        </c:if>
        <c:if test="${not empty poExpectedDate}">
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    var d = document.getElementById('issueDate');
                    if (d && !d.value) d.value = '${poExpectedDate}';
                });
            </script>
        </c:if>
        <script src="${pageContext.request.contextPath}/js/goods-issue-add.js?v=2"></script>

        <!-- Modal Import Excel -->
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
                                File Excel cần có các cột: <strong>SKU, Serial Numbers</strong>
                            </small>
                        </div>
                        <div class="alert alert-info" role="alert">
                            <strong>Hướng dẫn:</strong>
                            <ul class="mb-0">
                                <li>Cột <strong>SKU</strong>: Mã sản phẩm (bắt buộc)</li>
                                <li>Cột <strong>Serial Numbers</strong>: Các serial cách nhau bằng dấu phẩy (,) — serial phải đang tồn tại trong kho</li>
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

        <!-- Modal chọn serial -->
        <div class="modal fade" id="serialModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="serialModalTitle">Chọn serial xuất kho</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-2">
                            <input type="text" id="serialSearchInput" class="form-control form-control-sm" placeholder="Lọc serial...">
                        </div>
                        <div id="serialListContent">
                            <p class="text-center text-muted">Vui lòng chọn sản phẩm</p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <span class="text-muted me-auto small">Đã chọn: <strong id="selectedSerialCount">0</strong></span>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" id="saveSerials">Lưu</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
