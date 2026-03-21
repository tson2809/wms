/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.inventory;

import dal.InventorySheetDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.InventorySheet;
import model.User;

/**
 *
 * @author hung
 */
@WebServlet(name = "InventorySheetApproveController", urlPatterns = {"/inventory-sheet-approve"})
public class InventorySheetApproveController extends HttpServlet {

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
            out.println("<title>Servlet InventorySheetApproveController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InventorySheetApproveController at " + request.getContextPath() + "</h1>");
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    InventorySheetDAO dao = new InventorySheetDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }
        int sheetId = Integer.parseInt(request.getParameter("id"));
        String action = request.getParameter("action");
        InventorySheet sheet = dao.getSheetById(sheetId);
        if (sheet == null) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }
        String status = sheet.getStatus();
        if (status == null || !status.trim().equalsIgnoreCase("submitted")) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }
        if ("approve".equals(action)) {
            dao.approveSheet(sheetId, user.getUserId());
        } else if ("reject".equals(action)) {
            dao.updateStatus(sheetId, "rejected");
        }
        response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
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
