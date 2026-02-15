/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.inventory;

import dal.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.ProductInventory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author GIAKHANHPC
 */
@WebServlet(name = "ExportInventoryController", urlPatterns = {"/export-inventory"})
public class ExportInventoryController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ExportInventoryController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ExportInventoryController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String categoryRaw = request.getParameter("categoryId");
        String status = request.getParameter("status");
        Integer categoryId = null;
        if (categoryRaw != null && !categoryRaw.isEmpty()) {
            categoryId = Integer.parseInt(categoryRaw);
        }
        List<ProductInventory> list= dao.getInventoryList(keyword, categoryId, status, 1, 999999, null, null);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Inventory");
        int rowNum = 0;
        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue("SKU");
        header.createCell(1).setCellValue("Product");
        header.createCell(2).setCellValue("Category");
        header.createCell(3).setCellValue("Brand");
        header.createCell(4).setCellValue("Cost");
        header.createCell(5).setCellValue("Price");
        header.createCell(6).setCellValue("Quantity");
        header.createCell(7).setCellValue("Status");
        for (ProductInventory p : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getSku());
            row.createCell(1).setCellValue(p.getProductName());
            row.createCell(2).setCellValue(p.getCategoryName());
            row.createCell(3).setCellValue(p.getBrandName());
            row.createCell(4).setCellValue(p.getCostPrice());
            row.createCell(5).setCellValue(p.getSalePrice());
            row.createCell(6).setCellValue(p.getTotalQuantity());
            row.createCell(7).setCellValue(p.getStatus());
        }
        String time = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new java.util.Date());
        String fileName = "inventory_" + time + ".xlsx";
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
