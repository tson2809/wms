/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

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
import model.User;
import model.Role;

/**
 *
 * @author thais
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/view/admin/*", "/view/manager/*", "/view/sale/*", "/view/staff/*"})
public class AuthenticationFilter implements Filter {


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

        // Kiểm tra quyền truy cập trang dựa trên role
        String roleNameLower = roleName.toLowerCase();
        boolean hasAccess = false;

        // Kiểm tra URL yêu cầu và so sánh với role của user
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
            String redirectUrl = getRedirectUrlByRole(roleNameLower);
            httpResponse.sendRedirect(contextPath + redirectUrl);
            return;
        }

        // User có quyền truy cập -> tiếp tục xử lý request
        chain.doFilter(request, response);
    }

    private String getRedirectUrlByRole(String roleName) {
        switch (roleName.toLowerCase()) {
            case "admin":
                return "/indexAdmin";
            case "manager":
                return "/indexManager";
            case "sale":
                return "/indexSale";
            case "staff":
                return "/indexStaff";
            default:
                return "/login";
        }
    }

}
