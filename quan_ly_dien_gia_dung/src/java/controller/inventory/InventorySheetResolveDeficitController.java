package controller.inventory;

import dal.InventorySheetDAO;
import dal.ProductSerialDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import model.InventorySheet;
import model.ProductInventory;
import model.ProductSerial;
import model.User;

@WebServlet(name = "InventorySheetResolveDeficitController", urlPatterns = {"/inventory-sheet-resolve-deficit"})
public class InventorySheetResolveDeficitController extends HttpServlet {

    InventorySheetDAO sheetDao = new InventorySheetDAO();
    ProductSerialDAO serialDao = new ProductSerialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }

        int sheetId = Integer.parseInt(idParam);
        InventorySheet sheet = sheetDao.getSheetById(sheetId);
        if (sheet == null || !"approved".equals(sheet.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }

        List<ProductInventory> allDetails = sheetDao.getSheetDetails(sheetId);
        List<ProductInventory> deficitDetails = new ArrayList<>();
        Map<Integer, List<ProductSerial>> serialsMap = new HashMap<>();
        
        for (ProductInventory pi : allDetails) {
            if (pi.getCountedQuantity() < pi.getSystemQuantity()) {
                deficitDetails.add(pi);
                List<ProductSerial> inStockSerials = serialDao.getInStockSerialsByVariant(pi.getVariantId());
                serialsMap.put(pi.getVariantId(), inStockSerials);
            }
        }
        
        request.setAttribute("sheet", sheet);
        request.setAttribute("deficitDetails", deficitDetails);
        request.setAttribute("serialsMap", serialsMap);
        request.getRequestDispatcher("/view/manager/sheet-resolve-deficit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
            return;
        }

        String sheetIdParam = request.getParameter("sheet_id");
        if (sheetIdParam != null) {
            String[] defectiveSerialIdsStr = request.getParameterValues("defective_serial_ids");
            if (defectiveSerialIdsStr != null && defectiveSerialIdsStr.length > 0) {
                List<Integer> serialIds = new ArrayList<>();
                for (String s : defectiveSerialIdsStr) {
                    serialIds.add(Integer.parseInt(s));
                }
                serialDao.updateSerialsStatus(serialIds, "defective", "Đã đánh dấu lỗi sau khi kiểm kê phiếu #" + sheetIdParam);
            }
        }
        request.getSession().setAttribute("message", "Đã xử lý serial thiếu thành công!");
        response.sendRedirect(request.getContextPath() + "/inventory-sheet-list");
    }
}
