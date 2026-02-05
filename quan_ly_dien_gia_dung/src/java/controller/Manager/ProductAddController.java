package controller.Manager;

import dal.CategoryDAO;
import dal.BrandDAO;
import dal.SupplierDAO;
import dal.UnitDAO;
import dal.ProductDAO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.User;
import model.Category;
import model.Brand;
import model.Supplier;
import model.Unit;
import modelDTO.ProductAddDTO;
import modelDTO.ProductVariantSimpleDTO;

@WebServlet(name = "ProductAddController", urlPatterns = { "/product-add" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class ProductAddController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final UnitDAO unitDAO = new UnitDAO();

    private void loadDropdownData(HttpServletRequest request) {
        request.setAttribute("categories", categoryDAO.getActiveCategories());
        request.setAttribute("brands", brandDAO.getActiveBrands());
        request.setAttribute("suppliers", supplierDAO.getActiveSuppliers());
        request.setAttribute("units", unitDAO.getAllUnits());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (loggedUser.getRole() == null || !"Manager".equalsIgnoreCase(loggedUser.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/indexManager");
            return;
        }

        loadDropdownData(request);
        request.getRequestDispatcher("/view/manager/product_add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (loggedUser.getRole() == null || !"Manager".equalsIgnoreCase(loggedUser.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/indexManager");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        loadDropdownData(request);

        String productName = getParam(request, "productName");
        String baseSku = getParam(request, "baseSku");
        String baseBarcode = getParam(request, "baseBarcode");
        String categoryId = getParam(request, "categoryId");
        String brandId = getParam(request, "brandId");
        String supplierId = getParam(request, "supplierId");
        String unitId = getParam(request, "unitId");
        String description = getParam(request, "description");

        String[] variantSkus = request.getParameterValues("variantSku");
        String[] variantBarcodes = request.getParameterValues("variantBarcode");
        String attributeNamesStr = getParam(request, "attributeNames");
        String[] variantAttrValuesArr = request.getParameterValues("variantAttrValues");

        boolean hasError = false;

        if (productName == null || productName.isBlank()) {
            request.setAttribute("errorProductName", "Tên sản phẩm không được để trống.");
            hasError = true;
        }

        if (categoryId == null || categoryId.isBlank()) {
            request.setAttribute("errorCategoryId", "Vui lòng chọn danh mục.");
            hasError = true;
        }

        if (brandId == null || brandId.isBlank()) {
            request.setAttribute("errorBrandId", "Vui lòng chọn thương hiệu.");
            hasError = true;
        }

        if (supplierId == null || supplierId.isBlank()) {
            request.setAttribute("errorSupplierId", "Vui lòng chọn nhà cung cấp.");
            hasError = true;
        }

        if (unitId == null || unitId.isBlank()) {
            request.setAttribute("errorUnitId", "Vui lòng chọn đơn vị tính.");
            hasError = true;
        }

        if (variantSkus != null && variantSkus.length > 0) {
            for (String sku : variantSkus) {
                if (sku == null || sku.trim().isEmpty()) {
                    request.setAttribute("errorVariant", "SKU của phiên bản không được để trống.");
                    hasError = true;
                    break;
                }
                if (productDAO.isSkuExists(sku.trim())) {
                    request.setAttribute("errorVariant", "Mã SKU \"" + sku.trim() + "\" đã tồn tại trong hệ thống.");
                    hasError = true;
                    break;
                }
            }
            if (!hasError && variantBarcodes != null) {
                for (int i = 0; i < variantBarcodes.length; i++) {
                    String barcode = variantBarcodes[i];
                    if (barcode != null && !barcode.trim().isEmpty() && productDAO.isBarcodeExists(barcode.trim())) {
                        request.setAttribute("errorVariant", "Barcode \"" + barcode.trim() + "\" đã tồn tại trong hệ thống.");
                        hasError = true;
                        break;
                    }
                }
            }
        } else {
            if (baseSku == null || baseSku.isBlank()) {
                request.setAttribute("errorBaseSku", "Mã SKU không được để trống.");
                hasError = true;
            } else if (productDAO.isSkuExists(baseSku.trim())) {
                request.setAttribute("errorBaseSku", "Mã SKU \"" + baseSku.trim() + "\" đã tồn tại trong hệ thống.");
                hasError = true;
            } else if (baseBarcode != null && !baseBarcode.isBlank() && productDAO.isBarcodeExists(baseBarcode.trim())) {
                request.setAttribute("errorBaseBarcode", "Barcode \"" + baseBarcode.trim() + "\" đã tồn tại trong hệ thống.");
                hasError = true;
            }
        }

        if (hasError) {
            request.setAttribute("productName", productName);
            request.setAttribute("baseSku", baseSku);
            request.setAttribute("baseBarcode", baseBarcode);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("brandId", brandId);
            request.setAttribute("supplierId", supplierId);
            request.setAttribute("unitId", unitId);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/manager/product_add.jsp").forward(request, response);
            return;
        }

        String picturePath = "";
        try {
            Part filePart = request.getPart("productImage");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    String ext = "";
                    int dot = fileName.lastIndexOf('.');
                    if (dot > 0) {
                        ext = fileName.substring(dot);
                    }
                    String saveName = System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    String uploadPath = getServletContext().getRealPath("/img/product");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    File saveFile = new File(uploadDir, saveName);
                    try (InputStream is = filePart.getInputStream()) {
                        Files.copy(is, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    picturePath = "img/products/" + saveName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProductAddDTO dto = new ProductAddDTO();
        dto.setProductName(productName.trim());
        dto.setBaseSku(baseSku != null ? baseSku.trim() : "");
        dto.setBaseBarcode(baseBarcode != null ? baseBarcode.trim() : "");
        dto.setCategoryId(Integer.parseInt(categoryId));
        dto.setBrandId(Integer.parseInt(brandId));
        dto.setSupplierId(Integer.parseInt(supplierId));
        dto.setUnitId(Integer.parseInt(unitId));
        dto.setPicture(picturePath);
        dto.setDescription(description != null ? description.trim() : "");

        List<String> attributeNames = new ArrayList<>();
        if (attributeNamesStr != null && !attributeNamesStr.trim().isEmpty()) {
            attributeNames = Arrays.stream(attributeNamesStr.split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        dto.setAttributeNames(attributeNames);

        List<ProductVariantSimpleDTO> variants = new ArrayList<>();
        if (variantSkus != null && variantSkus.length > 0) {
            for (int i = 0; i < variantSkus.length; i++) {
                ProductVariantSimpleDTO variant = new ProductVariantSimpleDTO();
                variant.setSku(variantSkus[i].trim());
                variant.setBarcode(variantBarcodes != null && i < variantBarcodes.length
                        ? variantBarcodes[i].trim()
                        : "");

                List<String> attrValues = new ArrayList<>();
                if (variantAttrValuesArr != null && i < variantAttrValuesArr.length) {
                    String vals = variantAttrValuesArr[i];
                    if (vals != null && !vals.trim().isEmpty()) {
                        attrValues = Arrays.stream(vals.split("\\|"))
                                .map(String::trim)
                                .collect(Collectors.toList());
                    }
                }
                variant.setAttributeValues(attrValues);
                variants.add(variant);
            }
        }
        dto.setVariants(variants);

        int productId = productDAO.insertProductFromDTO(dto);

        if (productId > 0) {
            request.setAttribute("successMessage", "Thêm sản phẩm thành công.");
            loadDropdownData(request);
            request.getRequestDispatcher("/view/manager/product_add.jsp").forward(request, response);
        } else {
            request.setAttribute("errorProductName", "Không thể thêm sản phẩm. Vui lòng thử lại.");
            request.setAttribute("productName", productName);
            request.setAttribute("baseSku", baseSku);
            request.setAttribute("baseBarcode", baseBarcode);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("brandId", brandId);
            request.setAttribute("supplierId", supplierId);
            request.setAttribute("unitId", unitId);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/manager/product_add.jsp").forward(request, response);
        }
    }

    private String getParam(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }
}
