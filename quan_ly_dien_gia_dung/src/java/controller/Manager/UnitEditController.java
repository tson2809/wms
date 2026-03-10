/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Manager;

import dal.UnitDAO;
import java.io.IOException;
import java.net.URLEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Unit;
import model.User;

@WebServlet(name = "UnitEditController", urlPatterns = {"/unit-edit"})
public class UnitEditController extends HttpServlet {
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) return;

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/unit-list");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Unit unit = unitDAO.getUnitById(id);
            if (unit == null) {
                response.sendRedirect(request.getContextPath() + "/unit-list?message="
                        + URLEncoder.encode("Không tìm thấy đơn vị.", "UTF-8")
                        + "&messageType=danger");
                return;
            }
            request.setAttribute("unit", unit);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/unit-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String idParam = request.getParameter("unitId");
        String unitName = request.getParameter("unitName");

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/unit-list");
            return;
        }

        Unit unit = unitDAO.getUnitById(id);
        if (unit == null) {
            response.sendRedirect(request.getContextPath() + "/unit-list?message="
                    + URLEncoder.encode("Không tìm thấy đơn vị.", "UTF-8")
                    + "&messageType=danger");
            return;
        }

        if (unitName == null || unitName.trim().isEmpty()) {
            request.setAttribute("error", "Tên đơn vị không được để trống.");
            request.setAttribute("unit", unit);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
            return;
        }

        if (unitDAO.unitNameExists(unitName.trim(), id)) {
            request.setAttribute("error", "Tên đơn vị đã tồn tại.");
            unit.setUnitName(unitName);
            request.setAttribute("unit", unit);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
            return;
        }

        if (unitDAO.updateUnit(id, unitName)) {
            response.sendRedirect(request.getContextPath() + "/unit-list?message="
                    + URLEncoder.encode("Cập nhật đơn vị thành công.", "UTF-8")
                    + "&messageType=success");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
            unit.setUnitName(unitName);
            request.setAttribute("unit", unit);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
        }
    }
}
