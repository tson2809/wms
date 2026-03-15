
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Chỉnh sửa sản phẩm</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/product-form.css" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/manager/components/sidebarManager.jsp" />
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                <div class="container-fluid pt-4 px-4 product-add-section">
                    <div class="row">
                        <div class="col-12">
                            <div class="mb-4">
                                <h4 class="mb-0">Chỉnh sửa sản phẩm</h4>
                            </div>
                            <c:if test="${not empty successMessage}">
                                <div class="alert alert-success alert-dismissible fade show" role="alert">
                                    ${successMessage}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                            <c:if test="${productHasTransactions}">
                                <div class="alert alert-warning" role="alert">
                                    <strong>Không thể cập nhật:</strong> Sản phẩm đã tham gia giao dịch (nhập hàng, trả hàng) nên không được phép sửa thông tin.
                                </div>
                            </c:if>
                            <c:if test="${not empty errorVariant}">
                                <div class="alert alert-danger">${errorVariant}</div>
                            </c:if>

                            <form id="productEditForm" action="${pageContext.request.contextPath}/product-edit" method="post" enctype="multipart/form-data">
                                <input type="hidden" name="productId" value="${productId}">
                                <div id="hiddenVariantDataContainer" style="display:none;"></div>

                                <div class="form-card">
                                    <div class="form-card-header">Thông tin cơ bản</div>
                                    <div class="row">
                                        <div class="col-md-2">
                                            <label class="form-label">Ảnh sản phẩm</label>
                                            <div class="image-upload-box" onclick="document.getElementById('productImage').click()" id="imageUploadBox">
                                                <c:choose>
                                                    <c:when test="${not empty productEdit.picture}">
                                                        <img src="${pageContext.request.contextPath}/${productEdit.picture}" alt="" style="width: 100%; height: 100%; object-fit: cover; border-radius: 6px;">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fa fa-camera"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <input type="file" id="productImage" name="productImage" accept="image/*" style="display: none;">
                                        </div>
                                        <div class="col-md-10">
                                            <div class="row">
                                                <div class="col-md-12 mb-3">
                                                    <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                                                    <input type="text" class="form-control" name="productName" placeholder="Nhập tên sản phẩm"
                                                           value="${not empty productName ? productName : (productEdit != null ? productEdit.productName : '')}" required>
                                                    <c:if test="${not empty errorProductName}"><small class="text-danger">${errorProductName}</small></c:if>
                                                    </div>
                                                    <div class="col-md-4 mb-3">
                                                        <label class="form-label">Danh mục <span class="text-danger">*</span></label>
                                                        <select class="form-select" name="categoryId" required>
                                                            <option value="">Chọn danh mục</option>
                                                        <c:forEach var="c" items="${categories}">
                                                            <c:set var="selCat" value="${not empty categoryId ? categoryId : (productEdit != null ? productEdit.categoryId : '')}"/>
                                                            <option value="${c.categoryId}" ${selCat == c.categoryId ? 'selected' : ''}>${c.categoryName}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <c:if test="${not empty errorCategoryId}"><small class="text-danger">${errorCategoryId}</small></c:if>
                                                    </div>
                                                    <div class="col-md-4 mb-3">
                                                        <label class="form-label">Thương hiệu <span class="text-danger">*</span></label>
                                                        <select class="form-select" name="brandId" required>
                                                            <option value="">Chọn thương hiệu</option>
                                                        <c:forEach var="b" items="${brands}">
                                                            <c:set var="selBrand" value="${not empty brandId ? brandId : (productEdit != null ? productEdit.brandId : '')}"/>
                                                            <option value="${b.brandId}" ${selBrand == b.brandId ? 'selected' : ''}>${b.brandName}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <c:if test="${not empty errorBrandId}"><small class="text-danger">${errorBrandId}</small></c:if>
                                                    </div>
                                                    <div class="col-md-4 mb-3">
                                                        <label class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                                                        <select class="form-select" name="supplierId" required>
                                                            <option value="">Chọn nhà cung cấp</option>
                                                        <c:forEach var="s" items="${suppliers}">
                                                            <c:set var="selSup" value="${not empty supplierId ? supplierId : (productEdit != null ? productEdit.supplierId : '')}"/>
                                                            <option value="${s.supplierId}" ${selSup == s.supplierId ? 'selected' : ''}>${s.supplierName}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <c:if test="${not empty errorSupplierId}"><small class="text-danger">${errorSupplierId}</small></c:if>
                                                    </div>
                                                    <div class="col-md-4 mb-3">
                                                        <label class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                                                        <select class="form-select" name="unitId" required>
                                                            <option value="">Chọn đơn vị</option>
                                                        <c:forEach var="u" items="${units}">
                                                            <c:set var="selUnit" value="${not empty unitId ? unitId : (productEdit != null ? productEdit.unitId : '')}"/>
                                                            <option value="${u.unitId}" ${selUnit == u.unitId ? 'selected' : ''}>${u.unitName}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <c:if test="${not empty errorUnitId}"><small class="text-danger">${errorUnitId}</small></c:if>
                                                    </div>
                                                    <!-- Không còn base SKU/Barcode: tất cả SKU nằm ở variants -->
                                                    <div class="col-md-12 mb-3">
                                                        <label class="form-label">Mô tả</label>
                                                        <textarea class="form-control" name="description" rows="3" placeholder="Nhập mô tả sản phẩm">${not empty description ? description : (productEdit != null ? productEdit.description : '')}</textarea>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-card">
                                    <div class="form-card-header d-flex justify-content-between align-items-center"><span>Thuộc tính</span></div>
                                    <div id="attributesContainer"></div>
                                    <button type="button" class="btn-add-attribute" id="btnAddAttribute"><i class="fa fa-plus-circle"></i> Thêm thuộc tính khác</button>
                                </div>

                                <div class="form-card" id="variantsCard" style="display: none;">
                                    <div class="form-card-header d-flex justify-content-between align-items-center">
                                        <div>
                                            <input type="checkbox" id="selectAllVariants">
                                            <span id="variantCount">0 phiên bản</span>
                                        </div>
                                        <button type="button" class="btn-delete-selected-variants" id="btnDeleteSelectedVariants" onclick="deleteSelectedVariants()"><i class="fa fa-trash"></i> Xóa</button>
                                    </div>
                                    <div id="variantsContainer"></div>
                                </div>

                                <div class="form-card">
                                    <div class="d-flex justify-content-end gap-2">
                                        <button type="button" class="btn-cancel" onclick="window.location.href = '${pageContext.request.contextPath}/product-list'">Hủy</button>
                                        <c:choose>
                                            <c:when test="${productHasTransactions}">
                                                <button type="button" class="btn-save" disabled style="opacity:0.5;cursor:not-allowed;background:#9ca3af;border-color:#9ca3af;" title="Sản phẩm đã tham gia giao dịch"><i class="fa fa-save me-2"></i>Cập nhật sản phẩm</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" class="btn-save"><i class="fa fa-save me-2"></i>Cập nhật sản phẩm</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <c:if test="${not empty editDataJson}">
            <script type="application/json" id="edit-data-json">${editDataJson}</script>
        </c:if>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
                                            var contextPath = '${pageContext.request.contextPath}';
                                            let attributes = [];
                                            let attributeIdCounter = 0;
                                            let variants = [];

                                            function esc(s) {
                                                return (s || '').replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/'/g, '&#39;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
                                            }

                                            document.addEventListener('DOMContentLoaded', function () {
                                                var editDataEl = document.getElementById('edit-data-json');
                                                if (editDataEl && editDataEl.textContent) {
                                                    try {
                                                        var editData = JSON.parse(editDataEl.textContent);
                                                        if (editData.attributeNames && editData.attributeNames.length > 0) {
                                                            attributes = editData.attributeNames.map(function (name, idx) {
                                                                return {
                                                                    id: attributeIdCounter++,
                                                                    name: name,
                                                                    values: (editData.attributeValues && editData.attributeValues[idx]) ? editData.attributeValues[idx] : []
                                                                };
                                                            });
                                                            renderAttributes();
                                                        if (editData.variants && editData.variants.length > 0) {
                                                                variants = editData.variants.map(function (v) {
                                                                    return (v.attributeValues || []).slice();
                                                                });
                                                                document.getElementById('variantsCard').style.display = 'block';
                                                                document.getElementById('variantCount').textContent = variants.length + ' phiên bản';
                                                                var container = document.getElementById('variantsContainer');
                                                                container.innerHTML = '';
                                                                editData.variants.forEach(function (v, index) {
                                                                    var variantDiv = document.createElement('div');
                                                                    variantDiv.className = 'variant-item';
                                                                    var variantText = (v.attributeValues || []).join(' / ');
                                                                    var variantIdVal = (v.variantId != null && v.variantId > 0) ? String(v.variantId) : '';
                                                                    var variantPic = (v.variantPicture != null && String(v.variantPicture).trim() !== '') ? String(v.variantPicture).trim() : '';
                                                                    variantDiv.setAttribute('data-variant-picture', variantPic);
                                                                    variantDiv.innerHTML =
                                                                            '<input type="hidden" name="variantId" value="' + esc(variantIdVal) + '">' +
                                                                            '<input type="checkbox" class="variant-checkbox" data-index="' + index + '">' +
                                                                            '<div class="variant-combination">' + esc(variantText) + '</div>' +
                                                                            '<input type="text" name="variantSku" placeholder="Mã SKU" value="' + esc(v.sku) + '" style="width: 150px;" required>' +
                                                                            '<input type="text" name="variantBarcode" placeholder="Barcode" value="' + esc(v.barcode) + '" style="width: 150px;">' +
                                                                            '<div class="variant-image-box" onclick="document.getElementById(\'variantImage-' + index + '\').click()">' +
                                                                            (variantPic ? '<img src="' + contextPath + '/' + esc(variantPic) + '" alt="">' : '<i class="fa fa-camera"></i>') +
                                                                            '</div>' +
                                                                            '<input type="file" id="variantImage-' + index + '" name="variantImage" accept="image/*" style="display:none">';
                                                                    container.appendChild(variantDiv);
                                                                });
                                                            }
                                                        } else {
                                                            addAttributeRow();
                                                        }
                                                    } catch (e) {
                                                        addAttributeRow();
                                                    }
                                                } else {
                                                    addAttributeRow();
                                                }
                                                updateAddAttributeButton();

                                                syncHiddenVariantData();

                                                // Đảm bảo sync lại hidden data và validate phải có ít nhất 1 variant trước khi submit
                                                document.getElementById('productEditForm').addEventListener('submit', function (e) {
                                                    if (variants.length === 0) {
                                                        e.preventDefault();
                                                        alert('Phải có ít nhất 1 phiên bản (SKU).');
                                                        return false;
                                                    }
                                                    syncHiddenVariantData();
                                                });
                                            });

                                            function syncHiddenVariantData() {
                                                var container = document.getElementById('hiddenVariantDataContainer');
                                                container.innerHTML = '';
                                                var validAttrs = attributes.filter(function (a) {
                                                    return a.name && a.values.length > 0;
                                                });
                                                if (validAttrs.length > 0 && variants.length > 0) {
                                                    var names = validAttrs.map(function (a) {
                                                        return a.name;
                                                    }).join('|');
                                                    var input = document.createElement('input');
                                                    input.type = 'hidden';
                                                    input.name = 'attributeNames';
                                                    input.value = names;
                                                    container.appendChild(input);
                                                    variants.forEach(function (variant) {
                                                        var inp = document.createElement('input');
                                                        inp.type = 'hidden';
                                                        inp.name = 'variantAttrValues';
                                                        inp.value = variant.join('|');
                                                        container.appendChild(inp);
                                                    });
                                                }
                                            }

                                            document.getElementById('btnAddAttribute').addEventListener('click', addAttributeRow);

                                            function addAttributeRow() {
                                                if (attributes.length >= 3) {
                                                    alert('Chỉ được thêm tối đa 3 thuộc tính!');
                                                    return;
                                                }
                                                var attrId = attributeIdCounter++;
                                                attributes.push({id: attrId, name: '', values: []});
                                                renderAttributes();
                                                updateAddAttributeButton();
                                                generateVariants();
                                                syncHiddenVariantData();
                                            }

                                            function updateAttributeName(id, name) {
                                                var attr = attributes.find(function (a) {
                                                    return a.id === id;
                                                });
                                                if (attr) {
                                                    attr.name = name;
                                                    generateVariants();
                                                }
                                            }

                                            function addValue(id) {
                                                var input = document.getElementById('valueInput-' + id);
                                                var value = input.value.trim();
                                                if (value) {
                                                    var attr = attributes.find(function (a) {
                                                        return a.id === id;
                                                    });
                                                    if (attr && attr.values.indexOf(value) === -1) {
                                                        attr.values.push(value);
                                                        var tagContainer = input.parentElement;
                                                        var tagSpan = document.createElement('span');
                                                        tagSpan.className = 'tag';
                                                        tagSpan.innerHTML = value + ' <span class="remove-tag" onclick="removeValue(' + id + ', \'' + value.replace(/'/g, "\\'") + '\')">×</span>';
                                                        tagContainer.insertBefore(tagSpan, input);
                                                        input.value = '';
                                                        generateVariants();
                                                    } else if (attr && attr.values.indexOf(value) !== -1) {
                                                        alert('Giá trị "' + value + '" đã tồn tại!');
                                                    }
                                                }
                                            }

                                            function removeValue(attrId, value) {
                                                var attr = attributes.find(function (a) {
                                                    return a.id === attrId;
                                                });
                                                if (attr) {
                                                    attr.values = attr.values.filter(function (v) {
                                                        return v !== value;
                                                    });
                                                    if (event && event.target && event.target.parentElement)
                                                        event.target.parentElement.remove();
                                                    generateVariants();
                                                }
                                            }

                                            function deleteAttributeRow(id) {
                                                attributes = attributes.filter(function (attr) {
                                                    return attr.id !== id;
                                                });
                                                renderAttributes();
                                                generateVariants();
                                                updateAddAttributeButton();
                                            }

                                            function renderAttributes() {
                                                var container = document.getElementById('attributesContainer');
                                                var html = '';
                                                attributes.forEach(function (attr) {
                                                    var tagsHtml = '';
                                                    (attr.values || []).forEach(function (value) {
                                                        tagsHtml += '<span class="tag">' + esc(value) + ' <span class="remove-tag" onclick="removeValue(' + attr.id + ', \'' + String(value).replace(/'/g, "\\'") + '\')">×</span></span>';
                                                    });
                                                    html += '<div class="attribute-row" data-id="' + attr.id + '">' +
                                                            '<div class="row align-items-center">' +
                                                            '<div class="col-md-3"><label class="form-label mb-0">Tên thuộc tính</label>' +
                                                            '<input type="text" class="form-control mt-2 attr-name" placeholder="Nhập tên thuộc tính" value="' + esc(attr.name) + '" oninput="updateAttributeName(' + attr.id + ', this.value)">' +
                                                            '</div><div class="col-md-8"><label class="form-label mb-0">Giá trị</label>' +
                                                            '<div class="tag-input-container mt-2" data-id="' + attr.id + '">' + tagsHtml +
                                                            '<input type="text" class="tag-input" id="valueInput-' + attr.id + '" placeholder="Nhập ký tự và ấn enter" onkeypress="if(event.key === \'Enter\') { event.preventDefault(); addValue(' + attr.id + '); }">' +
                                                            '</div></div><div class="col-md-1 text-end">' +
                                                            '<button type="button" class="btn-delete-attribute" onclick="deleteAttributeRow(' + attr.id + ')"><i class="fa fa-trash"></i></button>' +
                                                            '</div></div></div>';
                                                });
                                                container.innerHTML = html;
                                            }

                                            function updateAddAttributeButton() {
                                                var btn = document.getElementById('btnAddAttribute');
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

                                            function cartesianProduct(arrays) {
                                                if (arrays.length === 0)
                                                    return [[]];
                                                if (arrays.length === 1)
                                                    return arrays[0].map(function (v) {
                                                        return [v];
                                                    });
                                                var result = [];
                                                var rest = cartesianProduct(arrays.slice(1));
                                                for (var i = 0; i < arrays[0].length; i++) {
                                                    for (var j = 0; j < rest.length; j++) {
                                                        result.push([arrays[0][i]].concat(rest[j]));
                                                    }
                                                }
                                                return result;
                                            }

                                            function generateVariants() {
                                                var validAttrs = attributes.filter(function (attr) {
                                                    return attr.name && attr.values.length > 0;
                                                });

                                                if (validAttrs.length === 0) {
                                                    document.getElementById('variantsCard').style.display = 'none';
                                                    variants = [];
                                                    syncHiddenVariantData();
                                                    return;
                                                }

                                                //  map variant cũ
                                                var oldVariantMap = {};
                                                document.querySelectorAll('#variantsContainer .variant-item').forEach(function (row) {
                                                    var key = row.querySelector('.variant-combination')?.textContent;
                                                    if (!key) return;
                                                    var sku = row.querySelector('input[name="variantSku"]')?.value || '';
                                                    var barcode = row.querySelector('input[name="variantBarcode"]')?.value || '';
                                                    var variantIdInput = row.querySelector('input[name="variantId"]');
                                                    var variantPicture = row.getAttribute('data-variant-picture') || '';
                                                    oldVariantMap[key] = {
                                                        variantId: variantIdInput ? variantIdInput.value : null,
                                                        sku: sku,
                                                        barcode: barcode,
                                                        variantPicture: variantPicture
                                                    };
                                                });

                                                //  generate tổ hợp mới
                                                var newCombinations = cartesianProduct(
                                                        validAttrs.map(function (attr) {
                                                            return attr.values;
                                                        })
                                                        );

                                                variants = newCombinations;

                                                var container = document.getElementById('variantsContainer');
                                                container.innerHTML = '';
                                                document.getElementById('variantsCard').style.display = 'block';
                                                document.getElementById('variantCount').textContent = variants.length + ' phiên bản';

                                                newCombinations.forEach(function (variant, index) {
                                                    var key = variant.join(' / ');
                                                    var old = oldVariantMap[key];
                                                    if (!old) {
                                                        var k = key;
                                                        while (k) {
                                                            var parentKey = k.replace(/ \/ [^/]*$/, '').trim();
                                                            if (parentKey === k) break;
                                                            k = parentKey;
                                                            if (oldVariantMap[k]) { old = oldVariantMap[k]; break; }
                                                        }
                                                        if (!old) {
                                                            for (var ok in oldVariantMap) {
                                                                if (ok === key || ok.indexOf(key + ' / ') === 0) {
                                                                    old = oldVariantMap[ok];
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    old = old || {};
                                                    var variantDiv = document.createElement('div');
                                                    variantDiv.className = 'variant-item';
                                                    var variantIdVal = (old.variantId && String(old.variantId).trim()) ? esc(old.variantId) : '';
                                                    var pic = (old.variantPicture && String(old.variantPicture).trim()) ? String(old.variantPicture).trim() : '';
                                                    variantDiv.setAttribute('data-variant-picture', pic);
                                                    variantDiv.innerHTML =
                                                            '<input type="hidden" name="variantId" value="' + variantIdVal + '">' +
                                                            '<input type="checkbox" class="variant-checkbox" data-index="' + index + '">' +
                                                            '<div class="variant-combination">' + esc(key) + '</div>' +
                                                            '<input type="text" name="variantSku" value="' + esc(old.sku || '') + '" placeholder="Mã SKU" style="width:150px" required>' +
                                                            '<input type="text" name="variantBarcode" value="' + esc(old.barcode || '') + '" placeholder="Barcode" style="width:150px">' +
                                                            '<div class="variant-image-box" onclick="document.getElementById(\'variantImage-' + index + '\').click()">' +
                                                            (pic ? '<img src="' + contextPath + '/' + esc(pic) + '" alt="">' : '<i class="fa fa-camera"></i>') +
                                                            '</div>' +
                                                            '<input type="file" id="variantImage-' + index + '" name="variantImage" accept="image/*" style="display:none">';
                                                    container.appendChild(variantDiv);
                                                });

                                                syncHiddenVariantData();
                                            }

                                            function deleteSelectedVariants() {
                                                var checkedBoxes = document.querySelectorAll('.variant-checkbox:checked');
                                                if (checkedBoxes.length === 0) {
                                                    alert('Vui lòng chọn phiên bản cần xóa.');
                                                    return;
                                                }
                                                var indicesToDelete = Array.from(checkedBoxes).map(function (cb) {
                                                    return parseInt(cb.getAttribute('data-index'), 10);
                                                }).sort(function (a, b) {
                                                    return b - a;
                                                });
                                                var container = document.getElementById('variantsContainer');
                                                var rows = container.querySelectorAll('.variant-item');
                                                var preservedValues = [];
                                                rows.forEach(function (row, i) {
                                                    if (indicesToDelete.indexOf(i) === -1) {
                                                        var skuInput = row.querySelector('input[name="variantSku"]');
                                                        var barcodeInput = row.querySelector('input[name="variantBarcode"]');
                                                        var variantIdInput = row.querySelector('input[name="variantId"]');
                                                        var variantPicture = row.getAttribute('data-variant-picture') || '';
                                                        preservedValues.push({
                                                            sku: skuInput ? skuInput.value : '',
                                                            barcode: barcodeInput ? barcodeInput.value : '',
                                                            variantId: variantIdInput ? variantIdInput.value : '',
                                                            variantPicture: variantPicture
                                                        });
                                                    }
                                                });
                                                indicesToDelete.forEach(function (idx) {
                                                    variants.splice(idx, 1);
                                                });
                                                var validAttrs = attributes.filter(function (attr) {
                                                    return attr.name && attr.values.length > 0;
                                                });
                                                validAttrs.forEach(function (attr, attrIndex) {
                                                    var usedValues = new Set(variants.map(function (v) {
                                                        return v[attrIndex];
                                                    }));
                                                    attr.values = attr.values.filter(function (v) {
                                                        return usedValues.has(v);
                                                    });
                                                });
                                                renderAttributes();
                                                updateAddAttributeButton();
                                                document.getElementById('variantCount').textContent = variants.length + ' phiên bản';
                                                document.getElementById('selectAllVariants').checked = false;
                                                container.innerHTML = '';
                                                variants.forEach(function (variant, index) {
                                                    var variantDiv = document.createElement('div');
                                                    variantDiv.className = 'variant-item';
                                                    var preserved = preservedValues[index] || {sku: '', barcode: '', variantId: '', variantPicture: ''};
                                                    var variantIdVal = (preserved.variantId && String(preserved.variantId).trim()) ? esc(preserved.variantId) : '';
                                                    var pic = (preserved.variantPicture && String(preserved.variantPicture).trim()) ? String(preserved.variantPicture).trim() : '';
                                                    variantDiv.setAttribute('data-variant-picture', pic);
                                                    variantDiv.innerHTML = '<input type="hidden" name="variantId" value="' + variantIdVal + '">' +
                                                            '<input type="checkbox" class="variant-checkbox" data-index="' + index + '">' +
                                                            '<div class="variant-combination">' + esc(variant.join(' / ')) + '</div>' +
                                                            '<input type="text" name="variantSku" placeholder="Mã SKU" value="' + esc(preserved.sku) + '" style="width: 150px;" required>' +
                                                            '<input type="text" name="variantBarcode" placeholder="Barcode" value="' + esc(preserved.barcode) + '" style="width: 150px;">' +
                                                            '<div class="variant-image-box" onclick="document.getElementById(\'variantImage-' + index + '\').click()">' +
                                                            (pic ? '<img src="' + contextPath + '/' + esc(pic) + '" alt="">' : '<i class="fa fa-camera"></i>') +
                                                            '</div>' +
                                                            '<input type="file" id="variantImage-' + index + '" name="variantImage" accept="image/*" style="display:none">';
                                                    container.appendChild(variantDiv);
                                                });
                                                if (variants.length === 0) {
                                                    document.getElementById('variantsCard').style.display = 'none';
                                                }
                                                syncHiddenVariantData();
                                            }

                                            document.getElementById('selectAllVariants').addEventListener('change', function () {
                                                document.querySelectorAll('.variant-checkbox').forEach(function (cb) {
                                                    cb.checked = this.checked;
                                                }.bind(this));
                                            });

                                            document.getElementById('productImage').addEventListener('change', function (e) {
                                                var file = e.target.files[0];
                                                if (file) {
                                                    var reader = new FileReader();
                                                    reader.onload = function (ev) {
                                                        var uploadBox = document.getElementById('imageUploadBox');
                                                        uploadBox.innerHTML = '<img src="' + ev.target.result + '" style="width: 100%; height: 100%; object-fit: cover; border-radius: 6px;" alt="">';
                                                    };
                                                    reader.readAsDataURL(file);
                                                }
                                            });

                                            document.getElementById('productEditForm').addEventListener('change', function (e) {
                                                if (e.target && e.target.name === 'variantImage') {
                                                    var file = e.target.files[0];
                                                    if (!file) return;
                                                    var row = e.target.closest('.variant-item');
                                                    var box = row ? row.querySelector('.variant-image-box') : null;
                                                    if (!box || !box.classList.contains('variant-image-box')) return;
                                                    var reader = new FileReader();
                                                    reader.onload = function (ev) {
                                                        box.innerHTML = '<img src="' + ev.target.result + '" alt="">';
                                                    };
                                                    reader.readAsDataURL(file);
                                                }
                                            });
        </script>
    </body>
</html>
