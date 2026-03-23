package controller.Manager;

import dal.CategoryDAO;
import dal.BrandDAO;
import dal.SupplierDAO;
import dal.UnitDAO;
import dal.ProductDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelDTO.ProductAddDTO;
import modelDTO.ProductVariantSimpleDTO;

/**
 * 
 * @author laptop368
 */
@WebServlet(name = "ProductEditController", urlPatterns = {"/product-edit"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class ProductEditController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final UnitDAO unitDAO = new UnitDAO();
    private final Gson gson = new Gson();

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
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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

        boolean hasTransactions = productDAO.hasParticipatedInTransactions(productId);
        request.setAttribute("productId", productId);
        request.setAttribute("productEdit", dto);
        request.setAttribute("productHasTransactions", hasTransactions);
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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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

        if (productDAO.hasParticipatedInTransactions(productId)) {
            request.setAttribute("productId", productId);
            request.setAttribute("productEdit", existingDto);
            request.setAttribute("productHasTransactions", true);
            request.setAttribute("errorVariant", "Không thể cập nhật do sản phẩm đã tham gia giao dịch.");
            request.setAttribute("editDataJson", buildEditDataJsonFromDTO(existingDto));
            request.getRequestDispatcher("/view/manager/product_edit.jsp").forward(request, response);
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
        } else if (productDAO.isProductNameExistsExcludingProduct(productName.trim(), productId)) {
            request.setAttribute("errorProductName", "Tên sản phẩm đã tồn tại.");
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

            // Preserve existing variant pictures (paths) so they don't disappear on validation errors.
            Map<Integer, String> existingPictureByVariantId = new HashMap<>();
            if (existingDto.getVariants() != null) {
                for (ProductVariantSimpleDTO ev : existingDto.getVariants()) {
                    if (ev.getVariantId() != null && ev.getVariantPicture() != null && !ev.getVariantPicture().trim().isEmpty()) {
                        existingPictureByVariantId.put(ev.getVariantId(), ev.getVariantPicture().trim());
                    }
                }
            }

            request.setAttribute("editDataJson", buildEditDataJsonFromParams(
                    attributeNamesStr,
                    variantAttrValuesArr,
                    variantIds,
                    variantSkus,
                    variantBarcodes,
                    existingPictureByVariantId
            ));

            // Preserve variant images (base64) so user doesn't lose previews after validation error.
            if (variantSkus != null && variantSkus.length > 0) {
                List<String> variantImagesBase64 = new ArrayList<>();
                for (int i = 0; i < variantSkus.length; i++) {
                    String base64 = request.getParameter("variantImageBase64_" + i);
                    variantImagesBase64.add(base64 != null ? base64 : "");
                }
                Map<String, Object> preserve = new HashMap<>();
                preserve.put("variantImagesBase64", variantImagesBase64);
                request.setAttribute("preserveStateJson", gson.toJson(preserve));
            }
            request.getRequestDispatcher("/view/manager/product_edit.jsp").forward(request, response);
            return;
        }

        String picturePath = existingDto.getPicture() != null ? existingDto.getPicture() : "";
        try {
            Part filePart = request.getPart("productImage");
            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                String fileName = filePart.getSubmittedFileName();
                String saveName = System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                // 1. Lưu vào web/ (source) - ảnh có trong project
                String rootPath = getServletContext().getRealPath("/");
                if (rootPath != null && rootPath.contains("build")) {
                    rootPath = rootPath.substring(0, rootPath.indexOf("build"));
                }
                String webPath = rootPath + "web" + File.separator + "img" + File.separator + "products";
                File webDir = new File(webPath);
                if (!webDir.exists()) {
                    webDir.mkdirs();
                }

                try {
                    filePart.write(webPath + File.separator + saveName);
                    picturePath = "img/products/" + saveName;

                    // 2. Copy sang build/ - ảnh hiển thị ngay khi chạy
                    String buildPath = getServletContext().getRealPath("/img/products");
                    File buildDir = new File(buildPath);
                    if (buildDir.exists() || buildDir.mkdirs()) {
                        Files.copy(Path.of(webPath, saveName), Path.of(buildPath, saveName), StandardCopyOption.REPLACE_EXISTING);
                    }
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

        List<Part> variantImageParts = new ArrayList<>();
        try {
            for (Part p : request.getParts()) {
                if ("variantImage".equals(p.getName())) {
                    variantImageParts.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.util.Map<Integer, String> existingPictureByVariantId = new java.util.HashMap<>();
        if (existingDto.getVariants() != null) {
            for (ProductVariantSimpleDTO ev : existingDto.getVariants()) {
                if (ev.getVariantId() != null && ev.getVariantPicture() != null && !ev.getVariantPicture().trim().isEmpty()) {
                    existingPictureByVariantId.put(ev.getVariantId(), ev.getVariantPicture().trim());
                }
            }
        }

        List<ProductVariantSimpleDTO> variants = new ArrayList<>();
        if (variantSkus != null && variantSkus.length > 0) {
            for (int i = 0; i < variantSkus.length; i++) {
                ProductVariantSimpleDTO v = new ProductVariantSimpleDTO();
                v.setSku(variantSkus[i].trim());
                v.setBarcode(variantBarcodes != null && i < variantBarcodes.length
                        ? (variantBarcodes[i] != null ? variantBarcodes[i].trim() : "")
                        : "");
                Integer variantId = null;
                if (variantIds != null && i < variantIds.length && variantIds[i] != null && !variantIds[i].trim().isEmpty()) {
                    try {
                        variantId = Integer.parseInt(variantIds[i].trim());
                        v.setVariantId(variantId);
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

                String variantPicture = null;
                if (i < variantImageParts.size()) {
                    Part imgPart = variantImageParts.get(i);
                    if (imgPart != null && imgPart.getSize() > 0) {
                        String submitted = imgPart.getSubmittedFileName();
                        if (submitted != null && !submitted.isBlank()) {
                            variantPicture = saveVariantImage(imgPart, request);
                        }
                    }
                }

                // Nếu không có file upload, thử lấy ảnh variant dạng base64 (preserveState sau validation lỗi).
                if (variantPicture == null) {
                    String base64Param = request.getParameter("variantImageBase64_" + i);
                    if (base64Param != null && base64Param.startsWith("data:")) {
                        variantPicture = saveVariantImageFromBase64(base64Param, request);
                    }
                }
                if (variantPicture == null && variantId != null) {
                    variantPicture = existingPictureByVariantId.get(variantId);
                }
                v.setVariantPicture(variantPicture != null ? variantPicture : "");

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
            request.setAttribute("editDataJson", buildEditDataJsonFromDTO(dto));
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
            String[] variantIds, String[] variantSkus, String[] variantBarcodes,
            Map<Integer, String> existingPictureByVariantId) {
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
                    Integer vid = Integer.parseInt(variantIds[i].trim());
                    v.setVariantId(vid);
                    if (existingPictureByVariantId != null && existingPictureByVariantId.containsKey(vid)) {
                        v.setVariantPicture(existingPictureByVariantId.get(vid));
                    }
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
                    .append(",\"variantPicture\":").append(escapeJson(v.getVariantPicture() != null ? v.getVariantPicture() : ""))
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

    private String saveVariantImage(Part filePart, HttpServletRequest request) {
        try {
            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.isBlank()) {
                return null;
            }
            String saveName = System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

            String rootPath = getServletContext().getRealPath("/");
            if (rootPath != null && rootPath.contains("build")) {
                rootPath = rootPath.substring(0, rootPath.indexOf("build"));
            }
            String webPath = rootPath + "web" + File.separator + "img" + File.separator + "variants";
            File webDir = new File(webPath);
            if (!webDir.exists()) {
                webDir.mkdirs();
            }

            filePart.write(webPath + File.separator + saveName);

            String buildPath = getServletContext().getRealPath("/img/variants");
            File buildDir = new File(buildPath);
            if (buildDir.exists() || buildDir.mkdirs()) {
                Files.copy(Path.of(webPath, saveName), Path.of(buildPath, saveName), StandardCopyOption.REPLACE_EXISTING);
            }

            return "img/variants/" + saveName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveVariantImageFromBase64(String dataUrl, HttpServletRequest request) {
        try {
            int comma = dataUrl.indexOf(',');
            if (comma < 0) return null;

            String base64 = dataUrl.substring(comma + 1);
            byte[] bytes = Base64.getDecoder().decode(base64);
            if (bytes == null || bytes.length == 0) return null;

            String ext = "png";
            if (dataUrl.startsWith("data:image/jpeg") || dataUrl.startsWith("data:image/jpg")) ext = "jpg";
            else if (dataUrl.startsWith("data:image/gif")) ext = "gif";
            else if (dataUrl.startsWith("data:image/webp")) ext = "webp";

            String saveName = System.currentTimeMillis() + "_variant." + ext;

            String rootPath = getServletContext().getRealPath("/");
            if (rootPath != null && rootPath.contains("build")) {
                rootPath = rootPath.substring(0, rootPath.indexOf("build"));
            }
            String webPath = rootPath + "web" + File.separator + "img" + File.separator + "variants";
            File webDir = new File(webPath);
            if (!webDir.exists()) webDir.mkdirs();

            Files.write(Path.of(webPath, saveName), bytes);

            String buildPath = getServletContext().getRealPath("/img/variants");
            File buildDir = new File(buildPath);
            if (buildDir.exists() || buildDir.mkdirs()) {
                Files.copy(Path.of(webPath, saveName), Path.of(buildPath, saveName), StandardCopyOption.REPLACE_EXISTING);
            }

            return "img/variants/" + saveName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
