package models;

public class Transaksi {
    private String jenis;
    private String kategori;
    private long jumlah;
    private String tanggal;
    private String keterangan;
    private int id;

    // Tanpa parameter id untuk insert transaksi, dll.
    public Transaksi(String jenis, String kategori, long jumlah, String tanggal, String keterangan) {
        this.jenis = jenis;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.keterangan = keterangan;
    }

    // Dengan parameter id untuk delete dan edit transaksi
    public Transaksi(int id, String jenis, String kategori, long jumlah, String tanggal, String keterangan) {
        this.id = id;
        this.jenis = jenis;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.keterangan = keterangan;
    }

    // Getter & Setter
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public long getJumlah() { return jumlah; }
    public void setJumlah(long jumlah) { this.jumlah = jumlah; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
}