package controller.Staff;

import dal.SalesReturnDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "SalesReturnClaimController", urlPatterns = {"/sales-return-claim"})
public class SalesReturnClaimController extends HttpServlet {

    private final SalesReturnDAO salesReturnDAO = new SalesReturnDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (user.getRole() == null || user.getRole().getRoleId() != 3) {
            response.sendRedirect(request.getContextPath() + "/sales-return-list");
            return;
        }

        Integer salesReturnId = parsePositiveInt(request.getParameter("id"));
        if (salesReturnId == null) {
            response.sendRedirect(request.getContextPath() + "/sales-return-list");
            return;
        }
        
        String action = request.getParameter("action");
        if ("cancel".equalsIgnoreCase(action)) {
            salesReturnDAO.cancelClaimSalesReturn(salesReturnId, user.getUserId());
        } else {
            
            salesReturnDAO.claimSalesReturn(salesReturnId, user.getUserId());
        }
        response.sendRedirect(request.getContextPath() + "/sales-return-list");
    }

    private Integer parsePositiveInt(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            int n = Integer.parseInt(s.trim());
            return n > 0 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

