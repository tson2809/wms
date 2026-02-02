package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author thais
 */
public class DBContext {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quan_ly_dien_gia_dung_demo";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123@123a";
    
    public Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver không tìm thấy!", e);
        }
    }
    
    /**
     * Test kết nối database
     */
    public static void main(String[] args) {
        DBContext dbContext = new DBContext();
        try (Connection conn = dbContext.getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối database thành công!");
                System.out.println("Database: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
