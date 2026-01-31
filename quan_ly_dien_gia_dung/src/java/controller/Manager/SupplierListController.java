/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Manager;

import dal.SupplierDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Supplier;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "SupplierListController", urlPatterns = {"/supplier-list"})
public class SupplierListController extends HttpServlet {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private static final int DEFAULT_PAGE_SIZE = 10;

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

        int page = 1;
        int pageSize = DEFAULT_PAGE_SIZE;
        String keyword = null;
        String status = null;
        String sort = "supplier_id";
        String dir = "desc";

        @SuppressWarnings("unchecked")
        java.util.Map<String, String> saved = (session != null) ? (java.util.Map<String, String>) session.getAttribute("supplierListFilter") : null;
        if (saved != null) {
            session.removeAttribute("supplierListFilter");
            keyword = saved.get("keyword");
            status = saved.get("status");
            String sp = saved.get("sort");
            String dp = saved.get("dir");
            if (sp != null && !sp.isEmpty()) {
                sort = sp.equals("supplier_name_desc") ? "supplier_name" : sp;
                dir = "supplier_name_desc".equals(sp) ? "desc" : (dp != null && "desc".equalsIgnoreCase(dp) ? "desc" : "asc");
            }
            try {
                page = Integer.parseInt(saved.getOrDefault("page", "1"));
            } catch (NumberFormatException e) { }
            try {
                int ps = Integer.parseInt(saved.get("pageSize"));
                if (ps == 5 || ps == 10 || ps == 20) pageSize = ps;
            } catch (Exception e) { }
        }

        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword.trim().replaceAll("\\s+", " ");
        }

        int totalSuppliers = supplierDAO.countSuppliers(keyword, status);
        int totalPages = totalSuppliers > 0 ? (int) Math.ceil((double) totalSuppliers / pageSize) : 1;
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        List<Supplier> suppliers = supplierDAO.getSuppliersByPage(page, pageSize, keyword, status, sort, dir);

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("sort", sort);
        request.setAttribute("dir", dir);
        request.getRequestDispatcher("/view/manager/supplier_list.jsp").forward(request, response);
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

        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("status");
        if (idParam != null && statusParam != null) {
            try {
                int supplierId = Integer.parseInt(idParam);
                supplierDAO.updateSupplierStatus(supplierId, statusParam);
            } catch (NumberFormatException e) {
            }
            java.util.Map<String, String> filter = new java.util.HashMap<>();
            String kw = request.getParameter("keyword");
            String st = request.getParameter("statusFilter");
            String so = request.getParameter("sort");
            String di = request.getParameter("dir");
            String pg = request.getParameter("page");
            String pz = request.getParameter("pageSize");
            if (kw != null) filter.put("keyword", kw);
            if (st != null) filter.put("status", st);
            if (so != null) filter.put("sort", so);
            if (di != null) filter.put("dir", di);
            if (pg != null) filter.put("page", pg);
            if (pz != null) filter.put("pageSize", pz);
            request.getSession().setAttribute("supplierListFilter", filter);
            response.sendRedirect(request.getContextPath() + "/supplier-list");
            return;
        }

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); } catch (NumberFormatException e) { page = 1; }
        }
        int pageSize = DEFAULT_PAGE_SIZE;
        String pageSizeParam = request.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            try {
                int ps = Integer.parseInt(pageSizeParam);
                if (ps == 5 || ps == 10 || ps == 20) pageSize = ps;
            } catch (NumberFormatException e) { }
        }
        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword.trim().replaceAll("\\s+", " ");
        }
        String status = request.getParameter("status");
        String sortParam = request.getParameter("sort");
        String dirParam = request.getParameter("dir");
        String sort = "supplier_id";
        String dir = "desc";
        if (sortParam != null && !sortParam.isEmpty()) {
            if ("supplier_name_desc".equals(sortParam)) {
                sort = "supplier_name";
                dir = "desc";
            } else if ("supplier_name".equals(sortParam)) {
                sort = "supplier_name";
                dir = (dirParam != null && "desc".equalsIgnoreCase(dirParam)) ? "desc" : "asc";
            } else {
                sort = sortParam;
                dir = (dirParam != null && "desc".equalsIgnoreCase(dirParam)) ? "desc" : "asc";
            }
        }

        int totalSuppliers = supplierDAO.countSuppliers(keyword, status);
        int totalPages = totalSuppliers > 0 ? (int) Math.ceil((double) totalSuppliers / pageSize) : 1;
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        List<Supplier> suppliers = supplierDAO.getSuppliersByPage(page, pageSize, keyword, status, sort, dir);

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("sort", sort);
        request.setAttribute("dir", dir);
        request.getRequestDispatcher("/view/manager/supplier_list.jsp").forward(request, response);
    }
}
