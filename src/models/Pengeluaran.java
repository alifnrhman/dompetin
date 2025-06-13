package models;

public class Pengeluaran extends Transaksi {
    public Pengeluaran(String kategori, double jumlah, String tanggal, String keterangan) {
        super("Pengeluaran", kategori, jumlah, tanggal, keterangan);
    }
}
