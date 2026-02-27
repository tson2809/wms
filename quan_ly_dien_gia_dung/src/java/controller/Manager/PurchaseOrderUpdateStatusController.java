package controller.Manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.PurchaseOrderService;

import java.io.IOException;

@WebServlet(name = "PurchaseOrderUpdateStatusController", urlPatterns = {"/purchase-order/update-status"})
public class PurchaseOrderUpdateStatusController extends HttpServlet {
    private PurchaseOrderService purchaseOrderService;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderService = new PurchaseOrderService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 1. Kiểm tra đăng nhập
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Kiểm tra quyền Manager
        User user = (User) session.getAttribute("user");
        if (user.getRole() == null || !"Manager".equalsIgnoreCase(user.getRole().getRoleName())) {
            session.setAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này.");
            response.sendRedirect(request.getContextPath() + "/indexManager");
            return;
        }

        // 3. Lấy tham số
        String idParam = request.getParameter("id");
        String newStatus = request.getParameter("status");
        String source = request.getParameter("source"); // "list" hoặc null (detail)

        String listRedirect = request.getContextPath() + "/purchase-order/list";

        // 4. Validate input cơ bản
        if (idParam == null || idParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Thiếu thông tin đơn đặt hàng.");
            response.sendRedirect(listRedirect);
            return;
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Vui lòng chọn trạng thái mới.");
            redirect(response, request, idParam, source);
            return;
        }

        try {
            int purchaseOrderId = Integer.parseInt(idParam);

            // 5. Gọi service để validate nghiệp vụ và cập nhật
            String result = purchaseOrderService.updatePurchaseOrderStatus(
                    purchaseOrderId, newStatus, user.getUserId());

            if ("SUCCESS".equals(result)) {
                String statusLabel = getStatusLabel(newStatus);
                session.setAttribute("successMessage",
                        "Cập nhật trạng thái đơn hàng thành \"" + statusLabel + "\" thành công!");
            } else {
                session.setAttribute("errorMessage", result);
            }

            redirect(response, request, idParam, source);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(listRedirect);
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            redirect(response, request, idParam, source);
        }
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, 
            String id, String source) throws IOException {
        if ("list".equals(source)) {
            response.sendRedirect(request.getContextPath() + "/purchase-order/list");
        } else {
            response.sendRedirect(request.getContextPath() + "/purchase-order/view?id=" + id);
        }
    }

    private String getStatusLabel(String status) {
        switch (status) {
            case "draft": return "Nháp";
            case "submitted": return "Đã gửi";
            case "approved": return "Đã duyệt";
            case "received": return "Đã nhận hàng";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}
