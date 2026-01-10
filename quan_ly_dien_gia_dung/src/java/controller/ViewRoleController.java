/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.RoleDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import model.Role;

/**
 *
 * @author laptop368
 */
public class ViewRoleController extends HttpServlet {
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
    
   
    
    
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private RoleDAO roleDAO = new RoleDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            // Lấy danh sách tất cả roles
            List<Role> roles = roleDAO.getAllRole();
            
            // Set attribute để hiển thị trong JSP
            request.setAttribute("roles", roles);
            
            // Forward đến viewrole.jsp
            request.getRequestDispatcher("viewrole.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ViewRole?error=true");
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            // Lấy các tham số từ form
            String roleIdStr = request.getParameter("roleId");
            String roleName = request.getParameter("roleName");
            String roleDescription = request.getParameter("roleDescription");
            String isActiveStr = request.getParameter("isActive");
            
            // Validation
            if (roleIdStr == null || roleName == null || roleName.trim().isEmpty()) {
                response.sendRedirect("ViewRole?error=true");
                return;
            }
            
            int roleId;
            try {
                roleId = Integer.parseInt(roleIdStr);
            } catch (NumberFormatException e) {
                response.sendRedirect("ViewRole?error=true");
                return;
            }
            
            boolean isActive = "true".equals(isActiveStr);
            
            // Tạo Role object
            Role role = new Role();
            role.setRoleId(roleId);
            role.setRoleName(roleName.trim());
            role.setRoleDescription(roleDescription != null ? roleDescription.trim() : "");
            role.setIsActive(isActive);
            
            // Update vào database
            int rowsAffected = roleDAO.updateRole(role);
            
            if (rowsAffected > 0) {
                response.sendRedirect("ViewRole?success=true");
            } else {
                response.sendRedirect("ViewRole?error=true");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ViewRole?error=true");
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Controller để quản lý cấp bậc (roles)";
    }

}
