package gui;

import database.DatabaseHelper;
import models.Transaksi;
import utils.Utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel PanelMain, PanelTable, PanelForm;
    private JTable TableBulanan;
    private DefaultTableModel tableModel;
    private JComboBox cbBulan, cbTahun;
    private JButton editButton;
    private JButton hapusButton;
    private JButton tambahButton;
    private JButton simpanButton;
    private JButton kembaliButton;
    private JTextField tfKeterangan;
    private JComboBox cbJenis;
    private JComboBox cbKategori;
    private JTextField tfTanggal;
    private JTextField tfJumlah;
    private JScrollPane scrollPane;
    private JLabel labelPemasukan;
    private JLabel labelSaldo;
    private JLabel labelPengeluaran;
    private JPanel PanelEdit;
    private JButton batalButton;
    private JTextField tfEditJumlah;
    private JTextField tfEditTanggal;
    private JTextField tfEditKeterangan;
    private JComboBox cbEditJenis;
    private JComboBox cbEditKategori;
    private JButton simpanEditButton;

    private DatabaseHelper dbHelper = new DatabaseHelper();
    List<Transaksi> transaksiList = dbHelper.getAllTransaksi();


    public MainFrame() {
        setTitle("Dompetin");
        setContentPane(PanelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(800, 610);
        setLocationRelativeTo(null);

        String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        cbBulan.setModel(new DefaultComboBoxModel<>(bulan));

        for (int tahun = 2020; tahun <= 2030; tahun++) {
            cbTahun.addItem(String.valueOf(tahun));
        }


        LocalDate now = LocalDate.now();
        cbBulan.setSelectedIndex(now.getMonthValue() - 1);
        cbTahun.setSelectedItem(String.valueOf(now.getYear()));

        PanelMain.setLayout(new CardLayout());
        PanelMain.add(PanelTable, "table");
        PanelMain.add(PanelForm, "form");
        PanelMain.add(PanelEdit, "edit");

        tampilkanDataBerdasarkanFilter();

        cbBulan.addActionListener(e -> tampilkanDataBerdasarkanFilter());
        cbTahun.addActionListener(e -> tampilkanDataBerdasarkanFilter());

        tambahButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showForm();
            }
        });

        TableBulanan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = TableBulanan.getSelectedRow();
                hapusButton.setEnabled(selectedRow != -1);
                editButton.setEnabled(selectedRow != -1);
            }
        });

        hapusButton.addActionListener(e -> {
            int selectedRow = TableBulanan.getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = (DefaultTableModel) TableBulanan.getModel();

                String tanggal = (String) model.getValueAt(selectedRow, 1);
                String jenis = (String) model.getValueAt(selectedRow, 2);
                String kategori = (String) model.getValueAt(selectedRow, 3);
                long jumlah = Utils.parseRupiah((String) model.getValueAt(selectedRow, 4));
                String keterangan = (String) model.getValueAt(selectedRow, 5);
                int id = (int) model.getValueAt(selectedRow, 6);

                // Buat objek Transaksi
                Transaksi tr = new Transaksi(id, jenis, kategori, jumlah, tanggal, keterangan);

                int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus data?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (dbHelper.deleteTransaksi(tr)) {
                        model.removeRow(selectedRow); // Hapus dari tabel
                        JOptionPane.showMessageDialog(null, "Data berhasil dihapus.");
                        hapusButton.setEnabled(false);
                        tampilkanDataBerdasarkanFilter();
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal menghapus data.");
                    }
                }
            }
        });

        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTable();
            }
        });

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String jenis = (String) cbJenis.getSelectedItem();
                    String kategori = (String) cbKategori.getSelectedItem();
                    long jumlah = Utils.parseRupiah(tfJumlah.getText());
                    String tanggal = tfTanggal.getText();
                    String keterangan = tfKeterangan.getText();

                    Transaksi transaksi = new Transaksi(jenis, kategori, jumlah, tanggal, keterangan);
                    new DatabaseHelper().insertTransaksi(transaksi);

                    JOptionPane.showMessageDialog(null, "Transaksi berhasil ditambahkan");
                    showTable();
                    tampilkanDataBerdasarkanFilter();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Input tidak valid: " + ex.getMessage());
                }
            }
        });


        editButton.addActionListener(e -> {
            int selectedRow = TableBulanan.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < transaksiList.size()) {
                Transaksi tr = transaksiList.get(selectedRow);
                showEditDialog(tr);
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih.");
            }
        });

        DocumentListener listener = new DocumentListener() {
            boolean ignore = false;

            public void insertUpdate(DocumentEvent e) { format(e); }
            public void removeUpdate(DocumentEvent e) { format(e); }
            public void changedUpdate(DocumentEvent e) { format(e); }

            private void format(DocumentEvent e) {
                if (ignore) return;
                ignore = true;

                SwingUtilities.invokeLater(() -> {
                    try {
                        JTextField field = null;
                        JButton button = null;

                        if (e.getDocument() == tfJumlah.getDocument()) {
                            field = tfJumlah;
                            button = simpanButton;
                        } else if (e.getDocument() == tfEditJumlah.getDocument()) {
                            field = tfEditJumlah;
                            button = simpanEditButton;
                        }

                        if (field != null) {
                            String rawText = field.getText().replaceAll("[^\\d]", "");
                            if (!rawText.isEmpty()) {
                                long value = Long.parseLong(rawText);
                                field.setText(Utils.formatRupiah(value));
                                if (button != null) button.setEnabled(true);
                            } else {
                                field.setText("");
                                if (button != null) button.setEnabled(false);
                            }
                        }
                    } finally {
                        ignore = false;
                    }
                });
            }
        };

        tfJumlah.getDocument().addDocumentListener(listener);
        tfEditJumlah.getDocument().addDocumentListener(listener);


        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTable();
            }
        });
    }

    private void showForm() {
        CardLayout cl = (CardLayout) PanelMain.getLayout();
        cl.show(PanelMain, "form");
    }
    private void showTable() {
        CardLayout cl = (CardLayout) PanelMain.getLayout();
        cl.show(PanelMain, "table");
    }
    private void showEdit() {
        CardLayout cl = (CardLayout) PanelMain.getLayout();
        cl.show(PanelMain, "edit");
    }

    public void tampilkanDataKeTabel(List<Transaksi> transaksiList) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"No", "Tanggal", "Jenis", "Kategori", "Jumlah", "Keterangan", "ID"}, 0
        );

        // Ambil data

        int no = 1; // Nomor urut dimulai dari 1
        for (Transaksi t : transaksiList) {
            model.addRow(new Object[]{
                    no++,
                    t.getTanggal(),
                    t.getJenis(),
                    t.getKategori(),
                    Utils.formatRupiah(t.getJumlah()),
                    t.getKeterangan(),
                    t.getId()
            });
        }

        TableBulanan.setModel(model);

        // kolom "No"
        TableBulanan.getColumnModel().getColumn(0).setMinWidth(30);
        TableBulanan.getColumnModel().getColumn(0).setPreferredWidth(30);
        TableBulanan.getColumnModel().getColumn(0).setMaxWidth(40);

        // Sembunyikan kolom ID
        TableBulanan.getColumnModel().getColumn(6).setMinWidth(0);
        TableBulanan.getColumnModel().getColumn(6).setMaxWidth(0);
        TableBulanan.getColumnModel().getColumn(6).setWidth(0);
    }

    public void tampilkanDataBerdasarkanFilter() {
        int bulanDipilih = cbBulan.getSelectedIndex() + 1;
        String tahunDipilih = (String) cbTahun.getSelectedItem();

        List<Transaksi> semuaTransaksi = dbHelper.getAllTransaksi();
        List<Transaksi> transaksiFiltered = new ArrayList<>();

        long totalMasuk = 0;
        long totalKeluar = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Transaksi tr : semuaTransaksi) {
            try {
                LocalDate tanggal = LocalDate.parse(tr.getTanggal(), formatter);
                if (tanggal.getMonthValue() == bulanDipilih && tanggal.getYear() == Integer.parseInt(tahunDipilih)) {
                    transaksiFiltered.add(tr);
                    if (tr.getJenis().equalsIgnoreCase("Pemasukan")) {
                        totalMasuk += tr.getJumlah();
                    } else if (tr.getJenis().equalsIgnoreCase("Pengeluaran")) {
                        totalKeluar += tr.getJumlah();
                    }
                }
            } catch (DateTimeParseException e) {
                System.err.println("Format tanggal salah: " + tr.getTanggal());
            }
        }

        long saldo = totalMasuk - totalKeluar;

        tampilkanDataKeTabel(transaksiFiltered);
        labelPemasukan.setText(Utils.formatRupiah(totalMasuk));
        labelPengeluaran.setText(Utils.formatRupiah(totalKeluar));
        labelSaldo.setText(Utils.formatRupiah(saldo));
    }

    private void showEditDialog(Transaksi tr) {
        showEdit();

        cbEditJenis.setSelectedItem(tr.getJenis());
        cbEditKategori.setSelectedItem(tr.getKategori());
        tfEditJumlah.setText(Utils.formatRupiah(tr.getJumlah()));
        tfEditTanggal.setText(tr.getTanggal());
        tfEditKeterangan.setText(tr.getKeterangan());

        simpanEditButton.addActionListener(e -> {
            tr.setJenis(cbEditJenis.getSelectedItem().toString());
            tr.setKategori(cbEditKategori.getSelectedItem().toString());
            tr.setJumlah(Utils.parseRupiah(tfEditJumlah.getText()));
            tr.setTanggal(tfEditTanggal.getText());
            tr.setKeterangan(tfEditKeterangan.getText());

            if (dbHelper.updateTransaksi(tr)) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                showTable();
                tampilkanDataBerdasarkanFilter();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data.");
            }
        });
    }
}

