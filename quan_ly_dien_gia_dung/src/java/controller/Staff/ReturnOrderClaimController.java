package controller.Staff;

import dal.ReturnOrderDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

@WebServlet(name = "ReturnOrderClaimController", urlPatterns = {"/return-claim"})
public class ReturnOrderClaimController extends HttpServlet {

    private final ReturnOrderDAO returnOrderDAO = new ReturnOrderDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (user.getRole() == null || user.getRole().getRoleId() != 3) {
            response.sendRedirect(request.getContextPath() + "/return-order-list");
            return;
        }

        String idStr = request.getParameter("id");
        Integer returnOrderId = parsePositiveInt(idStr);
        if (returnOrderId == null) {
            response.sendRedirect(request.getContextPath() + "/return-order-list");
            return;
        }
        
        String action = request.getParameter("action");
        if ("cancel".equalsIgnoreCase(action)) {
            returnOrderDAO.cancelClaimReturnOrder(returnOrderId, user.getUserId());
        } else {
            // mặc định: claim
            returnOrderDAO.claimReturnOrder(returnOrderId, user.getUserId());
        }
        response.sendRedirect(request.getContextPath() + "/return-order-list");
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
