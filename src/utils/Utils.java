package utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String formatRupiah(long amount) {
        Locale indo = new Locale("id", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(indo);
        String result = format.format(amount);
        return result.replace(",00", "").replace("Rp", "Rp");
    }

    public static long parseRupiah(String formatted) {
        return Long.parseLong(formatted.replaceAll("[^\\d]", ""));
    }

    public static String formatTanggalLengkap(String tanggal) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));

            Date date = sdfInput.parse(tanggal);
            return sdfOutput.format(date);
        } catch (Exception e) {
            return tanggal;
        }
    }
}
