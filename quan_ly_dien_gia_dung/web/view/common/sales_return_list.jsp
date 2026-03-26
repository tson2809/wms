
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="utf-8">
        <title>Danh sách đơn trả hàng (Sale về kho)</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        <meta content="" name="keywords">
        <meta content="" name="description">
        <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="icon">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;600;700&display=swap"
              rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css"
              rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"
              rel="stylesheet">
        <link href="${pageContext.request.contextPath}/lib/owlcarousel/assets/owl.carousel.min.css"
              rel="stylesheet">
        <link
            href="${pageContext.request.contextPath}/lib/tempusdominus/css/tempusdominus-bootstrap-4.min.css"
            rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

        <style>
            .sales-return-list-section .status-badge {
                padding: 4px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
                display: inline-block;
            }

            .sales-return-list-section .status-pending {
                background-color: #fef3c7;
                color: #92400e;
            }

            .sales-return-list-section .status-processing {
                background-color: #dbeafe;
                color: #1e40af;
            }

            .sales-return-list-section .status-completed {
                background-color: #d1fae5;
                color: #065f46;
            }

            .sales-return-list-section .status-cancelled {
                background-color: #fee2e2;
                color: #991b1b;
            }

            .sales-return-list-section .refund-not_refunded {
                background-color: #fef3c7;
                color: #92400e;
            }

            .sales-return-list-section .refund-refunded {
                background-color: #d1fae5;
                color: #065f46;
            }

            .sales-return-list-section .page-btn {
                width: 32px;
                height: 32px;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                text-decoration: none;
                color: #374151;
                font-weight: 600;
            }

            .sales-return-list-section .page-btn.disabled {
                pointer-events: none;
                opacity: 0.4;
            }

            .sales-return-list-section .page-number {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                color: #374151;
            }

            .sales-return-list-section .page-jump-form input {
                width: 48px;
                height: 30px;
                text-align: center;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
            }

            .sales-return-list-section .action-col {
                width: 140px;
                text-align: center;
            }

            .sales-return-list-section .action-btn-group {
                display: flex;
                justify-content: center;
                gap: 1rem;
            }

            .sales-return-list-section .action-btn {
                width: 38px;
                height: 38px;
                border-radius: 50%;
                border: 1px solid #e5e7eb;
                background-color: #fff;
                color: #374151;
                display: flex;
                justify-content: center;
                align-items: center;
                font-size: 18px;
                text-decoration: none;
            }

            .sales-return-list-section .action-btn.action-view:hover {
                background-color: #eef2ff;
                color: #4338ca;
            }
        </style>
    </head>

    <body>
        <div class="container-fluid position-relative bg-white d-flex p-0">
            <c:choose>
                <c:when test="${sessionScope.user.role.roleId == 2}">
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 3}">
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:when test="${sessionScope.user.role.roleId == 4}">
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/view/common/components/sidebar.jsp" />
                </c:otherwise>
            </c:choose>

            <div class="content">
                <jsp:include page="/view/common/components/navbar.jsp" />

                <div class="container-fluid pt-4 px-4">
                    <div class="row g-4">
                        <div class="col-12 sales-return-list-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0 fw-semibold">Danh sách đơn trả hàng từ Sale về kho</h5>
                                    <c:if test="${sessionScope.user.role.roleId == 4}">
                                        <a href="${pageContext.request.contextPath}/sales-return-add"
                                           class="btn btn-primary">
                                            <i class="fa fa-plus me-2"></i>Tạo đơn trả hàng
                                        </a>
                                    </c:if>
                            </div>

                            <!-- Filter Form -->
                            <form method="get" action="${pageContext.request.contextPath}/sales-return-list"
                                  class="mb-4 product-filter-form">
                                <div class="row g-3">
                                    <div class="col-md-4">
                                        <label class="form-label">Tìm kiếm</label>
                                        <input type="text" name="search" value="${search}"
                                               class="form-control" placeholder="Mã đơn trả hàng...">
                                    </div>

                                    <div class="col-md-2">
                                        <label class="form-label">Trạng thái đơn</label>
                                        <select name="orderStatus" class="form-select"
                                                onchange="this.form.submit()">
                                            <option value="">Tất cả</option>
                                            <option value="pending" ${orderStatus=='pending' ? 'selected': '' }>Chờ xử lý</option>
                                            <option value="processing" ${orderStatus=='processing'? 'selected' : '' }>Đang xử lý</option>
                                            <option value="completed" ${orderStatus=='completed'? 'selected' : '' }>Hoàn tất</option>
                                            <option value="cancelled" ${orderStatus=='cancelled'? 'selected' : '' }>Đã hủy</option>
                                        </select>
                                    </div>

                                    <div class="col-md-2">
                                        <label class="form-label">Trạng thái hoàn tiền</label>
                                        <select name="refundStatus" class="form-select"
                                                onchange="this.form.submit()">
                                            <option value="">Tất cả</option>
                                            <option value="not_refunded" ${refundStatus=='not_refunded'? 'selected' : '' }>Chưa hoàn</option>
                                            <option value="refunded" ${refundStatus=='refunded' ? 'selected': '' }>Đã hoàn</option>
                                        </select>
                                    </div>

                                    <div class="col-md-4">
                                        <label class="form-label">&nbsp;</label>
                                        <div class="d-flex flex-nowrap gap-2 align-items-center">
                                            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                                            <a href="${pageContext.request.contextPath}/sales-return-list"
                                               class="btn btn-secondary">Xóa bộ lọc</a>
                                        </div>
                                    </div>
                                </div>
                            </form>

                            <table class="table table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th style="width: 120px;">Mã đơn</th>
                                        <th style="width: 110px;">Ngày tạo</th>
                                        <th style="width: 120px;">Trạng thái đơn</th>
                                        <th style="width: 150px;">Trạng thái hoàn tiền</th>
                                        <th>Người tạo</th>
                                        <th>Nhân viên thực hiện</th>
                                        <th class="action-col">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty salesReturns}">
                                            <tr>
                                                <td colspan="7" class="text-center py-4">
                                                    <p class="mb-0">Không có đơn trả hàng nào</p>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach items="${salesReturns}" var="sr">
                                                <tr>
                                                    <td>
                                                        <div class="return-code">${sr.srCode}</div>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${sr.returnDate != null}">
                                                                <fmt:formatDate value="${sr.returnDate}"
                                                                                pattern="dd/MM/yyyy" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <fmt:formatDate value="${sr.createdAt}"
                                                                                pattern="dd/MM/yyyy" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${sr.status == 'pending'}">
                                                                <span class="status-badge status-pending">Chờ xử lý</span>
                                                            </c:when>
                                                            <c:when test="${sr.status == 'processing'}">
                                                                <span class="status-badge status-processing">Đang xử lý</span>
                                                            </c:when>
                                                            <c:when test="${sr.status == 'completed'}">
                                                                <span class="status-badge status-completed">Hoàn tất</span>
                                                            </c:when>
                                                            <c:when test="${sr.status == 'cancelled'}">
                                                                <span class="status-badge status-cancelled">Đã hủy</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-badge">${sr.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${sr.refundStatus == 'refunded'}">
                                                                <span class="status-badge refund-refunded">Đã hoàn</span>
                                                            </c:when>
                                                            <c:when test="${sr.refundStatus == 'not_refunded'}">
                                                                <span class="status-badge refund-not_refunded">Chưa hoàn</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-badge">${sr.refundStatus}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${sr.createdByUserName != null && !empty sr.createdByUserName ? sr.createdByUserName : '—'}</td>
                                                    <td>${sr.receivedByUserName != null && !empty sr.receivedByUserName ? sr.receivedByUserName : '—'}</td>
                                                    <td class="action-col">
                                                        <div class="action-btn-group">
                                                            <c:choose>
                                                                <c:when test="${sessionScope.user.role.roleId == 4}">
                                                                    <a href="${pageContext.request.contextPath}/sales-return-edit?id=${sr.salesReturnId}"
                                                                               class="action-btn action-view"
                                                                               title="Chỉnh sửa đơn trả hàng">
                                                                                <iconify-icon icon="lucide:edit-2"></iconify-icon>
                                                                            </a>
                                                                            <c:if test="${sr.status == 'pending' && sr.receivedBy == null}">
                                                                                <form action="${pageContext.request.contextPath}/sales-return-list"
                                                                                      method="post"
                                                                                      class="d-inline"
                                                                                      onsubmit="return confirm('Bạn xác nhận hủy đơn trả hàng này?');">
                                                                                    <input type="hidden" name="id" value="${sr.salesReturnId}">
                                                                                    <input type="hidden" name="action" value="cancel">
                                                                                    <input type="hidden" name="search" value="${search}">
                                                                                    <input type="hidden" name="orderStatus" value="${orderStatus}">
                                                                                    <input type="hidden" name="refundStatus" value="${refundStatus}">
                                                                                    <input type="hidden" name="page" value="${currentPage}">
                                                                                    <input type="hidden" name="numberPerPage" value="${numberPerPage}">
                                                                                    <button type="submit"
                                                                                            class="action-btn action-view"
                                                                                            title="Hủy đơn trả hàng"
                                                                                            style="cursor:pointer;">
                                                                                        <iconify-icon icon="lucide:x"></iconify-icon>
                                                                                    </button>
                                                                                </form>
                                                                            </c:if>
                                                                            
                                                                </c:when>
                                                                <c:when test="${sessionScope.user.role.roleId == 3}">
                                                                    <c:if test="${sr.status == 'pending' && sr.receivedBy == null}">
                                                                        <form action="${pageContext.request.contextPath}/sales-return-claim" method="post" class="d-inline"
                                                                              onsubmit="return confirm('Bạn xác nhận nhận đơn trả hàng này để thực hiện?');">
                                                                            <input type="hidden" name="id" value="${sr.salesReturnId}">
                                                                            <button type="submit" class="action-btn border-0 bg-transparent p-0" title="Nhận đơn"
                                                                                    style="cursor:pointer;">
                                                                                <iconify-icon icon="lucide:user-check"></iconify-icon>
                                                                            </button>
                                                                        </form>
                                                                    </c:if>
                                                                    <c:if test="${sr.status == 'processing' && sr.receivedBy == sessionScope.user.userId}">
                                                                        <a href="${pageContext.request.contextPath}/goods-receipt-add?salesReturnId=${sr.salesReturnId}"
                                                                           class="action-btn action-view"
                                                                           title="Tạo phiếu nhập kho">
                                                                            <iconify-icon icon="lucide:package-plus"></iconify-icon>
                                                                        </a>
                                                                        <form action="${pageContext.request.contextPath}/sales-return-claim" method="post" class="d-inline"
                                                                              onsubmit="return confirm('Bạn xác nhận hủy việc đã nhận đơn trả hàng này?');">
                                                                            <input type="hidden" name="id" value="${sr.salesReturnId}">
                                                                            <input type="hidden" name="action" value="cancel">
                                                                            <button type="submit"
                                                                                    class="action-btn action-view"
                                                                                    title="Hủy nhận đơn"
                                                                                    style="cursor:pointer;">
                                                                                <iconify-icon icon="lucide:x"></iconify-icon>
                                                                            </button>
                                                                        </form>
                                                                    </c:if>
                                                                    <a href="${pageContext.request.contextPath}/sales-return-edit?id=${sr.salesReturnId}"
                                                                       class="action-btn action-view"
                                                                       title="Xem đơn trả hàng">
                                                                        <iconify-icon icon="lucide:eye"></iconify-icon>
                                                                    </a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <a href="${pageContext.request.contextPath}/sales-return-edit?id=${sr.salesReturnId}"
                                                                       class="action-btn action-view"
                                                                       title="Xem đơn trả hàng">
                                                                        <iconify-icon icon="lucide:eye"></iconify-icon>
                                                                    </a>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>

                            <!-- Pagination Section (giống return_list) -->
                            <div class="d-flex flex-wrap align-items-center gap-3 pt-3 mb-4 border-top-0">
                                <div class="d-flex align-items-center gap-2 flex-grow-1">
                                    <c:choose>
                                        <c:when test="${currentPage > 1}">
                                            <form method="get"
                                                  action="${pageContext.request.contextPath}/sales-return-list"
                                                  class="d-inline">
                                                <input type="hidden" name="page" value="${currentPage - 1}">
                                                <input type="hidden" name="numberPerPage"
                                                       value="${numberPerPage}">
                                                <input type="hidden" name="search" value="${search}">
                                                <input type="hidden" name="orderStatus"
                                                       value="${orderStatus}">
                                                <input type="hidden" name="refundStatus"
                                                       value="${refundStatus}">
                                                <button type="submit"
                                                        class="page-btn border-0 bg-transparent p-0">‹</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="page-btn disabled">‹</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <span class="page-number">
                                        Trang
                                        <form action="${pageContext.request.contextPath}/sales-return-list"
                                              method="get" class="page-jump-form d-inline">
                                            <input type="hidden" name="numberPerPage"
                                                   value="${numberPerPage}">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="orderStatus"
                                                   value="${orderStatus}">
                                            <input type="hidden" name="refundStatus"
                                                   value="${refundStatus}">
                                            <input type="number" name="page" min="1" max="${totalPages}"
                                                   value="${currentPage}" onchange="this.form.submit()">
                                        </form>
                                        / ${totalPages > 0 ? totalPages : 1}
                                    </span>

                                    <c:choose>
                                        <c:when test="${currentPage < totalPages}">
                                            <form method="get"
                                                  action="${pageContext.request.contextPath}/sales-return-list"
                                                  class="d-inline">
                                                <input type="hidden" name="page" value="${currentPage + 1}">
                                                <input type="hidden" name="numberPerPage"
                                                       value="${numberPerPage}">
                                                <input type="hidden" name="search" value="${search}">
                                                <input type="hidden" name="orderStatus"
                                                       value="${orderStatus}">
                                                <input type="hidden" name="refundStatus"
                                                       value="${refundStatus}">
                                                <button type="submit"
                                                        class="page-btn border-0 bg-transparent p-0">›</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="page-btn disabled">›</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="d-flex align-items-center gap-2 ms-n5">
                                    <label class="form-label small mb-0 me-2">Hiển thị</label>
                                    <form method="get"
                                          action="${pageContext.request.contextPath}/sales-return-list"
                                          class="d-inline" id="numberPerPageForm">
                                        <input type="hidden" name="page" value="1">
                                        <input type="hidden" name="search" value="${search}">
                                        <input type="hidden" name="orderStatus"
                                               value="${orderStatus}">
                                        <input type="hidden" name="refundStatus"
                                               value="${refundStatus}">
                                        <select name="numberPerPage"
                                                class="form-select form-select-sm w-auto"
                                                onchange="this.form.submit()">
                                            <option value="5" ${numberPerPage==5 ? 'selected' : '' }>5</option>
                                            <option value="10" ${numberPerPage==10 ? 'selected' : '' }>10</option>
                                            <option value="20" ${numberPerPage==20 ? 'selected' : '' }>20</option>
                                        </select>
                                    </form>
                                    <span class="small text-muted">kết quả</span>
                                </div>

                                <div class="flex-grow-1"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
</html>

