package controller.Manager;

import dal.BrandDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Brand;

@WebServlet(name = "BrandListController", urlPatterns = {"/brand-list"})
public class BrandListController extends HttpServlet {

    private final BrandDAO brandDAO = new BrandDAO();

    private int parseIntOrDefault(String raw, int def) {
        if (raw == null || raw.isBlank()) {
            return def;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");
        String sortDir = request.getParameter("sortDir");

        int page = parseIntOrDefault(request.getParameter("page"), 1);
        int size = parseIntOrDefault(request.getParameter("size"), 10);
        if (size != 5 && size != 10 && size != 20) {
            size = 10;
        }
        if (page < 1) {
            page = 1;
        }

        int totalBrands = brandDAO.countBrands(keyword, status);
        int totalPages = (int) Math.ceil((double) totalBrands / size);
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        List<Brand> brands = brandDAO.getBrandsByPage(page, size, keyword, status, sortBy, sortDir);

        request.setAttribute("brands", brands);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalBrands", totalBrands);
        request.setAttribute("size", size);

        request.getRequestDispatcher("/view/manager/brand-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/brand-list");
    }
}
