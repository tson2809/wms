<%-- 
    Document   : homepage
    Created on : 19 thg 1, 2026, 19:47:40
    Author     : thais
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>WMS_HA - Hệ Thống Quản Lý Kho</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>

    <body>
        <!-- Header -->
        <header id="header">
            <div class="container">
                <nav>
                    <a href="#" class="logo">
                        <i class="fa-solid fa-cube text-gradient"></i>
                        <span>WMS_HA</span>
                    </a>
                    <ul class="nav-links">
                        <li><a href="#features">Tính Năng</a></li>
                        <li><a href="#solutions">Giải Pháp</a></li>
                        <li><a href="#pricing">Bảng Giá</a></li>
                        <li><a href="#contact">Liên Hệ</a></li>
                    </ul>
                    <div class="auth-buttons">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng Nhập</a>
                    </div>
                </nav>
            </div>
        </header>

        <!-- Hero Section -->
        <section class="hero">
            <div class="container hero-content">
                <!-- Text Content -->
                <div class="hero-text">

                    <h1>
                        <span style="display: block; white-space: nowrap;">Hệ Thống</span>
                        <span style="display: block; white-space: nowrap;">Quản Lý Kho</span>
                        <span class="text-gradient" style="display: block; white-space: nowrap;">Điện Gia Dụng</span>
                    </h1>
                    <p class="hero-desc">
                        Tối ưu hóa quy trình nhập xuất, kiểm soát tồn kho theo thời gian thực và tự động hóa báo cáo với
                        hệ
                        thống WMS tiên tiến nhất.
                    </p>
                    <div class="hero-actions">
                        <a href="#" class="btn btn-primary">Dùng Thử</a>
                        <a href="#" class="btn btn-glass"><i class="fa-solid fa-phone"></i> Liên Hệ</a>
                    </div>
                </div>
            </div>

            <div class="hero-visual">
                <div class="glass-panel stats-card">
                    <div
                        style="margin-bottom: 1.5rem; display: flex; justify-content: space-between; align-items: center;">
                        <h3 style="font-size: 1.25rem;">Tổng Quan Kho</h3>
                    </div>

                    <div class="stats-row">
                        <div style="display: flex; align-items: center;">
                            <div class="stat-icon" style="background: rgba(0, 156, 255, 0.2); color: var(--primary);">
                                <i class="fa-solid fa-box-open"></i>
                            </div>
                            <div class="stat-info">
                                <h4>Tổng Tồn Kho</h4>
                                <p>12,450 SP</p>
                            </div>
                        </div>
                        <div class="trend-up"><i class="fa-solid fa-arrow-trend-up"></i> +12%</div>
                    </div>

                    <div class="stats-row">
                        <div style="display: flex; align-items: center;">
                            <div class="stat-icon" style="background: rgba(255, 0, 156, 0.2); color: var(--secondary);">
                                <i class="fa-solid fa-dolly"></i>
                            </div>
                            <div class="stat-info">
                                <h4>Hàng Nhập Kho</h4>
                                <p>500 SP</p>
                            </div>
                        </div>
                        <div class="trend-up"><i class="fa-solid fa-arrow-trend-up"></i> +5%</div>
                    </div>

                    <div class="stats-row">
                        <div style="display: flex; align-items: center;">
                            <div class="stat-icon" style="background: rgba(0, 212, 255, 0.2); color: var(--accent);">
                                <i class="fa-solid fa-bell"></i>
                            </div>
                            <div class="stat-info">
                                <h4>Cảnh Báo Sắp Hết</h4>
                                <p>3 Items</p>
                            </div>
                        </div>
                        <div style="color: #f59e0b; font-size: 0.875rem;">Cần Nhập</div>
                    </div>

                    <div
                        style="margin-top: 1.5rem; height: 4px; background: rgba(255,255,255,0.1); border-radius: 2px; overflow: hidden;">
                        <div
                            style="width: 92%; height: 100%; background: linear-gradient(90deg, var(--primary), var(--secondary));">
                        </div>
                    </div>
                    <p style="text-align: right; font-size: 0.75rem; color: var(--text-muted); margin-top: 0.5rem;">Hiệu
                        suất kho đạt 92%</p>
                </div>

                <div style="
                     position: absolute;
                     top: -20px;
                     right: -20px;
                     width: 100px;
                     height: 100px;
                     background: var(--primary);
                     border-radius: 50%;
                     filter: blur(80px);
                     z-index: -1;
                     opacity: 0.6;">
                </div>
            </div>
        </div>
    </section>

    <footer
        style="padding: 4rem 0; border-top: 1px solid var(--glass-border); text-align: center; color: var(--text-muted);">
        <div class="container">
            <p>&copy; 2026 WMS_HA. All rights reserved.</p>
        </div>
    </footer>

    <script>
        const header = document.getElementById('header');
        window.addEventListener('scroll', () => {
            if (window.scrollY > 50) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        });
    </script>
    </body>
</html>