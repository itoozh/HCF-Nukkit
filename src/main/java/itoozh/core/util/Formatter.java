package itoozh.core.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Formatter {
    private static final DecimalFormat HEALTH_FORMATTER;
    private static final Map<Long, String> ENERGY_CACHE;
    private static final long MINUTE;
    private static final Map<Double, String> DTR_CACHE;
    private static final long HOUR;
    private static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING;
    private static final DecimalFormat BARD_ENERGY_FORMATTER;
    private static final DecimalFormat DTR_FORMATTER;
    private static final ThreadLocal<DecimalFormat> REMAINING_SECONDS;
    public static SimpleDateFormat DATE_FORMAT;

    static {
        DTR_FORMATTER = new DecimalFormat("0.0");
        HEALTH_FORMATTER = new DecimalFormat("#.#");
        BARD_ENERGY_FORMATTER = new DecimalFormat("0.0");
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
        REMAINING_SECONDS = ThreadLocal.withInitial(() -> new DecimalFormat("0.#"));
        REMAINING_SECONDS_TRAILING = ThreadLocal.withInitial(() -> new DecimalFormat("0.0"));
        DTR_CACHE = new HashMap<>();
        ENERGY_CACHE = new HashMap<>();
        DATE_FORMAT = new SimpleDateFormat("dd MMMM hh:mm");
    }

    public static String formatHHMMSS(long timer) {
        long seconds = timer / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes %= 60;
        seconds %= 60;

        String formattedHours = String.format("%02d", hours);
        String formattedMinutes = String.format("%02d", minutes);
        String formattedSeconds = String.format("%02d", seconds);

        return formattedHours + ":" + formattedMinutes + ":" + formattedSeconds;
    }


    public static String formatDtr(double time) {
        String dtr = Formatter.DTR_CACHE.get(time);
        if (dtr != null) {
            return dtr;
        }
        String format = Formatter.DTR_FORMATTER.format(time);
        Formatter.DTR_CACHE.put(time, format);

        return format;
    }

    public static Long parse(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        if (input.equals("0")) {
            return 0L;
        }
        long l = 0L;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            char chr = input.charAt(i);
            if (Character.isDigit(chr)) {
                builder.append(chr);
            } else {
                String s;
                if (Character.isLetter(chr) && !(s = String.valueOf(builder)).isEmpty()) {
                    l += convert(Integer.parseInt(s), chr);
                    builder = new StringBuilder();
                }
            }
        }
        return (l == 0L || l == -1L) ? null : l;
    }

    private static long convert(int time, char timers) {
        switch (timers) {
            case 'y': {
                return time * TimeUnit.DAYS.toMillis(365L);
            }
            case 'M': {
                return time * TimeUnit.DAYS.toMillis(30L);
            }
            case 'd': {
                return time * TimeUnit.DAYS.toMillis(1L);
            }
            case 'h': {
                return time * TimeUnit.HOURS.toMillis(1L);
            }
            case 'm': {
                return time * TimeUnit.MINUTES.toMillis(1L);
            }
            case 's': {
                return time * TimeUnit.SECONDS.toMillis(1L);
            }
            default: {
                return -1L;
            }
        }
    }


    public static String formatBardEnergy(long time) {
        String s = Formatter.ENERGY_CACHE.get(time);
        if (s != null) {
            return s;
        }
        String ss = Formatter.BARD_ENERGY_FORMATTER.format(time);
        Formatter.ENERGY_CACHE.put(time, ss);
        return ss;
    }

    public static String formatHealth(double input) {
        return Formatter.HEALTH_FORMATTER.format(input);
    }

    public static String formatDetailed(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("El tiempo no puede ser negativo.");
        }

        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        StringBuilder result = new StringBuilder();

        if (weeks > 0) {
            result.append(weeks).append(" semana").append(weeks > 1 ? "s" : "").append(" ");
            days %= 7;
        }

        if (days > 0) {
            result.append(days).append(" día").append(days > 1 ? "s" : "").append(" ");
        }

        if (hours > 0) {
            result.append(hours).append(" hora").append(hours > 1 ? "s" : "").append(" ");
            minutes %= 60;
        }

        if (minutes > 0) {
            result.append(minutes).append(" minuto").append(minutes > 1 ? "s" : "").append(" ");
            seconds %= 60;
        }

        if (seconds > 0) {
            result.append(seconds).append(" segundo").append(seconds > 1 ? "s" : "").append(" ");
        }

        return result.toString().trim();
    }

    public static String getRemaining(long timer, boolean format) {
        if (format && timer < 60000) {
            int seconds = (int) (timer / 1000);
            return (seconds + "s");
        }

        int hours = (int) (timer / 3600000);  // 3600000 milisegundos en una hora
        int minutes = (int) ((timer % 3600000) / 60000);
        int seconds = (int) ((timer % 60000) / 1000);

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }


}

