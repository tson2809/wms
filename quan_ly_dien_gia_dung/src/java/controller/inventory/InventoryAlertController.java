/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.inventory;

import dal.CategoryDAO;
import dal.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.ProductInventory;

/**
 *
 * @author hung
 */
@WebServlet(name = "InventoryAlertController", urlPatterns = {"/inventory-alert"})
public class InventoryAlertController extends HttpServlet {

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
            out.println("<title>Servlet InventoryAlertController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InventoryAlertController at " + request.getContextPath() + "</h1>");
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
    ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String toggle = request.getParameter("toggleAlert");
        if (toggle != null) {
            boolean enabled = toggle.equals("true");
            session.setAttribute("alertEnabled", enabled);
        }
        boolean alertEnabled = true;
        if (session.getAttribute("alertEnabled") != null) {
            alertEnabled = (boolean) session.getAttribute("alertEnabled");
        }
        String status = request.getParameter("status");
        String keyword = request.getParameter("keyword");
        String sort = request.getParameter("sort");
        if (status != null && status.isBlank()) {
            status = null;
        }
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        if (sort != null && sort.isBlank()) {
            sort = null;
        }
        int PAGE_SIZE = 10;
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception ignored) {
        }
        int totalRecords = dao.countInventoryAlerts(status, keyword);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        List<ProductInventory> list
                = dao.getInventoryAlerts(page, PAGE_SIZE, status, keyword, sort);
        request.setAttribute("list", list);
        request.setAttribute("alertCount", totalRecords);
        request.setAttribute("alertEnabled", alertEnabled);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("keyword", keyword);
        request.setAttribute("sort", sort);
        request.setAttribute("activePage", "alert");
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/view/manager/inventory-alert.jsp").forward(request, response);
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
        processRequest(request, response);
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
