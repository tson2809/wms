# Hệ Thống Quản Lý Kho & Điện Gia Dụng (Warehouse Management System - WMS)

Một ứng dụng web quản lý kho hàng và kinh doanh thiết bị điện gia dụng chuyên nghiệp được xây dựng trên nền tảng **Java Servlet, JSP, JDBC** và **SQL Server**. Hệ thống cung cấp giải pháp quản lý toàn diện từ kiểm soát danh mục sản phẩm, biến thể, mã Serial, đến quy trình Nhập kho (Goods Receipt), Xuất kho (Goods Issue), Kiểm kê (Inventory Sheet), Trả hàng (Return Order) và Báo cáo quản trị.

---

## 1. Tính Năng Nổi Bật

### 1.1 Quản Lý Hệ Thống & Phân Quyền (Security & Access Control)
- **Xác thực & Bảo mật**: Đăng nhập, Đăng xuất, Khôi phục mật khẩu (Forgot Password), Đổi mật khẩu và Quản lý hồ sơ cá nhân.
- **Phân quyền đa cấp**: Phân quyền chi tiết theo Vai trò (Role) và Quyền hạn (Permission Matrix) cho Quản lý (Manager), Thủ kho (Warehouse Staff), Bán hàng (Sales Staff).
- **Nhật ký thao tác (Audit Log)**: Ghi lại lịch sử tác động hệ thống của người dùng để đảm bảo tính minh bạch và an toàn dữ liệu.

### 1.2 Quản Lý Sản Phẩm & Biến Thể (Catalog & Serial Management)
- **Thương hiệu & Thể loại (Brand & Category)**: Quản lý thương hiệu thiết bị gia dụng (Panasonic, Sunhouse, Philips...) và nhóm ngành hàng.
- **Sản phẩm & Biến thể (Product & Variants)**: Quản lý chi tiết thuộc tính sản phẩm, biến thể màu sắc/công suất, đơn vị tính (Unit).
- **Mã Serial (Product Serial)**: Quản lý định danh duy nhất theo Serial từng máy, phục vụ bảo hành và tra cứu xuất xứ.
- **Lịch sử giá (Price History)**: Theo dõi biến động giá nhập và giá bán theo thời gian.

### 1.3 Quản Lý Kho Hàng & Giao Dịch (Warehouse & Inventory Operations)
- **Phiếu Nhập Kho (Goods Receipt - GRN)**: Tạo, cập nhật và phê duyệt phiếu nhập hàng từ nhà cung cấp vào kho.
- **Phiếu Xuất Kho (Goods Issue - GIN)**: Quản lý xuất hàng cho bán lẻ hoặc điều chuyển.
- **Kiểm kê kho (Inventory Sheet)**: Tạo phiếu kiểm kê thực tế, so sánh chênh lệch, phê duyệt và xử lý thất thoát/thừa thiếu (Resolve Deficit).
- **Cảnh báo tồn kho (Inventory Alert)**: Tự động cảnh báo khi tồn kho giảm xuống dưới ngưỡng tối thiểu hoặc sản phẩm sắp hết hàng.
- **Lịch sử giao dịch kho (Inventory Transactions)**: Ghi nhận chi tiết từng giao dịch tăng/giảm kho.

### 1.4 Quản Lý Mua Hàng & Nhà Cung Cấp (Procurement & Supplier)
- **Nhà cung cấp (Suppliers)**: Quản lý thông tin nhà cung cấp, thông tin liên hệ và lịch sử giao dịch.
- **Đơn Mua Hàng (Purchase Order - PO)**: Lập đơn đặt hàng mua thiết bị từ nhà cung cấp, theo dõi trạng thái duyệt, nhận hàng và xác nhận đơn hàng (Claim).

### 1.5 Quản Lý Trả Hàng (Return & Claim Management)
- **Trả hàng Nhà cung cấp (Return Order)**: Xử lý trả hàng lỗi/hỏng về nhà cung cấp.
- **Khách hàng trả hàng (Sales Return)**: Tiếp nhận và xử lý hàng khách trả lại, hoàn tồn kho hoặc xuất bù.

### 1.6 Báo Cáo Thống Kê & Thông Báo Nội Bộ (Analytics & Notifications)
- **Báo cáo quản trị (Manager Reports)**: Báo cáo tổng quan nhập xuất tồn, doanh thu và xu hướng sản phẩm.
- **Thông báo nội bộ (Notifications)**: Gửi và quản lý thông báo giữa các phòng ban.

---

## 2. Công Nghệ Sử Dụng (Tech Stack)

- **Backend**: Java Web (Servlet 4.0 / Jakarta EE), JDBC, DAO Pattern.
- **Frontend**: JSP (JavaServer Pages), JSTL, HTML5, CSS3, JavaScript, Bootstrap.
- **Database**: Microsoft SQL Server.
- **IDE Hỗ trợ**: NetBeans / Apache NetBeans, Eclipse, IntelliJ IDEA.
- **Build Tool / Project Format**: Apache Ant / NetBeans Web Project.

---

## 3. Cấu Trúc Thư Mục Dự Án

```
quan_ly_dien_gia_dung/
├── src/java/
│   ├── controller/               # Java Servlets xử lý request & routing
│   │   ├── Auth/                 # Đăng nhập, đổi mật khẩu, profile
│   │   ├── Manager/              # Quản lý thương hiệu, thể loại, báo cáo
│   │   ├── Admin/                # Quản lý user, phân quyền, audit log
│   │   └── Warehouse/            # Quản lý nhập kho, xuất kho, kiểm kê
│   ├── dal/                      # Data Access Objects (DAOs) tương tác DB
│   ├── entity/ / model/          # Các đối tượng Entity (Product, User, PO...)
│   ├── dto/                      # Data Transfer Objects
│   └── filter/                   # Authentication & Audit Filters
└── web/                          # Giao diện JSP & Static Assets
    ├── view/                     # Thư mục chứa các trang JSP theo role
    │   ├── admin/
    │   ├── manager/
    │   └── warehouse/
    ├── css/
    ├── js/
    └── images/
```

---

## 4. Hướng Dẫn Cài Đặt & Chạy Dự Án

### 4.1 Yêu Cầu Môi Trường
- **JDK**: Java Development Kit (JDK 8 / JDK 11 / JDK 17).
- **Web Server**: Apache Tomcat 9.0+.
- **Database**: Microsoft SQL Server (SQL Server Management Studio - SSMS).
- **IDE**: NetBeans IDE 12+.

### 4.2 Các Bước Cài Đặt
1. **Clone repository**:
   ```bash
   git clone https://github.com/tson2809/wms.git
   ```
2. **Cấu hình Database**:
   - Chạy script tạo cơ sở dữ liệu SQL Server.
   - Chỉnh sửa thông tin kết nối DB (Server, Port, Database Name, User, Password) trong file `DBContext.java`:
     `quan_ly_dien_gia_dung/src/java/dal/DBContext.java`
3. **Mở dự án trong NetBeans**:
   - File -> Open Project -> Chọn thư mục `quan_ly_dien_gia_dung`.
4. **Deploy & Run**:
   - Chuột phải vào project -> Select `Clean and Build`.
   - Chuột phải chọn `Run` (hoặc nhấn `F6`) để khởi chạy trên Apache Tomcat Server.