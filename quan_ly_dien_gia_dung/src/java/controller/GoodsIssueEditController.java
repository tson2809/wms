/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dal.GoodsIssueDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.GoodsIssue;
import model.GoodsIssueDetail;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "GoodsIssueEditController", urlPatterns = {"/goods-issue-detail"})
public class GoodsIssueEditController extends HttpServlet {
    private GoodsIssueDAO goodsIssueDAO = new GoodsIssueDAO();
    private static final Gson gson = new Gson();

    private void loadAndForward(int issueId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GoodsIssue issue = goodsIssueDAO.getGoodsIssueById(issueId);

        if (issue == null) {
            response.sendRedirect(request.getContextPath() + "/goods-issue-list");
            return;
        }

        List<GoodsIssueDetail> details = goodsIssueDAO.getGoodsIssueDetails(issueId);
        boolean readOnly = "completed".equals(issue.getStatus()) || "cancelled".equals(issue.getStatus());

        // Convert details to JSON for JS rendering (similar to goods-receipt-edit)
        List<Map<String, Object>> productsForJs = new ArrayList<>();
        int idCounter = 1;
        for (GoodsIssueDetail d : details) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", idCounter++);
            m.put("variantId", d.getVariantId());
            m.put("code", d.getVariantSku());
            m.put("name", d.getProductName());
            m.put("unit", d.getUnitName());
            m.put("quantity", d.getQuantity());
            m.put("serials", d.getSerials() != null ? d.getSerials() : new ArrayList<>());
            productsForJs.add(m);
        }
        String productsJson = gson.toJson(productsForJs);

        request.setAttribute("issue", issue);
        request.setAttribute("details", details);
        request.setAttribute("readOnly", readOnly);
        request.setAttribute("productsJson", productsJson);
        request.getRequestDispatcher("/view/common/goods-issue-edit.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/goods-issue-list");
            return;
        }

        try {
            int issueId = Integer.parseInt(idParam);
            loadAndForward(issueId, request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/goods-issue-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String statusParam = request.getParameter("status");

        if (idParam == null || statusParam == null) {
            response.sendRedirect(request.getContextPath() + "/goods-issue-list");
            return;
        }

        try {
            int issueId = Integer.parseInt(idParam);
            User user = (User) request.getSession().getAttribute("user");
            Integer approvedBy = "completed".equals(statusParam) && user != null ? user.getUserId() : null;

            boolean success = goodsIssueDAO.updateGoodsIssueStatus(issueId, statusParam, approvedBy);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/goods-issue-list");
                return;
            } else {
                response.sendRedirect(request.getContextPath() + "/goods-issue-list");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/goods-issue-list");
        }
    }
}
