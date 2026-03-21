package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


/**
 * 
 * @author thais
 */
public class DBContext {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/quan_ly_dien_gia_dung"
            + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "12345";
    private static final String JNDI_NAME = "java:comp/env/jdbc/quan_ly_dien_gia_dung";
    private static volatile DataSource dataSource;

    public Connection getConnection() throws SQLException {
        DataSource ds = getDataSource();
        if (ds != null) {
            return ds.getConnection();
        }

        // Fallback cho local/dev nếu chưa cấu hình JNDI.
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

        } catch (SQLException e) {
            throw new SQLException("Khong the mo ket noi DB (JNDI va DriverManager deu that bai)", e);
        }
    }

    private DataSource getDataSource() throws SQLException {
        if (dataSource != null) {
            return dataSource;
        }

        synchronized (DBContext.class) {
            if (dataSource != null) {
                return dataSource;
            }

            try {
                InitialContext context = new InitialContext();
                dataSource = (DataSource) context.lookup(JNDI_NAME);
                return dataSource;
            } catch (NamingException e) {
                return null;
            }
        }
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC Driver khong tim thay");
        }
    }
    
    /**
     * Test kết nối database
     */
    public static void main(String[] args) {
        DBContext dbContext = new DBContext();
        try (Connection conn = dbContext.getConnection()) {
            if (conn != null) {
                System.out.println("Ket noi database thanh cong!");
                System.out.println("Database: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("Loi ket noi database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
