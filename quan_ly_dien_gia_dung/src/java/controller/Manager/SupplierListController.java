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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Supplier;

/**
 *
 * @author thais
 */
@WebServlet(name = "SupplierListController", urlPatterns = {"/supplier-list"})
public class SupplierListController extends HttpServlet {
    private SupplierDAO supplierDAO = new SupplierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("status");
        if (idParam != null && statusParam != null) {
            try {
                supplierDAO.updateSupplierStatus(Integer.parseInt(idParam), statusParam);
            } catch (NumberFormatException e) {
            }
            response.sendRedirect(request.getContextPath() + "/supplier-list");
            return;
        }

        handleList(request, response);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String searchNormalized = (search != null && !search.trim().isEmpty())
                ? search.trim().replaceAll("\\s+", " ") : null;
        String status = request.getParameter("status");
        String sort = request.getParameter("sort");
        String pageRaw = request.getParameter("page");
        String numberPerPageRaw = request.getParameter("numberPerPage");

        int page = 1;
        int numberPerPage = 10;
        try {
            if (pageRaw != null) {
                page = Integer.parseInt(pageRaw);
            }
        } catch (NumberFormatException ignored) {
        }
        try {
            if (numberPerPageRaw != null) {
                numberPerPage = Integer.parseInt(numberPerPageRaw);
                if (numberPerPage != 5 && numberPerPage != 10 && numberPerPage != 20) {
                    numberPerPage = 10;
                }
            }
        } catch (NumberFormatException ignored) {
        }

        List<Supplier> list;
        if (searchNormalized != null && !searchNormalized.isEmpty()) {
            list = new ArrayList<>(supplierDAO.searchSuppliers(searchNormalized));
        } else {
            list = new ArrayList<>(supplierDAO.getAllSuppliers());
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            list.removeIf(s -> !status.equalsIgnoreCase(s.getStatus()));
        }

        if ("name_asc".equals(sort)) {
            list.sort((a, b) -> a.getSupplierName().compareToIgnoreCase(b.getSupplierName()));
        } else if ("name_desc".equals(sort)) {
            list.sort((a, b) -> b.getSupplierName().compareToIgnoreCase(a.getSupplierName()));
        }

        int totalSuppliers = list.size();
        int listOfPage = (int) Math.ceil((double) totalSuppliers / numberPerPage);
        int fromIndex = (page - 1) * numberPerPage;
        int toIndex = Math.min(fromIndex + numberPerPage, totalSuppliers);
        List<Supplier> paginatedList = (fromIndex < totalSuppliers)
                ? list.subList(fromIndex, toIndex)
                : Collections.emptyList();

        request.setAttribute("suppliers", paginatedList);
        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("sort", sort != null ? sort : "");
        request.setAttribute("page", page);
        request.setAttribute("listOfPage", listOfPage);
        request.setAttribute("numberPerPage", numberPerPage);
        request.setAttribute("totalSuppliers", totalSuppliers);
        request.getRequestDispatcher("/view/manager/supplier_list.jsp").forward(request, response);
    }
}
