<%-- 
    Document   : RoleSideBar
    Created on : 3 thg 2, 2026, 08:11:13
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    model.Role userRole = (model.Role) session.getAttribute("userRole");
    String sidebarPath = "/view/admin/components/sidebarAdmin.jsp";
    
    if (userRole != null && userRole.getRoleName() != null) {
        String role = userRole.getRoleName().toLowerCase();
        sidebarPath = String.format("/view/%s/components/sidebar%s.jsp", 
                                     role, 
                                     role.substring(0,1).toUpperCase() + role.substring(1));
    }
%>
<jsp:include page="<%= sidebarPath %>" />

