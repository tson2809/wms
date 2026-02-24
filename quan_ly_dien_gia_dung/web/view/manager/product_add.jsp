<%-- Document : product_add Created on : 5 thg 2, 2026 Author : laptop368 --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="utf-8">
                <title>Thêm sản phẩm mới</title>
                <meta content="width=device-width, initial-scale=1.0" name="viewport">
                <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap"
                    rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css"
                    rel="stylesheet">
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"
                    rel="stylesheet">
                <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
                <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
                <link href="${pageContext.request.contextPath}/css/product-form.css" rel="stylesheet">
            </head>

            <body>
                <div class="container-fluid position-relative bg-white d-flex p-0">
                    <!-- Sidebar -->
                    <jsp:include page="/view/manager/components/sidebarManager.jsp" />

                    <!-- Content Start -->
                    <div class="content">
                        <!-- Navbar -->
                        <jsp:include page="/view/common/components/navbar.jsp" />

                        <!-- Product Add Section -->
                        <div class="container-fluid pt-4 px-4 product-add-section">
                            <div class="row">
                                <div class="col-12">
                                    <div class="mb-4">
                                        <h4 class="mb-0">Thêm sản phẩm mới</h4>
                                    </div>

                                    <c:if test="${not empty successMessage}">
                                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                                            ${successMessage}
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty errorVariant}">
                                        <div class="alert alert-danger">${errorVariant}</div>
                                    </c:if>

                                    <form id="productAddForm" action="${pageContext.request.contextPath}/product-add"
                                        method="post" enctype="multipart/form-data">

                                        <!-- Basic Info Card -->
                                        <div class="form-card">
                                            <div class="form-card-header">Thông tin cơ bản</div>

                                            <div class="row">
                                                <!-- Product Image -->
                                                <div class="col-md-2">
                                                    <label class="form-label">Ảnh sản phẩm</label>
                                                    <div class="image-upload-box"
                                                        onclick="document.getElementById('productImage').click()">
                                                        <i class="fa fa-camera"></i>
                                                    </div>
                                                    <input type="file" id="productImage" name="productImage"
                                                        accept="image/*" style="display: none;">
                                                </div>

                                                <div class="col-md-10">
                                                    <div class="row">
                                                        <!-- Product Name -->
                                                        <div class="col-md-12 mb-3">
                                                            <label class="form-label">Tên sản phẩm <span
                                                                    class="text-danger">*</span></label>
                                                            <input type="text" class="form-control" name="productName"
                                                                placeholder="Nhập tên sản phẩm" value="${productName}"
                                                                required>
                                                            <c:if test="${not empty errorProductName}"><small
                                                                    class="text-danger">${errorProductName}</small>
                                                            </c:if>
                                                        </div>

                                                        <!-- Category, Brand, Supplier -->
                                                        <div class="col-md-4 mb-3">
                                                            <label class="form-label">Danh mục <span
                                                                    class="text-danger">*</span></label>
                                                            <select class="form-select" name="categoryId" required>
                                                                <option value="">Chọn danh mục</option>
                                                                <c:forEach var="c" items="${categories}">
                                                                    <option value="${c.categoryId}" ${categoryId !=null
                                                                        && categoryId==c.categoryId ? 'selected' : '' }>
                                                                        ${c.categoryName}</option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty errorCategoryId}"><small
                                                                    class="text-danger">${errorCategoryId}</small>
                                                            </c:if>
                                                        </div>

                                                        <div class="col-md-4 mb-3">
                                                            <label class="form-label">Thương hiệu <span
                                                                    class="text-danger">*</span></label>
                                                            <select class="form-select" name="brandId" required>
                                                                <option value="">Chọn thương hiệu</option>
                                                                <c:forEach var="b" items="${brands}">
                                                                    <option value="${b.brandId}" ${brandId !=null &&
                                                                        brandId==b.brandId ? 'selected' : '' }>
                                                                        ${b.brandName}</option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty errorBrandId}"><small
                                                                    class="text-danger">${errorBrandId}</small></c:if>
                                                        </div>

                                                        <div class="col-md-4 mb-3">
                                                            <label class="form-label">Nhà cung cấp <span
                                                                    class="text-danger">*</span></label>
                                                            <select class="form-select" name="supplierId" required>
                                                                <option value="">Chọn nhà cung cấp</option>
                                                                <c:forEach var="s" items="${suppliers}">
                                                                    <option value="${s.supplierId}" ${supplierId !=null
                                                                        && supplierId==s.supplierId ? 'selected' : '' }>
                                                                        ${s.supplierName}</option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty errorSupplierId}"><small
                                                                    class="text-danger">${errorSupplierId}</small>
                                                            </c:if>
                                                        </div>

                                                        <!-- Unit -->
                                                        <div class="col-md-4 mb-3">
                                                            <label class="form-label">Đơn vị tính <span
                                                                    class="text-danger">*</span></label>
                                                            <select class="form-select" name="unitId" required>
                                                                <option value="">Chọn đơn vị</option>
                                                                <c:forEach var="u" items="${units}">
                                                                    <option value="${u.unitId}" ${unitId !=null &&
                                                                        unitId==u.unitId ? 'selected' : '' }>
                                                                        ${u.unitName}</option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty errorUnitId}"><small
                                                                    class="text-danger">${errorUnitId}</small></c:if>
                                                        </div>

                                                        <!-- Không còn base SKU/Barcode: tất cả SKU nằm ở variants -->

                                                        <!-- Description -->
                                                        <div class="col-md-12 mb-3">
                                                            <label class="form-label">Mô tả</label>
                                                            <textarea class="form-control" name="description" rows="3"
                                                                placeholder="Nhập mô tả sản phẩm">${description}</textarea>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Attributes Card -->
                                        <div class="form-card">
                                            <div
                                                class="form-card-header d-flex justify-content-between align-items-center">
                                                <span>Thuộc tính</span>
                                            </div>

                                            <div id="attributesContainer">
                                                <!-- Attribute rows will be added here dynamically -->
                                            </div>

                                            <button type="button" class="btn-add-attribute" id="btnAddAttribute">
                                                <i class="fa fa-plus-circle"></i> Thêm thuộc tính khác
                                            </button>
                                        </div>

                                        <!-- Variants Card -->
                                        <div class="form-card" id="variantsCard" style="display: none;">
                                            <div
                                                class="form-card-header d-flex justify-content-between align-items-center">
                                                <div>
                                                    <input type="checkbox" id="selectAllVariants">
                                                    <span id="variantCount">0 phiên bản</span>
                                                </div>
                                                <button type="button" class="btn-delete-selected-variants"
                                                    id="btnDeleteSelectedVariants" onclick="deleteSelectedVariants()">
                                                    <i class="fa fa-trash"></i> Xóa
                                                </button>
                                            </div>

                                            <div id="variantsContainer">
                                                <!-- Variants will be generated here -->
                                            </div>
                                        </div>

                                        <!-- Action Buttons -->
                                        <div class="form-card">
                                            <div class="d-flex justify-content-end gap-2">
                                                <button type="button" class="btn-cancel"
                                                    onclick="window.location.href='${pageContext.request.contextPath}/product-list'">
                                                    Hủy
                                                </button>
                                                <button type="submit" class="btn-save">
                                                    <i class="fa fa-save me-2"></i>Lưu sản phẩm
                                                </button>
                                            </div>
                                        </div>

                                    </form>
                                </div>
                            </div>
                        </div>
                        <!-- Product Add Section End -->
                    </div>
                    <!-- Content End -->
                </div>

                <!-- JavaScript Libraries -->
                <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
                <script src="${pageContext.request.contextPath}/js/main.js"></script>

                <script>
                    // Global state
                    let attributes = [];
                    let attributeIdCounter = 0;
                    let variants = [];

                    // Add first attribute row on load
                    document.addEventListener('DOMContentLoaded', function () {
                        addAttributeRow();
                        updateAddAttributeButton();

                        // Trước khi submit: kiểm tra phải có ít nhất 1 variant, rồi thêm hidden fields cho attributeNames và variantAttrValues
                        document.getElementById('productAddForm').addEventListener('submit', function (e) {
                            if (variants.length === 0) {
                                e.preventDefault();
                                alert('Phải có ít nhất 1 phiên bản (SKU).');
                                return false;
                            }
                            document.querySelectorAll('input[name="attributeNames"]').forEach(el => el.remove());
                            document.querySelectorAll('input[name="variantAttrValues"]').forEach(el => el.remove());

                            const validAttrs = attributes.filter(a => a.name && a.values.length > 0);
                            if (validAttrs.length > 0) {
                                const names = validAttrs.map(a => a.name).join('|');
                                const input = document.createElement('input');
                                input.type = 'hidden';
                                input.name = 'attributeNames';
                                input.value = names;
                                this.appendChild(input);

                                variants.forEach(variant => {
                                    const vals = variant.join('|');
                                    const inp = document.createElement('input');
                                    inp.type = 'hidden';
                                    inp.name = 'variantAttrValues';
                                    inp.value = vals;
                                    this.appendChild(inp);
                                });
                            }
                        });
                    });

                    // Add attribute row button
                    document.getElementById('btnAddAttribute').addEventListener('click', addAttributeRow);

                    // Add new attribute
                    function addAttributeRow() {
                        // Check limit
                        if (attributes.length >= 3) {
                            alert('Chỉ được thêm tối đa 3 thuộc tính!');
                            return;
                        }

                        const attrId = attributeIdCounter++;
                        attributes.push({
                            id: attrId,
                            name: '',
                            values: []
                        });
                        renderAttributes();
                        updateAddAttributeButton();
                    }

                    // Update attribute name
                    function updateAttributeName(id, name) {
                        const attr = attributes.find(a => a.id === id);
                        if (attr) {
                            attr.name = name;
                            generateVariants();
                        }
                    }

                    // Add value to attribute
                    function addValue(id) {
                        const input = document.getElementById('valueInput-' + id);
                        const value = input.value.trim();

                        if (value) {
                            const attr = attributes.find(a => a.id === id);
                            if (attr && !attr.values.includes(value)) {
                                // Add to data
                                attr.values.push(value);

                                // Create and insert tag element (without re-rendering)
                                const tagContainer = input.parentElement;
                                const tagSpan = document.createElement('span');
                                tagSpan.className = 'tag';
                                tagSpan.innerHTML = value + ' <span class="remove-tag" onclick="removeValue(' + id + ', \'' + value + '\')">×</span>';
                                tagContainer.insertBefore(tagSpan, input);

                                // Clear input
                                input.value = '';

                                // Update variants
                                generateVariants();
                            } else if (attr && attr.values.includes(value)) {
                                alert('Giá trị "' + value + '" đã tồn tại!');
                            }
                        }
                    }

                    // Remove value from attribute
                    function removeValue(attrId, value) {
                        const attr = attributes.find(a => a.id === attrId);
                        if (attr) {
                            // Remove from data
                            attr.values = attr.values.filter(v => v !== value);

                            // Remove tag element from DOM (without re-rendering)
                            event.target.parentElement.remove();

                            // Update variants
                            generateVariants();
                        }
                    }

                    // Delete entire attribute row
                    function deleteAttributeRow(id) {
                        attributes = attributes.filter(attr => attr.id !== id);
                        renderAttributes();
                        generateVariants();
                        updateAddAttributeButton();
                    }

                    // Render all attributes
                    function renderAttributes() {
                        const container = document.getElementById('attributesContainer');
                        let html = '';

                        attributes.forEach(attr => {
                            // Build tags HTML
                            let tagsHtml = '';
                            attr.values.forEach(value => {
                                tagsHtml += '<span class="tag">' +
                                    value +
                                    ' <span class="remove-tag" onclick="removeValue(' + attr.id + ', \'' + value + '\')">×</span>' +
                                    '</span>';
                            });

                            // Build attribute row HTML
                            html += '<div class="attribute-row" data-id="' + attr.id + '">' +
                                '  <div class="row align-items-center">' +
                                '    <div class="col-md-3">' +
                                '      <label class="form-label mb-0">Tên thuộc tính</label>' +
                                '      <input type="text" class="form-control mt-2 attr-name" ' +
                                '             placeholder="Nhập tên thuộc tính" ' +
                                '             value="' + attr.name + '" ' +
                                '             oninput="updateAttributeName(' + attr.id + ', this.value)">' +
                                '    </div>' +
                                '    <div class="col-md-8">' +
                                '      <label class="form-label mb-0">Giá trị</label>' +
                                '      <div class="tag-input-container mt-2" data-id="' + attr.id + '">' +
                                tagsHtml +
                                '        <input type="text" class="tag-input" ' +
                                '               id="valueInput-' + attr.id + '" ' +
                                '               placeholder="Nhập ký tự và ấn enter" ' +
                                '               onkeypress="if(event.key === \'Enter\') { event.preventDefault(); addValue(' + attr.id + '); }">' +
                                '      </div>' +
                                '    </div>' +
                                '    <div class="col-md-1 text-end">' +
                                '      <button type="button" class="btn-delete-attribute" onclick="deleteAttributeRow(' + attr.id + ')">' +
                                '        <i class="fa fa-trash"></i>' +
                                '      </button>' +
                                '    </div>' +
                                '  </div>' +
                                '</div>';
                        });

                        container.innerHTML = html;
                    }

                    // Update "Add Attribute" button state
                    function updateAddAttributeButton() {
                        const btn = document.getElementById('btnAddAttribute');
                        if (attributes.length >= 3) {
                            btn.disabled = true;
                            btn.style.opacity = '0.5';
                            btn.style.cursor = 'not-allowed';
                        } else {
                            btn.disabled = false;
                            btn.style.opacity = '1';
                            btn.style.cursor = 'pointer';
                        }
                    }

                    function generateVariants() {
                        // Filter attributes with name and values
                        const validAttrs = attributes.filter(attr => attr.name && attr.values.length > 0);

                        if (validAttrs.length === 0) {
                            // Chưa có thuộc tính/giá trị → chưa sinh được variant
                            document.getElementById('variantsCard').style.display = 'none';
                            variants = [];
                            return;
                        }

                        // Generate cartesian product
                        variants = cartesianProduct(validAttrs.map(attr => attr.values));

                        // Show variants card
                        document.getElementById('variantsCard').style.display = 'block';
                        document.getElementById('variantCount').textContent = variants.length + ' phiên bản';

                        // Render variants
                        const container = document.getElementById('variantsContainer');
                        container.innerHTML = '';

                        variants.forEach((variant, index) => {
                            const variantDiv = document.createElement('div');
                            variantDiv.className = 'variant-item';

                            // Build variant text
                            const variantText = variant.join(' / ');
                            const esc = (s) => (s || '').replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

                            variantDiv.innerHTML =
                                '<input type="checkbox" class="variant-checkbox" data-index="' + index + '">' +
                                '<div class="variant-combination">' + esc(variantText) + '</div>' +
                                '<input type="text" name="variantSku" placeholder="Mã SKU" style="width: 150px;" required>' +
                                '<input type="text" name="variantBarcode" placeholder="Barcode" style="width: 150px;">';

                            container.appendChild(variantDiv);
                        });
                    }

                    // Cartesian product helper
                    function cartesianProduct(arrays) {
                        if (arrays.length === 0) return [[]];
                        if (arrays.length === 1) return arrays[0].map(v => [v]);

                        const result = [];
                        const rest = cartesianProduct(arrays.slice(1));

                        for (let value of arrays[0]) {
                            for (let combination of rest) {
                                result.push([value, ...combination]);
                            }
                        }

                        return result;
                    }

                    // Delete selected variants (user ticks checkbox then clicks Xóa button)
                    function deleteSelectedVariants() {
                        const checkedBoxes = document.querySelectorAll('.variant-checkbox:checked');
                        if (checkedBoxes.length === 0) {
                            alert('Vui lòng chọn phiên bản cần xóa.');
                            return;
                        }

                        // Collect indices to delete (descending order to splice correctly)
                        const indicesToDelete = Array.from(checkedBoxes)
                            .map(cb => parseInt(cb.getAttribute('data-index')))
                            .sort((a, b) => b - a);

                        // Preserve SKU/Barcode values for remaining variants before re-render
                        const container = document.getElementById('variantsContainer');
                        const rows = container.querySelectorAll('.variant-item');
                        const preservedValues = [];
                        rows.forEach((row, i) => {
                            if (!indicesToDelete.includes(i)) {
                                const skuInput = row.querySelector('input[name="variantSku"]');
                                const barcodeInput = row.querySelector('input[name="variantBarcode"]');
                                preservedValues.push({
                                    sku: skuInput ? skuInput.value : '',
                                    barcode: barcodeInput ? barcodeInput.value : ''
                                });
                            }
                        });

                        // Remove variants from array
                        indicesToDelete.forEach(idx => variants.splice(idx, 1));

                        // Đồng bộ thuộc tính: xóa các giá trị không còn được dùng bởi phiên bản còn lại
                        const validAttrs = attributes.filter(attr => attr.name && attr.values.length > 0);
                        validAttrs.forEach((attr, attrIndex) => {
                            const usedValues = new Set(variants.map(v => v[attrIndex]));
                            attr.values = attr.values.filter(v => usedValues.has(v));
                        });

                        renderAttributes();
                        updateAddAttributeButton();

                        document.getElementById('variantCount').textContent = variants.length + ' phiên bản';
                        document.getElementById('selectAllVariants').checked = false;

                        // Re-render variants
                        container.innerHTML = '';
                        variants.forEach((variant, index) => {
                            const variantDiv = document.createElement('div');
                            variantDiv.className = 'variant-item';

                            const variantText = variant.join(' / ');
                            const preserved = preservedValues[index] || { sku: '', barcode: '' };
                            const esc = (s) => (s || '').replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

                            variantDiv.innerHTML =
                                '<input type="checkbox" class="variant-checkbox" data-index="' + index + '">' +
                                '<div class="variant-combination">' + esc(variantText) + '</div>' +
                                '<input type="text" name="variantSku" placeholder="Mã SKU" value="' + esc(preserved.sku) + '" style="width: 150px;" required>' +
                                '<input type="text" name="variantBarcode" placeholder="Barcode" value="' + esc(preserved.barcode) + '" style="width: 150px;">';

                            container.appendChild(variantDiv);
                        });

                        // Hide variants card if no variants left
                        if (variants.length === 0) {
                            document.getElementById('variantsCard').style.display = 'none';
                        }
                    }

                    // Select all variants
                    document.getElementById('selectAllVariants').addEventListener('change', function () {
                        document.querySelectorAll('.variant-checkbox').forEach(cb => {
                            cb.checked = this.checked;
                        });
                    });

                    // Image preview
                    document.getElementById('productImage').addEventListener('change', function (e) {
                        const file = e.target.files[0];
                        if (file) {
                            const reader = new FileReader();
                            reader.onload = function (e) {
                                const uploadBox = document.querySelector('.image-upload-box');
                                uploadBox.innerHTML = `<img src="${e.target.result}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 6px;">`;
                            }
                            reader.readAsDataURL(file);
                        }
                    });
                </script>
            </body>

            </html>