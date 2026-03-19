<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Chỉnh sửa đơn trả hàng</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <c:choose>
                <c:when test="${sessionScope.user.role.roleId == 2}">
                    <jsp:include page="/view/manager/components/sidebarManager.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 3}">
                    <jsp:include page="/view/staff/components/sidebarStaff.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 4}">
                    <jsp:include page="/view/sale/components/sidebarSale.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/view/common/components/RoleSideBar.jsp" />
                </c:otherwise>
            </c:choose>

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <h5 class="mb-1">Chỉnh sửa đơn trả hàng (Sale về kho)</h5>
                        </div>

                        <div class="col-lg-9" id="productSection">
                            <div class="bg-light rounded p-2 mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-8">
                                        <label class="form-label mb-1">Tìm sản phẩm</label>
                                        <div class="position-relative">
                                            <input type="text" class="form-control" id="searchProduct" placeholder="Mã hàng, tên sản phẩm..." autocomplete="off" />
                                            <div id="searchDropdown" class="dropdown-menu w-100" style="display:none;max-height:280px;overflow-y:auto"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="bg-light rounded p-3">
                                <table class="table table-bordered table-sm" id="productTable">
                                    <thead>
                                        <tr>
                                            <th style="width:200px">Mã SKU</th>
                                            <th>Tên sản phẩm</th>
                                            <th style="width:80px">Đơn vị</th>
                                            <th style="width:80px">Số lượng</th>
                                            <th style="width:110px">Giá nhập</th>
                                            <th style="width:110px">Giá trả lại</th>
                                            <th style="width:120px">Thành tiền</th>
                                            <th style="width:50px"></th>
                                        </tr>
                                    </thead>
                                    <tbody id="productTableBody">
                                        <tr><td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm.</td></tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="col-lg-3">
                            <div class="bg-light rounded p-4">
                                <form id="salesReturnEditForm" method="post" action="${pageContext.request.contextPath}/sales-return-edit">
                                    <input type="hidden" name="salesReturnId" value="${salesReturn.salesReturnId}">

                                    <div class="mb-3">
                                        <label class="form-label">Mã phiếu trả <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" name="srCode" id="srCode" value="${salesReturn.srCode}" required />
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Ngày trả <span class="text-danger">*</span></label>
                                        <c:set var="returnDateFormatted" value="" />
                                        <c:if test="${salesReturn != null && salesReturn.returnDate != null}">
                                            <fmt:formatDate value="${salesReturn.returnDate}" pattern="yyyy-MM-dd" var="returnDateFormatted"/>
                                        </c:if>
                                        <input type="date" class="form-control" name="returnDate" id="returnDate" value="${returnDateFormatted}" required />
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Trạng thái hoàn tiền</label>
                                        <select class="form-select" name="refundStatus" id="refundStatus">
                                            <option value="not_refunded" ${salesReturn.refundStatus == 'not_refunded' ? 'selected' : ''}>Chưa hoàn</option>
                                            <option value="refunded" ${salesReturn.refundStatus == 'refunded' ? 'selected' : ''}>Đã hoàn</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Tổng tiền hoàn</label>
                                        <input type="text" class="form-control fw-bold" id="totalDisplay" readonly value="0 ₫" />
                                        <input type="hidden" name="products" id="productsData" value="[]"/>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Lý do</label>
                                        <textarea class="form-control" name="description" id="description" rows="4">${salesReturn.description}</textarea>
                                    </div>

                                    <c:if test="${not empty generalError}">
                                        <div class="alert alert-danger py-2 small">${generalError}</div>
                                    </c:if>

                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/sales-return-list" class="btn btn-secondary flex-fill">Quay lại</a>
                                        <button type="submit" class="btn btn-primary flex-fill" id="btnSubmit">Cập nhật</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <input type="hidden" id="editDataJsonRaw" value="<c:out value='${editDataJson}' escapeXml='true'/>"/>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
            (function () {
                var ctx = '${pageContext.request.contextPath}';
                var salesReturnId = '${salesReturn.salesReturnId}';
                var orderStatus = ('${salesReturn.status}').toLowerCase();
                var roleViewOnly = ${roleViewOnly == true ? 'true' : 'false'};
                var isPending = orderStatus === 'pending';
                var isCancelled = orderStatus === 'cancelled';
                var isRefundOnly = (orderStatus === 'processing' || orderStatus === 'completed');
                var products = [];

                function formatCurrency(n) {
                    var num = Number(n || 0);
                    return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(num);
                }

                function findProductIndex(variantId) {
                    for (var i = 0; i < products.length; i++) {
                        if (String(products[i].variantId) === String(variantId)) return i;
                    }
                    return -1;
                }

                function syncHidden() {
                    $('#productsData').val(JSON.stringify(products));
                }

                function updateTotals() {
                    var total = 0;
                    products.forEach(function (p) {
                        total += Number(p.quantity || 0) * Number(p.refundPrice || 0);
                    });
                    $('#totalDisplay').val(formatCurrency(total));
                    syncHidden();
                }

                function rebuildTable() {
                    var $tbody = $('#productTableBody').empty();
                    if (!products.length) {
                        $tbody.html('<tr><td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm.</td></tr>');
                        return;
                    }
                    products.forEach(function (p) {
                        var subtotal = Number(p.quantity || 0) * Number(p.refundPrice || 0);
                        var row = '' +
                            '<tr data-variant-id="' + p.variantId + '" data-stock="' + (p.stock || 0) + '">' +
                            '  <td>' + (p.code || '') + '</td>' +
                            '  <td>' + (p.name || '') + '</td>' +
                            '  <td class="text-center">' + (p.unit || '') + '</td>' +
                            '  <td><input type="number" class="form-control form-control-sm qtyInput" min="0" step="1" value="' + (p.quantity || 0) + '" style="width:70px" ' + (!isPending ? 'readonly' : '') + '></td>' +
                            '  <td class="text-end">' + formatCurrency(p.originalPrice || 0) + '</td>' +
                            '  <td><input type="number" class="form-control form-control-sm refundInput" min="0" step="1000" value="' + (p.refundPrice || 0) + '" style="width:110px" ' + (!isPending ? 'readonly' : '') + '></td>' +
                            '  <td class="text-end subtotalCell">' + formatCurrency(subtotal) + '</td>' +
                            '  <td>' + (isPending ? '<button type="button" class="btn btn-sm btn-danger delRow"><i class="fas fa-times"></i></button>' : '') + '</td>' +
                            '</tr>';
                        $tbody.append(row);
                    });
                }

                function applyModeLock() {
                    if (roleViewOnly) {
                        $('#salesReturnEditForm input, #salesReturnEditForm textarea, #salesReturnEditForm select').prop('disabled', true);
                        $('#searchProduct').prop('disabled', true);
                        $('#btnSubmit').hide();
                        return;
                    }
                    if (isCancelled) {
                        $('#salesReturnEditForm input, #salesReturnEditForm textarea, #salesReturnEditForm select').prop('disabled', true);
                        $('#searchProduct').prop('disabled', true);
                        $('#btnSubmit').hide();
                        return;
                    }
                    if (isRefundOnly) {
                        $('#srCode, #returnDate, #description').prop('readonly', true);
                        $('#searchProduct').prop('disabled', true);
                        $('#productSection input, #productSection button').prop('disabled', true);
                        $('#refundStatus').prop('disabled', false);
                        $('#btnSubmit').text('Cập nhật trạng thái hoàn tiền');
                    }
                }

                function loadEditData() {
                    var raw = $('#editDataJsonRaw').val();
                    if (!raw) return;
                    try {
                        var arr = JSON.parse(raw);
                        if (!Array.isArray(arr)) return;
                        products = arr.map(function (p) {
                            return {
                                variantId: p.variantId,
                                code: p.code,
                                name: p.name,
                                unit: p.unit,
                                quantity: Number(p.quantity || 0),
                                originalPrice: Number(p.originalPrice || 0),
                                refundPrice: Number(p.refundPrice || 0),
                                stock: Number(p.stock || 0)
                            };
                        });
                    } catch (e) {
                    }
                    rebuildTable();
                    updateTotals();
                }

                function loadProducts(keyword) {
                    if (!isPending) return;
                    $.ajax({
                        url: ctx + '/sales-return-edit',
                        type: 'POST',
                        dataType: 'json',
                        data: {action: 'searchProduct', salesReturnId: salesReturnId, search: keyword || ''},
                        success: function (data) {
                            var list = Array.isArray(data) ? data : [];
                            var $dd = $('#searchDropdown').empty();
                            if (!list.length) {
                                $dd.append('<div class="dropdown-item text-muted">Không tìm thấy sản phẩm</div>');
                            } else {
                                list.forEach(function (p) {
                                    var html = '<a href="#" class="dropdown-item"><div><strong>' + (p.code || '') + '</strong></div><div class="small text-muted">' + (p.name || '') + '</div></a>';
                                    var $a = $(html);
                                    $a.on('click', function (e) {
                                        e.preventDefault();
                                        if (findProductIndex(p.variantId) >= 0) return;
                                        products.push({
                                            variantId: p.variantId,
                                            code: p.code || '',
                                            name: p.name || '',
                                            unit: p.unit || '',
                                            quantity: 1,
                                            originalPrice: Number(p.costPrice || 0),
                                            refundPrice: Number(p.costPrice || 0),
                                            stock: Number(p.stock || 0)
                                        });
                                        rebuildTable();
                                        updateTotals();
                                        $dd.hide();
                                        $('#searchProduct').val('');
                                    });
                                    $dd.append($a);
                                });
                            }
                            $dd.show();
                        }
                    });
                }

                var t;
                $('#searchProduct').on('focus', function () { loadProducts($(this).val()); });
                $('#searchProduct').on('keyup', function () {
                    clearTimeout(t);
                    var q = $(this).val();
                    t = setTimeout(function () { loadProducts(q); }, 300);
                });
                $(document).on('click', function (e) {
                    if (!$(e.target).closest('#searchProduct, #searchDropdown').length) $('#searchDropdown').hide();
                });

                $('#productTableBody').on('input', '.qtyInput', function () {
                    var row = $(this).closest('tr');
                    var variantId = row.data('variant-id');
                    var qty = Number($(this).val() || 0);
                    if (qty < 0) qty = 0;
                    var idx = findProductIndex(variantId);
                    if (idx >= 0) {
                        products[idx].quantity = qty;
                        row.find('.subtotalCell').text(formatCurrency(qty * Number(products[idx].refundPrice || 0)));
                        updateTotals();
                    }
                });

                $('#productTableBody').on('input', '.refundInput', function () {
                    var row = $(this).closest('tr');
                    var variantId = row.data('variant-id');
                    var price = Number($(this).val() || 0);
                    if (price < 0) price = 0;
                    var idx = findProductIndex(variantId);
                    if (idx >= 0) {
                        products[idx].refundPrice = price;
                        row.find('.subtotalCell').text(formatCurrency(price * Number(products[idx].quantity || 0)));
                        updateTotals();
                    }
                });

                $('#productTableBody').on('click', '.delRow', function () {
                    var variantId = $(this).closest('tr').data('variant-id');
                    products = products.filter(function (p) { return String(p.variantId) !== String(variantId); });
                    rebuildTable();
                    updateTotals();
                });

                $('#salesReturnEditForm').on('submit', function () {
                    syncHidden();
                });

                loadEditData();
                applyModeLock();
            })();
        </script>
    </body>
</html>

