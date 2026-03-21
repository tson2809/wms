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

@WebServlet(name = "UnitAddController", urlPatterns = {"/unit-add"})
public class UnitAddController extends HttpServlet {
    private final UnitDAO unitDAO = new UnitDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("mode", "add");
        request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String unitName = request.getParameter("unitName");

        if (unitName == null || unitName.trim().isEmpty()) {
            request.setAttribute("error", "Tên đơn vị không được để trống.");
            request.setAttribute("unitName", unitName);
            request.setAttribute("mode", "add");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
            return;
        }

        if (unitDAO.unitNameExists(unitName.trim(), null)) {
            request.setAttribute("error", "Tên đơn vị đã tồn tại.");
            request.setAttribute("unitName", unitName);
            request.setAttribute("mode", "add");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
            return;
        }

        if (unitDAO.createUnit(unitName)) {
            response.sendRedirect(request.getContextPath() + "/unit-list?message="
                    + URLEncoder.encode("Thêm đơn vị thành công.", "UTF-8")
                    + "&messageType=success");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
            request.setAttribute("unitName", unitName);
            request.setAttribute("mode", "add");
            request.getRequestDispatcher("/view/manager/unit_detail.jsp").forward(request, response);
        }
    }
}
