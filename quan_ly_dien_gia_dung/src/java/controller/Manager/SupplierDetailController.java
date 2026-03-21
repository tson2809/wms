/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.Manager;

import dal.SupplierDAO;
import java.io.IOException;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Supplier;

/**
 *
 * @author thais
 */
@WebServlet(name = "SupplierDetailController", urlPatterns = {"/supplier-detail"})
public class SupplierDetailController extends HttpServlet {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/supplier-list");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            Supplier s = supplierDAO.getSupplierById(id);
            if (s == null) {
                response.sendRedirect(request.getContextPath() + "/supplier-list");
                return;
            }
            request.setAttribute("supplier", s);
            request.getRequestDispatcher("/view/manager/supplier_detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/supplier-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/supplier-list");
            return;
        }
        int supplierId;
        try {
            supplierId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/supplier-list");
            return;
        }

        String supplierName = request.getParameter("supplierName");
        String contactPerson = request.getParameter("contactPerson");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Pattern namePattern = Pattern.compile("^[\\p{L}\\s]+$");
        Pattern phonePattern = Pattern.compile("^0\\d{9}$");

        boolean hasError = false;

        if (supplierName == null || supplierName.isBlank()) {
            request.setAttribute("errorSupplierName", "Tên nhà cung cấp không được để trống.");
            hasError = true;
        }
        if (contactPerson == null || contactPerson.isBlank()) {
            request.setAttribute("errorContactPerson", "Người liên hệ không được để trống.");
            hasError = true;
        } else if (!namePattern.matcher(contactPerson.trim()).matches()) {
            request.setAttribute("errorContactPerson", "Họ tên không được chứa số hoặc ký tự đặc biệt.");
            hasError = true;
        }
        if (email == null || email.isBlank()) {
            request.setAttribute("errorEmail", "Email không được để trống.");
            hasError = true;
        } else if (!emailPattern.matcher(email.trim()).matches()) {
            request.setAttribute("errorEmail", "Email không đúng định dạng.");
            hasError = true;
        }
        if (phone == null || phone.isBlank()) {
            request.setAttribute("errorPhone", "Số điện thoại không được để trống.");
            hasError = true;
        } else if (!phonePattern.matcher(phone.trim().replaceAll("\\s", "")).matches()) {
            request.setAttribute("errorPhone", "Số điện thoại phải có 10 số và bắt đầu từ 0.");
            hasError = true;
        }
        if (description == null || description.isBlank()) {
            request.setAttribute("errorDescription", "Mô tả không được để trống.");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("supplierName", supplierName);
            request.setAttribute("contactPerson", contactPerson);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("status", status != null ? status : "active");
            request.setAttribute("description", description);
            request.setAttribute("supplierId", supplierId);
            request.getRequestDispatcher("/view/manager/supplier_detail.jsp").forward(request, response);
            return;
        }

        Supplier s = new Supplier();
        s.setSupplierId(supplierId);
        s.setSupplierName(supplierName.trim());
        s.setContactPerson(contactPerson.trim());
        s.setEmail(email.trim());
        s.setPhone(phone.trim().replaceAll("\\s", ""));
        s.setStatus(status != null && (status.equals("active") || status.equals("inactive")) ? status : "active");
        s.setDescription(description.trim());

        int n = supplierDAO.updateSupplier(s);
        if (n > 0) {
            Supplier updated = supplierDAO.getSupplierById(supplierId);
            request.setAttribute("supplier", updated);
            request.setAttribute("successMessage", "Cập nhật nhà cung cấp thành công.");
            request.getRequestDispatcher("/view/manager/supplier_detail.jsp").forward(request, response);
        } else {
            request.setAttribute("errorSupplierName", "Không thể cập nhật. Vui lòng thử lại.");
            request.setAttribute("supplier", s);
            request.getRequestDispatcher("/view/manager/supplier_detail.jsp").forward(request, response);
        }
    }
}
