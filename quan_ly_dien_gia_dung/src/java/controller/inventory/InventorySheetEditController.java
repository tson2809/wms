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
import java.util.List;
import model.InventorySheet;
import model.ProductInventory;
import model.User;

/**
 *
 * @author hung
 */
@WebServlet(name = "InventorySheetEditController", urlPatterns = {"/inventory-sheet-edit"})
public class InventorySheetEditController extends HttpServlet {

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
            out.println("<title>Servlet InventorySheetEditController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InventorySheetEditController at " + request.getContextPath() + "</h1>");
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
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        int sheetId = Integer.parseInt(request.getParameter("id"));
        InventorySheetDAO dao = new InventorySheetDAO();
        InventorySheet sheet = dao.getSheetById(sheetId);
        if (sheet == null
                || !"draft".equals(sheet.getStatus())
                || sheet.getCreatedBy() == null
                || sheet.getCreatedBy() != user.getUserId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        List<ProductInventory> details = dao.getSheetDetails(sheetId);
        request.setAttribute("sheet", sheet);
        request.setAttribute("details", details);
        request.getRequestDispatcher("/view/common/sheet-edit.jsp").forward(request, response);
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
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        int sheetId = Integer.parseInt(request.getParameter("sheetId"));
        InventorySheetDAO dao = new InventorySheetDAO();
        InventorySheet sheet = dao.getSheetById(sheetId);
        if (sheet == null
                || sheet.getCreatedBy() == null
                || sheet.getCreatedBy() != user.getUserId()
                || !"draft".equalsIgnoreCase(sheet.getStatus())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String[] detailIds = request.getParameterValues("detailId");
        String[] countedQtys = request.getParameterValues("countedQty");
        dao.updateCountedQuantities(detailIds, countedQtys);
        dao.updateStatus(sheetId, "submitted");
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
