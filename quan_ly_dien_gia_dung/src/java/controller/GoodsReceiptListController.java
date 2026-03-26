/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.GoodsReceiptDAO;
import dal.PurchaseOrderDAO;
import dal.SalesReturnDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Date;
import java.util.List;
import java.time.LocalDate;
import model.GoodsReceipt;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsReceiptListController", urlPatterns = {"/goods-receipt-list"})
public class GoodsReceiptListController extends HttpServlet {
    private GoodsReceiptDAO goodsReceiptDAO = new GoodsReceiptDAO();
    private SalesReturnDAO salesReturnDAO = new SalesReturnDAO();
    private PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();

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
                User user = (User) request.getSession().getAttribute("user");
                Integer approvedBy = "completed".equals(statusParam) && user != null ? user.getUserId() : null;
                int receiptId = Integer.parseInt(idParam);
                boolean success = goodsReceiptDAO.updateGoodsReceiptStatus(receiptId, statusParam, approvedBy);
                if (success) {
                    if ("completed".equals(statusParam) || "cancelled".equals(statusParam)) {
                        GoodsReceipt receipt = goodsReceiptDAO.getGoodsReceiptById(receiptId);
                        if (receipt != null) {
                            if ("completed".equals(statusParam)) {
                                if (receipt.getSalesReturnId() != null) {
                                    salesReturnDAO.completeSalesReturn(receipt.getSalesReturnId());
                                }
                                if (receipt.getPurchaseOrderId() != null) {
                                    purchaseOrderDAO.completePurchaseOrder(receipt.getPurchaseOrderId());
                                }
                            } else if ("cancelled".equals(statusParam)) {
                                if (receipt.getPurchaseOrderId() != null) {
                                    purchaseOrderDAO.cancelPurchaseOrder(receipt.getPurchaseOrderId());
                                }
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
            }
            response.sendRedirect(request.getContextPath() + "/goods-receipt-list");
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

        Date todaySql = Date.valueOf(LocalDate.now());
        if (toDate != null && toDate.after(todaySql)) {
            toDate = todaySql;
        }

        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            Date temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }
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

        List<GoodsReceipt> list;
        if (searchNormalized != null && !searchNormalized.isEmpty()) {
            list = new ArrayList<>(goodsReceiptDAO.searchGoodsReceipts(searchNormalized));
        } else {
            list = new ArrayList<>(goodsReceiptDAO.getAllGoodsReceipts());
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            list.removeIf(gr -> !status.equalsIgnoreCase(gr.getStatus()));
        }

        final Date finalFromDate = fromDate;
        final Date finalToDate = toDate;
        if (finalFromDate != null || finalToDate != null) {
            list.removeIf(gr -> {
                Date rd = gr.getReceiptDate();
                if (rd == null) {
                    return false;
                }
                boolean beforeFrom = finalFromDate != null && rd.before(finalFromDate);
                boolean afterTo = finalToDate != null && rd.after(finalToDate);
                return beforeFrom || afterTo;
            });
        }

        // Sort
        if ("date_asc".equals(sort)) {
            list.sort((a, b) -> a.getReceiptDate().compareTo(b.getReceiptDate()));
        } else if ("date_desc".equals(sort)) {
            list.sort((a, b) -> b.getReceiptDate().compareTo(a.getReceiptDate()));
        } else if ("code_asc".equals(sort)) {
            list.sort((a, b) -> a.getReceiptCode().compareToIgnoreCase(b.getReceiptCode()));
        } else if ("code_desc".equals(sort)) {
            list.sort((a, b) -> b.getReceiptCode().compareToIgnoreCase(a.getReceiptCode()));
        }

        int totalReceipts = list.size();
        int listOfPage = (int) Math.ceil((double) totalReceipts / numberPerPage);
        int fromIndex = (page - 1) * numberPerPage;
        int toIndex = Math.min(fromIndex + numberPerPage, totalReceipts);
        List<GoodsReceipt> paginatedList = (fromIndex < totalReceipts)
                ? list.subList(fromIndex, toIndex)
                : Collections.emptyList();

        request.setAttribute("receipts", paginatedList);
        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("sort", sort != null ? sort : "");
        request.setAttribute("fromDate", fromDate != null ? fromDate.toString() : "");
        request.setAttribute("toDate", toDate != null ? toDate.toString() : "");
        request.setAttribute("page", page);
        request.setAttribute("listOfPage", listOfPage);
        request.setAttribute("numberPerPage", numberPerPage);
        request.setAttribute("totalReceipts", totalReceipts);
        request.getRequestDispatcher("/view/common/goods-receipt-list.jsp").forward(request, response);
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
