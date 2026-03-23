package controller.Manager;

import dal.BrandDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Brand;

@WebServlet(name = "BrandAddController", urlPatterns = {"/brand-add"})
public class BrandAddController extends HttpServlet {

    private final BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/manager/brand-add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String brandName = request.getParameter("brandName");
        String description = request.getParameter("description");

        boolean hasError = false;

        if (brandName == null || brandName.isBlank()) {
            request.setAttribute("brandNameError", "Tên thương hiệu không được để trống");
            hasError = true;
        } else if (brandName.trim().length() > 100) {
            request.setAttribute("brandNameError", "Tên thương hiệu tối đa 100 ký tự");
            hasError = true;
        } else if (brandDAO.isBrandNameExists(brandName.trim())) {
            request.setAttribute("brandNameError", "Tên thương hiệu đã tồn tại");
            hasError = true;
        }

        if (description != null && description.trim().length() > 255) {
            request.setAttribute("descriptionError", "Mô tả tối đa 255 ký tự");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("brandName", brandName);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/manager/brand-add.jsp").forward(request, response);
            return;
        }

        Brand brand = new Brand();
        brand.setBrandName(brandName.trim());
        brand.setDescription(description == null ? null : description.trim());
        brand.setStatus("active");

        boolean ok = brandDAO.insertBrand(brand);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/brand-list?success=created");
        } else {
            request.setAttribute("generalError", "Không thể tạo thương hiệu. Vui lòng thử lại.");
            request.setAttribute("brandName", brandName);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/manager/brand-add.jsp").forward(request, response);
        }
    }
}
