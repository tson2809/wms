/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Admin;

import dal.PermissionDAO;
import dal.RoleDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Permission;
import model.Role;

/**
 *
 * @author thais
 */
public class ViewPermissionController extends HttpServlet {    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RoleDAO roleDAO = new RoleDAO();
        PermissionDAO permissionDAO = new PermissionDAO();
        
        List<Role> allRoles = roleDAO.getAllRole();
        
        List<Permission> allPermissions = permissionDAO.getAllPermission();
        
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
        List<Role> allRoles = roleDAO.getAllRole();
        
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
            
            boolean success = roleDAO.updateRolePermissions(roleId, permissionIds);
            if (!success) {
                allSuccess = false;
            }
        }
        
        if (allSuccess) {
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
