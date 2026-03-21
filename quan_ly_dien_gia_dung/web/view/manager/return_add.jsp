
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Tạo đơn trả hàng</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
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
            .serial-tag {
                background: #0d6efd;
                color: white;
                padding: 4px 10px;
                border-radius: 16px;
                font-size: 13px;
                display: inline-flex;
                align-items: center;
                gap: 4px;
            }
            .serial-tag .remove-tag { cursor: pointer; font-weight: bold; opacity: 0.9; }
            .serial-tag .remove-tag:hover { opacity: 1; }
            .serial-tag-input {
                border: none;
                outline: none;
                flex: 1;
                min-width: 100px;
                padding: 4px;
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
                        <div class="col-12"><h5 class="mb-1">Tạo đơn trả hàng</h5></div>

                        <%-- Bảng sản phẩm --%>
                        <div class="col-lg-9">
                            <div class="bg-light rounded p-2 mb-3">
                                <small class="text-muted">Chọn nhà cung cấp trước, sau đó tìm sản phẩm do nhà đó cung cấp.</small>
                                <div class="d-flex gap-2 mt-2">
                                    <div class="flex-grow-1 position-relative">
                                        <input type="text" id="searchProduct" class="form-control" readonly
                                               placeholder="Chọn nhà cung cấp trước">
                                        <div id="searchDropdown" class="dropdown-menu w-100 shadow" style="display:none;max-height:280px;overflow-y:auto"></div>
                                    </div>
                                    <button type="button" class="btn btn-primary" id="btnSearchProduct" disabled>Tìm kiếm</button>
                                </div>
                            </div>
                            <div class="bg-light rounded p-3">
                                <table class="table table-bordered table-sm" id="productTable">
                                    <thead>
                                        <tr>
                                            <th style="width:200px">Mã SKU</th>
                                            <th>Tên sản phẩm</th>
                                            <th style="width:80px">Đơn vị</th>
                                            <th style="width:70px">Số lượng</th>
                                            <th style="width:100px">Giá nhập</th>
                                            <th style="width:100px">Giá trả lại</th>
                                            <th style="width:100px">Thành tiền</th>
                                            <th style="width:50px"></th>
                                        </tr>
                                    </thead>
                                    <tbody id="productTableBody">
                                        <tr><td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm. Chọn NCC và tìm kiếm để thêm.</td></tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <%-- Form bên phải --%>
                        <div class="col-lg-3">
                            <div class="bg-light rounded p-4">
                                <form id="returnForm">
                                    <div class="mb-3">
                                        <label class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                                        <select class="form-select" name="supplierId" id="supplierId" required>
                                            <option value="">-- Chọn nhà cung cấp --</option>
                                            <c:forEach items="${suppliers}" var="s">
                                                <option value="${s.supplierId}" ${supplierIdValue != null && supplierIdValue == s.supplierId ? 'selected' : ''}>${s.supplierName}</option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty supplierIdError}"><small class="text-danger">${supplierIdError}</small></c:if>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Ngày trả <span class="text-danger">*</span></label>
                                        <input type="date" class="form-control" name="returnDate" id="returnDate" value="${returnDateValue}">
                                        <c:if test="${not empty returnDateError}"><small class="text-danger">${returnDateError}</small></c:if>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Mã phiếu trả <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" name="returnCode" id="returnCode" placeholder="VD: RT001" value="${returnCodeValue}" required>
                                        <c:if test="${not empty returnCodeError}"><small class="text-danger">${returnCodeError}</small></c:if>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Tổng tiền hoàn</label>
                                        <input type="text" class="form-control fw-bold" id="totalDisplay" readonly value="0 ₫">
                                        <input type="hidden" name="totalRefundAmount" id="totalRefundAmount" value="0">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Lý do</label>
                                        <textarea class="form-control" name="description" rows="2" placeholder="Lý do trả hàng...">${descriptionValue}</textarea>
                                    </div>
                                    <c:if test="${not empty productsError}"><div class="alert alert-danger py-2 small">${productsError}</div></c:if>
                                    <c:if test="${not empty generalError}"><div class="alert alert-danger py-2 small">${generalError}</div></c:if>
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/return-order-list" class="btn btn-secondary flex-fill">Hủy</a>
                                        <button type="submit" class="btn btn-primary flex-fill" id="btnSubmit">Tạo đơn trả</button>
                                    </div>
                                    <input type="hidden" name="products" id="productsData">
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- Modal chọn serial --%>
        <div class="modal fade" id="serialModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Chọn serial trả hàng</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body" id="serialModalBody"></div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" id="btnSaveSerials">Lưu</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
        (function() {
            var products = [];
            var productIdGen = 1;
            var currentSerials = [];
            var currentProductId = null;
            var ctx = '${pageContext.request.contextPath}';

            function formatCurrency(n) {
                return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n || 0);
            }

            function initDate() {
                var rd = document.getElementById('returnDate');
                if (rd && !rd.value) rd.valueAsDate = new Date();
            }

            function toggleSearch() {
                var sid = $('#supplierId').val();
                var on = sid && sid.trim() !== '';
                $('#searchProduct').prop('readonly', !on).attr('placeholder', on ? 'Mã hàng, tên SP...' : 'Chọn nhà cung cấp trước');
                $('#btnSearchProduct').prop('disabled', !on);
            }

            $('#supplierId').on('change', function() {
                toggleSearch();
                $('#searchProduct').val('');
                $('#searchDropdown').hide();
                if (products.length > 0) {
                    products = [];
                    productIdGen = 1;
                    $('#productTableBody').html('<tr><td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm. Chọn NCC và tìm kiếm để thêm.</td></tr>');
                    updateTotal();
                }
            });

            $('#searchProduct').on('focus', function() { if ($('#supplierId').val()) loadProducts($(this).val()); });
            $('#searchProduct').on('keyup', function() {
                if (!$('#supplierId').val()) return;
                clearTimeout(window._searchT);
                window._searchT = setTimeout(function() { loadProducts($('#searchProduct').val()); }, 300);
            });
            $(document).on('click', function(e) {
                if (!$(e.target).closest('#searchProduct, #searchDropdown').length) $('#searchDropdown').hide();
            });

            function loadProducts(q) {
                $.ajax({
                    url: ctx + '/return-add',
                    type: 'POST',
                    data: { action: 'searchProduct', search: q || '', supplierId: $('#supplierId').val() },
                    dataType: 'json',
                    success: function(data) {
                        var list = Array.isArray(data) ? data : [];
                        var dd = $('#searchDropdown').empty();
                        if (list.length === 0) {
                            dd.append('<div class="dropdown-item text-muted">Không tìm thấy sản phẩm</div>');
                        } else {
                            list.forEach(function(p) {
                                var code = p.code || p.sku || '';
                                var name = p.name || p.productName || '';
                                var unit = p.unit || '';
                                var price = Number(p.price || p.costPrice) || 0;
                                var $a = $('<a href="#" class="dropdown-item"></a>').html(
                                    '<strong>' + code + '</strong> - ' + name + ' <small class="text-muted">' + unit + ' | ' + formatCurrency(price) + '</small>'
                                );
                                $a.on('click', function(ev) {
                                    ev.preventDefault();
                                    if (products.some(function(x) { return x.variantId === p.variantId; })) return;
                                    var item = { id: productIdGen++, variantId: p.variantId, code: code, name: name, unit: unit,
                                        originalPrice: price, refundPrice: price, quantity: 0, serials: [] };
                                    products.push(item);
                                    $('#productTableBody tr:has(td[colspan])').remove();
                                    var row = '<tr data-pid="' + item.id + '">' +
                                        '<td><span class="me-1">' + code + '</span><button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-pid="' + item.id + '"><i class="fas fa-barcode me-1"></i>Chọn serial</button></td>' +
                                        '<td>' + name + '</td><td>' + unit + '</td>' +
                                        '<td><input type="number" class="form-control form-control-sm qty-cell" readonly value="0" data-pid="' + item.id + '" style="width:55px"></td>' +
                                        '<td class="text-end">' + formatCurrency(price) + '</td>' +
                                        '<td><input type="number" class="form-control form-control-sm refund-cell" min="0" step="1000" value="' + price + '" data-pid="' + item.id + '" style="width:85px"></td>' +
                                        '<td class="text-end subtotal-cell" data-pid="' + item.id + '">0 ₫</td>' +
                                        '<td><button type="button" class="btn btn-sm btn-danger del-btn" data-pid="' + item.id + '"><i class="fas fa-times"></i></button></td></tr>';
                                    $('#productTableBody').append(row);
                                    $('#searchProduct').val('');
                                    dd.hide();
                                    updateTotal();
                                });
                                dd.append($a);
                            });
                        }
                        dd.show();
                    }
                });
            }

            $('#btnSearchProduct').on('click', function() {
                if (!$('#supplierId').val()) { alert('Chọn nhà cung cấp trước.'); return; }
                loadProducts('');
            });

            $(document).on('input', '.refund-cell', function() {
                var id = $(this).data('pid');
                var p = products.find(function(x) { return x.id === id; });
                if (p) { p.refundPrice = parseFloat($(this).val()) || 0; updateTotal(); }
            });

            $(document).on('click', '.del-btn', function() {
                var id = $(this).data('pid');
                products = products.filter(function(x) { return x.id !== id; });
                $('tr[data-pid="' + id + '"]').remove();
                if (products.length === 0) $('#productTableBody').html('<tr><td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm. Chọn NCC và tìm kiếm để thêm.</td></tr>');
                updateTotal();
            });

            $(document).on('click', '.serial-btn', function() {
                var id = $(this).data('pid');
                var p = products.find(function(x) { return x.id === id; });
                if (!p) return;
                currentProductId = id;
                currentSerials = (p.serials || []).slice();
                renderSerialModal(p);
                new bootstrap.Modal(document.getElementById('serialModal')).show();
            });

            function renderSerialModal(product) {
                var tags = '';
                currentSerials.forEach(function(s, i) {
                    var sn = (s && typeof s === 'object' && s.serialNumber) ? s.serialNumber : String(s);
                    tags += '<span class="serial-tag">' + sn + ' <span class="remove-tag" data-i="' + i + '">×</span></span>';
                });
                var html = '<div class="serial-tag-container" id="serialTagContainer">' + tags +
                    '<input type="text" class="serial-tag-input" id="serialTagInput" placeholder="Nhập serial rồi Enter">' +
                    '</div><small id="serialErr" class="text-danger" style="display:none"></small>' +
                    '<div class="mt-2"><button type="button" class="btn btn-sm btn-outline-secondary" id="btnLoadSerials"><i class="fa fa-list me-1"></i>Load serial trong kho</button></div>';

                $('#serialModalBody').html(html);

                $('#serialTagInput').on('keypress', function(e) {
                    if (e.key === 'Enter') { e.preventDefault(); addSerialManual(); }
                });

                $('#serialModalBody').on('click', '.remove-tag', function() {
                    var i = parseInt($(this).data('i'), 10);
                    currentSerials.splice(i, 1);
                    renderSerialModal(products.find(function(x) { return x.id === currentProductId; }));
                });

                $('#btnLoadSerials').on('click', function() {
                    if (!product || !product.variantId) return;
                    $.ajax({
                        url: ctx + '/return-add',
                        type: 'POST',
                        data: { action: 'getSerials', variantId: product.variantId },
                        dataType: 'json',
                        success: function(data) {
                            if (Array.isArray(data) && data.length > 0) {
                                data.forEach(function(s) {
                                    var exists = currentSerials.some(function(x) {
                                        var xn = (x && typeof x === 'object') ? x.serialNumber : x;
                                        return xn === s.serialNumber;
                                    });
                                    if (!exists) currentSerials.push({ serialId: s.serialId, serialNumber: s.serialNumber });
                                });
                                renderSerialModal(products.find(function(x) { return x.id === currentProductId; }));
                            }
                        }
                    });
                });
            }

            function addSerialManual(onDone) {
                var val = $('#serialTagInput').val().trim();
                if (!val) { if (onDone) onDone(); return; }
                var p = products.find(function(x) { return x.id === currentProductId; });
                if (!p || !p.variantId) { if (onDone) onDone(); return; }
                var exists = currentSerials.some(function(s) {
                    var xn = (s && typeof s === 'object') ? s.serialNumber : s;
                    return xn === val;
                });
                if (exists) { $('#serialErr').text('Serial đã có').show(); if (onDone) onDone(); return; }
                $('#serialErr').hide();
                $.ajax({
                    url: ctx + '/return-add',
                    type: 'POST',
                    data: { action: 'checkSerial', variantId: p.variantId, serialNumber: val },
                    dataType: 'json',
                    success: function(res) {
                        if (res && res.valid && res.serialId) {
                            currentSerials.push({ serialId: res.serialId, serialNumber: val });
                            $('#serialTagInput').val('');
                            renderSerialModal(p);
                        } else {
                            $('#serialErr').text(res && res.message ? res.message : 'Serial không hợp lệ').show();
                        }
                        if (onDone) onDone();
                    },
                    error: function() { $('#serialErr').text('Lỗi kiểm tra').show(); if (onDone) onDone(); }
                });
            }

            $('#btnSaveSerials').on('click', function() {
                var val = $('#serialTagInput').val().trim();
                if (val) {
                    addSerialManual(function() { doSaveSerials(); });
                } else {
                    doSaveSerials();
                }
            });

            function doSaveSerials() {
                var p = products.find(function(x) { return x.id === currentProductId; });
                if (p) {
                    p.serials = currentSerials.map(function(s) {
                        return (s && typeof s === 'object') ? s : { serialNumber: String(s) };
                    });
                    p.quantity = p.serials.length;
                    $('.qty-cell[data-pid="' + currentProductId + '"]').val(p.quantity);
                    updateTotal();
                }
                bootstrap.Modal.getInstance(document.getElementById('serialModal')).hide();
            }

            function updateTotal() {
                var total = 0;
                products.forEach(function(p) {
                    var q = p.quantity || 0, pr = p.refundPrice || 0;
                    var sub = q * pr;
                    total += sub;
                    $('.subtotal-cell[data-pid="' + p.id + '"]').text(formatCurrency(sub));
                });
                $('#totalDisplay').val(formatCurrency(total));
                $('#totalRefundAmount').val(total);
            }

            $('#returnForm').on('submit', function(e) {
                e.preventDefault();
                if (products.length === 0) {
                    alert('Vui lòng thêm ít nhất một sản phẩm.');
                    return false;
                }
                var missing = products.filter(function(p) { return (p.quantity || 0) === 0; });
                if (missing.length > 0) {
                    alert('Vui lòng chọn serial cho tất cả sản phẩm.');
                    return false;
                }

                var payload = products.map(function(p) {
                    var serials = p.serials || [];
                    var ids = serials.filter(function(s) { return s && (s.serialId != null && s.serialId !== undefined); }).map(function(s) { return s.serialId; });
                    var nums = serials.filter(function(s) { return s && s.serialNumber && (s.serialId == null || s.serialId === undefined); }).map(function(s) { return s.serialNumber; });
                    return {
                        variantId: p.variantId,
                        quantity: p.quantity,
                        originalPrice: p.originalPrice || 0,
                        refundPrice: p.refundPrice || 0,
                        serialIds: ids,
                        serialNumbers: nums
                    };
                });

                $('#productsData').val(JSON.stringify(payload));
                var $btn = $('#btnSubmit');
                $btn.prop('disabled', true);

                $.ajax({
                    url: ctx + '/return-add',
                    type: 'POST',
                    data: $('#returnForm').serialize(),
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                    success: function(html) {
                        if (typeof html === 'string' && html.indexOf('id="returnForm"') !== -1) {
                            document.open();
                            document.write(html);
                            document.close();
                        } else {
                            window.location = ctx + '/return-order-list';
                        }
                    },
                    error: function(xhr) {
                        $btn.prop('disabled', false);
                        if (xhr.status === 200 && xhr.responseText) {
                            document.open();
                            document.write(xhr.responseText);
                            document.close();
                        } else {
                            alert('Có lỗi xảy ra. Vui lòng thử lại.');
                        }
                    }
                });
                return false;
            });

            $(function() {
                initDate();
                toggleSearch();
            });
        })();
        </script>
    </body>
</html>
