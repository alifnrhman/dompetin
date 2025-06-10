package database;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHelper {
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:keuangan.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Koneksi berhasil!");
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
}
