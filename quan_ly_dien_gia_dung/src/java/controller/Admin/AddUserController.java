package controller.Admin;

import dal.RoleDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import model.Role;
import model.User;

@WebServlet(name = "AddUserController", urlPatterns = {"/user-add"})
@MultipartConfig
public class AddUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Role> roles = roleDAO.getAllRole();
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("view/admin/user_add.jsp").forward(request, response);
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

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String e = email.trim();
        return e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        String u = username.trim();
        return u.matches("^[A-Za-z0-9._-]{3,50}$");
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return true;
        }
        String p = phone.trim();
        return p.matches("^[0-9+\\-\\s]{6,20}$");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        Integer roleId = parseIntegerOrNull(request.getParameter("roleId"));
        String isActiveRaw = request.getParameter("isActive");
        boolean isActive = isActiveRaw == null ? true : Boolean.parseBoolean(isActiveRaw);

        boolean hasError = false;

        if (username == null || username.isBlank()) {
            request.setAttribute("usernameError", "Username không được để trống");
            hasError = true;
        } else if (!isValidUsername(username)) {
            request.setAttribute("usernameError", "Username 3-50 ký tự, chỉ gồm chữ/số và . _ -");
            hasError = true;
        } else if (userDAO.existsUsername(username)) {
            request.setAttribute("usernameError", "Username đã tồn tại");
            hasError = true;
        }

        if (email == null || email.isBlank()) {
            request.setAttribute("emailError", "Email không được để trống");
            hasError = true;
        } else if (!isValidEmail(email)) {
            request.setAttribute("emailError", "Email không đúng định dạng");
            hasError = true;
        } else if (userDAO.existsEmail(email)) {
            request.setAttribute("emailError", "Email đã tồn tại");
            hasError = true;
        }

        if (fullName == null || fullName.isBlank()) {
            request.setAttribute("fullNameError", "Họ tên không được để trống");
            hasError = true;
        }

        if (address == null || address.isBlank()) {
            request.setAttribute("addressError", "Địa chỉ không được để trống");
            hasError = true;
        } else if (address.trim().length() > 50) {
            request.setAttribute("addressError", "Địa chỉ tối đa 50 ký tự");
            hasError = true;
        }

        if (!isValidPhone(phone)) {
            request.setAttribute("phoneError", "Số điện thoại không hợp lệ");
            hasError = true;
        }

        if (password == null || password.isBlank()) {
            request.setAttribute("passwordError", "Mật khẩu không được để trống");
            hasError = true;
        } else if (password.length() < 6) {
            request.setAttribute("passwordError", "Mật khẩu tối thiểu 6 ký tự");
            hasError = true;
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            request.setAttribute("confirmPasswordError", "Vui lòng nhập lại mật khẩu");
            hasError = true;
        } else if (password != null && !password.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Mật khẩu nhập lại không khớp");
            hasError = true;
        }

        if (hasError) {
            List<Role> roles = roleDAO.getAllRole();
            request.setAttribute("roles", roles);

            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("roleId", roleId);
            request.setAttribute("isActive", isActive);

            request.getRequestDispatcher("view/admin/user_add.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setUserName(username.trim());
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setPhone(phone == null ? null : phone.trim());
        user.setAddress(address.trim());
        user.setPassword(password);
        user.setIsActive(isActive);

        if (roleId != null) {
            Role role = new Role();
            role.setRoleId(roleId);
            user.setRole(role);
        }

        String avatarFileName = "img/avatar/avt_1.jpg";
        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {
            String fileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("/img/avatar");

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String savedFileName = System.currentTimeMillis() + "_" + fileName;
            avatarPart.write(uploadPath + File.separator + savedFileName);
            avatarFileName = "img/avatar/" + savedFileName;
        }

        user.setAvatar(avatarFileName);

        int newId = userDAO.insert(user);
        if (newId > 0) {
            response.sendRedirect(request.getContextPath() + "/user-list?success=created");
        } else {
            request.setAttribute("generalError", "Không thể tạo user. Vui lòng thử lại.");
            List<Role> roles = roleDAO.getAllRole();
            request.setAttribute("roles", roles);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("roleId", roleId);
            request.setAttribute("isActive", isActive);
            request.getRequestDispatcher("view/admin/user_add.jsp").forward(request, response);
        }
    }
}
