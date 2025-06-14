package utils;

import java.text.NumberFormat;
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
}
