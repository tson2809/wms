package controller.Manager;

import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Category;

@WebServlet(name = "CategoryEditController", urlPatterns = {"/category-edit"})
public class CategoryEditController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    private Integer parseIntegerOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseIntegerOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/category-list?error=invalid_request");
            return;
        }

        Category category = categoryDAO.getCategoryById(id);
        if (category == null) {
            response.sendRedirect(request.getContextPath() + "/category-list?error=not_found");
            return;
        }

        request.setAttribute("category", category);
        request.getRequestDispatcher("/view/manager/category-edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer id = parseIntegerOrNull(request.getParameter("categoryId"));
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");

        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/category-list?error=invalid_request");
            return;
        }

        Category old = categoryDAO.getCategoryById(id);
        if (old == null) {
            response.sendRedirect(request.getContextPath() + "/category-list?error=not_found");
            return;
        }

        boolean hasError = false;

        if (categoryName == null || categoryName.isBlank()) {
            request.setAttribute("categoryNameError", "Tên danh mục không được để trống");
            hasError = true;
        } else if (categoryName.trim().length() > 100) {
            request.setAttribute("categoryNameError", "Tên danh mục tối đa 100 ký tự");
            hasError = true;
        } else if (categoryDAO.isCategoryNameExists(categoryName.trim(), id)) {
            request.setAttribute("categoryNameError", "Tên danh mục đã tồn tại");
            hasError = true;
        }

        if (hasError) {
            old.setCategoryName(categoryName == null ? old.getCategoryName() : categoryName);
            old.setDescription(description);
            request.setAttribute("category", old);
            request.getRequestDispatcher("/view/manager/category-edit.jsp").forward(request, response);
            return;
        }

        old.setCategoryName(categoryName.trim());
        old.setDescription(description == null ? null : description.trim());

        boolean ok = categoryDAO.updateCategory(old);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/category-list?success=updated");
        } else {
            request.setAttribute("generalError", "Không thể cập nhật danh mục. Vui lòng thử lại.");
            request.setAttribute("category", old);
            request.getRequestDispatcher("/view/manager/category-edit.jsp").forward(request, response);
        }
    }
}
