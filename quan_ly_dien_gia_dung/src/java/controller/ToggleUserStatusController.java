package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ToggleUserStatusController", urlPatterns = {"/user-toggle-status"})
public class ToggleUserStatusController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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

    private Boolean parseBooleanOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        if ("true".equalsIgnoreCase(raw) || "1".equals(raw)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(raw) || "0".equals(raw)) {
            return Boolean.FALSE;
        }
        return null;
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
        Integer userId = parseIntegerOrNull(request.getParameter("id"));
        Boolean active = parseBooleanOrNull(request.getParameter("active"));

        String referer = request.getHeader("Referer");
        String fallback = request.getContextPath() + "/user-list";
        String redirectUrl = (referer != null && !referer.isBlank()) ? referer : fallback;

        if (userId == null || active == null) {
            response.sendRedirect(addParam(redirectUrl, "error", "invalid_request"));
            return;
        }

        boolean ok = userDAO.updateUserStatus(userId, active);
        if (ok) {
            response.sendRedirect(addParam(redirectUrl, "success", "status_updated"));
        } else {
            response.sendRedirect(addParam(redirectUrl, "error", "status_update_failed"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/user-list");
    }
}
