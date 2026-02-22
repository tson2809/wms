/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.inventory;

import dal.InventoryTransactionDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.InventoryTransaction;

/**
 *
 * @author GIAKHANHPC
 */
@WebServlet(name = "InventoryTransactionListController", urlPatterns = {"/inventory-transactions"})
public class InventoryTransactionListController extends HttpServlet {

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
            out.println("<title>Servlet InventoryTransactionListController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet InventoryTransactionListController at " + request.getContextPath() + "</h1>");
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
    InventoryTransactionDAO dao = new InventoryTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = 1;
        int pageSize = 10;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String refType = request.getParameter("refType");
        String qtyType = request.getParameter("qtyType");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        String sort = request.getParameter("sort");
        String dir = request.getParameter("dir");
        Integer trxId = null;
        Integer refId = null;
        Integer variantId = null;
        String createdBy = null;
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        if (type != null && type.trim().isEmpty()) {
            type = null;
        }
        if (refType != null && refType.trim().isEmpty()) {
            refType = null;
        }
        if (dateFrom != null && dateFrom.trim().isEmpty()) {
            dateFrom = null;
        }
        if (dateTo != null && dateTo.trim().isEmpty()) {
            dateTo = null;
        }
        if (qtyType != null && qtyType.trim().isEmpty()) {
            qtyType = null;
        }
        try {
            if (request.getParameter("trxId") != null && !request.getParameter("trxId").isEmpty()) {
                trxId = Integer.parseInt(request.getParameter("trxId"));
            }
            if (request.getParameter("refId") != null && !request.getParameter("refId").isEmpty()) {
                refId = Integer.parseInt(request.getParameter("refId"));
            }
            if (request.getParameter("variantId") != null && !request.getParameter("variantId").isEmpty()) {
                variantId = Integer.parseInt(request.getParameter("variantId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<InventoryTransaction> list = dao.getTransactions(
                trxId, refId, variantId, keyword,
                type, qtyType, refType, createdBy,
                dateFrom, dateTo,
                page, pageSize, sort, dir
        );

        int total = dao.countTransactions(
                trxId, refId, variantId, keyword,
                type, qtyType, refType, createdBy,
                dateFrom, dateTo
        );

        int totalPages = (int) Math.ceil((double) total / pageSize);

        request.setAttribute("transactions", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/view/inventory/transaction-list.jsp").forward(request, response);
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
