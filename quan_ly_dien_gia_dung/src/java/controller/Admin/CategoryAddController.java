package controller.Admin;

import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Category;
import model.User;

@WebServlet(name = "CategoryAddController", urlPatterns = {"/category-add"})
public class CategoryAddController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    private boolean hasRole(HttpServletRequest request, String... roles) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object u = session.getAttribute("user");
        if (!(u instanceof User)) {
            return false;
        }
        User user = (User) u;
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            return false;
        }
        String roleName = user.getRole().getRoleName().toLowerCase();
        for (String r : roles) {
            if (r != null && roleName.equals(r.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!hasRole(request, "admin", "manager")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/view/admin/category-add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!hasRole(request, "admin", "manager")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");

        boolean hasError = false;

        if (categoryName == null || categoryName.isBlank()) {
            request.setAttribute("categoryNameError", "Tên danh mục không được để trống");
            hasError = true;
        } else if (categoryName.trim().length() > 100) {
            request.setAttribute("categoryNameError", "Tên danh mục tối đa 100 ký tự");
            hasError = true;
        } else if (categoryDAO.isCategoryNameExists(categoryName.trim())) {
            request.setAttribute("categoryNameError", "Tên danh mục đã tồn tại");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("categoryName", categoryName);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/admin/category-add.jsp").forward(request, response);
            return;
        }

        Category category = new Category();
        category.setCategoryName(categoryName.trim());
        category.setDescription(description == null ? null : description.trim());
        category.setStatus("active");

        boolean ok = categoryDAO.insertCategory(category);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/category-list?success=created");
        } else {
            request.setAttribute("generalError", "Không thể tạo danh mục. Vui lòng thử lại.");
            request.setAttribute("categoryName", categoryName);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/admin/category-add.jsp").forward(request, response);
        }
    }
}
