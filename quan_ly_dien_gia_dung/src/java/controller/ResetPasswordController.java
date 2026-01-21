/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author hung
 */
@WebServlet(name = "ResetPasswordController", urlPatterns = {"/reset-password"})
public class ResetPasswordController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ResetPasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResetPasswordController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    UserDAO userDAO = new UserDAO();
    private static final String SECRET_KEY = "9H8fTz2RkL8aWcXQv1u7N4M0JkZPqXyB";
    private static final Set<String> USED_TOKENS = Collections.synchronizedSet(new HashSet<>());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || USED_TOKENS.contains(token)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Integer userId = verifyToken(token);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher("/view/common/reset-password.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || USED_TOKENS.contains(token)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Integer userId = verifyToken(token);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");
        if (!password.equals(confirm)) {
            request.setAttribute("error", "Passwords do not match");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/view/common/reset-password.jsp").forward(request, response);
            return;
        }
        userDAO.updatePassword(userId, password);
        USED_TOKENS.add(token);
        request.setAttribute("message", "Password reset successfully");
        request.getRequestDispatcher("/view/common/login.jsp").forward(request, response);
    }

    private Integer verifyToken(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token));
            String[] p = decoded.split(":");
            if (p.length != 3) {
                return null;
            }
            int userId = Integer.parseInt(p[0]);
            long expiry = Long.parseLong(p[1]);
            if (System.currentTimeMillis() > expiry) {
                return null;
            }
            String data = p[0] + ":" + p[1];
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256"));
            String sig = Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
            if (!sig.equals(p[2])) {
                return null;
            }
            return userId;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
