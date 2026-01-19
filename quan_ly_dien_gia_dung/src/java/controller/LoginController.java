/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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

        String storedPassword = fullUser.getPassword();
        if (storedPassword == null || !password.equals(storedPassword)) {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", fullUser);
        session.setAttribute("userId", fullUser.getUserId());
        session.setAttribute("userEmail", fullUser.getEmail());
        session.setAttribute("userName", fullUser.getFullName());
        session.setAttribute("userRole", fullUser.getRole());

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

        String redirectUrl = getRedirectUrlByRole(fullUser.getRole());
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    private String getRedirectUrlByRole(model.Role role) {
        if (role == null) {
            return "/view/common/login.jsp";
        }
        
        String roleName = role.getRoleName();
        if (roleName == null) {
            return "/view/common/login.jsp";
        }
        
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
                return "/view/common/login.jsp";
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}
