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
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/view/admin/*", "/view/manager/*", "/view/sale/*", "/view/staff/*"})
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

        // Kiểm tra quyền truy cập trang dựa trên role
        String roleNameLower = roleName.toLowerCase();
        boolean hasAccess = false;

        if (requestURI.contains("/view/admin/")) {
            hasAccess = roleNameLower.equals("admin");
        } else if (requestURI.contains("/view/manager/")) {
            hasAccess = roleNameLower.equals("manager");
        } else if (requestURI.contains("/view/sale/")) {
            hasAccess = roleNameLower.equals("sale");
        } else if (requestURI.contains("/view/staff/")) {
            hasAccess = roleNameLower.equals("staff");
        }

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
                return getPermissionBasedLanding(permissions, "/purchase-order/list");
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
