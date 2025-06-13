package models;

public class Pemasukan extends Transaksi {
    public Pemasukan(String kategori, double jumlah, String tanggal, String keterangan) {
        super("Pemasukan", kategori, jumlah, tanggal, keterangan);
    }
}
