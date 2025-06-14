package gui;

import database.DatabaseHelper;
import models.Transaksi;
import utils.Utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel PanelMain, PanelTable, PanelForm;
    private JTable TableBulanan;
    private DefaultTableModel tableModel;
    private JComboBox cbBulan, cbTahun;
    private JButton editButton;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
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

    private DatabaseHelper dbHelper = new DatabaseHelper();

    public MainFrame() {
        setTitle("Dompetin");
        setContentPane(PanelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(800, 610);
        setLocationRelativeTo(null);
        loadDataKeTable();

        PanelMain.setLayout(new CardLayout());
        PanelMain.add(PanelTable, "table");
        PanelMain.add(PanelForm, "form");

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
                    loadDataKeTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Input tidak valid: " + ex.getMessage());
                }
            }
        });

        tfJumlah.getDocument().addDocumentListener(new DocumentListener() {
            boolean ignore = false;

            public void insertUpdate(DocumentEvent e) { format(); }
            public void removeUpdate(DocumentEvent e) { format(); }
            public void changedUpdate(DocumentEvent e) { format(); }

            private void format() {
                if (ignore) return;
                ignore = true;

                SwingUtilities.invokeLater(() -> {
                    try {
                        String text = tfJumlah.getText().replaceAll("[^\\d]", "");
                        if (!text.isEmpty()) {
                            long value = Long.parseLong(text);
                            tfJumlah.setText(Utils.formatRupiah(value));
                            simpanButton.setEnabled(true);
                        } else {
                            tfJumlah.setText("");
                            simpanButton.setEnabled(false);
                        }
                    } finally {
                        ignore = false;
                    }
                });
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

    public void loadDataKeTable() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"No", "Tanggal", "Jenis", "Kategori", "Jumlah", "Keterangan", "ID"}, 0
        );

        // Ambil data
        List<Transaksi> transaksiList = dbHelper.getAllTransaksi();

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
        TableColumn noColumn = TableBulanan.getColumnModel().getColumn(0);
        noColumn.setPreferredWidth(40);
        noColumn.setMinWidth(30);
        noColumn.setMaxWidth(50);

        // Sembunyikan kolom ID
        TableBulanan.getColumnModel().getColumn(6).setMinWidth(0);
        TableBulanan.getColumnModel().getColumn(6).setMaxWidth(0);
        TableBulanan.getColumnModel().getColumn(6).setWidth(0);
    }
}
