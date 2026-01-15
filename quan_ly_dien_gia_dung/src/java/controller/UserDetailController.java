package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;

@WebServlet(name = "UserDeatilController", urlPatterns = {"/user-detail"})
public class UserDetailController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        Integer id = Integer.parseInt(idRaw);
        

        if (id == null) {
            response.sendRedirect("user-list?error=invalid_id");
            return;
        }

        User user = userDAO.getUserById(id);
        if (user == null) {
            response.sendRedirect("user-list?error=not_found");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("user_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
