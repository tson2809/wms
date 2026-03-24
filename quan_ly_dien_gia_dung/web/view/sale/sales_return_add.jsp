<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Tạo đơn trả hàng (Sale)</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <style>
            .serial-tag-container {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                align-items: center;
                border: 1px solid #dee2e6;
                border-radius: 6px;
                padding: 8px;
                min-height: 42px;
                background: #fff;
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
                            <h5 class="mb-1">Tạo đơn trả hàng (Sale về kho)</h5>
                        </div>

                        <%-- Bảng sản phẩm --%>
                        <div class="col-lg-9">
                            <div class="bg-light rounded p-2 mb-3">
                                <div class="row g-2 align-items-end">
                                    <div class="col-md-8">
                                        <label class="form-label mb-1">Tìm sản phẩm</label>
                                        <div class="position-relative">
                                            <input type="text" class="form-control" id="searchProduct"
                                                   name="searchProduct" placeholder="Mã hàng, tên sản phẩm..."
                                                   autocomplete="off" />
                                            <div id="searchDropdown" class="dropdown-menu w-100"
                                                 style="display:none;max-height:280px;overflow-y:auto"></div>
                                        </div>
                                       
                                    </div>
                                </div>
                            </div>

                            <div class="bg-light rounded p-3">
                                <table class="table table-bordered table-sm" id="productTable">
                                    <thead>
                                        <tr>
                                            <th style="width: 200px;">Mã SKU</th>
                                            <th>Tên sản phẩm</th>
                                            <th style="width:80px">Đơn vị</th>
                                            <th style="width:70px">Số lượng</th>
                                            <th style="width:110px">Giá nhập</th>
                                            <th style="width:110px">Giá trả lại</th>
                                            <th style="width:120px">Thành tiền</th>
                                            <th style="width:50px"></th>
                                        </tr>
                                    </thead>
                                    <tbody id="productTableBody">
                                        <tr>
                                            <td colspan="8" class="text-center text-muted py-4">
                                                Chưa có sản phẩm. 
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <%-- Form bên phải --%>
                        <div class="col-lg-3">
                            <div class="bg-light rounded p-4">
                                <form method="post" action="${pageContext.request.contextPath}/sales-return-add">

                                    <div class="mb-3">
                                        <label class="form-label">Mã phiếu trả <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" name="srCode"
                                               placeholder="VD: SR001" required value="${param.srCode}" />
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Ngày trả <span class="text-danger">*</span></label>
                                        <input type="date" class="form-control" id="returnDate" name="returnDate" required value="${param.returnDate}" />
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Tổng tiền hoàn</label>
                                        <input type="text" class="form-control fw-bold" id="totalDisplay" readonly value="0 ₫" />
                                        <input type="hidden" name="totalRefundAmount" id="totalRefundAmount" value="0" />
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Lý do</label>
                                        <textarea class="form-control" name="description" rows="4"
                                                  placeholder="Lý do trả hàng...">${param.description}</textarea>
                                    </div>

                                    <input type="hidden" name="products" id="productsData" value="[]"/>

                                    <c:if test="${not empty generalError}">
                                        <div class="alert alert-danger py-2 small">${generalError}</div>
                                    </c:if>

                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/sales-return-list"
                                           class="btn btn-secondary flex-fill">Hủy</a>
                                        <button type="submit" class="btn btn-primary flex-fill">
                                            Tạo đơn trả
                                        </button>
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
        <c:if test="${not empty param.products}">
            <div id="sales-return-add-products-json" style="display:none">
                <c:out value="${fn:replace(param.products, '</', '&lt;/')}" escapeXml="false"/>
            </div>
        </c:if>
        <script>
            (function () {
                var ctx = '${pageContext.request.contextPath}';
                var products = [];
                var returnDateInput = document.getElementById('returnDate');
                var today = new Date().toISOString().split('T')[0];

                if (returnDateInput) {
                    returnDateInput.min = today;
                }

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
                        var qty = Number(p.quantity || 0);
                        var rp = Number(p.refundPrice || 0);
                        total += rp * qty;
                    });
                    $('#totalDisplay').val(formatCurrency(total));
                    $('#totalRefundAmount').val(total);
                    syncHidden();
                }

                function renderEmpty() {
                    $('#productTableBody').html(
                        '<tr><td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm.</td></tr>'
                    );
                }

                function rebuildTable() {
                    var $tbody = $('#productTableBody');
                    $tbody.empty();

                    if (!products.length) {
                        renderEmpty();
                        return;
                    }

                    products.forEach(function (p) {
                        var variantId = p.variantId;
                        var sku = p.code || '';
                        var name = p.name || '';
                        var unit = p.unit || '';
                        var originalPrice = Number(p.originalPrice || 0);
                        var stock = Number(p.stock || 0);
                        var qty = Number(p.quantity || 0);
                        var refundPrice = Number(p.refundPrice || originalPrice);
                        var subtotal = refundPrice * qty;

                        var row = '' +
                            '<tr data-variant-id="' + variantId + '" data-stock="' + stock + '">' +
                            '  <td><span class="me-1">' + sku + '</span></td>' +
                            '  <td>' + name + '</td>' +
                            '  <td class="text-center">' + unit + '</td>' +
                            '  <td style="width:90px">' +
                            '    <input type="number" class="form-control form-control-sm qtyInput" min="0" step="1" value="' + qty + '" max="' + stock + '" style="width:70px"/>' +
                            '  </td>' +
                            '  <td class="text-end">' + formatCurrency(originalPrice) + '</td>' +
                            '  <td style="width:120px">' +
                            '    <input type="number" class="form-control form-control-sm refundInput" min="0" step="1000" value="' + refundPrice + '" style="width:110px"/>' +
                            '  </td>' +
                            '  <td class="text-end subtotalCell">' + formatCurrency(subtotal) + '</td>' +
                            '  <td class="text-center">' +
                            '    <button type="button" class="btn btn-sm btn-danger delRow">' +
                            '      <i class="fas fa-times"></i>' +
                            '    </button>' +
                            '  </td>' +
                            '</tr>';

                        $tbody.append(row);
                    });
                }

                function addOrUpdateProduct(item) {
                    var idx = findProductIndex(item.variantId);
                    if (idx >= 0) return;
                    products.push(item);
                    rebuildTable();
                    updateTotals();
                }

                function updateRowFromProducts(variantId) {
                    var idx = findProductIndex(variantId);
                    if (idx < 0) return;
                    var p = products[idx];
                    var row = $('#productTableBody').find('tr[data-variant-id="' + variantId + '"]');
                    var originalPrice = Number(p.originalPrice || 0);
                    var qty = Number(p.quantity || 0);
                    var refundPrice = Number(p.refundPrice || 0);
                    var subtotal = refundPrice * qty;

                    row.find('.qtyInput').val(qty);
                    row.find('.refundInput').val(refundPrice);
                    row.find('.subtotalCell').text(formatCurrency(subtotal));
                }

                function loadProducts(keyword) {
                    $.ajax({
                        url: ctx + '/sales-return-add',
                        type: 'POST',
                        data: {action: 'searchProduct', search: keyword || ''},
                        dataType: 'json',
                        success: function (data) {
                            var list = Array.isArray(data) ? data : [];
                            var $dd = $('#searchDropdown');
                            $dd.empty();

                            if (!list.length) {
                                $dd.append('<div class="dropdown-item text-muted">Không tìm thấy sản phẩm</div>');
                            } else {
                                list.forEach(function (p) {
                                    var code = p.code || '';
                                    var name = p.name || '';
                                    var unit = p.unit || '';
                                    var costPrice = Number(p.costPrice || 0);
                                    var stock = Number(p.stock || 0);

                                    var html = '' +
                                        '<a href="#" class="dropdown-item">' +
                                        '  <div><strong>' + code + '</strong></div>' +
                                        '  <div class="text-muted small">' + name + '</div>' +
                                        '  <div class="text-muted small">Tồn: ' + stock + ' | Giá nhập: ' + formatCurrency(costPrice) + '</div>' +
                                        '</a>';

                                    var $a = $(html);
                                    $a.on('click', function (e) {
                                        e.preventDefault();
                                        var variantId = p.variantId;
                                        if (findProductIndex(variantId) >= 0) return;
                                        addOrUpdateProduct({
                                            variantId: variantId,
                                            code: code,
                                            name: name,
                                            unit: unit,
                                            originalPrice: costPrice,
                                            refundPrice: costPrice,
                                            quantity: 1,
                                            stock: stock
                                        });
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

                var _t;
                $('#searchProduct').on('keyup', function () {
                    clearTimeout(_t);
                    var q = $(this).val();
                    _t = setTimeout(function () {
                        loadProducts(q);
                    }, 300);
                });

                // Click/focus vào ô tìm kiếm sẽ load danh sách sản phẩm luôn.
                $('#searchProduct').on('focus', function () {
                    loadProducts($(this).val());
                });

                $(document).on('click', function (e) {
                    if (!$(e.target).closest('#searchProduct, #searchDropdown').length) {
                        $('#searchDropdown').hide();
                    }
                });

                $('#productTableBody').on('input', '.qtyInput', function () {
                    var row = $(this).closest('tr');
                    var variantId = row.data('variant-id');
                    var stock = Number(row.data('stock') || 0);
                    var qty = Number($(this).val() || 0);
                    if (qty < 0) qty = 0;
                    if (qty > stock) qty = stock;
                    $(this).val(qty);

                    var idx = findProductIndex(variantId);
                    if (idx >= 0) {
                        products[idx].quantity = qty;
                        updateRowFromProducts(variantId);
                        updateTotals();
                    }
                });

                $('#productTableBody').on('input', '.refundInput', function () {
                    var row = $(this).closest('tr');
                    var variantId = row.data('variant-id');
                    var refundPrice = Number($(this).val() || 0);
                    if (refundPrice < 0) refundPrice = 0;
                    $(this).val(refundPrice);

                    var idx = findProductIndex(variantId);
                    if (idx >= 0) {
                        products[idx].refundPrice = refundPrice;
                        updateRowFromProducts(variantId);
                        updateTotals();
                    }
                });

                $('#productTableBody').on('click', '.delRow', function () {
                    var row = $(this).closest('tr');
                    var variantId = row.data('variant-id');
                    products = products.filter(function (p) { return String(p.variantId) !== String(variantId); });
                    rebuildTable();
                    updateTotals();
                });

                function initFromJsonIfExists() {
                    var el = document.getElementById('sales-return-add-products-json');
                    if (!el) return;
                    var txt = (el.textContent || el.innerText || '').trim();
                    if (!txt) return;
                    try {
                        var arr = JSON.parse(txt);
                        if (Array.isArray(arr)) {
                            products = arr;
                        }
                    } catch (e) {
                        // ignore
                    }
                    rebuildTable();
                    updateTotals();
                }

                initFromJsonIfExists();

                $('form').on('submit', function () {
                    if (returnDateInput && returnDateInput.value && returnDateInput.value < today) {
                        alert('Ngày trả không được nhỏ hơn ngày hiện tại.');
                        return false;
                    }
                    syncHidden();
                });
            })();
        </script>
    </body>
</html>

