/**
 * goods-issue-edit.js
 * Script cho trang xem/duyệt phiếu xuất kho (Manager).
 * Cần có window.GOODS_ISSUE_EDIT từ JSP: { contextPath, isManager, readOnly }
 */
(function() {
    var config = window.GOODS_ISSUE_EDIT || {};
    var readOnly = !!config.readOnly;
    var isManager = !!config.isManager || readOnly;

    var products = [];
    var productIdCounter = 1;
    var currentSerials = [];

    // ── Render rows ──────────────────────────────────────────────────
    function addProductRowFromData(product) {
        if ($('#productTableBody tr td[colspan]').length) $('#productTableBody').empty();

        var serialBtnText = 'Xem serial';
        var row = (
            '<tr data-id="' + product.id + '">' +
            '<td>' + product.id + '</td>' +
            '<td>' + product.code +
            '<div class="mt-1"><button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-id="' + product.id + '">' +
            '<i class="fas fa-barcode me-1"></i>' + serialBtnText + '</button></div></td>' +
            '<td>' + product.name + '</td>' +
            '<td>' + product.unit + '</td>' +
            '<td><input type="number" class="form-control form-control-sm quantity-display" value="' + (product.quantity || 0) + '" readonly data-id="' + product.id + '"></td>' +
            '</tr>'
        );
        $('#productTableBody').append(row);
    }

    // ── Serial modal (read-only view) ─────────────────────────────────
    function renderSerialTags() {
        var tagsHtml = '';
        currentSerials.forEach(function(serial) {
            tagsHtml += '<span class="tag">' + serial + '</span>';
        });

        var html = (
            '<div class="tag-input-container" id="serialTagContainer">' +
            (tagsHtml || '<span class="text-muted">Không có serial nào</span>') +
            '</div>'
        );
        $('#serialListContent').html(html);
    }

    $(document).on('click', '.serial-btn', function() {
        var productId = $(this).data('id');
        var product = products.find(function(p) { return p.id === productId; });
        if (!product) return;

        $('#serialModal').data('productId', productId);
        currentSerials = product.serials ? product.serials.slice() : [];

        var modal = new bootstrap.Modal(document.getElementById('serialModal'));
        modal.show();
        renderSerialTags();
    });

    // ── Init: load products from hidden div ──────────────────────────
    var jsonEl = document.getElementById('goods-issue-edit-products-json');
    var initialStr = (jsonEl && jsonEl.textContent) ? jsonEl.textContent.replace(/&lt;\//g, '</') : null;
    if (initialStr) {
        try {
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
