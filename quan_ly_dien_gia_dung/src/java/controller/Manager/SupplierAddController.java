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
import jakarta.servlet.http.HttpSession;
import model.Supplier;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "SupplierAddController", urlPatterns = {"/supplier-add"})
public class SupplierAddController extends HttpServlet {

    private final SupplierDAO supplierDAO = new SupplierDAO();

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

        request.getRequestDispatcher("/view/manager/supplier_add.jsp").forward(request, response);
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
            request.getRequestDispatcher("/view/manager/supplier_add.jsp").forward(request, response);
            return;
        }

        Supplier s = new Supplier();
        s.setSupplierName(supplierName.trim());
        s.setContactPerson(contactPerson.trim());
        s.setEmail(email.trim());
        s.setPhone(phone.trim().replaceAll("\\s", ""));
        s.setStatus(status != null && (status.equals("active") || status.equals("inactive")) ? status : "active");
        s.setDescription(description.trim());

        int id = supplierDAO.insertSupplier(s);
        if (id > 0) {
            request.setAttribute("successMessage", "Thêm nhà cung cấp thành công.");
            request.getRequestDispatcher("/view/manager/supplier_add.jsp").forward(request, response);
        } else {
            request.setAttribute("errorSupplierName", "Không thể thêm nhà cung cấp. Vui lòng thử lại.");
            request.setAttribute("supplierName", supplierName);
            request.setAttribute("contactPerson", contactPerson);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("status", status);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/view/manager/supplier_add.jsp").forward(request, response);
        }
    }
}
