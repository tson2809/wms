/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author thais
 */
public class Notification {
    private int notificationId;
    private int creatorId;
    private String title;
    private String content;
    private Timestamp createdAt;

    public Notification() {
    }

    public Notification(int notificationId, int creatorId, String title, String content, Timestamp createdAt) {
        this.notificationId = notificationId;
        this.creatorId = creatorId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" + "notificationId=" + notificationId + ", creatorId=" + creatorId + ", title=" + title + ", content=" + content + ", createdAt=" + createdAt + '}';
    }
    
    
}
