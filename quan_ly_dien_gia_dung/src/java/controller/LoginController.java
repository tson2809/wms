/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.RoleDAO;
import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author thais
 */
@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("view/common/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.findByUsername(username.trim());

        if (user == null) {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        User fullUser = userDAO.getUserById(user.getUserId());

        if (fullUser == null) {
            request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        if (!fullUser.isIsActive()) {
            request.setAttribute("error", "Tài khoản đã bị vô hiệu hóa!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

//        String storedPassword = fullUser.getPassword();
//        if (storedPassword == null) {
//            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
//            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
//            return;
//        }
        
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        if (!encoder.matches(password, storedPassword)) {
//            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
//            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
//            return;
//        }

        HttpSession session = request.getSession();
        session.setAttribute("user", fullUser);
        session.setAttribute("userId", fullUser.getUserId());
        session.setAttribute("userEmail", fullUser.getEmail());
        session.setAttribute("userName", fullUser.getFullName());
        session.setAttribute("userRole", fullUser.getRole());

        List<String> permissionNames = roleDAO.getRolePermissionNames(fullUser.getRoleId());
        Set<String> userPermissions = new HashSet<>(permissionNames);
        session.setAttribute("userPermissions", userPermissions);

        session.setMaxInactiveInterval(30 * 60);

        if (rememberMe != null && rememberMe.equals("on")) {
            Cookie usernameCookie = new Cookie("rememberedUser", username.trim());
            usernameCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            usernameCookie.setPath("/");
            response.addCookie(usernameCookie);
        } else {
            Cookie usernameCookie = new Cookie("rememberedUser", "");
            usernameCookie.setMaxAge(0);
            usernameCookie.setPath("/");
            response.addCookie(usernameCookie);
        }

        String redirectUrl = getRedirectUrlByRole(fullUser.getRole(), userPermissions);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    private String getRedirectUrlByRole(model.Role role, Set<String> permissions) {
        if (role == null) {
            return "/view/common/login.jsp";
        }
        
        String roleName = role.getRoleName();
        if (roleName == null) {
            return "/view/common/login.jsp";
        }
        
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
                return "/view/common/login.jsp";
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

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}
