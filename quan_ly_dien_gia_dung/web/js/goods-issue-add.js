/**
 * goods-issue-add.js
 * Script cho trang tạo phiếu xuất kho.
 * Cần có window.GOODS_ISSUE_ADD từ JSP: { contextPath, addUrl }
 */
(function() {
    var config = window.GOODS_ISSUE_ADD || {};
    var contextPath = config.contextPath || '';
    var addUrl = config.addUrl || (contextPath + '/goods-issue-add');

    var products = [];
    var productIdCounter = 1;
    var searchTimeout;
    var currentProductId = -1;
    var availableSerials = [];

    // ── Search dropdown ─────────────────────────────────────────────
    function loadProducts(searchValue) {
        $.ajax({
            url: addUrl, type: 'POST',
            data: { action: 'searchProduct', search: searchValue },
            dataType: 'json',
            success: function(data) { showDropdown(data); },
            error: function() {
                $('#searchDropdown').empty()
                    .append('<div class="dropdown-item text-danger">Lỗi khi tải dữ liệu</div>').show();
            }
        });
    }

    function showDropdown(productList) {
        var dropdown = $('#searchDropdown');
        dropdown.empty();
        if (!productList.length) {
            dropdown.append('<div class="dropdown-item text-muted">Không tìm thấy sản phẩm</div>');
        } else {
            productList.forEach(function(p) {
                var item = $(
                    '<a href="#" class="dropdown-item">' +
                    '<div><strong>' + p.code + '</strong> - ' + p.name + '</div>' +
                    '<small class="text-muted">' + p.unit + ' - Tồn kho: ' + p.stock + '</small>' +
                    '</a>'
                );
                item.on('click', function(e) {
                    e.preventDefault();
                    addProductRow({ id: productIdCounter++, variantId: p.variantId, code: p.code, name: p.name, unit: p.unit, stock: p.stock, quantity: 0, serials: [] });
                    $('#searchProduct').val('');
                    dropdown.hide();
                });
                dropdown.append(item);
            });
        }
        dropdown.show();
    }

    function searchProduct() { loadProducts(''); }
    window.searchProduct = searchProduct;

    // ── Table rows ───────────────────────────────────────────────────
    function buildRow(product) {
        return (
            '<tr data-id="' + product.id + '">' +
            '<td>' + product.id + '</td>' +
            '<td>' + product.code +
            '<div class="mt-1"><button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-id="' + product.id + '">' +
            '<i class="fas fa-barcode me-1"></i>Chọn serial</button></div></td>' +
            '<td>' + product.name + '</td>' +
            '<td>' + product.unit + '</td>' +
            '<td>Tồn: ' + product.stock + '</td>' +
            '<td><input type="number" class="form-control form-control-sm quantity-display" value="' + (product.quantity || 0) + '" readonly data-id="' + product.id + '"></td>' +
            '<td class="text-center"><button type="button" class="btn btn-sm btn-danger delete-btn" data-id="' + product.id + '"><i class="fas fa-times"></i></button></td>' +
            '</tr>'
        );
    }

    function addProductRowFromData(product) {
        if ($('#productTableBody tr td[colspan]').length) $('#productTableBody').empty();
        $('#productTableBody').append(buildRow(product));
    }

    function addProductRow(product) {
        if (products.find(function(p) { return p.variantId === product.variantId; })) return;
        if ($('#productTableBody tr td[colspan]').length) $('#productTableBody').empty();
        products.push(product);
        $('#productTableBody').append(buildRow(product));
    }

    $(document).on('click', '.delete-btn', function() {
        var id = $(this).data('id');
        products = products.filter(function(p) { return p.id !== id; });
        $('tr[data-id="' + id + '"]').remove();
        if (!products.length) {
            $('#productTableBody').html('<tr><td colspan="7" class="text-center text-muted">Chưa có sản phẩm nào. Tìm kiếm để thêm sản phẩm.</td></tr>');
        }
    });

    // ── Serial modal ─────────────────────────────────────────────────
    function renderSerialCheckboxes(serials, selectedSerials) {
        if (!serials.length) {
            $('#serialListContent').html('<p class="text-muted p-3">Không có serial in_stock nào</p>');
            return;
        }
        var html = '';
        serials.forEach(function(s) {
            var checked = selectedSerials.indexOf(s.serialNumber) !== -1 ? 'checked' : '';
            html += (
                '<div class="form-check border-bottom py-2 px-3">' +
                '<input class="form-check-input serial-checkbox" type="checkbox" value="' + s.serialNumber + '" id="sc_' + s.serialId + '" ' + checked + '>' +
                '<label class="form-check-label" for="sc_' + s.serialId + '">' + s.serialNumber + '</label>' +
                '</div>'
            );
        });
        $('#serialListContent').html(html);
        updateSelectedCount();
    }

    function updateSelectedCount() {
        $('#selectedSerialCount').text($('.serial-checkbox:checked').length);
    }

    $(document).on('change', '.serial-checkbox', updateSelectedCount);

    $(document).on('input', '#serialSearchInput', function() {
        var kw = $(this).val().toLowerCase();
        var product = products.find(function(p) { return p.id === currentProductId; });
        var selectedSerials = product ? product.serials : [];
        var filtered = availableSerials.filter(function(s) { return s.serialNumber.toLowerCase().indexOf(kw) !== -1; });
        renderSerialCheckboxes(filtered, selectedSerials);
    });

    $(document).on('click', '.serial-btn', function() {
        currentProductId = $(this).data('id');
        var product = products.find(function(p) { return p.id === currentProductId; });
        if (!product) return;

        $('#serialModalTitle').text('Chọn serial: ' + product.name + ' (Tồn: ' + product.stock + ')');
        $('#serialSearchInput').val('');
        $('#serialListContent').html('<p class="text-center p-3"><i class="fas fa-spinner fa-spin"></i></p>');
        $('#selectedSerialCount').text(0);

        var modal = new bootstrap.Modal(document.getElementById('serialModal'));
        modal.show();

        $.ajax({
            url: addUrl, type: 'POST',
            data: { action: 'getSerials', variantId: product.variantId },
            dataType: 'json',
            success: function(serials) {
                availableSerials = serials;
                renderSerialCheckboxes(serials, product.serials || []);
            },
            error: function() {
                $('#serialListContent').html('<p class="text-danger p-3">Lỗi khi tải danh sách serial</p>');
            }
        });
    });

    $(document).on('click', '#saveSerials', function() {
        var selected = [];
        $('.serial-checkbox:checked').each(function() { selected.push($(this).val()); });
        var product = products.find(function(p) { return p.id === currentProductId; });
        if (product) {
            product.serials = selected;
            product.quantity = selected.length;
            $('.quantity-display[data-id="' + product.id + '"]').val(product.quantity);
        }
        bootstrap.Modal.getInstance(document.getElementById('serialModal')).hide();
    });

    // ── Import Excel ─────────────────────────────────────────────────
    function processExcelData(jsonData) {
        var successCount = 0;
        var errors = [];

        jsonData.forEach(function(row, index) {
            var sku = (row['SKU'] || row['sku'] || '').toString().trim();
            var serialsStr = (row['Serial Numbers'] || row['serial numbers'] || row['serials'] || '').toString();

            if (!sku) {
                errors.push('Dòng ' + (index + 2) + ': Thiếu mã SKU');
                return;
            }

            var serials = serialsStr.split(',').map(function(s) { return s.trim(); }).filter(function(s) { return s.length > 0; });

            if (!serials.length) {
                errors.push('Dòng ' + (index + 2) + ': Thiếu serial number');
                return;
            }

            var product = null;
            $.ajax({
                url: addUrl, type: 'POST',
                data: { action: 'searchProductBySKU', sku: sku },
                dataType: 'json',
                async: false,
                success: function(data) { if (data && data.variantId) product = data; }
            });

            if (!product) {
                errors.push('Dòng ' + (index + 2) + ': Không tìm thấy sản phẩm với SKU "' + sku + '" hoặc hết tồn kho');
                return;
            }

            var availableForProduct = [];
            $.ajax({
                url: addUrl, type: 'POST',
                data: { action: 'getSerials', variantId: product.variantId },
                dataType: 'json',
                async: false,
                success: function(data) { availableForProduct = data || []; }
            });

            var availableNumbers = availableForProduct.map(function(s) { return s.serialNumber; });
            var validSerials = [];
            var invalidSerials = [];

            serials.forEach(function(sn) {
                if (availableNumbers.indexOf(sn) !== -1) {
                    validSerials.push(sn);
                } else {
                    invalidSerials.push(sn);
                }
            });

            if (invalidSerials.length) {
                errors.push('Dòng ' + (index + 2) + ' (' + sku + '): Serial không tồn tại trong kho: ' + invalidSerials.join(', '));
            }

            if (!validSerials.length) return;

            var existing = products.find(function(p) { return p.variantId === product.variantId; });
            if (existing) {
                var added = 0;
                validSerials.forEach(function(sn) {
                    if (existing.serials.indexOf(sn) === -1) {
                        existing.serials.push(sn);
                        added++;
                    }
                });
                if (added) {
                    existing.quantity = existing.serials.length;
                    $('.quantity-display[data-id="' + existing.id + '"]').val(existing.quantity);
                    successCount++;
                }
            } else {
                var newProduct = {
                    id: productIdCounter++,
                    variantId: product.variantId,
                    code: product.code,
                    name: product.name,
                    unit: product.unit,
                    stock: product.stock,
                    quantity: validSerials.length,
                    serials: validSerials
                };
                products.push(newProduct);
                addProductRowFromData(newProduct);
                successCount++;
            }
        });

        if (successCount > 0 && errors.length === 0) {
            $('#importSuccess').text('Import thành công ' + successCount + ' sản phẩm!').show();
            setTimeout(function() {
                var modalEl = document.getElementById('importModal');
                if (modalEl && bootstrap.Modal.getInstance(modalEl)) {
                    bootstrap.Modal.getInstance(modalEl).hide();
                }
            }, 2000);
        } else if (successCount > 0) {
            $('#importSuccess').text('Import thành công ' + successCount + ' sản phẩm').show();
            $('#importError').html(errors.join('<br>')).show();
        } else if (errors.length) {
            $('#importError').html(errors.join('<br>')).show();
        } else {
            $('#importError').text('File Excel không có dữ liệu hợp lệ!').show();
        }
    }

    $(document).on('click', '#importFileBtn', function() {
        $('#importError').hide();
        $('#importSuccess').hide();
        $('#excelFile').val('');
        new bootstrap.Modal(document.getElementById('importModal')).show();
    });

    $(document).on('click', '#processImport', function() {
        var file = document.getElementById('excelFile').files[0];
        if (!file) {
            $('#importError').text('Vui lòng chọn file Excel!').show();
            return;
        }
        $('#importError').hide();
        $('#importSuccess').hide();

        var reader = new FileReader();
        reader.onload = function(e) {
            try {
                var data = new Uint8Array(e.target.result);
                var workbook = XLSX.read(data, { type: 'array' });
                var jsonData = XLSX.utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]]);
                if (!jsonData.length) {
                    $('#importError').text('File Excel không có dữ liệu!').show();
                    return;
                }
                processExcelData(jsonData);
            } catch (err) {
                $('#importError').text('Lỗi khi đọc file: ' + err.message).show();
            }
        };
        reader.readAsArrayBuffer(file);
    });

    // ── Form submit ──────────────────────────────────────────────────
    $('#issueForm').on('submit', function(e) {
        if (!products.length) {
            e.preventDefault(); alert('Vui lòng thêm ít nhất một sản phẩm!'); return false;
        }
        var noSerial = products.filter(function(p) { return !p.serials || !p.serials.length; });
        if (noSerial.length) {
            e.preventDefault();
            alert('Vui lòng chọn serial cho: ' + noSerial.map(function(p) { return p.name; }).join(', '));
            return false;
        }
        $('#productsData').val(JSON.stringify(products));
    });

    // ── Search bindings ──────────────────────────────────────────────
    $('#searchProduct').on('focus', function() { loadProducts(''); });
    $('#searchProduct').on('keyup', function() {
        clearTimeout(searchTimeout);
        var v = $(this).val().trim();
        searchTimeout = setTimeout(function() { loadProducts(v); }, 300);
    });
    $(document).on('click', function(e) {
        if (!$(e.target).closest('#searchProduct, #searchDropdown').length) $('#searchDropdown').hide();
    });

    // ── Init date ────────────────────────────────────────────────────
    var dateEl = document.getElementById('issueDate');
    if (dateEl && !dateEl.value) { dateEl.valueAsDate = new Date(); }

    // ── Restore from server (validation error) ───────────────────────
    var jsonEl = document.getElementById('goods-issue-add-products-json');
    var initialStr = (jsonEl && jsonEl.textContent) ? jsonEl.textContent.replace(/&lt;\//g, '</') : null;
    if (initialStr) {
        try {
            $('#productTableBody').empty();
            var restoredProducts = JSON.parse(initialStr);
            products = [];
            productIdCounter = 1;
            restoredProducts.forEach(function(p) {
                products.push(p);
                addProductRowFromData(p);
                if (p.id >= productIdCounter) productIdCounter = p.id + 1;
            });
        } catch (e) {}
    }
})();
