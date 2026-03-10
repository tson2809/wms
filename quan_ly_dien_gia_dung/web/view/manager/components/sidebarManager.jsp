<%-- Document : sidebar Created on : 7 thg 1, 2026, 19:06:03 Author : thais --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <% String currentURI=request.getRequestURI(); String indexActive=currentURI.contains("/indexManager") ? "active"
            : "" ; String supplierActive=(currentURI.contains("/supplier-list") || currentURI.contains("/supplier-add")
            || currentURI.contains("/supplier-detail")) ? "active" : "" ; String purchaseOrderActive=(currentURI.contains("/purchase-order")) ? "active" : "" ; %>
            <!-- Sidebar Start -->
            <div class="sidebar pe-6 pb-5">
                <nav class="navbar bg-light navbar-light">
                    <a href="${pageContext.request.contextPath}/indexManager" class="navbar-brand mx-4 mb-3">
                        <h3 class="text-primary">WMS_HA</h3>
                    </a>
                    <div class="navbar-nav w-100">
                        <a href="${pageContext.request.contextPath}/indexManager"
                            class="nav-item nav-link <%= indexActive %>"><i class="fa fa-tachometer-alt me-2"></i>Trang
                            chủ</a>
                        <a href="${pageContext.request.contextPath}/supplier-list"
                            class="nav-item nav-link <%= supplierActive %>"><i class="fa fa-truck me-2"></i>Nhà cung
                            cấp</a>
                        <a href="${pageContext.request.contextPath}/purchase-order/list"
                            class="nav-item nav-link <%= purchaseOrderActive %>"><i class="fa fa-shopping-cart me-2"></i>Quản lý
                            Purchase Order</a>
                        <a href="/quan_ly_dien_gia_dung/product-list" class="nav-item nav-link"><i class="fa fa-box me-2"></i>Sản phẩm</a>
                    </div>
                </nav>
            </div>
            <!-- Sidebar End -->