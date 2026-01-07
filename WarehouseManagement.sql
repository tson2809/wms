-- Database: quan_ly_dien_gia_dung
CREATE DATABASE quan_li_dien_gia_dung CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quan_li_dien_gia_dung;

-- Bảng Roles
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    role_description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name),
    INDEX idx_is_active (is_active)
);

-- Bảng Users
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(50),
    avatar VARCHAR(255),
    role_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE SET NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role_id (role_id),
    INDEX idx_is_active (is_active)
);

-- Bảng Permissions
CREATE TABLE permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) UNIQUE NOT NULL,
    permission_description TEXT,
    module VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_module (module)
);

-- Bảng Role_Permissions
CREATE TABLE role_permissions (
    role_permission_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE,
    UNIQUE KEY unique_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
);

-- Insert Roles
INSERT INTO roles (role_name, role_description, is_active) VALUES
('Admin', 'Admin hệ thống', TRUE),
('User', 'Người dùng', TRUE),
('Manager', 'Quản lý', TRUE);

-- Insert Users (password: 12345)
INSERT INTO users (username, email, password_hash, full_name, phone, address, role_id, is_active) VALUES
('admin', 'admin@example.com', '$2y$10$N9qo8uLOickgx2ZMRZoMye3RIJjfNcHdt/ZHpqAcpyqvlGQ6T8X7i', 'Admin', '0123456789', 'Hà Nội', 1, TRUE),
('manager1', 'manager1@example.com', '$2y$10$N9qo8uLOickgx2ZMRZoMye3RIJjfNcHdt/ZHpqAcpyqvlGQ6T8X7i', 'Nguyễn Văn A', '0987654321', 'TP HCM', 3, TRUE),
('user1', 'user1@example.com', '$2y$10$N9qo8uLOickgx2ZMRZoMye3RIJjfNcHdt/ZHpqAcpyqvlGQ6T8X7i', 'Trần Thị B', '0912345678', 'Đà Nẵng', 2, TRUE),
('user2', 'user2@example.com', '$2y$10$N9qo8uLOickgx2ZMRZoMye3RIJjfNcHdt/ZHpqAcpyqvlGQ6T8X7i', 'Lê Văn C', '0909876543', 'Cần Thơ', 2, TRUE);

-- Insert Permissions
INSERT INTO permissions (permission_name, permission_description, module) VALUES
('View Homepage', 'Xem trang chủ', 'Common'),
('Login', 'Đăng nhập', 'Common'),
('Logout', 'Đăng xuất', 'Common'),
('Forgot Password', 'Quên mật khẩu', 'Common'),
('View My Profile', 'Xem thông tin cá nhân', 'Common'),
('Change Password', 'Đổi mật khẩu', 'Common'),
('View User List', 'Xem danh sách người dùng', 'Manager'),
('View User Information', 'Xem thông tin người dùng', 'Manager'),
('Add New User', 'Thêm người dùng mới', 'Manager'),
('Active/Deactive User', 'Kích hoạt/Vô hiệu hóa người dùng', 'Manager'),
('Update User Information', 'Cập nhật thông tin người dùng', 'Manager'),
('View Role List', 'Xem danh sách vai trò', 'Admin'),
('View Role Details', 'Xem chi tiết vai trò', 'Admin'),
('Update Role Information', 'Cập nhật thông tin vai trò', 'Admin'),
('Active/Deactive Role', 'Kích hoạt/Vô hiệu hóa vai trò', 'Admin'),
('View Role Permissions', 'Xem quyền của vai trò', 'Admin'),
('Edit Role Permissions', 'Chỉnh sửa quyền của vai trò', 'Admin');

-- Insert Role_Permissions
-- Admin
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_name = 'Admin';

-- User
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_name = 'User' AND p.module = 'Common';

-- Manager
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_name = 'Manager' 
AND p.module IN ('Common', 'Manager');
