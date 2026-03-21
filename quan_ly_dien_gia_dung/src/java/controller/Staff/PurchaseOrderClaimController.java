package controller.Staff;

import dal.PurchaseOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

@WebServlet(name = "PurchaseOrderClaimController", urlPatterns = {"/purchase-order/claim"})
public class PurchaseOrderClaimController extends HttpServlet {
    private PurchaseOrderDAO purchaseOrderDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderDAO = new PurchaseOrderDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Thiếu thông tin đơn đặt hàng.");
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
            return;
        }

        try {
            int poId = Integer.parseInt(idParam);
            boolean ok = purchaseOrderDAO.claimPurchaseOrder(poId, user.getUserId());
            if (ok) {
                session.setAttribute("successMessage", "Nhận đơn thành công! Bạn đã trở thành nhân viên phụ trách đơn đặt hàng này.");
            } else {
                session.setAttribute("errorMessage", "Không thể nhận đơn. Đơn hàng có thể đã được người khác nhận hoặc không còn ở trạng thái chờ xử lý.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Mã đơn hàng không hợp lệ.");
        }
        String orderType = request.getParameter("orderType");
        String redirectUrl = request.getContextPath() + "/purchase-order/list"
                + ("sale".equals(orderType) ? "?orderType=sale" : "");
        response.sendRedirect(redirectUrl);
    }
}
