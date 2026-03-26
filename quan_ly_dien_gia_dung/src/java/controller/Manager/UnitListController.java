/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Manager;

import dal.UnitDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Unit;

@WebServlet(name = "UnitListController", urlPatterns = {"/unit-list"})
public class UnitListController extends HttpServlet {
    private final UnitDAO unitDAO = new UnitDAO();

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

        List<Unit> allUnits = unitDAO.getAllUnitsForManagement();

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
        handleList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String toggleIdParam = request.getParameter("toggleId");
        if (toggleIdParam != null) {
            String message;
            String messageType;
            try {
                int id = Integer.parseInt(toggleIdParam);
                if (unitDAO.toggleUnitStatus(id)) {
                    message = "Cập nhật trạng thái đơn vị thành công.";
                    messageType = "success";
                } else {
                    message = "Không thể cập nhật trạng thái đơn vị. Vui lòng thử lại.";
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
