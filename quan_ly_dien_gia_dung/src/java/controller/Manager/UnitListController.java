package controller.Manager;

import dal.UnitDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Unit;
import model.User;

@WebServlet(name = "UnitListController", urlPatterns = {"/unit-list"})
public class UnitListController extends HttpServlet {
    private final UnitDAO unitDAO = new UnitDAO();

    private boolean checkManager(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (user.getRole() == null || !"Manager".equalsIgnoreCase(user.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/indexManager");
            return false;
        }
        return true;
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        if (search == null) search = "";

        String pageParam = request.getParameter("page");
        String perPageParam = request.getParameter("numberPerPage");

        int numberPerPage = 10;
        int page = 1;

        try { numberPerPage = Integer.parseInt(perPageParam); } catch (Exception e) {}
        try { page = Integer.parseInt(pageParam); } catch (Exception e) {}

        List<Unit> allUnits = unitDAO.getAllUnits();

        // Filter by search
        if (!search.trim().isEmpty()) {
            String keyword = search.trim().toLowerCase();
            allUnits.removeIf(u -> !u.getUnitName().toLowerCase().contains(keyword));
        }

        int totalUnits = allUnits.size();
        int listOfPage = (int) Math.ceil((double) totalUnits / numberPerPage);
        if (listOfPage == 0) listOfPage = 1;
        if (page < 1) page = 1;
        if (page > listOfPage) page = listOfPage;

        int fromIndex = (page - 1) * numberPerPage;
        int toIndex = Math.min(fromIndex + numberPerPage, totalUnits);
        List<Unit> units = allUnits.subList(fromIndex, toIndex);

        request.setAttribute("units", units);
        request.setAttribute("search", search);
        request.setAttribute("page", page);
        request.setAttribute("listOfPage", listOfPage);
        request.setAttribute("numberPerPage", numberPerPage);
        request.setAttribute("totalUnits", totalUnits);
        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("messageType", request.getParameter("messageType"));
        request.getRequestDispatcher("/view/manager/unit_list.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) return;
        handleList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) return;

        String deleteIdParam = request.getParameter("deleteId");
        if (deleteIdParam != null) {
            String message;
            String messageType;
            try {
                int id = Integer.parseInt(deleteIdParam);
                if (unitDAO.isUnitUsed(id)) {
                    message = "Không thể xóa đơn vị này vì đang được sử dụng cho sản phẩm.";
                    messageType = "danger";
                } else if (unitDAO.deleteUnit(id)) {
                    message = "Xóa đơn vị thành công.";
                    messageType = "success";
                } else {
                    message = "Không thể xóa đơn vị. Vui lòng thử lại.";
                    messageType = "danger";
                }
            } catch (NumberFormatException e) {
                message = "ID đơn vị không hợp lệ.";
                messageType = "danger";
            }
            response.sendRedirect(request.getContextPath() + "/unit-list?message="
                    + URLEncoder.encode(message, "UTF-8")
                    + "&messageType=" + messageType);
            return;
        }

        // Search / filter POST
        handleList(request, response);
    }
}
