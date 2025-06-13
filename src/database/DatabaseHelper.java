package database;
import models.Transaksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    public static final String DB_URL = "jdbc:sqlite:dompetin.db";
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS transaksi ("+
                "id INT PRIMARY KEY AUTOINCREMENT, "+
                "jenis TEXT not null, "+
                "kategori TEXT not null, "+
                "jumlah INT not null, "+
                "tanggal TEXT not null, "+
                "keterangan TEXT";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertTransaksi(Transaksi tr) {
        String sql = "INSERT INTO transaksi(jenis, kategori, jumlah, tanggal, keterangan) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, tr.getJenis());
            pstmt.setString(2, tr.getKategori());
            pstmt.setDouble(3, tr.getJumlah());
            pstmt.setString(4, tr.getTanggal());
            pstmt.setString(5, tr.getKeterangan());
            pstmt.executeUpdate();
            System.out.println("Transaksi berhasil ditambahkan.");
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTransaksi(Transaksi tr) {
        String sql = "UPDATE transaksi set jenis = ?, kategori = ?, jumlah = ?, tanggal = ?, keterangan = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, tr.getJenis());
            pstmt.setString(2, tr.getKategori());
            pstmt.setDouble(3, tr.getJumlah());
            pstmt.setString(4, tr.getTanggal());
            pstmt.setString(5, tr.getKeterangan());
            pstmt.setInt(6, tr.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Transaksi berhasil diperbarui.");
                return true;
            } else {
                System.out.println("Transaksi dengan ID " + tr.getId() + " tidak ditemukan.");
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTransaksi(Transaksi tr) {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, tr.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Transaksi berhasil dihapus.");
                return true;
            } else {
                System.out.println("Transaksi dengan ID " + tr.getId() + " tidak ditemukan.");
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
        public List<Transaksi> getAllTransaksi() {
            List<Transaksi> transaksiList = new ArrayList<>();
            String sql = "SELECT id, jenis, kategori, jumlah, tanggal, keterangan FROM Transaksi";
            try (Connection conn = connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()){
                    Transaksi transaksi = new Transaksi(
                            rs.getInt("id"),
                            rs.getString("jenis"),
                            rs.getString("kategori"),
                            rs.getDouble("jumlah"),
                            rs.getString("tanggal"),
                            rs.getString("keterangan"));
                    transaksiList.add(transaksi);
                }

                } catch (SQLException e){
                e.printStackTrace();
            }
            return transaksiList;
    }

    public List<Transaksi> getTransaksiByMonth(String month, String year) {
        List<Transaksi> transaksiList = new ArrayList<>();
        String sql = "SELECT id, jenis, kategori, jumlah, tanggal, keterangan FROM Transaksi WHERE substr(tanggal, 1, 7) = ? ORDER BY tanggal DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, year + "-" + month);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaksi transaksi = new Transaksi(
                        rs.getInt("id"),
                        rs.getString("jenis"),
                        rs.getString("kategori"),
                        rs.getDouble("jumlah"),
                        rs.getString("tanggal"),
                        rs.getString("keterangan")
                );
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transaksiList;
    }
}

