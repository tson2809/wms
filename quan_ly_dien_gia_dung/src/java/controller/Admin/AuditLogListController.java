package controller.Admin;

import dal.AuditLogDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.AuditLog;
import model.User;

@WebServlet(name = "AuditLogListController", urlPatterns = {"/audit-log-list"})
public class AuditLogListController extends HttpServlet {

    private static final int PAGE_SIZE = 20;
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || user.getRole() == null || user.getRole().getRoleName() == null
                || !"admin".equalsIgnoreCase(user.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/login?denied=true");
            return;
        }

        String keyword = request.getParameter("keyword");
        String actionType = request.getParameter("actionType");
        String tableName = request.getParameter("tableName");
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }

        int totalRecords = auditLogDAO.countAuditLogs(keyword, actionType, tableName);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<AuditLog> logs = auditLogDAO.getAuditLogs(page, PAGE_SIZE, keyword, actionType, tableName);

        request.setAttribute("logs", logs);
        request.setAttribute("keyword", keyword);
        request.setAttribute("actionType", actionType);
        request.setAttribute("tableName", tableName);
        request.setAttribute("actionTypes", auditLogDAO.getDistinctActionTypes());
        request.setAttribute("tableNames", auditLogDAO.getDistinctTableNames());
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/view/admin/audit-log-list.jsp").forward(request, response);
    }
}
