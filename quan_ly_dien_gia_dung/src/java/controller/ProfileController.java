/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.util.UUID;
import model.UserH;

/**
 *
 * @author hung
 */
@WebServlet(name = "ProfileController", urlPatterns = {"/profile"})
@MultipartConfig
public class ProfileController extends HttpServlet {

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
            out.println("<title>Servlet ProfileController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProfileController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    private UserDAO userDAO = new UserDAO();

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserH loggedUser = (session != null) ? (UserH) session.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("login");
            return;
        }
        UserH user = userDAO.getUserByIdH(loggedUser.getUserId());
        request.setAttribute("user", user);
        request.setAttribute("activePage", "profile");
        request.getRequestDispatcher("/view/common/profile.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        UserH loggedUser = (session != null) ? (UserH) session.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("login");
            return;
        }
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        if (phone == null) {
            phone = loggedUser.getPhone();
        }
        if (address == null) {
            address = loggedUser.getAddress();
        }
        String avatarName = loggedUser.getAvatar();
        String projectRoot = System.getProperty("user.dir");
        String uploadPath = projectRoot + File.separator + "uploads" + File.separator + "images";
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {

            String originalName = avatarPart.getSubmittedFileName();
            String ext = "";
            int dot = originalName.lastIndexOf(".");
            if (dot > 0) {
                ext = originalName.substring(dot);
            }
            String newAvatar = UUID.randomUUID().toString() + ext;
            avatarPart.write(uploadPath + File.separator + newAvatar);
            avatarName = newAvatar;
        }
        UserH user = new UserH();
        user.setUserId(loggedUser.getUserId());
        user.setPhone(phone);
        user.setAddress(address);
        user.setAvatar(avatarName);
        boolean success = userDAO.updateProfile(user);
        if (success) {
            loggedUser.setPhone(phone);
            loggedUser.setAddress(address);
            loggedUser.setAvatar(avatarName);
            session.setAttribute("user", loggedUser);
            response.sendRedirect("profile?success=true");
        } else {
            response.sendRedirect("profile?error=true");
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
