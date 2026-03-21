package controller.Manager;

import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Category;

@WebServlet(name = "CategoryAddController", urlPatterns = {"/category-add"})
public class CategoryAddController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/manager/category-add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

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
            request.getRequestDispatcher("/view/manager/category-add.jsp").forward(request, response);
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
            request.getRequestDispatcher("/view/manager/category-add.jsp").forward(request, response);
        }
    }
}
