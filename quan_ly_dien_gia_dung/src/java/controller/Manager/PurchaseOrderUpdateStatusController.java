package controller.Manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller này đã bị thay thế bởi luồng mới:
 * - Manager hủy đơn: POST /purchase-order/list?action=cancel
 * - Staff nhận đơn: POST /purchase-order/claim
 * Giữ lại để tránh lỗi 404 nếu có link cũ.
 */
@WebServlet(name = "PurchaseOrderUpdateStatusController", urlPatterns = {"/purchase-order/update-status"})
public class PurchaseOrderUpdateStatusController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("errorMessage", "Chức năng cập nhật trạng thái đã thay đổi. Vui lòng sử dụng các nút thao tác trong danh sách đơn hàng.");
        }
        response.sendRedirect(request.getContextPath() + "/purchase-order/list");
    }
}
