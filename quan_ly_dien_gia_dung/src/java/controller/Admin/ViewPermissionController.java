/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Admin;

import dal.PermissionDAO;
import dal.RoleDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Permission;
import model.Role;
import model.User;

/**
 *
 * @author thais
 */
public class ViewPermissionController extends HttpServlet {    
    private static final String CLAIM_PURCHASE_ORDER = "claim purchase order";

    private Integer findPermissionIdByName(List<Permission> permissions, String targetPermissionName) {
        if (permissions == null || targetPermissionName == null) {
            return null;
        }
        for (Permission permission : permissions) {
            if (permission != null && permission.getPermissionName() != null
                    && targetPermissionName.equalsIgnoreCase(permission.getPermissionName().trim())) {
                return permission.getPermissionId();
            }
        }
        return null;
    }

    private boolean isStaffRole(Role role) {
        return role != null
                && role.getRoleName() != null
                && "staff".equalsIgnoreCase(role.getRoleName().trim());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RoleDAO roleDAO = new RoleDAO();
        PermissionDAO permissionDAO = new PermissionDAO();

        permissionDAO.ensurePermissionExists("View Inventory", "Xem tồn kho");
        permissionDAO.ensurePermissionExists("View Supplier", "Xem nhà cung cấp");
        permissionDAO.ensurePermissionExists("Create Supplier", "Thêm nhà cung cấp");
        permissionDAO.ensurePermissionExists("Edit Supplier", "Sửa nhà cung cấp");
        permissionDAO.ensurePermissionExists("Deactivate Supplier", "Kích hoạt/vô hiệu hóa nhà cung cấp");
        permissionDAO.ensurePermissionExists("View Category", "Xem danh mục");
        permissionDAO.ensurePermissionExists("View Brand", "Xem thương hiệu");
        permissionDAO.ensurePermissionExists("Create Brand", "Thêm thương hiệu");
        permissionDAO.ensurePermissionExists("Edit Brand", "Sửa thương hiệu");
        permissionDAO.ensurePermissionExists("Deactivate Brand", "Kích hoạt/vô hiệu hóa thương hiệu");
        permissionDAO.ensurePermissionExists("View Unit", "Xem đơn vị tính");
        permissionDAO.ensurePermissionExists("Create Unit", "Thêm đơn vị tính");
        permissionDAO.ensurePermissionExists("Edit Unit", "Sửa đơn vị tính");
        permissionDAO.ensurePermissionExists("Delete Unit", "Xóa đơn vị tính");
        permissionDAO.ensurePermissionExists("View Product", "Xem sản phẩm");
        permissionDAO.ensurePermissionExists("Create Product", "Thêm sản phẩm");
        permissionDAO.ensurePermissionExists("Edit Product", "Sửa sản phẩm");
        permissionDAO.ensurePermissionExists("Deactivate Product", "Kích hoạt/vô hiệu hóa sản phẩm");
        permissionDAO.ensurePermissionExists("View Purchase Order", "Xem đơn đặt hàng");
        permissionDAO.ensurePermissionExists("Create Purchase Order", "Tạo đơn đặt hàng");
        permissionDAO.ensurePermissionExists("Edit Purchase Order", "Sửa đơn đặt hàng");
        permissionDAO.ensurePermissionExists("Cancel Purchase Order", "Hủy đơn đặt hàng");
        permissionDAO.ensurePermissionExists("Claim Purchase Order", "Nhận xử lý đơn đặt hàng");
        
        List<Role> allRoles = roleDAO.getAllRole();
        
        List<Permission> allPermissions = permissionDAO.getAllPermission();
        Integer claimPermissionId = findPermissionIdByName(allPermissions, CLAIM_PURCHASE_ORDER);
        if (claimPermissionId != null) {
            allPermissions.removeIf(p -> p != null && p.getPermissionId() == claimPermissionId);
        }
        
        Map<Integer, List<Integer>> allRolePermissions = new LinkedHashMap<>();
        for (Role role : allRoles) {
            List<Integer> permissionIds = roleDAO.getRolePermissionIds(role.getRoleId());
            allRolePermissions.put(role.getRoleId(), permissionIds);
        }
        
        request.setAttribute("allPermissions", allPermissions);
        request.setAttribute("allRoles", allRoles);
        request.setAttribute("allRolePermissions", allRolePermissions);
        
        request.getRequestDispatcher("view/admin/viewpermission.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RoleDAO roleDAO = new RoleDAO();
        PermissionDAO permissionDAO = new PermissionDAO();
        List<Role> allRoles = roleDAO.getAllRole();
        List<Permission> allPermissions = permissionDAO.getAllPermission();
        Integer claimPermissionId = findPermissionIdByName(allPermissions, CLAIM_PURCHASE_ORDER);
        
        boolean allSuccess = true;
        
        for (Role role : allRoles) {
            int roleId = role.getRoleId();
            
            String paramName = "permissions_" + roleId;
            String[] permissionIdsArray = request.getParameterValues(paramName);
            
            List<Integer> permissionIds = new ArrayList<>();
            if (permissionIdsArray != null) {
                for (String id : permissionIdsArray) {
                    try {
                        permissionIds.add(Integer.parseInt(id));
                    } catch (NumberFormatException e) {
                    }
                }
            }

            // Keep "Claim Purchase Order" fixed for Staff only, not editable from ViewPermission.
            if (claimPermissionId != null) {
                permissionIds.removeIf(id -> id != null && id.equals(claimPermissionId));
                if (isStaffRole(role)) {
                    permissionIds.add(claimPermissionId);
                }
            }
            
            boolean success = roleDAO.updateRolePermissions(roleId, permissionIds);
            if (!success) {
                allSuccess = false;
            }
        }
        
        if (allSuccess) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null) {
                    List<String> permissionNames = roleDAO.getRolePermissionNames(currentUser.getRoleId());
                    session.setAttribute("userPermissions", new HashSet<>(permissionNames));
                }
            }
            response.sendRedirect("viewpermission?success=true");
        } else {
            response.sendRedirect("viewpermission?error=true");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
