/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.NotificationDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Notification;

/**
 *
 * @author thais
 */
@WebServlet(name = "NotificationController", urlPatterns = { "/notification-list" })
public class NotificationListController extends HttpServlet {
    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String action = request.getParameter("action");

        if (idParam != null && "delete".equals(action)) {
            try {
                int id = Integer.parseInt(idParam);
                notificationDAO.deleteNotification(id);
            } catch (NumberFormatException e) {
            }
            response.sendRedirect(request.getContextPath() + "/notification-list");
            return;
        }

        handleList(request, response);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String searchNormalized = (search != null && !search.trim().isEmpty())
                ? search.trim().replaceAll("\\s+", " ")
                : null;
        String notificationType = request.getParameter("notificationType");
        String sort = request.getParameter("sort");
        String pageRaw = request.getParameter("page");
        String numberPerPageRaw = request.getParameter("numberPerPage");
        
        List<String> allTypes = notificationDAO.getNotificationTypes();
        
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

        List<Notification> list;
        if (searchNormalized != null && !searchNormalized.isEmpty()) {
            list = new ArrayList<>(notificationDAO.searchNotifications(searchNormalized));
        } else {
            list = new ArrayList<>(notificationDAO.getAllNotifications());
        }

        // Filter by notification type
        if (notificationType != null && !notificationType.trim().isEmpty()
                && !"ALL".equalsIgnoreCase(notificationType)) {
            list.removeIf(n -> !notificationType.equalsIgnoreCase(n.getNotificationType()));
        }

        // Sort
        if ("title_asc".equals(sort)) {
            list.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        } else if ("title_desc".equals(sort)) {
            list.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
        } else if ("date_asc".equals(sort)) {
            list.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        } else if ("date_desc".equals(sort)) {
            list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        int totalNotifications = list.size();
        int listOfPage = (int) Math.ceil((double) totalNotifications / numberPerPage);
        int fromIndex = (page - 1) * numberPerPage;
        int toIndex = Math.min(fromIndex + numberPerPage, totalNotifications);
        List<Notification> paginatedList = (fromIndex < totalNotifications)
                ? list.subList(fromIndex, toIndex)
                : Collections.emptyList();
        
        request.setAttribute("allTypes", allTypes);
        request.setAttribute("notifications", paginatedList);
        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("notificationType", notificationType != null ? notificationType : "");
        request.setAttribute("sort", sort != null ? sort : "");
        request.setAttribute("page", page);
        request.setAttribute("listOfPage", listOfPage);
        request.setAttribute("numberPerPage", numberPerPage);
        request.setAttribute("totalNotifications", totalNotifications);
        request.getRequestDispatcher("/view/common/notification_list.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Notification List Controller";
    }
}
