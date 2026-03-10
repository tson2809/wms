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
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Notification;
import model.User;

/**
 *
 * @author thais
 */
@WebServlet(name = "NotificationEditController", urlPatterns = { "/notification-edit" })
public class NotificationEditController extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/notification-list");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Notification notification = notificationDAO.getNotificationById(id);

            if (notification == null) {
                response.sendRedirect(request.getContextPath() + "/notification-list");
                return;
            }

            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.setAttribute("notification", notification);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/common/notification_detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/notification-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User loggedUser = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        String notificationType = request.getParameter("notificationType");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        boolean hasError = false;

        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/notification-list");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/notification-list");
            return;
        }

        Notification existingNotification = notificationDAO.getNotificationById(id);
        if (existingNotification == null) {
            response.sendRedirect(request.getContextPath() + "/notification-list");
            return;
        }

        if (notificationType == null || notificationType.isBlank()) {
            request.setAttribute("errorNotificationType", "Loại thông báo không được để trống.");
            hasError = true;
        }

        if (title == null || title.isBlank()) {
            request.setAttribute("errorTitle", "Tiêu đề không được để trống.");
            hasError = true;
        } else if (title.trim().length() > 255) {
            request.setAttribute("errorTitle", "Tiêu đề không được vượt quá 255 ký tự.");
            hasError = true;
        }

        if (content == null || content.isBlank()) {
            request.setAttribute("errorContent", "Nội dung không được để trống.");
            hasError = true;
        }

        if (hasError) {
            existingNotification.setNotificationType(notificationType);
            existingNotification.setTitle(title);
            existingNotification.setContent(content);
            request.setAttribute("notification", existingNotification);
            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/common/notification_detail.jsp").forward(request, response);
            return;
        }

        Notification notification = new Notification();
        notification.setNotificationId(id);
        notification.setNotificationType(notificationType.trim());
        notification.setTitle(title.trim());
        notification.setContent(content.trim());
        notification.setCreatedAt(existingNotification.getCreatedAt());

        if (loggedUser != null) {
            notification.setCreatorId(loggedUser.getUserId());
        } else {
            notification.setCreatorId(existingNotification.getCreatorId());
        }

        int result = notificationDAO.updateNotification(notification);

        if (result > 0) {
            request.setAttribute("message", "Cập nhật thông báo thành công!");
            request.setAttribute("notification", notification);
            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/common/notification_detail.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Không thể cập nhật thông báo. Vui lòng thử lại.");
            request.setAttribute("notification", notification);
            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/view/common/notification_detail.jsp").forward(request, response);
        }
    }
}
