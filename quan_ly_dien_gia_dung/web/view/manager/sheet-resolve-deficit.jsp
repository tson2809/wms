<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Xử lý serial thiếu</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <!-- Favicon -->
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">

        <!-- Google Web Fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">

        <!-- Customized Bootstrap Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">

        <!-- Template Stylesheet -->
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <script>
            function validateSerials() {
                let isValid = true;
                const groups = document.querySelectorAll('.deficit-group');
                groups.forEach(group => {
                    const select = group.querySelector('select');
                    const requiredCount = parseInt(select.getAttribute('data-required'));
                    const selectedCount = select.selectedOptions.length;
                    
                    if (selectedCount !== requiredCount) {
                        alert("Vui lòng chọn đúng " + requiredCount + " serial cho sản phẩm: " + select.getAttribute('data-name'));
                        isValid = false;
                    }
                });
                return isValid;
            }
        </script>
    </head>
    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <jsp:include page="/view/common/components/sidebar.jsp"/>
            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp"/>

                <div class="container-fluid pt-4 px-4">
                    <div class="row">
                        <div class="col-12">
                            <div class="bg-white rounded shadow-sm p-4">
                                <div class="d-flex justify-content-between mb-3">
                                    <h5 class="mb-0">Xử lý serial thiếu cho phiếu #${sheet.sheetId}</h5>
                                    <a href="inventory-sheet-list" class="btn btn-secondary btn-sm">
                                        ← Quay lại
                                    </a>
                                </div>
                                <c:if test="${empty deficitDetails}">
                                    <div class="alert alert-success">Phiếu kiểm kê này không có sản phẩm nào bị thiếu số lượng!</div>
                                </c:if>
                                <c:if test="${not empty deficitDetails}">
                                    <form method="post" action="${pageContext.request.contextPath}/inventory-sheet-resolve-deficit" onsubmit="return validateSerials()">
                                        <input type="hidden" name="sheet_id" value="${sheet.sheetId}">
                                        
                                        <c:forEach items="${deficitDetails}" var="d">
                                            <div class="card mb-3 deficit-group">
                                                <div class="card-header bg-light">
                                                    <strong>${d.productName} (SKU: ${d.sku})</strong>
                                                </div>
                                                <div class="card-body">
                                                    <p class="mb-2 text-danger">Số lượng thiếu: ${d.systemQuantity - d.countedQuantity}</p>
                                                    <label class="form-label">Chọn ${d.systemQuantity - d.countedQuantity} serial để đánh dấu lỗi/mất:</label>
                                                    
                                                    <select name="defective_serial_ids" 
                                                            class="form-select" 
                                                            multiple 
                                                            data-required="${d.systemQuantity - d.countedQuantity}"
                                                            data-name="${d.productName}"
                                                            style="min-height: 150px;">
                                                        <c:forEach items="${serialsMap[d.variantId]}" var="serial">
                                                            <option value="${serial.serialId}">${serial.serialNumber}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <small class="text-muted">Giữ Ctrl (Windows) hoặc Cmd (Mac) để chọn nhiều dòng.</small>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        
                                        <div class="mt-4">
                                            <button type="submit" class="btn btn-primary">Xác nhận chuyển sang Defective</button>
                                        </div>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
</html>
