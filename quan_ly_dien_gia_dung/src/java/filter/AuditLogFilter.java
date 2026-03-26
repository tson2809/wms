package filter;

import dal.AuditLogDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebFilter(filterName = "AuditLogFilter", urlPatterns = {"/*"})
public class AuditLogFilter implements Filter {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String path = httpRequest.getRequestURI().substring(contextPath.length());
        String method = httpRequest.getMethod();

        chain.doFilter(request, response);

        if (!shouldLogRequest(path, method)) {
            return;
        }

        int status = httpResponse.getStatus();
        if (!isSuccessfulMutation(httpRequest, httpResponse, status)) {
            return;
        }

        Integer userId = getCurrentUserId(httpRequest.getSession(false));
        String actionType = resolveActionType(path, method, httpRequest);
        String tableName = resolveTableName(path);

        if (actionType == null || tableName == null) {
            return;
        }

        auditLogDAO.insertAuditLog(userId, actionType, tableName);
    }

    private boolean isSuccessfulMutation(HttpServletRequest request, HttpServletResponse response, int status) {
        // Success with redirect (most POST flows in this codebase)
        if (status >= 300 && status < 400) {
            String location = response.getHeader("Location");
            if (location == null || location.isBlank()) {
                return false;
            }
            String lowerLocation = location.toLowerCase();
            return !(lowerLocation.contains("/login")
                    || lowerLocation.contains("denied=true")
                    || lowerLocation.contains("error=")
                    || lowerLocation.contains("invalid"));
        }

        // Success with forward (e.g., supplier-add uses forward + successMessage)
        if (status >= 200 && status < 300) {
            Object successMessage = request.getAttribute("successMessage");
            Object successFlag = request.getAttribute("success");
            Object messageType = request.getAttribute("messageType");

            if (successMessage != null) {
                return true;
            }
            if (Boolean.TRUE.equals(successFlag)) {
                return true;
            }
            if (messageType != null && "success".equalsIgnoreCase(String.valueOf(messageType))) {
                return true;
            }
        }

        return false;
    }

    private Integer getCurrentUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object raw = session.getAttribute("user");
        if (raw instanceof User) {
            return ((User) raw).getUserId();
        }
        Object rawUserId = session.getAttribute("userId");
        if (rawUserId instanceof Integer) {
            return (Integer) rawUserId;
        }
        return null;
    }

    private boolean shouldLogRequest(String path, String method) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        String lower = path.toLowerCase();
        if (lower.startsWith("/css/") || lower.startsWith("/js/") || lower.startsWith("/img/")
                || lower.startsWith("/lib/") || lower.startsWith("/view/") || lower.startsWith("/image/")) {
            return false;
        }
        if (lower.endsWith(".css") || lower.endsWith(".js") || lower.endsWith(".png")
                || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif")
                || lower.endsWith(".ico") || lower.endsWith(".svg") || lower.endsWith(".woff")
                || lower.endsWith(".woff2") || lower.endsWith(".ttf")) {
            return false;
        }

        if ("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)) {
            return true;
        }

        return false;
    }

    private String resolveActionType(String path, String method, HttpServletRequest request) {
        String action = request.getParameter("action");
        if (action != null && !action.isBlank()) {
            return action.trim().toUpperCase();
        }

        String lowerPath = path.toLowerCase();
        if (lowerPath.contains("toggle-status") || lowerPath.contains("deactivate")) {
            return "TOGGLE_STATUS";
        }
        if (lowerPath.contains("approve")) {
            return "APPROVE";
        }
        if (lowerPath.contains("claim")) {
            return "CLAIM";
        }
        if (lowerPath.contains("cancel")) {
            return "CANCEL";
        }
        if (lowerPath.contains("add") || lowerPath.contains("create")) {
            return "CREATE";
        }
        if (lowerPath.contains("edit") || lowerPath.contains("update") || lowerPath.contains("detail")) {
            return "UPDATE";
        }

        if ("DELETE".equalsIgnoreCase(method)) {
            return "DELETE";
        }
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            return "UPDATE";
        }
        return null;
    }

    private String resolveTableName(String path) {
        String lower = path.toLowerCase();

        // Only map to users table for explicit user-management endpoints.
        if (lower.startsWith("/user-") || lower.equals("/updateuser")) {
            return "users";
        }
        if (lower.contains("permission")) {
            return "role_permissions";
        }
        if (lower.contains("role")) {
            return "roles";
        }
        if (lower.contains("supplier")) {
            return "suppliers";
        }
        if (lower.contains("category")) {
            return "categories";
        }
        if (lower.contains("brand")) {
            return "brands";
        }
        if (lower.contains("unit")) {
            return "units";
        }
        if (lower.contains("product")) {
            return "products";
        }
        if (lower.contains("purchase-order") || lower.contains("sale-order")) {
            return "purchase_orders";
        }
        if (lower.contains("goods-receipt")) {
            return "goods_receipts";
        }
        if (lower.contains("goods-issue")) {
            return "goods_issues";
        }
        if (lower.contains("inventory-sheet")) {
            return "inventory_sheets";
        }
        if (lower.contains("inventory") || lower.contains("transaction")) {
            return "inventory_transactions";
        }
        if (lower.contains("sales-return")) {
            return "sales_returns";
        }
        if (lower.contains("return-order") || lower.contains("return")) {
            return "return_orders";
        }
        if (lower.contains("notification")) {
            return "notifications";
        }

        return null;
    }

}
