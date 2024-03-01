package itoozh.core.util;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LanguageUtils {

    protected static Config config = new Config(new File(Main.getInstance().getDataFolder(), "language.yml"), Config.YAML);

    public static String getString(String path) {
        Object value = config.get(path);

        if (value instanceof String) {
            return TextFormat.colorize((String) value); // El valor es una cadena
        } else if (value instanceof Iterable<?>) {
            StringBuilder stringBuilder = new StringBuilder();

            for (Object element : (Iterable<?>) value) {
                stringBuilder.append(element.toString()).append("\n");
            }

            return TextFormat.colorize(stringBuilder.toString().trim()); // Convertir lista a cadena con saltos de línea
        }

        return ""; // Valor no es una cadena ni una lista, retornar cadena vacía
    }

    public static List<String> splitStringToList(String input) {
        String[] splitArray = input.split("\n");
        return new ArrayList<>(Arrays.asList(splitArray));
    }

    public static String join(List<String> list, String delimiter) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

    public static String convertListToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        for (String element : list) {
            result.append(element).append("\n");
        }
        return result.toString();
    }

    public static String formatDetailed(long input) {
        return input == -1L ? "Permanent" : formatDetailed(input, TimeUnit.MILLISECONDS);
    }
    public static String formatDetailed(long input, TimeUnit timeUnit) {
        if (input == -1L) {
            return "Permanent";
        } else {
            long secs = timeUnit.toSeconds(input);
            if (secs == 0L) {
                return "0 seconds";
            } else {
                long remainder = secs % 86400L;
                long days = secs / 86400L;
                long hours = remainder / 3600L;
                long minutes = remainder / 60L - hours * 60L;
                long seconds = remainder % 3600L - minutes * 60L;
                String fDays = days > 0L ? " " + days + " day" + (days > 1L ? "s" : "") : "";
                String fHours = hours > 0L ? " " + hours + " hour" + (hours > 1L ? "s" : "") : "";
                String fMinutes = minutes > 0L ? " " + minutes + " minute" + (minutes > 1L ? "s" : "") : "";
                String fSeconds = seconds > 0L ? " " + seconds + " second" + (seconds > 1L ? "s" : "") : "";
                return (fDays + fHours + fMinutes + fSeconds).trim();
            }
        }
    }

    public static String formatTimeShort(long input) {
        return input == -1L ? "Permanent" : formatTimeShort(input, TimeUnit.MILLISECONDS);
    }

    public static String formatTimeShort(long input, TimeUnit timeUnit) {
        if (input == -1L) {
            return "Permanent";
        } else {
            long secs = timeUnit.toSeconds(input);
            if (secs == 0L) {
                return "0s";
            } else {
                long remainder = secs % 86400L;
                long days = secs / 86400L;
                long hours = remainder / 3600L;
                long minutes = remainder / 60L - hours * 60L;
                long seconds = remainder % 3600L - minutes * 60L;
                String fDays = days > 0L ? " " + days + "d" : "";
                String fHours = hours > 0L ? " " + hours + "h" : "";
                String fMinutes = minutes > 0L ? " " + minutes + "m" : "";
                String fSeconds = seconds > 0L ? " " + seconds + "s" : "";
                return (fDays + fHours + fMinutes + fSeconds).trim();
            }
        }
    }
}
