import com.formdev.flatlaf.FlatLightLaf;
import database.DatabaseHelper;
import gui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Buat tabel jika blm ada
        DatabaseHelper db = new DatabaseHelper();
        db.createTable();

        FlatLightLaf.setup();
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10); // semua komponen umum
        UIManager.put("TextComponent.arc", 10); // textfield, textarea, dll

        // Tampilkan GUI (di thread Swing)
        SwingUtilities.invokeLater(() -> {
            MainFrame form = new MainFrame();
            form.setVisible(true);
        });
    }
}
