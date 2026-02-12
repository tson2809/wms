<%-- 
    Document   : goods-receipt-edit
    Created on : Feb 09, 2026
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Sửa phiếu nhập kho</title>
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
            
            .tag .remove-tag {
                cursor: pointer;
                font-weight: bold;
                font-size: 16px;
            }
            
            .tag .remove-tag:hover {
                color: #fee;
            }
            
            .tag-input {
                border: none;
                outline: none;
                flex: 1;
                min-width: 150px;
                padding: 4px;
                font-size: 14px;
            }
        </style>
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
                <c:otherwise>
                    <jsp:include page="/view/common/components/RoleSideBar.jsp" />
                </c:otherwise>
            </c:choose>

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />
                
                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12">
                            <h5 class="mb-1">Sửa phiếu nhập kho</h5>
                        </div>
                        
                        <div class="col-lg-9">
                            <c:if test="${sessionScope.user.role.roleId == 3}">
                                <div class="bg-light rounded p-2 mb-3">
                                    <div class="d-flex gap-2 align-items-stretch position-relative">
                                        <div class="flex-grow-1 position-relative">
                                            <input type="text" id="searchProduct" class="form-control" style="height: 40px;" placeholder="Mã hàng, tên sản phẩm...">
                                            <div id="searchDropdown" class="dropdown-menu w-100" style="display: none; max-height: 300px; overflow-y: auto;">
                                            </div>
                                        </div>
                                        <button class="btn btn-primary d-flex align-items-center justify-content-center px-4" style="height: 40px; white-space: nowrap; min-width: 100px;" type="button" onclick="searchProduct()">
                                            Tìm kiếm
                                        </button>
                                        <button class="btn btn-success d-flex align-items-center justify-content-center px-4" style="height: 40px; white-space: nowrap; min-width: 120px;" type="button" id="importFileBtn">
                                            Import File
                                        </button>
                                    </div>
                                </div>
                            </c:if>
                            
                            <div class="bg-light rounded p-3">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="productTable">
                                        <thead>
                                            <tr>
                                                <th style="width: 50px;">Id</th>
                                                <th style="width: 220px;">Mã hàng</th>
                                                <th>Tên hàng</th>
                                                <th style="width: 80px;">Đơn vị</th>
                                                <th style="width: 120px;">Giá</th>
                                                <th style="width: 100px;">Số lượng</th>
                                                <c:if test="${sessionScope.user.role.roleId == 3}">
                                                    <th style="width: 80px;">Thao tác</th>
                                                </c:if>
                                            </tr>
                                        </thead>
                                        <tbody id="productTableBody">
                                            <tr>
                                                <td colspan="${sessionScope.user.role.roleId == 3 ? '7' : '6'}" class="text-center text-muted">
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
                                <form id="receiptForm" method="post" action="${pageContext.request.contextPath}/goods-receipt-edit">
                                    <input type="hidden" name="receiptId" value="${receipt.receiptId}">
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Nhà cung cấp</label>
                                        <select class="form-select" name="supplierId" ${sessionScope.user.role.roleId == 2 ? 'disabled' : ''}>
                                            <option value="">Chọn nhà cung cấp</option>
                                            <c:forEach items="${suppliers}" var="s">
                                                <option value="${s.supplierId}" ${receipt.supplier.supplierId eq s.supplierId ? 'selected' : ''}>${s.supplierName}</option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty supplierIdError}">
                                            <small class="text-danger">${supplierIdError}</small>
                                        </c:if>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Ngày nhập</label>
                                        <fmt:formatDate value="${receipt.receiptDate}" pattern="yyyy-MM-dd" var="formattedDate" />
                                        <input type="date" class="form-control" name="receiptDate" id="receiptDate" value="${formattedDate}" ${sessionScope.user.role.roleId == 2 ? 'readonly' : ''} required>
                                        <c:if test="${not empty receiptDateError}">
                                            <small class="text-danger">${receiptDateError}</small>
                                        </c:if>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Mã phiếu nhập</label>
                                        <input type="text" class="form-control" name="receiptCode" placeholder="Nhập mã phiếu (VD: PN001)" value="${receipt.receiptCode}" ${sessionScope.user.role.roleId == 2 ? 'readonly' : ''}>
                                        <c:if test="${not empty receiptCodeError}">
                                            <small class="text-danger">${receiptCodeError}</small>
                                        </c:if>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Mã đơn hàng</label>
                                        <input type="text" class="form-control" name="purchaseOrderCode" placeholder="Nhập mã đơn hàng (nếu có)" value="${receipt.purchaseOrderId}" readonly disabled>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Tổng tiền:</label>
                                        <input type="text" class="form-control" id="totalAmount" readonly value="0 ₫">
                                        <input type="hidden" name="totalAmount" id="totalAmountValue" value="0">
                                    </div>
                                    
                                    <c:if test="${sessionScope.user.role.roleId == 2}">
                                        <div class="mb-3">
                                            <label class="form-label">Trạng thái:</label>
                                            <select class="form-select" name="status">
                                                <option value="draft" ${receipt.status == 'draft' ? 'selected' : ''}>Nháp</option>
                                                <option value="completed" ${receipt.status == 'completed' ? 'selected' : ''}>Hoàn thành</option>
                                                <option value="cancelled" ${receipt.status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                            </select>
                                        </div>
                                    </c:if>
                                    
                                    <div class="mb-4">
                                        <label class="form-label">Ghi chú:</label>
                                        <textarea class="form-control" name="notes" rows="4" placeholder="Nhập ghi chú..." ${sessionScope.user.role.roleId == 2 ? 'readonly' : ''}>${receipt.notes}</textarea>
                                    </div>
                                    
                                    <c:if test="${not empty productsError}">
                                        <div class="alert alert-danger">
                                            ${productsError}
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty generalError}">
                                        <div class="alert alert-danger">
                                            ${generalError}
                                        </div>
                                    </c:if>
                                    
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/goods-receipt-list" class="btn btn-secondary flex-fill">
                                            <c:choose>
                                                <c:when test="${sessionScope.user.role.roleId == 2}">Đóng</c:when>
                                                <c:otherwise>Hủy</c:otherwise>
                                            </c:choose>
                                        </a>
                                        <c:choose>
                                            <c:when test="${sessionScope.user.role.roleId == 2}">
                                                <button type="submit" class="btn btn-success flex-fill">Cập nhật trạng thái</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" class="btn btn-primary flex-fill">Cập nhật</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    
                                    <input type="hidden" name="products" id="productsData">
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
        <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
        
        <!-- Modal for Serial Numbers -->
        <div class="modal fade" id="serialModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="serialModalTitle">
                            <c:choose>
                                <c:when test="${sessionScope.user.role.roleId == 2}">Xem Serial Number</c:when>
                                <c:otherwise>Nhập Serial Number</c:otherwise>
                            </c:choose>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="serialListContent">
                            <p class="text-center text-muted">Vui lòng chọn sản phẩm</p>
                        </div>
                    </div>
                    <c:if test="${sessionScope.user.role.roleId == 3}">
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" id="saveSerials">Lưu</button>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
        
        <!-- Modal for Import Excel -->
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
                                File Excel cần có các cột: <strong>SKU, Quantity, Serial Numbers</strong>
                            </small>
                        </div>
                        <div class="alert alert-info" role="alert">
                            <strong>Hướng dẫn:</strong>
                            <ul class="mb-0">
                                <li>Cột <strong>SKU</strong>: Mã sản phẩm (bắt buộc)</li>
                                <li>Cột <strong>Quantity</strong>: Số lượng (bắt buộc)</li>
                                <li>Cột <strong>Serial Numbers</strong>: Các serial cách nhau bằng dấu phẩy (,)</li>
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
             
        <script>
            const isManager = ${sessionScope.user.role.roleId == 2};
            let products = [];
            let productIdCounter = 1;
            let searchTimeout;
            
            // Load existing receipt products
            <c:if test="${not empty productsJson}">
                try {
                    // Use a hidden div to store JSON safely
                    const jsonDiv = document.createElement('div');
                    jsonDiv.style.display = 'none';
                    jsonDiv.textContent = '${productsJson}';
                    document.body.appendChild(jsonDiv);
                    const jsonStr = jsonDiv.textContent;
                    document.body.removeChild(jsonDiv);
                    
                    const restoredProducts = JSON.parse(jsonStr);
                    products = [];
                    productIdCounter = 1;
                    restoredProducts.forEach(product => {
                        products.push(product);
                        addProductRowFromData(product);
                        // Update counter to avoid ID conflicts
                        if (product.id >= productIdCounter) {
                            productIdCounter = product.id + 1;
                        }
                    });
                    updateTotal();
                } catch (e) {
                }
            </c:if>
            
            // Show dropdown when input is focused or typing
            $('#searchProduct').on('focus', function() {
                loadProducts('');
            });
            
            $('#searchProduct').on('keyup', function() {
                clearTimeout(searchTimeout);
                const searchValue = $(this).val().trim();
                searchTimeout = setTimeout(() => {
                    loadProducts(searchValue);
                }, 300); // Debounce 300ms
            });
            
            // Hide dropdown when clicking outside
            $(document).on('click', function(e) {
                if (!$(e.target).closest('#searchProduct, #searchDropdown').length) {
                    $('#searchDropdown').hide();
                }
            });
            
            function loadProducts(searchValue) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/goods-receipt-edit',
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
                    productList.forEach(product => {
                        const item = $(`
                            <a href="#" class="dropdown-item">
                                <div><strong>\${product.code}</strong> - \${product.name}</div>
                                <small class="text-muted">\${product.unit} - \${formatCurrency(product.price)}</small>
                            </a>
                        `);
                        
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
            
            // Product search functionality
            $('#searchProduct').on('keypress', function(e) {
                if (e.which === 13) {
                    e.preventDefault();
                    // Don't search on Enter, use dropdown selection instead
                }
            });
            
            function searchProduct() {
                // Button click - show all products
                loadProducts('');
            }
            
            function addProductRowFromData(product) {
                // This function is used to restore products from server (no duplicate check)
                // Check if table has empty message
                const emptyRow = $('#productTableBody tr td[colspan]');
                if (emptyRow.length) {
                    $('#productTableBody').empty();
                }
                
                const serialBtnText = isManager ? 'Xem serial' : 'Nhập serial';
                const deleteButtonHtml = isManager ? '' : `
                    <td class="text-center">
                        <button type="button" class="btn btn-sm btn-danger delete-btn" data-id="\${product.id}">
                            <i class="fas fa-times"></i>
                        </button>
                    </td>
                `;
                
                const row = `
                    <tr data-id="\${product.id}">
                        <td>\${product.id}</td>
                        <td>
                            \${product.code}
                            <div class="mt-1">
                                <button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-id="\${product.id}">
                                    <i class="fas fa-barcode me-1"></i>\${serialBtnText}
                                </button>
                            </div>
                        </td>
                        <td>\${product.name}</td>
                        <td>\${product.unit}</td>
                        <td class="text-end">\${formatCurrency(product.price)}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm quantity-display" value="\${product.quantity || 0}" readonly data-id="\${product.id}">
                        </td>
                        \${deleteButtonHtml}
                    </tr>
                `;
                
                $('#productTableBody').append(row);
            }
            
            function addProductRow(product) {
                // Check if product already exists in the table (by variantId)
                const existingProduct = products.find(p => p.variantId === product.variantId);
                if (existingProduct) {
                    // Product already exists - do nothing
                    return;
                }
                
                // Check if table has empty message
                const emptyRow = $('#productTableBody tr td[colspan]');
                if (emptyRow.length) {
                    $('#productTableBody').empty();
                }
                
                // Product doesn't exist - add new row
                products.push(product);
                
                const serialBtnText = isManager ? 'Xem serial' : 'Nhập serial';
                const deleteButtonHtml = isManager ? '' : `
                    <td class="text-center">
                        <button type="button" class="btn btn-sm btn-danger delete-btn" data-id="\${product.id}">
                            <i class="fas fa-times"></i>
                        </button>
                    </td>
                `;
                
                const row = `
                    <tr data-id="\${product.id}">
                        <td>\${product.id}</td>
                        <td>
                            \${product.code}
                            <div class="mt-1">
                                <button type="button" class="btn btn-sm btn-outline-primary serial-btn" data-id="\${product.id}">
                                    <i class="fas fa-barcode me-1"></i>\${serialBtnText}
                                </button>
                            </div>
                        </td>
                        <td>\${product.name}</td>
                        <td>\${product.unit}</td>
                        <td class="text-end">\${formatCurrency(product.price)}</td>
                        <td>
                            <input type="number" class="form-control form-control-sm quantity-display" value="0" readonly data-id="\${product.id}">
                        </td>
                        \${deleteButtonHtml}
                    </tr>
                `;
                
                $('#productTableBody').append(row);
                updateTotal();
            }
            
            // Handle delete
            $(document).on('click', '.delete-btn', function() {
                const id = $(this).data('id');
                products = products.filter(p => p.id !== id);
                $('tr[data-id="' + id + '"]').remove();
                
                if (products.length === 0) {
                    const colspan = isManager ? 6 : 7;
                    $('#productTableBody').html(`
                        <tr>
                            <td colspan="\${colspan}" class="text-center text-muted">
                                Chưa có sản phẩm nào. Tìm kiếm để thêm sản phẩm.
                            </td>
                        </tr>
                    `);
                }
                
                updateTotal();
            });
            
            let currentSerials = [];
            
            // Handle serial button click - show modal to input serial numbers
            $(document).on('click', '.serial-btn', function() {
                const productId = $(this).data('id');
                const product = products.find(p => p.id === productId);
                
                if (!product) return;
                
                // Store current product for modal
                $('#serialModal').data('productId', productId);
                
                // Initialize serials array if not exists
                if (!product.serials) {
                    product.serials = [];
                }
                currentSerials = [...product.serials];
                
                // Show modal
                const modal = new bootstrap.Modal(document.getElementById('serialModal'));
                modal.show();
                
                // Build tag input container
                renderSerialTags();
            });
            
            // Render serial tags
            function renderSerialTags() {
                let tagsHtml = '';
                currentSerials.forEach((serial, index) => {
                    const removeBtn = isManager ? '' : `<span class="remove-tag" onclick="removeSerialTag(\${index})">×</span>`;
                    tagsHtml += `<span class="tag">\${serial} \${removeBtn}</span>`;
                });
                
                const inputHtml = isManager ? '' : `<input type="text" class="tag-input" id="serialTagInput" placeholder="Nhập serial number">`;
                
                const html = `
                    <div class="tag-input-container" onclick="!isManager && document.getElementById('serialTagInput')?.focus()">
                        \${tagsHtml}
                        \${inputHtml}
                    </div>
                    <small id="serialError" class="text-danger" style="display: none;"></small>
                `;
                
                $('#serialListContent').html(html);
                
                if (!isManager) {
                    // Add Enter key handler
                    $('#serialTagInput').on('keypress', function(e) {
                        if (e.key === 'Enter') {
                            e.preventDefault();
                            addSerialTag();
                            updateProductQuantity();
                        }
                    });
                    
                    // Clear error on input
                    $('#serialTagInput').on('input', function() {
                        $('#serialError').hide();
                    });
                }
            }
            
            // Add serial tag
            function addSerialTag() {
                const input = $('#serialTagInput');
                const value = input.val().trim();
                const errorDiv = $('#serialError');
                
                if (!value) {
                    return;
                }
                
                // Check length
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
                
                // Check duplicate in current product
                if (currentSerials.includes(value)) {
                    errorDiv.text('Serial number đã tồn tại trong danh sách này!').show();
                    input.focus();
                    return;
                }
                
                // Check duplicate in other products in the same receipt
                let duplicateInOtherProduct = false;
                products.forEach(p => {
                    if (p.serials && p.serials.includes(value)) {
                        duplicateInOtherProduct = true;
                    }
                });
                
                if (duplicateInOtherProduct) {
                    errorDiv.text('Serial number đã được sử dụng cho sản phẩm khác!').show();
                    input.focus();
                    return;
                }
                
                // Check duplicate in database
                $.ajax({
                    url: '${pageContext.request.contextPath}/goods-receipt-edit',
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
                            // Add new serial
                            currentSerials.push(value);
                            renderSerialTags();
                            // Auto focus back to input for continuous entry
                            $('#serialTagInput').focus();
                        }
                    },
                    error: function() {
                        errorDiv.text('Lỗi khi kiểm tra serial number!').show();
                        input.focus();
                    }
                });
            }
            
            // Remove serial tag
            window.removeSerialTag = function(index) {
                currentSerials.splice(index, 1);
                renderSerialTags();
                updateProductQuantity();
            };
            
            // Update product quantity and total based on current serials
            function updateProductQuantity() {
                const productId = $('#serialModal').data('productId');
                const product = products.find(p => p.id === productId);
                
                if (!product) return;
                
                // Store serials in product object
                product.serials = [...currentSerials];
                
                // Update quantity based on serial count
                product.quantity = product.serials.length;
                
                // Update the quantity display
                $(`.quantity-display[data-id="\${productId}"]`).val(product.quantity);
                
                // Update total
                updateTotal();
            }
            
            // Handle save serial numbers from modal
            $(document).on('click', '#saveSerials', function() {
                // First, add current input value if any (like pressing Enter)
                const currentInput = $('#serialTagInput').val().trim();
                if (currentInput) {
                    addSerialTag();
                }
                
                updateProductQuantity();
            });
            
            function updateTotal() {
                const total = products.reduce((sum, p) => sum + (p.price * p.quantity), 0);
                $('#totalAmount').val(formatCurrency(total));
                $('#totalAmountValue').val(total);
            }
            
            function formatCurrency(amount) {
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                }).format(amount);
            }
            
            // Handle form submission
            $('#receiptForm').on('submit', function(e) {
                if (products.length === 0) {
                    e.preventDefault();
                    alert('Vui lòng thêm ít nhất một sản phẩm!');
                    return false;
                }
                
                // Check if any product has quantity = 0
                const zeroQuantityProducts = products.filter(p => p.quantity === 0);
                if (zeroQuantityProducts.length > 0) {
                    e.preventDefault();
                    alert('Vui lòng nhập serial number!');
                    return false;
                }
                
                // Serials are already stored in product.serials array
                // Just convert products array to JSON
                $('#productsData').val(JSON.stringify(products));
                
                // Clear localStorage after successful submission
            });
            
            // Handle cancel button - clear localStorage
            $('a[href*="goods-receipt-list"]').on('click', function() {
            });
            
            // import excel      
            // Show import modal
            $('#importFileBtn').on('click', function() {
                $('#importError').hide();
                $('#importSuccess').hide();
                $('#excelFile').val('');
                const modal = new bootstrap.Modal(document.getElementById('importModal'));
                modal.show();
            });
            
            // Process import
            $('#processImport').on('click', function() {
                const fileInput = document.getElementById('excelFile');
                const file = fileInput.files[0];
                
                if (!file) {
                    $('#importError').text('Vui lòng chọn file Excel!').show();
                    return;
                }
                
                $('#importError').hide();
                $('#importSuccess').hide();
                
                const reader = new FileReader();
                reader.onload = function(e) {
                    try {
                        const data = new Uint8Array(e.target.result);
                        const workbook = XLSX.read(data, { type: 'array' });
                        const sheetName = workbook.SheetNames[0];
                        const worksheet = workbook.Sheets[sheetName];
                        const jsonData = XLSX.utils.sheet_to_json(worksheet);
                        
                        if (jsonData.length === 0) {
                            $('#importError').text('File Excel không có dữ liệu!').show();
                            return;
                        }
                        
                        // Process each row
                        processExcelData(jsonData);
                        
                    } catch (error) {
                        $('#importError').text('Lỗi khi đọc file: ' + error.message).show();
                    }
                };
                
                reader.readAsArrayBuffer(file);
            });
            
            function processExcelData(jsonData) {
                let successCount = 0;
                let errorCount = 0;
                const errors = [];
                const allDuplicateSerials = []; // Collect all duplicate serials
                const processedInFile = {}; // Track what's being processed in this file
                
                // Process each row
                jsonData.forEach((row, index) => {
                    const sku = (row['SKU'] || row['sku'] || '').toString().trim();
                    const quantity = parseInt(row['Quantity'] || row['quantity']) || 0;
                    const serialsStr = (row['Serial Numbers'] || row['serial numbers'] || row['serials'] || '').toString();
                    
                    if (!sku) {
                        errors.push(`Dòng ${index + 2}: Thiếu mã SKU`);
                        errorCount++;
                        return;
                    }
                    
                    if (quantity <= 0) {
                        errors.push(`Dòng ${index + 2}: Số lượng phải lớn hơn 0`);
                        errorCount++;
                        return;
                    }
                    
                    // Parse serials - trim each serial carefully
                    const serials = serialsStr.split(',')
                        .map(s => s.trim())
                        .filter(s => s.length > 0);
                    
                    if (serials.length !== quantity) {
                        errors.push(`Dòng ${index + 2}: Số lượng serial (${serials.length}) không khớp với quantity (${quantity})`);
                        errorCount++;
                        return;
                    }
                    
                    // Check duplicate in this import file
                    const rowKey = sku + '-' + [...serials].sort().join(',');
                    
                    if (processedInFile[rowKey]) {
                        errors.push(`Dòng ${index + 2}: Dữ liệu trùng với dòng ${processedInFile[rowKey]}`);
                        errorCount++;
                        return;
                    }
                    processedInFile[rowKey] = index + 2;
                    
                    // Search product by SKU via AJAX
                    $.ajax({
                        url: '${pageContext.request.contextPath}/goods-receipt-edit',
                        type: 'POST',
                        data: {
                            action: 'searchProductBySKU',
                            sku: sku
                        },
                        dataType: 'json',
                        async: false,
                        success: function(product) {
                            if (product && product.variantId) {
                                // Check if product already exists
                                const existing = products.find(p => p.variantId === product.variantId);
                                
                                if (existing) {
                                    // Product exists - check serials
                                    let addedCount = 0;
                                    let duplicateSerials = [];
                                    
                                    // Check each serial against both local list and database
                                    serials.forEach(serial => {
                                        // Check if serial already in current product
                                        if (existing.serials.includes(serial)) {
                                            return;
                                        }
                                        
                                        // Check if serial exists in database
                                        let serialExists = false;
                                        $.ajax({
                                            url: '${pageContext.request.contextPath}/goods-receipt-edit',
                                            type: 'POST',
                                            data: {
                                                action: 'checkSerial',
                                                serial: serial
                                            },
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
                                        allDuplicateSerials.push(...duplicateSerials);
                                    }
                                    
                                    if (addedCount > 0) {
                                        // Update quantity
                                        existing.quantity = existing.serials.length;
                                        
                                        // Update display
                                        const row = $('tr[data-id="' + existing.id + '"]');
                                        row.find('.quantity-display').val(existing.quantity);
                                        
                                        successCount++;
                                    } else if (duplicateSerials.length === 0 && serials.length > 0) {
                                        successCount++;
                                    }
                                } else {
                                    let validSerials = [];
                                    let duplicateSerials = [];
                                    
                                    serials.forEach(serial => {
                                        let serialExists = false;
                                        $.ajax({
                                            url: '${pageContext.request.contextPath}/goods-receipt-edit',
                                            type: 'POST',
                                            data: {
                                                action: 'checkSerial',
                                                serial: serial
                                            },
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
                                        allDuplicateSerials.push(...duplicateSerials);
                                    }
                                    
                                    if (validSerials.length > 0) {
                                        const newProduct = {
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
                                errors.push(`Dòng ${index + 2}: Không tìm thấy sản phẩm với SKU: ${sku}`);
                                errorCount++;
                            }
                        },
                        error: function() {
                            errors.push(`Dòng ${index + 2}: Lỗi khi tìm sản phẩm ${sku}`);
                            errorCount++;
                        }
                    });
                });
                
                updateTotal();
                
                if (allDuplicateSerials.length > 0) {
                    errors.unshift('Serial đã tồn tại: ' + allDuplicateSerials.join(', '));
                }
                
                if (successCount > 0 && errors.length === 0) {
                    $('#importSuccess').text('Import thành công ' + successCount + ' sản phẩm!').show();
                    setTimeout(() => {
                        bootstrap.Modal.getInstance(document.getElementById('importModal')).hide();
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
        </script>     
    </body>
</html>
