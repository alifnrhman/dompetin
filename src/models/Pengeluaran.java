package models;

public class Pengeluaran extends Transaksi {
    public Pengeluaran(Integer id, String kategori, long jumlah, String tanggal, String keterangan) {
        super(id, "Pengeluaran", kategori, jumlah, tanggal, keterangan);
    }
}
