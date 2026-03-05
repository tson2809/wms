(function() {
    const config = window.GOODS_RECEIPT_EDIT || {};
    const contextPath = config.contextPath || '';
    const editUrl = config.editUrl || (contextPath + '/goods-receipt-edit');
    const readOnly = !!config.readOnly;
    const isManager = !!config.isManager || readOnly;
    const initialProductsJson = config.productsJson || null;

    let products = [];
    let productIdCounter = 1;
    let searchTimeout;
    let currentSerials = [];

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    function loadProducts(searchValue) {
        $.ajax({
            url: editUrl,
            type: 'POST',
            data: {
                action: 'searchProduct',
                search: searchValue
            },
            dataType: 'json',
            success: function(data) {
                showDropdown(data);
            },
            error: function() {
                const dropdown = $('#searchDropdown');
                dropdown.empty();
                dropdown.append('<div class="dropdown-item text-danger">Lỗi khi tải dữ liệu</div>');
                dropdown.show();
            }
        });
    }

    function showDropdown(productList) {
        const dropdown = $('#searchDropdown');
        dropdown.empty();

        if (productList.length === 0) {
            dropdown.append('<div class="dropdown-item text-muted">Không tìm thấy sản phẩm</div>');
        } else {
            productList.forEach(function(product) {
                const item = $(
                    '<a href="#" class="dropdown-item">' +
                    '<div><strong>' + product.code + '</strong> - ' + product.name + '</div>' +
                    '<small class="text-muted">' + product.unit + ' - ' + formatCurrency(product.price) + '</small>' +
                    '</a>'
                );

                item.on('click', function(e) {
                    e.preventDefault();
                    addProductRow({
                        id: productIdCounter++,
                        variantId: product.variantId,
                        code: product.code,
                        name: product.name,
                        unit: product.unit,
                        price: product.price,
                        quantity: 0
                    });
                    $('#searchProduct').val('');
                    dropdown.hide();
                });

                dropdown.append(item);
            });
        }

        dropdown.show();
    }

    function searchProduct() {
        loadProducts('');
    }

    window.searchProduct = searchProduct;

    function addProductRowFromData(product) {
        const emptyRow = $('#productTableBody tr td[colspan]');
        if (emptyRow.length) {
            $('#productTableBody').empty();
        }

        const serialBtnText = isManager ? 'Xem serial' : 'Nhập serial';
        const deleteButtonHtml = isManager ? '' : (
            '<td class="text-center">' +
            '<button type="button" class="btn btn-sm btn-danger delete-btn" data-id="' + product.id + '">' +
            '<i class="fas fa-times"></i></button></td>'
        );

        const row = (
            '<tr data-id="' + product.id + '">' +
            '<td>' + product.id + '</td>' +
            '<td>' + product.code +
            '<div class="mt-1">' +
            '<button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-id="' + product.id + '">' +
            '<i class="fas fa-barcode me-1"></i>' + serialBtnText + '</button></div></td>' +
            '<td>' + product.name + '</td>' +
            '<td>' + product.unit + '</td>' +
            '<td class="text-end">' + formatCurrency(product.price) + '</td>' +
            '<td><input type="number" class="form-control form-control-sm quantity-display" value="' + (product.quantity || 0) + '" readonly data-id="' + product.id + '"></td>' +
            deleteButtonHtml +
            '</tr>'
        );

        $('#productTableBody').append(row);
    }

    function addProductRow(product) {
        const existingProduct = products.find(function(p) { return p.variantId === product.variantId; });
        if (existingProduct) {
            return;
        }

        const emptyRow = $('#productTableBody tr td[colspan]');
        if (emptyRow.length) {
            $('#productTableBody').empty();
        }

        products.push(product);

        const serialBtnText = isManager ? 'Xem serial' : 'Nhập serial';
        const deleteButtonHtml = isManager ? '' : (
            '<td class="text-center">' +
            '<button type="button" class="btn btn-sm btn-danger delete-btn" data-id="' + product.id + '">' +
            '<i class="fas fa-times"></i></button></td>'
        );

        const row = (
            '<tr data-id="' + product.id + '">' +
            '<td>' + product.id + '</td>' +
            '<td>' + product.code +
            '<div class="mt-1">' +
            '<button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-id="' + product.id + '">' +
            '<i class="fas fa-barcode me-1"></i>' + serialBtnText + '</button></div></td>' +
            '<td>' + product.name + '</td>' +
            '<td>' + product.unit + '</td>' +
            '<td class="text-end">' + formatCurrency(product.price) + '</td>' +
            '<td><input type="number" class="form-control form-control-sm quantity-display" value="0" readonly data-id="' + product.id + '"></td>' +
            deleteButtonHtml +
            '</tr>'
        );

        $('#productTableBody').append(row);
        updateTotal();
    }

    function updateTotal() {
        const total = products.reduce(function(sum, p) { return sum + (p.price * p.quantity); }, 0);
        $('#totalAmount').val(formatCurrency(total));
        $('#totalAmountValue').val(total);
    }

    function renderSerialTags() {
        var tagsHtml = '';
        currentSerials.forEach(function(serial, index) {
            var removeBtn = isManager ? '' : '<span class="remove-tag" data-index="' + index + '">×</span>';
            tagsHtml += '<span class="tag">' + serial + ' ' + removeBtn + '</span>';
        });

        var inputHtml = isManager ? '' : '<input type="text" class="tag-input" id="serialTagInput" placeholder="Nhập serial number">';

        var html = (
            '<div class="tag-input-container" id="serialTagContainer">' +
            tagsHtml + inputHtml +
            '</div>' +
            '<small id="serialError" class="text-danger" style="display: none;"></small>'
        );

        $('#serialListContent').html(html);

        if (!isManager) {
            $(document).off('click', '.remove-tag').on('click', '.remove-tag', function() {
                var idx = parseInt($(this).data('index'), 10);
                currentSerials.splice(idx, 1);
                renderSerialTags();
                updateProductQuantity();
            });

            $('#serialTagInput').on('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    addSerialTag();
                    updateProductQuantity();
                }
            });

            $('#serialTagInput').on('input', function() {
                $('#serialError').hide();
            });
        } else {
            $(document).off('click', '.remove-tag');
        }
    }

    function addSerialTag() {
        var input = $('#serialTagInput');
        var value = input.val().trim();
        var errorDiv = $('#serialError');

        if (!value) return;

        if (value.length < 3) {
            errorDiv.text('Serial number phải có ít nhất 3 ký tự!').show();
            input.focus();
            return;
        }

        if (value.length > 10) {
            errorDiv.text('Serial number không được quá 10 ký tự!').show();
            input.focus();
            return;
        }

        if (currentSerials.indexOf(value) !== -1) {
            errorDiv.text('Serial number đã tồn tại trong danh sách này!').show();
            input.focus();
            return;
        }

        var duplicateInOtherProduct = false;
        products.forEach(function(p) {
            if (p.serials && p.serials.indexOf(value) !== -1) {
                duplicateInOtherProduct = true;
            }
        });

        if (duplicateInOtherProduct) {
            errorDiv.text('Serial number đã được sử dụng cho sản phẩm khác!').show();
            input.focus();
            return;
        }

        $.ajax({
            url: editUrl,
            type: 'POST',
            data: {
                action: 'checkSerial',
                serial: value
            },
            async: false,
            success: function(response) {
                if (response.exists) {
                    errorDiv.text('Serial number đã tồn tại trong hệ thống!').show();
                    input.focus();
                } else {
                    currentSerials.push(value);
                    renderSerialTags();
                    $('#serialTagInput').focus();
                }
            },
            error: function() {
                errorDiv.text('Lỗi khi kiểm tra serial number!').show();
                input.focus();
            }
        });
    }

    function updateProductQuantity() {
        var productId = $('#serialModal').data('productId');
        var product = products.find(function(p) { return p.id === productId; });

        if (!product) return;

        product.serials = currentSerials.slice();
        product.quantity = product.serials.length;

        $('.quantity-display[data-id="' + productId + '"]').val(product.quantity);
        updateTotal();
    }

    function processExcelData(jsonData) {
        var successCount = 0;
        var errors = [];
        var allDuplicateSerials = [];
        var processedInFile = {};

        jsonData.forEach(function(row, index) {
            var sku = (row['SKU'] || row['sku'] || '').toString().trim();
            var quantity = parseInt(row['Quantity'] || row['quantity'], 10) || 0;
            var serialsStr = (row['Serial Numbers'] || row['serial numbers'] || row['serials'] || '').toString();

            if (!sku) {
                errors.push('Dòng ' + (index + 2) + ': Thiếu mã SKU');
                return;
            }

            if (quantity <= 0) {
                errors.push('Dòng ' + (index + 2) + ': Số lượng phải lớn hơn 0');
                return;
            }

            var serials = serialsStr.split(',').map(function(s) { return s.trim(); }).filter(function(s) { return s.length > 0; });

            if (serials.length !== quantity) {
                errors.push('Dòng ' + (index + 2) + ': Số lượng serial (' + serials.length + ') không khớp với quantity (' + quantity + ')');
                return;
            }

            var rowKey = sku + '-' + serials.slice().sort().join(',');
            if (processedInFile[rowKey]) {
                errors.push('Dòng ' + (index + 2) + ': Dữ liệu trùng với dòng ' + processedInFile[rowKey]);
                return;
            }
            processedInFile[rowKey] = index + 2;

            $.ajax({
                url: editUrl,
                type: 'POST',
                data: {
                    action: 'searchProductBySKU',
                    sku: sku
                },
                dataType: 'json',
                async: false,
                success: function(product) {
                    if (product && product.variantId) {
                        var existing = products.find(function(p) { return p.variantId === product.variantId; });

                        if (existing) {
                            var addedCount = 0;
                            var duplicateSerials = [];

                            serials.forEach(function(serial) {
                                if (existing.serials.indexOf(serial) !== -1) return;

                                var serialExists = false;
                                $.ajax({
                                    url: editUrl,
                                    type: 'POST',
                                    data: { action: 'checkSerial', serial: serial },
                                    dataType: 'json',
                                    async: false,
                                    success: function(response) {
                                        if (response.exists === true) {
                                            serialExists = true;
                                            duplicateSerials.push(serial);
                                        }
                                    }
                                });

                                if (!serialExists) {
                                    existing.serials.push(serial);
                                    addedCount++;
                                }
                            });

                            if (duplicateSerials.length > 0) {
                                allDuplicateSerials.push.apply(allDuplicateSerials, duplicateSerials);
                            }

                            if (addedCount > 0) {
                                existing.quantity = existing.serials.length;
                                var row = $('tr[data-id="' + existing.id + '"]');
                                row.find('.quantity-display').val(existing.quantity);
                                successCount++;
                            } else if (duplicateSerials.length === 0 && serials.length > 0) {
                                successCount++;
                            }
                        } else {
                            var validSerials = [];
                            var duplicateSerials = [];

                            serials.forEach(function(serial) {
                                var serialExists = false;
                                $.ajax({
                                    url: editUrl,
                                    type: 'POST',
                                    data: { action: 'checkSerial', serial: serial },
                                    dataType: 'json',
                                    async: false,
                                    success: function(response) {
                                        if (response.exists === true) {
                                            serialExists = true;
                                            duplicateSerials.push(serial);
                                        }
                                    }
                                });

                                if (!serialExists) {
                                    validSerials.push(serial);
                                }
                            });

                            if (duplicateSerials.length > 0) {
                                allDuplicateSerials.push.apply(allDuplicateSerials, duplicateSerials);
                            }

                            if (validSerials.length > 0) {
                                var newProduct = {
                                    id: productIdCounter++,
                                    variantId: product.variantId,
                                    code: product.code,
                                    name: product.name,
                                    unit: product.unit,
                                    price: product.price,
                                    quantity: validSerials.length,
                                    serials: validSerials
                                };

                                products.push(newProduct);
                                addProductRowFromData(newProduct);
                                successCount++;
                            }
                        }
                    } else {
                        errors.push('Dòng ' + (index + 2) + ': Không tìm thấy sản phẩm với SKU: ' + sku);
                    }
                },
                error: function() {
                    errors.push('Dòng ' + (index + 2) + ': Lỗi khi tìm sản phẩm ' + sku);
                }
            });
        });

        updateTotal();

        if (allDuplicateSerials.length > 0) {
            errors.unshift('Serial đã tồn tại: ' + allDuplicateSerials.join(', '));
        }

        if (successCount > 0 && errors.length === 0) {
            $('#importSuccess').text('Import thành công ' + successCount + ' sản phẩm!').show();
            setTimeout(function() {
                var modalEl = document.getElementById('importModal');
                if (modalEl && bootstrap.Modal.getInstance(modalEl)) {
                    bootstrap.Modal.getInstance(modalEl).hide();
                }
            }, 2000);
        } else if (successCount > 0 && errors.length > 0) {
            $('#importSuccess').text('Import thành công ' + successCount + ' sản phẩm').show();
            $('#importError').html(errors.join('<br>')).show();
        } else if (errors.length > 0) {
            $('#importError').html(errors.join('<br>')).show();
        } else {
            $('#importError').text('File Excel không có dữ liệu hợp lệ!').show();
        }
    }

    // Init: load existing products from server (from hidden div to avoid escaping issues)
    var jsonEl = document.getElementById('goods-receipt-edit-products-json');
    var initialStr = (jsonEl && jsonEl.textContent) ? jsonEl.textContent.replace(/&lt;\//g, '</') : (initialProductsJson || null);
    if (initialStr) {
        try {
            var restoredProducts = JSON.parse(initialStr);
            products = [];
            productIdCounter = 1;
            restoredProducts.forEach(function(product) {
                products.push(product);
                addProductRowFromData(product);
                if (product.id >= productIdCounter) {
                    productIdCounter = product.id + 1;
                }
            });
            updateTotal();
        } catch (e) {
            // ignore parse error
        }
    }

    // Search dropdown
    $('#searchProduct').on('focus', function() {
        loadProducts('');
    });

    $('#searchProduct').on('keyup', function() {
        clearTimeout(searchTimeout);
        var searchValue = $(this).val().trim();
        searchTimeout = setTimeout(function() {
            loadProducts(searchValue);
        }, 300);
    });

    $(document).on('click', function(e) {
        if (!$(e.target).closest('#searchProduct, #searchDropdown').length) {
            $('#searchDropdown').hide();
        }
    });

    $(document).on('click', '.delete-btn', function() {
        var id = $(this).data('id');
        products = products.filter(function(p) { return p.id !== id; });
        $('tr[data-id="' + id + '"]').remove();

        if (products.length === 0) {
            var colspan = isManager ? 6 : 7;
            $('#productTableBody').html(
                '<tr><td colspan="' + colspan + '" class="text-center text-muted">Chưa có sản phẩm nào. Tìm kiếm để thêm sản phẩm.</td></tr>'
            );
        }

        updateTotal();
    });

    $(document).on('click', '.serial-btn', function() {
        var productId = $(this).data('id');
        var product = products.find(function(p) { return p.id === productId; });

        if (!product) return;

        $('#serialModal').data('productId', productId);

        if (!product.serials) {
            product.serials = [];
        }
        currentSerials = product.serials.slice();

        var modal = new bootstrap.Modal(document.getElementById('serialModal'));
        modal.show();
        renderSerialTags();
    });

    $(document).on('click', '#saveSerials', function() {
        var currentInput = $('#serialTagInput').val().trim();
        if (currentInput) {
            addSerialTag();
        }
        updateProductQuantity();
    });

    $('#receiptForm').on('submit', function(e) {
        if (products.length === 0) {
            e.preventDefault();
            alert('Vui lòng thêm ít nhất một sản phẩm!');
            return false;
        }

        var zeroQuantityProducts = products.filter(function(p) { return p.quantity === 0; });
        if (zeroQuantityProducts.length > 0) {
            e.preventDefault();
            alert('Vui lòng nhập serial number!');
            return false;
        }

        $('#productsData').val(JSON.stringify(products));
    });

    // Use delegated events so handlers work even if elements are rendered after this script runs
    $(document).on('click', '#importFileBtn', function() {
        $('#importError').hide();
        $('#importSuccess').hide();
        $('#excelFile').val('');
        var modal = new bootstrap.Modal(document.getElementById('importModal'));
        modal.show();
    });

    $(document).on('click', '#processImport', function() {
        var fileInput = document.getElementById('excelFile');
        var file = fileInput.files[0];

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
                var sheetName = workbook.SheetNames[0];
                var worksheet = workbook.Sheets[sheetName];
                var jsonData = XLSX.utils.sheet_to_json(worksheet);

                if (jsonData.length === 0) {
                    $('#importError').text('File Excel không có dữ liệu!').show();
                    return;
                }

                processExcelData(jsonData);
            } catch (error) {
                $('#importError').text('Lỗi khi đọc file: ' + error.message).show();
            }
        };

        reader.readAsArrayBuffer(file);
    });
})();
