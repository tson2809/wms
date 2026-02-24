package controller.Manager;

import dal.CategoryDAO;
import dal.BrandDAO;
import dal.SupplierDAO;
import dal.UnitDAO;
import dal.ProductDAO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import modelDTO.ProductAddDTO;
import modelDTO.ProductVariantSimpleDTO;

@WebServlet(name = "ProductEditController", urlPatterns = {"/product-edit"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class ProductEditController extends HttpServlet {

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

    private boolean checkManager(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }
        int productId;
        try {
            productId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        ProductAddDTO dto = productDAO.getProductAddDTOById(productId);
        if (dto == null) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        request.setAttribute("productId", productId);
        request.setAttribute("productEdit", dto);
        loadDropdownData(request);
        request.setAttribute("editDataJson", buildEditDataJsonFromDTO(dto));

        if ("1".equals(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Cập nhật sản phẩm thành công.");
        }
        request.getRequestDispatcher("/view/manager/product_edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkManager(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        loadDropdownData(request);

        String productIdParam = request.getParameter("productId");
        if (productIdParam == null || productIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }
        int productId;
        try {
            productId = Integer.parseInt(productIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        ProductAddDTO existingDto = productDAO.getProductAddDTOById(productId);
        if (existingDto == null) {
            response.sendRedirect(request.getContextPath() + "/product-list");
            return;
        }

        String productName = getParam(request, "productName");
        String categoryId = getParam(request, "categoryId");
        String brandId = getParam(request, "brandId");
        String supplierId = getParam(request, "supplierId");
        String unitId = getParam(request, "unitId");
        String description = getParam(request, "description");

        String[] variantIds = request.getParameterValues("variantId");
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
            for (int i = 0; i < variantSkus.length; i++) {
                String sku = variantSkus[i] != null ? variantSkus[i].trim() : "";
                if (sku.isEmpty()) {
                    request.setAttribute("errorVariant", "SKU phiên bản không được để trống.");
                    hasError = true;
                    break;
                }
                if (productDAO.isSkuExistsExcludingProduct(sku, productId)) {
                    request.setAttribute("errorVariant", "Mã SKU \"" + sku + "\" đã tồn tại (sản phẩm khác).");
                    hasError = true;
                    break;
                }
            }
            if (!hasError && variantBarcodes != null) {
                for (int i = 0; i < variantBarcodes.length; i++) {
                    String barcode = variantBarcodes[i] != null ? variantBarcodes[i].trim() : "";
                    if (!barcode.isEmpty() && productDAO.isBarcodeExistsExcludingProduct(barcode, productId)) {
                        request.setAttribute("errorVariant", "Barcode \"" + barcode + "\" đã tồn tại (sản phẩm khác).");
                        hasError = true;
                        break;
                    }
                }
            }
        } else {
            // Không có SKU nào từ variant → không cho phép cập nhật
            request.setAttribute("errorVariant", "Phải có ít nhất 1 phiên bản (SKU).");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("productId", productId);
            request.setAttribute("productEdit", existingDto);
            request.setAttribute("productName", productName);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("brandId", brandId);
            request.setAttribute("supplierId", supplierId);
            request.setAttribute("unitId", unitId);
            request.setAttribute("description", description);
            request.setAttribute("editDataJson", buildEditDataJsonFromParams(attributeNamesStr, variantAttrValuesArr, variantIds, variantSkus, variantBarcodes));
            request.getRequestDispatcher("/view/manager/product_edit.jsp").forward(request, response);
            return;
        }

        String picturePath = existingDto.getPicture() != null ? existingDto.getPicture() : "";
        try {
            Part filePart = request.getPart("productImage");
            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                String fileName = filePart.getSubmittedFileName();
                String saveName = System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                String rootPath = getServletContext().getRealPath("/");
                if (rootPath != null && rootPath.contains("build")) {
                    rootPath = rootPath.substring(0, rootPath.indexOf("build"));
                }

                String uploadPath = rootPath + "web" + File.separator + "img" + File.separator + "products";

                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                try {
                    filePart.write(uploadPath + File.separator + saveName);
                    picturePath = "img/products/" + saveName;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> attributeNames = new ArrayList<>();
        if (attributeNamesStr != null && !attributeNamesStr.trim().isEmpty()) {
            attributeNames = Arrays.stream(attributeNamesStr.split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        List<ProductVariantSimpleDTO> variants = new ArrayList<>();
        if (variantSkus != null && variantSkus.length > 0) {
            for (int i = 0; i < variantSkus.length; i++) {
                ProductVariantSimpleDTO v = new ProductVariantSimpleDTO();
                v.setSku(variantSkus[i].trim());
                v.setBarcode(variantBarcodes != null && i < variantBarcodes.length
                        ? (variantBarcodes[i] != null ? variantBarcodes[i].trim() : "")
                        : "");
                if (variantIds != null && i < variantIds.length && variantIds[i] != null && !variantIds[i].trim().isEmpty()) {
                    try {
                        v.setVariantId(Integer.parseInt(variantIds[i].trim()));
                    } catch (NumberFormatException ignored) { }
                }
                List<String> attrValues = new ArrayList<>();
                if (variantAttrValuesArr != null && i < variantAttrValuesArr.length && variantAttrValuesArr[i] != null) {
                    String vals = variantAttrValuesArr[i].trim();
                    if (!vals.isEmpty()) {
                        attrValues = Arrays.stream(vals.split("\\|")).map(String::trim).collect(Collectors.toList());
                    }
                }
                v.setAttributeValues(attrValues);
                variants.add(v);
            }
        }

        ProductAddDTO dto = new ProductAddDTO();
        dto.setProductId(productId);
        dto.setProductName(productName.trim());
        dto.setCategoryId(Integer.parseInt(categoryId));
        dto.setBrandId(Integer.parseInt(brandId));
        dto.setSupplierId(Integer.parseInt(supplierId));
        dto.setUnitId(Integer.parseInt(unitId));
        dto.setPicture(picturePath);
        dto.setDescription(description != null ? description.trim() : "");
        dto.setAttributeNames(attributeNames);
        dto.setVariants(variants);

        boolean updated = productDAO.updateProductFromDTO(dto);
        if (updated) {
            response.sendRedirect(request.getContextPath() + "/product-edit?id=" + productId + "&success=1");
        } else {
            request.setAttribute("errorVariant", "Không thể cập nhật. Vui lòng thử lại.");
            request.setAttribute("productId", productId);
            request.setAttribute("productEdit", existingDto);
            request.setAttribute("productName", productName);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("brandId", brandId);
            request.setAttribute("supplierId", supplierId);
            request.setAttribute("unitId", unitId);
            request.setAttribute("description", description);
            request.setAttribute("editDataJson", buildEditDataJsonFromParams(attributeNamesStr, variantAttrValuesArr, variantIds, variantSkus, variantBarcodes));
            request.getRequestDispatcher("/view/manager/product_edit.jsp").forward(request, response);
        }
    }

    private String buildEditDataJsonFromDTO(ProductAddDTO dto) {
        List<String> names = dto.getAttributeNames();
        List<ProductVariantSimpleDTO> vars = dto.getVariants();
        if (names == null || names.isEmpty() || vars == null || vars.isEmpty()) {
            return "";
        }
        List<List<String>> attributeValues = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Set<String> distinct = new LinkedHashSet<>();
            for (ProductVariantSimpleDTO v : vars) {
                List<String> av = v.getAttributeValues();
                if (av != null && i < av.size() && av.get(i) != null) {
                    distinct.add(av.get(i).trim());
                }
            }
            attributeValues.add(new ArrayList<>(distinct));
        }
        return toEditDataJson(names, attributeValues, vars);
    }

    private String buildEditDataJsonFromParams(String attributeNamesStr, String[] variantAttrValuesArr,
            String[] variantIds, String[] variantSkus, String[] variantBarcodes) {
        if (attributeNamesStr == null || attributeNamesStr.trim().isEmpty() || variantAttrValuesArr == null || variantAttrValuesArr.length == 0) {
            return "";
        }
        List<String> names = Arrays.stream(attributeNamesStr.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (names.isEmpty()) return "";

        List<List<String>> attributeValues = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Set<String> distinct = new LinkedHashSet<>();
            for (String row : variantAttrValuesArr) {
                if (row == null) continue;
                List<String> parts = Arrays.stream(row.split("\\|")).map(String::trim).collect(Collectors.toList());
                if (i < parts.size() && !parts.get(i).isEmpty()) distinct.add(parts.get(i));
            }
            attributeValues.add(new ArrayList<>(distinct));
        }

        List<ProductVariantSimpleDTO> vars = new ArrayList<>();
        for (int i = 0; i < variantAttrValuesArr.length; i++) {
            ProductVariantSimpleDTO v = new ProductVariantSimpleDTO();
            if (variantIds != null && i < variantIds.length && variantIds[i] != null && !variantIds[i].trim().isEmpty()) {
                try {
                    v.setVariantId(Integer.parseInt(variantIds[i].trim()));
                } catch (NumberFormatException ignored) { }
            }
            v.setSku(variantSkus != null && i < variantSkus.length && variantSkus[i] != null ? variantSkus[i].trim() : "");
            v.setBarcode(variantBarcodes != null && i < variantBarcodes.length && variantBarcodes[i] != null ? variantBarcodes[i].trim() : "");
            String row = variantAttrValuesArr[i];
            if (row != null && !row.trim().isEmpty()) {
                v.setAttributeValues(Arrays.stream(row.split("\\|")).map(String::trim).collect(Collectors.toList()));
            }
            vars.add(v);
        }
        return toEditDataJson(names, attributeValues, vars);
    }

    private String toEditDataJson(List<String> attributeNames, List<List<String>> attributeValues, List<ProductVariantSimpleDTO> variants) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"attributeNames\":[");
        for (int i = 0; i < attributeNames.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeJson(attributeNames.get(i)));
        }
        sb.append("],\"attributeValues\":[");
        for (int i = 0; i < attributeValues.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("[");
            List<String> vals = attributeValues.get(i);
            for (int j = 0; j < vals.size(); j++) {
                if (j > 0) sb.append(",");
                sb.append(escapeJson(vals.get(j)));
            }
            sb.append("]");
        }
        sb.append("],\"variants\":[");
        for (int i = 0; i < variants.size(); i++) {
            if (i > 0) sb.append(",");
            ProductVariantSimpleDTO v = variants.get(i);
            sb.append("{\"variantId\":").append(v.getVariantId() != null ? v.getVariantId() : 0)
                    .append(",\"sku\":").append(escapeJson(v.getSku() != null ? v.getSku() : ""))
                    .append(",\"barcode\":").append(escapeJson(v.getBarcode() != null ? v.getBarcode() : ""))
                    .append(",\"attributeValues\":[");
            List<String> av = v.getAttributeValues();
            if (av != null) {
                for (int j = 0; j < av.size(); j++) {
                    if (j > 0) sb.append(",");
                    sb.append(escapeJson(av.get(j)));
                }
            }
            sb.append("]}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }

    private String getParam(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }
}
