package controller.Manager;

import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebServlet(name = "CategoryToggleStatusController", urlPatterns = {"/category-toggle-status"})
public class CategoryToggleStatusController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    private boolean hasRole(HttpServletRequest request, String... roles) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object u = session.getAttribute("user");
        if (!(u instanceof User)) {
            return false;
        }
        User user = (User) u;
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            return false;
        }
        String roleName = user.getRole().getRoleName().toLowerCase();
        for (String r : roles) {
            if (r != null && roleName.equals(r.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

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

        if (!hasRole(request, "manager")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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
