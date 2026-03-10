package controller.Manager;

import dal.BrandDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Brand;
import model.User;

@WebServlet(name = "BrandEditController", urlPatterns = {"/brand-edit"})
public class BrandEditController extends HttpServlet {

    private final BrandDAO brandDAO = new BrandDAO();

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
        if (!hasRole(request, "manager")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer id = parseIntegerOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/brand-list?error=invalid_request");
            return;
        }

        Brand brand = brandDAO.getBrandById(id);
        if (brand == null) {
            response.sendRedirect(request.getContextPath() + "/brand-list?error=not_found");
            return;
        }

        request.setAttribute("brand", brand);
        request.getRequestDispatcher("/view/manager/brand-edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!hasRole(request, "manager")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer id = parseIntegerOrNull(request.getParameter("brandId"));
        String brandName = request.getParameter("brandName");
        String description = request.getParameter("description");

        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/brand-list?error=invalid_request");
            return;
        }

        Brand old = brandDAO.getBrandById(id);
        if (old == null) {
            response.sendRedirect(request.getContextPath() + "/brand-list?error=not_found");
            return;
        }

        boolean hasError = false;

        if (brandName == null || brandName.isBlank()) {
            request.setAttribute("brandNameError", "Tên thương hiệu không được để trống");
            hasError = true;
        } else if (brandName.trim().length() > 100) {
            request.setAttribute("brandNameError", "Tên thương hiệu tối đa 100 ký tự");
            hasError = true;
        } else if (brandDAO.isBrandNameExists(brandName.trim(), id)) {
            request.setAttribute("brandNameError", "Tên thương hiệu đã tồn tại");
            hasError = true;
        }

        if (description != null && description.trim().length() > 255) {
            request.setAttribute("descriptionError", "Mô tả tối đa 255 ký tự");
            hasError = true;
        }

        if (hasError) {
            old.setBrandName(brandName == null ? old.getBrandName() : brandName);
            old.setDescription(description);
            request.setAttribute("brand", old);
            request.getRequestDispatcher("/view/admin/brand-edit.jsp").forward(request, response);
            return;
        }

        old.setBrandName(brandName.trim());
        old.setDescription(description == null ? null : description.trim());

        boolean ok = brandDAO.updateBrand(old);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/brand-list?success=updated");
        } else {
            request.setAttribute("generalError", "Không thể cập nhật thương hiệu. Vui lòng thử lại.");
            request.setAttribute("brand", old);
            request.getRequestDispatcher("/view/manager/brand-edit.jsp").forward(request, response);
        }
    }
}
