package models;

public class Pemasukan extends Transaksi {
    public Pemasukan(Integer id, String kategori, long jumlah, String tanggal, String keterangan) {
        super(id, "Pemasukan", kategori, jumlah, tanggal, keterangan);
    }
}
