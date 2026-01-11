/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp; 
/**
 *
 * @author laptop368
 */
public class User {
    private int userId;
    private String userName;
    private String email;
    private String password;
    private String fullName;
    private String address;
    private String avatar;
    private Role role;  
    private boolean isActive;
    private Timestamp createAt;

    public User() {
    }

    public User(int userId, String userName, String email, String password, String fullName, String address, String avatar, Role role, boolean isActive, Timestamp createAt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.avatar = avatar;
        this.role = role;
        this.isActive = isActive;
        this.createAt = createAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", userName=" + userName + ", email=" + email + ", password=" + password + ", fullName=" + fullName + ", address=" + address + ", avatar=" + avatar + ", role=" + role + ", isActive=" + isActive + ", createAt=" + createAt + '}';
    }
    
    
    
}
