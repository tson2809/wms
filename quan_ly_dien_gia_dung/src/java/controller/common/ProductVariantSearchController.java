package controller.common;

import com.google.gson.Gson;
import dal.BrandDAO;
import dal.CategoryDAO;
import dal.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ProductInventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AJAX endpoint: tìm kiếm product variants theo keyword, categoryId, brandId.
 * URL: /product-variant/search
 * Params: keyword, categoryId (optional), brandId (optional)
 * Returns: JSON array of {variantId, sku, productName, costPrice}
 */
@WebServlet(name = "ProductVariantSearchController", urlPatterns = {"/product-variant/search"})
public class ProductVariantSearchController extends HttpServlet {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;
    private static final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.brandDAO = new BrandDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Auth check: chỉ cho user đã đăng nhập
        if (request.getSession(false) == null || request.getSession(false).getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("[]");
            return;
        }

        String keyword = request.getParameter("keyword");
        String categoryIdStr = request.getParameter("categoryId");
        String brandIdStr = request.getParameter("brandId");

        Integer categoryId = null;
        Integer brandId = null;
        try { if (categoryIdStr != null && !categoryIdStr.isEmpty()) categoryId = Integer.parseInt(categoryIdStr); } catch (NumberFormatException ignored) {}
        try { if (brandIdStr != null && !brandIdStr.isEmpty()) brandId = Integer.parseInt(brandIdStr); } catch (NumberFormatException ignored) {}

        List<ProductInventory> variants = productDAO.searchVariants(keyword, categoryId, brandId);

        // Map to simple JSON-friendly objects
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (ProductInventory v : variants) {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("variantId", v.getVariantId());
            m.put("sku", v.getSku());
            m.put("productName", v.getProductName() != null ? v.getProductName() : "");
            m.put("costPrice", v.getCostPrice());
            m.put("unitName", v.getUnitName() != null ? v.getUnitName() : "");
            result.add(m);
        }

        response.getWriter().write(gson.toJson(result));
    }
}
