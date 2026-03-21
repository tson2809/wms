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
import model.User;

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
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("login");
            return;
        }
        User user = userDAO.getUserByIdH(loggedUser.getUserId());
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
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedUser == null) {
            response.sendRedirect("login");
            return;
        }
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        Part avatarPart = request.getPart("avatar");
        boolean avatarOnlyRequest = email == null && phone == null && address == null
            && avatarPart != null && avatarPart.getSize() > 0;

        boolean hasError = false;
        String normalizedEmail = avatarOnlyRequest ? loggedUser.getEmail() : (email == null ? "" : email.trim());
        String normalizedPhone = avatarOnlyRequest ? loggedUser.getPhone() : (phone == null ? "" : phone.trim().replaceAll("\\s", ""));
        String normalizedAddress = avatarOnlyRequest ? loggedUser.getAddress() : (address == null ? "" : address.trim());

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String phoneRegex = "^0\\d{9}$";

        if (!avatarOnlyRequest && normalizedEmail.isEmpty()) {
            request.setAttribute("generalError", "Email không được để trống.");
            hasError = true;
        } else if (!avatarOnlyRequest && !normalizedEmail.matches(emailRegex)) {
            request.setAttribute("generalError", "Email không đúng định dạng.");
            hasError = true;
        } else if (!avatarOnlyRequest && userDAO.existsEmail(normalizedEmail, loggedUser.getUserId())) {
            request.setAttribute("generalError", "Email đã tồn tại.");
            hasError = true;
        }

        if (!avatarOnlyRequest && normalizedPhone.isEmpty()) {
            request.setAttribute("generalError", "Số điện thoại không được để trống.");
            hasError = true;
        } else if (!avatarOnlyRequest && !normalizedPhone.matches(phoneRegex)) {
            request.setAttribute("generalError", "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.");
            hasError = true;
        }

        if (!avatarOnlyRequest && normalizedAddress.isEmpty()) {
            request.setAttribute("generalError", "Địa chỉ không được để trống.");
            hasError = true;
        } else if (!avatarOnlyRequest && normalizedAddress.length() > 50) {
            request.setAttribute("generalError", "Địa chỉ tối đa 50 ký tự.");
            hasError = true;
        }

        if (hasError) {
            User user = userDAO.getUserByIdH(loggedUser.getUserId());
            if (user != null) {
                request.setAttribute("user", user);
            } else {
                request.setAttribute("user", loggedUser);
            }
            request.setAttribute("formEmail", email == null ? "" : email.trim());
            request.setAttribute("formPhone", phone == null ? "" : phone.trim());
            request.setAttribute("formAddress", address == null ? "" : address.trim());
            request.setAttribute("editMode", true);
            request.setAttribute("activePage", "profile");
            request.getRequestDispatcher("/view/common/profile.jsp").forward(request, response);
            return;
        }

        String avatarName = loggedUser.getAvatar();
        String uploadPath = getServletContext().getRealPath("/img/avatar");
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (avatarPart != null && avatarPart.getSize() > 0) {

            String originalName = avatarPart.getSubmittedFileName();
            String ext = "";
            int dot = originalName.lastIndexOf(".");
            if (dot > 0) {
                ext = originalName.substring(dot);
            }
            String newAvatar = UUID.randomUUID().toString() + ext;
            avatarPart.write(uploadPath + File.separator + newAvatar);
            avatarName = "img/avatar/" + newAvatar;
        }
        User user = new User();
        user.setUserId(loggedUser.getUserId());
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        user.setAddress(normalizedAddress);
        user.setAvatar(avatarName);
        boolean success = userDAO.updateProfile(user);
        if (success) {
            loggedUser.setEmail(normalizedEmail);
            loggedUser.setPhone(normalizedPhone);
            loggedUser.setAddress(normalizedAddress);
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
