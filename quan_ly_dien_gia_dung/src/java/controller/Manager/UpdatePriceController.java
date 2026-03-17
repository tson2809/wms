/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Manager;

import dal.PriceHistoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

/**
 *
 * @author GIAKHANHPC
 */
@WebServlet(name = "UpdatePriceController", urlPatterns = {"/update-price"})
public class UpdatePriceController extends HttpServlet {

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
            out.println("<title>Servlet UpdatePriceController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdatePriceController at " + request.getContextPath() + "</h1>");
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
        String[] variant = dao.getVariantPrice(variantId);
        request.setAttribute("variant", variant);
        request.getRequestDispatcher("/view/manager/update-price.jsp").forward(request, response);
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
        int variantId;
        double newCost;
        double newSale;
        try {
            variantId = Integer.parseInt(request.getParameter("variantId"));
        } catch (Exception e) {
            request.setAttribute("error", "Variant không hợp lệ");
            request.getRequestDispatcher("/view/manager/update-price.jsp").forward(request, response);
            return;
        }
        try {
            newCost = Double.parseDouble(request.getParameter("newCost"));
            newSale = Double.parseDouble(request.getParameter("newSale"));
        } catch (Exception e) {
            request.setAttribute("error", "Giá phải là số hợp lệ");
            request.setAttribute("variant", dao.getVariantPrice(variantId));
            request.getRequestDispatcher("/view/manager/update-price.jsp").forward(request, response);
            return;
        }
        if (newCost < 0 || newSale < 0) {
            request.setAttribute("error", "Giá không được nhỏ hơn 0");
            request.setAttribute("variant", dao.getVariantPrice(variantId));
            request.getRequestDispatcher("/view/manager/update-price.jsp").forward(request, response);
            return;
        }
        String reason = request.getParameter("reason");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        int changedBy = user.getUserId();
        dao.updatePrice(variantId, newCost, newSale, reason, changedBy);
        response.sendRedirect("price-history-detail?variantId=" + variantId);
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
