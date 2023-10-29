package com.github.kentyeh.tools;

import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MinguoDateTimeFormatter {

    private static final Map<String, DateTimeFormatter> pf = new ConcurrentHashMap<>();

    static {
        pf.put("yyyMMdd", new DateTimeFormatterBuilder().parseLenient()
                .appendPattern("yyyMMdd")
                .toFormatter()
                .withChronology(MinguoChronology.INSTANCE)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault())));
        pf.put("yyy/MM/dd", new DateTimeFormatterBuilder().parseLenient()
                .appendPattern("yyy/MM/dd")
                .toFormatter()
                .withChronology(MinguoChronology.INSTANCE)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault())));
    }

    public static DateTimeFormatter getMonoInstance() {
        return pf.get("yyyMMdd");
    }

    public static DateTimeFormatter getSlashInstance() {
        return pf.get("yyy/MM/dd");
    }

    public static DateTimeFormatter getInstance(String pattern) {
        DateTimeFormatter dtf = pf.get(pattern);
        if (dtf == null) {
            dtf = new DateTimeFormatterBuilder().parseLenient()
                    .appendPattern(pattern)
                    .toFormatter()
                    .withChronology(MinguoChronology.INSTANCE)
                    .withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
            pf.put(pattern, dtf);
            return dtf;
        } else {
            return dtf;
        }
    }
}
