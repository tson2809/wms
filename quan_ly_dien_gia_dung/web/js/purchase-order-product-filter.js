/**
 * purchase-order-product-filter.js
 * Shared script cho trang tạo đơn đặt hàng (Manager + Sale).
 * Cần: window.PO_FILTER = { contextPath, searchUrl }
 */
(function() {
    var ctx = (window.PO_FILTER || {}).contextPath || '';
    var searchUrl = ctx + '/product-variant/search';
    var searchTimer;

    function getFilters() {
        return {
            keyword:    document.getElementById('pf-keyword') ? document.getElementById('pf-keyword').value.trim() : '',
            categoryId: document.getElementById('pf-category') ? document.getElementById('pf-category').value : '',
            brandId:    document.getElementById('pf-brand')    ? document.getElementById('pf-brand').value    : ''
        };
    }

    function fetchVariants(filters, callback) {
        var params = new URLSearchParams();
        if (filters.keyword)    params.append('keyword',    filters.keyword);
        if (filters.categoryId) params.append('categoryId', filters.categoryId);
        if (filters.brandId)    params.append('brandId',    filters.brandId);
        fetch(searchUrl + '?' + params.toString())
            .then(function(r) { return r.ok ? r.json() : []; })
            .then(callback)
            .catch(function() { callback([]); });
    }

    function renderDropdown(variants) {
        var dd = document.getElementById('pf-dropdown');
        if (!dd) return;
        dd.innerHTML = '';
        if (!variants.length) {
            dd.innerHTML = '<div class="pf-dd-item text-muted">Không tìm thấy sản phẩm</div>';
        } else {
            variants.forEach(function(v) {
                var item = document.createElement('div');
                item.className = 'pf-dd-item';
                item.innerHTML = '<strong>' + escHtml(v.sku) + '</strong> — ' + escHtml(v.productName) + ' (' + escHtml(v.unitName) + ')';
                item.dataset.variantId  = v.variantId;
                item.dataset.sku        = v.sku;
                item.dataset.productName = v.productName;
                item.dataset.costPrice  = v.costPrice || 0;
                item.dataset.unitName   = v.unitName || '';
                item.addEventListener('click', function() {
                    addProductRow(this.dataset.variantId, this.dataset.sku,
                        this.dataset.productName, this.dataset.costPrice, this.dataset.unitName);
                    closeDd();
                });
                dd.appendChild(item);
            });
        }
        dd.style.display = 'block';
    }

    function closeDd() {
        var dd = document.getElementById('pf-dropdown');
        if (dd) dd.style.display = 'none';
    }

    function triggerSearch(delay) {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function() {
            var f = getFilters();
            if (f.keyword || f.categoryId || f.brandId) {
                fetchVariants(f, renderDropdown);
            } else {
                closeDd();
            }
        }, delay || 0);
    }

    // ── Add product row ─────────────────────────────────────────────────
    var rowIndex = 0; // 0 is the first static row

    function addProductRow(variantId, sku, productName, costPrice, unitName) {
        // Check duplicate
        var existing = document.querySelector('.variant-id-input[value="' + variantId + '"]');
        if (existing) {
            existing.closest('.product-row').style.outline = '2px solid #f90';
            setTimeout(function() { existing.closest('.product-row').style.outline = ''; }, 1000);
            return;
        }

        rowIndex++;
        var container = document.getElementById('productContainer');
        var row = document.createElement('div');
        row.className = 'product-row';
        row.setAttribute('data-index', rowIndex);
        row.innerHTML =
            '<div class="row align-items-end">' +
                '<div class="col-md-3">' +
                    '<label class="form-label">Sản phẩm</label>' +
                    '<input type="text" class="form-control" readonly value="' + escHtml(sku + ' – ' + productName) + '">' +
                    '<input type="hidden" class="variant-id-input" name="variantIds[]" value="' + escHtml(variantId) + '">' +
                '</div>' +
                '<div class="col-md-1">' +
                    '<label class="form-label">ĐVT</label>' +
                    '<input type="text" class="form-control" readonly value="' + escHtml(unitName) + '">' +
                '</div>' +
                '<div class="col-md-3">' +
                    '<label class="form-label">Số lượng <span class="text-danger">*</span></label>' +
                    '<input type="number" class="form-control quantity-input" name="quantities[]" min="1" value="1" required onchange="poCalcTotal(this)">' +
                '</div>' +
                '<div class="col-md-2">' +
                    '<label class="form-label">Đơn giá <span class="text-danger">*</span></label>' +
                    '<input type="number" class="form-control price-input" name="unitPrices[]" min="0" step="0.01" value="' + escHtml(costPrice) + '" required onchange="poCalcTotal(this)">' +
                '</div>' +
                '<div class="col-md-2">' +
                    '<label class="form-label">Thành tiền</label>' +
                    '<input type="text" class="form-control total-input" readonly>' +
                '</div>' +
                '<div class="col-md-1 d-flex align-items-end">' +
                    '<button type="button" class="btn btn-danger btn-sm w-100" onclick="poRemoveRow(this)"><i class="fa fa-trash"></i></button>' +
                '</div>' +
            '</div>';
        container.appendChild(row);
        poCalcTotal(row.querySelector('.quantity-input'));
    }

    window.poRemoveRow = function(btn) {
        var row = btn.closest('.product-row');
        row.parentNode.removeChild(row);
        poUpdateGrandTotal();
    };

    window.poCalcTotal = function(input) {
        var row = input.closest('.product-row');
        if (!row) return;
        var qty   = parseFloat(row.querySelector('.quantity-input').value)  || 0;
        var price = parseFloat(row.querySelector('.price-input').value)      || 0;
        var total = qty * price;
        row.querySelector('.total-input').value = total.toLocaleString('vi-VN') + ' ₫';
        poUpdateGrandTotal();
    };

    function poUpdateGrandTotal() {
        var total = 0;
        document.querySelectorAll('.product-row').forEach(function(r) {
            var qi = r.querySelector('.quantity-input');
            var pi = r.querySelector('.price-input');
            if (qi && pi) total += (parseFloat(qi.value)||0) * (parseFloat(pi.value)||0);
        });
        var el = document.getElementById('grandTotal');
        if (el) el.textContent = total.toLocaleString('vi-VN') + ' ₫';
    }
    window.poUpdateGrandTotal = poUpdateGrandTotal;

    // ── Bind filters ────────────────────────────────────────────────────
    document.addEventListener('DOMContentLoaded', function() {
        var kwEl  = document.getElementById('pf-keyword');
        var catEl = document.getElementById('pf-category');
        var brEl  = document.getElementById('pf-brand');

        if (kwEl) {
            kwEl.addEventListener('input', function() { triggerSearch(350); });
            kwEl.addEventListener('focus', function() { triggerSearch(0); });
        }
        if (catEl) catEl.addEventListener('change', function() { triggerSearch(0); });
        if (brEl)  brEl.addEventListener('change',  function() { triggerSearch(0); });

        document.addEventListener('click', function(e) {
            if (!e.target.closest('#pf-search-wrap')) closeDd();
        });

        // Calc initial totals for any static rows
        document.querySelectorAll('.quantity-input').forEach(function(el) { poCalcTotal(el); });
    });

    function escHtml(str) {
        return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }
})();
