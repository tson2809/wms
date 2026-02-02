/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Manager;

import dal.ProductViewDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.ProductView;

/**
 *
 * @author laptop368
 */
@WebServlet(name = "ProductListController", urlPatterns = { "/product-list" })
public class ProductListController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ProductListController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProductListController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String search = request.getParameter("search");
        String searchNormalized = (search != null && !search.trim().isEmpty())
                ? search.trim().replaceAll("\\s+", " ")
                : null;

        String categoryIdStr = request.getParameter("categoryId");
        String brandIdStr = request.getParameter("brandId");
        String status = request.getParameter("status");

        // Parse filter IDs
        Integer categoryId = null;
        Integer brandId = null;

        try {
            if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr.trim());
            }
        } catch (NumberFormatException e) {
            // Invalid categoryId, ignore
        }

        try {
            if (brandIdStr != null && !brandIdStr.trim().isEmpty()) {
                brandId = Integer.parseInt(brandIdStr.trim());
            }
        } catch (NumberFormatException e) {
            // Invalid brandId, ignore
        }

        // Pagination parameters
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("numberPerPage");

        int page = 1;
        int size = 10;

        try {
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                page = Integer.parseInt(pageStr.trim());
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        try {
            if (sizeStr != null && !sizeStr.trim().isEmpty()) {
                size = Integer.parseInt(sizeStr.trim());
            }
        } catch (NumberFormatException e) {
            size = 10;
        }

        // Validate size
        if (size != 5 && size != 10 && size != 20) {
            size = 10;
        }

        int offset = (page - 1) * size;
        ProductViewDAO dao = new ProductViewDAO();

        // Get filtered products
        List<ProductView> productList = dao.getProductWithSearchAndFilter(
                searchNormalized, categoryId, brandId, status, offset, size);
        int totalProducts = dao.countProductWithSearchAndFilter(
                searchNormalized, categoryId, brandId, status);
        int totalPages = (int) Math.ceil(totalProducts * 1.0 / size);

        // Ensure page doesn't exceed totalPages
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
            offset = (page - 1) * size;
            productList = dao.getProductWithSearchAndFilter(
                    searchNormalized, categoryId, brandId, status, offset, size);
        }

        // Get categories and brands for dropdowns
        dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
        dal.BrandDAO brandDAO = new dal.BrandDAO();

        request.setAttribute("categories", categoryDAO.getAllCategories());
        request.setAttribute("brands", brandDAO.getAllBrands());

        // Set product data
        request.setAttribute("productList", productList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("numberPerPage", size);
        request.setAttribute("totalProducts", totalProducts);

        // Set filter values for maintaining state
        request.setAttribute("search", search);
        request.setAttribute("categoryId", categoryIdStr);
        request.setAttribute("brandId", brandIdStr);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/view/manager/product_list.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle status update
        String productIdStr = request.getParameter("id");
        String newStatus = request.getParameter("status");

        if (productIdStr != null && newStatus != null) {
            try {
                int productId = Integer.parseInt(productIdStr);
                ProductViewDAO dao = new ProductViewDAO();

                // Update status
                boolean success = dao.updateProductStatus(productId, newStatus);

                if (success) {
                    // Preserve filter parameters when redirecting
                    String search = request.getParameter("search");
                    String categoryId = request.getParameter("categoryId");
                    String brandId = request.getParameter("brandId");
                    String status = request.getParameter("filterStatus");
                    String page = request.getParameter("page");
                    String numberPerPage = request.getParameter("numberPerPage");

                    // Build redirect URL with filters
                    StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/product-list?");

                    if (search != null && !search.isEmpty()) {
                        redirectUrl.append("search=").append(search).append("&");
                    }
                    if (categoryId != null && !categoryId.isEmpty()) {
                        redirectUrl.append("categoryId=").append(categoryId).append("&");
                    }
                    if (brandId != null && !brandId.isEmpty()) {
                        redirectUrl.append("brandId=").append(brandId).append("&");
                    }
                    if (status != null && !status.isEmpty()) {
                        redirectUrl.append("status=").append(status).append("&");
                    }
                    if (page != null && !page.isEmpty()) {
                        redirectUrl.append("page=").append(page).append("&");
                    }
                    if (numberPerPage != null && !numberPerPage.isEmpty()) {
                        redirectUrl.append("numberPerPage=").append(numberPerPage).append("&");
                    }

                    response.sendRedirect(redirectUrl.toString());
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // If update failed or invalid params, redirect to list
        response.sendRedirect(request.getContextPath() + "/product-list");
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
