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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author thais
 */
@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
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

        User fullUser;
        try {
            fullUser = userDAO.findByUsernameWithRole(username.trim());
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the dang nhap do loi ket noi CSDL", ex);
            request.setAttribute("error", "Hệ thống đang bận hoặc mất kết nối CSDL. Vui lòng thử lại sau!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        if (fullUser == null) {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        if (!fullUser.isIsActive()) {
            request.setAttribute("error", "Tài khoản đã bị vô hiệu hóa!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        String storedPassword = fullUser.getPassword();
        if (storedPassword == null || storedPassword.trim().isEmpty()) {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("view/common/login.jsp").forward(request, response);
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, storedPassword)) {
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
                return "/user-list";
            case "manager":
                return "/manager-report";
            case "sale":
                return "/purchase-order/list";
            case "staff":
                return "/inventory-list";
            default:
                return "/view/common/login.jsp";
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}
