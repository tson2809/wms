/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.RoleDAO;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import model.Role;
import model.User;

/**
 *
 * @author laptop368
 */

@WebServlet(name = "UpdateUserController", urlPatterns = {"/UpdateUser"})
@MultipartConfig

public class UpdateUserController extends HttpServlet {

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
            out.println("<title>Servlet UpdateUserController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateUserController at " + request.getContextPath() + "</h1>");
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
    private UserDAO userDAO = new UserDAO();
    private RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User user = userDAO.getUserById(id);
        List<Role> roles = roleDAO.getAllRole();
        request.setAttribute("user", user);
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("update_information.jsp").forward(request, response);

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
        int userId = Integer.parseInt(request.getParameter("userId"));
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        int roleId = Integer.parseInt(request.getParameter("roleId"));
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        boolean hasError = false;
        if (username == null || username.isBlank()) {
            request.setAttribute("usernameError", "Username không được để trống");
            hasError = true;
        }

        if (email == null || email.isBlank()) {
            request.setAttribute("emailError", "Email không được để trống");
            hasError = true;
        }

        if (fullName == null || fullName.isBlank()) {
            request.setAttribute("fullNameError", "Họ tên không được để trống");
            hasError = true;
        }

        if (address == null || address.isBlank()) {
            request.setAttribute("addressError", "Địa chỉ không được để trống");
            hasError = true;
        }

        User old = userDAO.getUserById(userId);
        if (hasError) {
            request.setAttribute("user", old);
            request.setAttribute("roles", roleDAO.getAllRole());
            request.getRequestDispatcher("update_information.jsp").forward(request, response);
            return;
        }
        old.setUserName(username);
        old.setEmail(email);
        old.setFullName(fullName);
        old.setAddress(address);
        old.setIsActive(isActive);

        if (password != null && !password.isBlank()) {
            old.setPassword(password); // test, chưa hash
        }

        Role role = new Role();
        role.setRoleId(roleId);
        old.setRole(role);

        // ===== UPLOAD AVATAR =====
        
        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {

            String fileName = Paths.get(avatarPart.getSubmittedFileName())
                    .getFileName().toString();

            String uploadPath = getServletContext().getRealPath("/img");

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String savedFileName = System.currentTimeMillis() + "_" + fileName;
            avatarPart.write(uploadPath + File.separator + savedFileName);

            old.setAvatar(savedFileName);
        }

        userDAO.update(old);

        response.sendRedirect("UpdateUser?id=" + userId);

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
