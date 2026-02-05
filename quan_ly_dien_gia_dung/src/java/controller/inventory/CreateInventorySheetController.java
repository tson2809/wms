/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.inventory;

import dal.CategoryDAO;
import dal.InventorySheetDAO;
import dal.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Category;
import java.sql.Date;
import model.ProductInventory;

/**
 *
 * @author hung
 */
@WebServlet(name = "CreateInventorySheetController", urlPatterns = {"/inventory-sheet-create"})
public class CreateInventorySheetController extends HttpServlet {

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
            out.println("<title>Servlet CreateInventorySheetController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateInventorySheetController at " + request.getContextPath() + "</h1>");
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
    CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        String categoryParam = request.getParameter("categoryId");
        if (categoryParam != null && !categoryParam.isEmpty()) {
            int categoryId = Integer.parseInt(categoryParam);

            ProductDAO productDAO = new ProductDAO();
            List<ProductInventory> inventory
                    = productDAO.getInventoryForCounting(categoryId);

            request.setAttribute("inventory", inventory);
        }
        request.getRequestDispatcher("/view/inventory/sheet-create.jsp").forward(request, response);
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
        String categoryParam = request.getParameter("categoryId");
        if (categoryParam == null || categoryParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-create");
            return;
        }
        Integer categoryId = Integer.parseInt(categoryParam);
        String action = request.getParameter("action");
        String[] variantIds = request.getParameterValues("variantId");
        String[] systemQtys = request.getParameterValues("systemQty");
        String[] countedQtys = request.getParameterValues("countedQty");
        if (variantIds == null || countedQtys == null) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-create?categoryId=" + categoryId);
            return;
        }
        InventorySheetDAO sheetDAO = new InventorySheetDAO();
        int sheetId = sheetDAO.createSheet(
                categoryId,
                new Date(System.currentTimeMillis()),
                getUserId(request)
        );
        sheetDAO.insertSheetDetails(sheetId, variantIds, systemQtys, countedQtys);
        if ("submit".equals(action)) {
            sheetDAO.updateStatus(sheetId, "submitted");
        }
        response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
    }

    private int getUserId(HttpServletRequest request) {
        return ((model.User) request.getSession().getAttribute("user")).getUserId();
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
