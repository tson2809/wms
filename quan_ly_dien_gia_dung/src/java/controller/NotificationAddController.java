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
@WebServlet(name = "NotificationAddController", urlPatterns = { "/notification-add" })
public class NotificationAddController extends HttpServlet {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String> allTypes = notificationDAO.getNotificationTypes();
        request.setAttribute("allTypes", allTypes);

        request.getRequestDispatcher("/view/common/notification_add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (User) session.getAttribute("user");      
        String notificationType = request.getParameter("notificationType");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        boolean hasError = false;

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
            request.setAttribute("notificationType", notificationType);
            request.setAttribute("title", title);
            request.setAttribute("content", content);
            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.getRequestDispatcher("/view/common/notification_add.jsp").forward(request, response);
            return;
        }

        Notification notification = new Notification();
        notification.setNotificationType(notificationType.trim());
        notification.setTitle(title.trim());
        notification.setContent(content.trim());

        if (loggedUser != null) {
            notification.setCreatorId(loggedUser.getUserId());
        }

        int id = notificationDAO.insertNotification(notification);

        if (id > 0) {
            request.setAttribute("successMessage", "Thêm thông báo thành công.");
            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.getRequestDispatcher("/view/common/notification_add.jsp").forward(request, response);
        } else {
            request.setAttribute("errorTitle", "Không thể thêm thông báo. Vui lòng thử lại.");
            request.setAttribute("notificationType", notificationType);
            request.setAttribute("title", title);
            request.setAttribute("content", content);
            List<String> allTypes = notificationDAO.getNotificationTypes();
            request.setAttribute("allTypes", allTypes);
            request.getRequestDispatcher("/view/common/notification_add.jsp").forward(request, response);
        }
    }
}
