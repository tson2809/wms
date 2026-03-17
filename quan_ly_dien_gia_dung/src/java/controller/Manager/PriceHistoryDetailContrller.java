/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Manager;

import com.google.gson.Gson;
import dal.PriceHistoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GIAKHANHPC
 */
@WebServlet(name = "PriceHistoryDetailContrller", urlPatterns = {"/price-history-detail"})
public class PriceHistoryDetailContrller extends HttpServlet {

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
            out.println("<title>Servlet PriceHistoryDetailContrller</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PriceHistoryDetailContrller at " + request.getContextPath() + "</h1>");
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
    PriceHistoryDAO dao = new PriceHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int variantId = Integer.parseInt(request.getParameter("variantId"));
        String[] product = dao.getVariantInfo(variantId);
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        int page = 1;
        int pageSize = 10;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception ignored) {
        }
        List<String[]> list = dao.getPriceHistoryByVariant(
                variantId, fromDate, toDate, page, pageSize);
        int total = dao.countPriceHistory(variantId, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) total / pageSize);
        List<String[]> chartList = dao.getPriceHistoryForChart(
                variantId, fromDate, toDate);
        List<String> dates = new ArrayList<>();
        List<Double> costPrices = new ArrayList<>();
        List<Double> salePrices = new ArrayList<>();
        for (String[] h : chartList) {
            dates.add(h[2]);
            costPrices.add(Double.valueOf(h[0]));
            salePrices.add(Double.valueOf(h[1]));
        }
        request.setAttribute("historyList", list);
        request.setAttribute("dates", new Gson().toJson(dates));
        request.setAttribute("costPrices", new Gson().toJson(costPrices));
        request.setAttribute("salePrices", new Gson().toJson(salePrices));
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("product", product);
        request.getRequestDispatcher("/view/manager/price-history-detail.jsp").forward(request, response);
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
