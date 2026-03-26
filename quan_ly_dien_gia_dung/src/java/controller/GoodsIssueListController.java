/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.GoodsIssueDAO;
import dal.ReturnOrderDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;
import model.GoodsIssue;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsIssueListController", urlPatterns = {"/goods-issue-list"})
public class GoodsIssueListController extends HttpServlet {
    private GoodsIssueDAO goodsIssueDAO = new GoodsIssueDAO();
    private ReturnOrderDAO returnOrderDAO = new ReturnOrderDAO();

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
                int issueId = Integer.parseInt(idParam);
                User user = (User) request.getSession().getAttribute("user");
                Integer approvedBy = "completed".equals(statusParam) && user != null ? user.getUserId() : null;
                GoodsIssue issue = goodsIssueDAO.getGoodsIssueById(issueId);
                boolean success = goodsIssueDAO.updateGoodsIssueStatus(issueId, statusParam, approvedBy);
                if (success && "completed".equals(statusParam) && issue != null) {
                    if (issue.getReturnOrderId() != null) {
                        returnOrderDAO.completeReturnOrder(issue.getReturnOrderId());
                    }
                    if (issue.getNotes() != null && issue.getNotes().contains("[PO_ID:")) {
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\[PO_ID:(\\d+)\\]").matcher(issue.getNotes());
                        if (m.find()) {
                            try {
                                int poId = Integer.parseInt(m.group(1));
                                dal.PurchaseOrderDAO poDAO = new dal.PurchaseOrderDAO();
                                poDAO.completePurchaseOrder(poId);
                            } catch (Exception ignored) {}
                        }
                    }
                } else if (success && "cancelled".equals(statusParam) && issue != null) {
                    if (issue.getNotes() != null && issue.getNotes().contains("[PO_ID:")) {
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\[PO_ID:(\\d+)\\]").matcher(issue.getNotes());
                        if (m.find()) {
                            try {
                                int poId = Integer.parseInt(m.group(1));
                                dal.PurchaseOrderDAO poDAO = new dal.PurchaseOrderDAO();
                                poDAO.cancelPurchaseOrder(poId);
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (NumberFormatException e) {}
            response.sendRedirect(request.getContextPath() + "/goods-issue-list");
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
        Date fromDate = parseSqlDate(request.getParameter("fromDate"));
        Date toDate = parseSqlDate(request.getParameter("toDate"));
        String pageRaw = request.getParameter("page");
        String numberPerPageRaw = request.getParameter("numberPerPage");

        Date todaySql = Date.valueOf(LocalDate.now());
        if (toDate != null && toDate.after(todaySql)) {
            toDate = todaySql;
        }

        int page = 1;
        int numberPerPage = 10;
        try { if (pageRaw != null) page = Integer.parseInt(pageRaw); } catch (NumberFormatException ignored) {}
        try {
            if (numberPerPageRaw != null) {
                numberPerPage = Integer.parseInt(numberPerPageRaw);
                if (numberPerPage != 5 && numberPerPage != 10 && numberPerPage != 20) numberPerPage = 10;
            }
        } catch (NumberFormatException ignored) {}

        List<GoodsIssue> list;
        if (searchNormalized != null && !searchNormalized.isEmpty()) {
            list = new ArrayList<>(goodsIssueDAO.searchGoodsIssues(searchNormalized));
        } else {
            list = new ArrayList<>(goodsIssueDAO.getAllGoodsIssues());
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            list.removeIf(gi -> !status.equalsIgnoreCase(gi.getStatus()));
        }

        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            Date temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        final Date finalFromDate = fromDate;
        final Date finalToDate = toDate;
        if (finalFromDate != null || finalToDate != null) {
            final LocalDate finalFromLocal = finalFromDate != null ? finalFromDate.toLocalDate() : null;
            final LocalDate finalToLocal = finalToDate != null ? finalToDate.toLocalDate() : null;
            list.removeIf(gi -> {
                if (gi.getIssueDate() == null) {
                    return false;
                }
                LocalDate issueLocal = gi.getIssueDate().toLocalDateTime().toLocalDate();
                boolean beforeFrom = finalFromLocal != null && issueLocal.isBefore(finalFromLocal);
                boolean afterTo = finalToLocal != null && issueLocal.isAfter(finalToLocal);
                return beforeFrom || afterTo;
            });
        }

        if ("date_asc".equals(sort)) {
            list.sort((a, b) -> a.getIssueDate().compareTo(b.getIssueDate()));
        } else if ("date_desc".equals(sort)) {
            list.sort((a, b) -> b.getIssueDate().compareTo(a.getIssueDate()));
        } else if ("code_asc".equals(sort)) {
            list.sort((a, b) -> a.getIssueCode().compareToIgnoreCase(b.getIssueCode()));
        } else if ("code_desc".equals(sort)) {
            list.sort((a, b) -> b.getIssueCode().compareToIgnoreCase(a.getIssueCode()));
        }

        int total = list.size();
        int listOfPage = (int) Math.ceil((double) total / numberPerPage);
        int fromIndex = (page - 1) * numberPerPage;
        int toIndex = Math.min(fromIndex + numberPerPage, total);
        List<GoodsIssue> paginatedList = (fromIndex < total)
                ? list.subList(fromIndex, toIndex)
                : Collections.emptyList();

        request.setAttribute("issues", paginatedList);
        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("sort", sort != null ? sort : "");
        request.setAttribute("fromDate", fromDate != null ? fromDate.toString() : "");
        request.setAttribute("toDate", toDate != null ? toDate.toString() : "");
        request.setAttribute("page", page);
        request.setAttribute("listOfPage", listOfPage);
        request.setAttribute("numberPerPage", numberPerPage);
        request.setAttribute("totalIssues", total);
        request.getRequestDispatcher("/view/common/goods-issue-list.jsp").forward(request, response);
    }

    private Date parseSqlDate(String param) {
        try {
            if (param == null || param.trim().isEmpty()) {
                return null;
            }
            return Date.valueOf(param.trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}
