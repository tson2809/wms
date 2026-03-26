/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import dal.RoleDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.User;
import model.Role;

/**
 *
 * @author thais
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
    "/view/admin/*", "/view/manager/*", "/view/sale/*", "/view/staff/*",
    "/user-list", "/user-add", "/user-edit", "/user-detail", "/user-toggle-status", "/UpdateUser",
    "/viewpermission", "/ViewRole", "/audit-log-list", "/manager-report",
    "/supplier-*", "/category-*", "/brand-*", "/unit-*", "/product-*",
    "/goods-receipt-*", "/goods-issue-*", "/inventory-*", "/transaction-detail", "/search-user", "/export-inventory",
    "/purchase-order/*", "/sale-order/*", "/return-order-*", "/sales-return-*"
})
public class AuthenticationFilter implements Filter {

    private final RoleDAO roleDAO = new RoleDAO();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Kiểm tra người dùng đã đăng nhập chưa
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // Lấy thông tin user từ session
        User user = (User) session.getAttribute("user");
        Role userRole = user.getRole();

        // Kiểm tra user có role không
        if (userRole == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        String roleName = userRole.getRoleName();
        if (roleName == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // Always refresh session permissions so role changes apply immediately.
        refreshSessionPermissions(session, user);

        String path = requestURI.substring(contextPath.length());

        // Kiểm tra quyền truy cập trang dựa trên role
        String roleNameLower = roleName.toLowerCase();
        boolean hasAccess = hasRoleAccess(path, roleNameLower);

        // Nếu không có quyền truy cập
        if (!hasAccess) {
            @SuppressWarnings("unchecked")
            Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
            String redirectUrl = getRedirectUrlByRole(roleNameLower, userPermissions);
            httpResponse.sendRedirect(contextPath + redirectUrl);
            return;
        }

        // User có quyền truy cập -> tiếp tục xử lý request
        chain.doFilter(request, response);
    }

    private boolean hasRoleAccess(String path, String roleNameLower) {
        switch (roleNameLower) {
            case "admin":
                return isAdminPath(path);
            case "manager":
                return isManagerPath(path) || isSharedManagerStaffPath(path) || isSalesReturnPath(path);
            case "staff":
                return isStaffPath(path) || isSharedManagerStaffPath(path) || isSalesReturnPath(path);
            case "sale":
                return isSalePath(path);
            default:
                return false;
        }
    }

    private boolean isAdminPath(String path) {
        return path.startsWith("/view/admin/")
                || "/user-list".equals(path)
                || "/user-add".equals(path)
                || "/user-edit".equals(path)
                || "/user-detail".equals(path)
                || "/user-toggle-status".equals(path)
                || "/UpdateUser".equals(path)
                || "/viewpermission".equals(path)
                || "/ViewRole".equals(path)
                || "/audit-log-list".equals(path);
    }

    private boolean isManagerPath(String path) {
        return path.startsWith("/view/manager/")
                || "/manager-report".equals(path)
                || path.startsWith("/supplier-")
                || path.startsWith("/category-")
                || path.startsWith("/brand-")
                || path.startsWith("/unit-")
                || path.startsWith("/product-");
    }

    private boolean isStaffPath(String path) {
        return path.startsWith("/view/staff/")
                || "/purchase-order/claim".equals(path)
                || "/purchase-order/update-status".equals(path)
                || path.startsWith("/return-order-");
    }

    private boolean isSalePath(String path) {
        return path.startsWith("/view/sale/")
                || path.startsWith("/sale-order/")
                || "/purchase-order/list".equals(path)
                || "/purchase-order/edit".equals(path)
                || "/purchase-order/view".equals(path)
                || isSalesReturnPath(path);
    }

    private boolean isSharedManagerStaffPath(String path) {
        return path.startsWith("/goods-receipt-")
                || path.startsWith("/goods-issue-")
                || path.startsWith("/inventory-")
                || "/transaction-detail".equals(path)
                || "/search-user".equals(path)
                || "/export-inventory".equals(path)
                || path.startsWith("/purchase-order/");
    }

    private boolean isSalesReturnPath(String path) {
        return path.startsWith("/sales-return-");
    }

    private void refreshSessionPermissions(HttpSession session, User user) {
        try {
            List<String> permissionNames = roleDAO.getRolePermissionNames(user.getRoleId());
            Set<String> userPermissions = new HashSet<>(permissionNames);
            session.setAttribute("userPermissions", userPermissions);
        } catch (Exception ignored) {
        }
    }

    private String getRedirectUrlByRole(String roleName, Set<String> permissions) {
        switch (roleName.toLowerCase()) {
            case "admin":
                return "/user-list";
            case "manager":
                return "/manager-report";
            case "sale":
                return getPermissionBasedLanding(permissions, "/sales-return-list");
            case "staff":
                return getPermissionBasedLanding(permissions, "/purchase-order/list");
            default:
                return "/login";
        }
    }

    private String getPermissionBasedLanding(Set<String> permissions, String fallback) {
        if (permissions == null || permissions.isEmpty()) {
            return fallback;
        }
        if (permissions.contains("view inventory")) {
            return "/inventory-list";
        }
        if (permissions.contains("view supplier")) {
            return "/supplier-list";
        }
        if (permissions.contains("view category")) {
            return "/category-list";
        }
        if (permissions.contains("view brand")) {
            return "/brand-list";
        }
        if (permissions.contains("view unit")) {
            return "/unit-list";
        }
        if (permissions.contains("view product")) {
            return "/product-list";
        }
        if (permissions.contains("view purchase order")) {
            return "/purchase-order/list";
        }
        if (permissions.contains("view goods receipt")) {
            return "/goods-receipt-list";
        }
        if (permissions.contains("view goods issue")) {
            return "/goods-issue-list";
        }
        return fallback;
    }

}
