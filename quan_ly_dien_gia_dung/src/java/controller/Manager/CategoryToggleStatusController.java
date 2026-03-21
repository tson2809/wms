package controller.Manager;

import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CategoryToggleStatusController", urlPatterns = {"/category-toggle-status"})
public class CategoryToggleStatusController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    private Integer parseIntegerOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw);
        } catch (Exception e) {
            return null;
        }
    }

    private String addParam(String url, String key, String value) {
        if (url == null || url.isBlank()) {
            return "";
        }
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + key + "=" + value;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer categoryId = parseIntegerOrNull(request.getParameter("id"));
        String status = request.getParameter("status");

        String referer = request.getHeader("Referer");
        String fallback = request.getContextPath() + "/category-list";
        String redirectUrl = (referer != null && !referer.isBlank()) ? referer : fallback;

        if (categoryId == null || status == null || status.isBlank()) {
            response.sendRedirect(addParam(redirectUrl, "error", "invalid_request"));
            return;
        }

        boolean ok = categoryDAO.updateCategoryStatus(categoryId, status);
        if (ok) {
            response.sendRedirect(addParam(redirectUrl, "success", "status_updated"));
        } else {
            response.sendRedirect(addParam(redirectUrl, "error", "status_update_failed"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/category-list");
    }
}
