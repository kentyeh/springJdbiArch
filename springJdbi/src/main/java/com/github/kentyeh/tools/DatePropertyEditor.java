package com.github.kentyeh.tools;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Kent Yeh
 */
public class DatePropertyEditor extends PropertyEditorSupport {

    private static final Logger logger = LogManager.getLogger(DatePropertyEditor.class);
    static final Map<String, String> patterns3 = new HashMap<>();
    static final Map<String, String> patterns4 = new HashMap<>();
    protected boolean containTime = true;
    protected boolean minguo = false;

    public DatePropertyEditor() {
    }

    public DatePropertyEditor(Date value) {
        super.setValue(value);
    }

    static {
        DatePropertyEditor.patterns3.put("yyyMMdd", "^\\d{7}$");
        DatePropertyEditor.patterns3.put("yyyMMddHHmmss", "^\\d{13}$");
        DatePropertyEditor.patterns3.put("yyy/M/d", "^\\d{3}/\\d{1,2}/\\d{1,2}$");
        DatePropertyEditor.patterns3.put("yyy/M/d H:m", "^\\d{3}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns3.put("yyy/M/d H:m:s", "^\\d{3}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns3.put("yyy-M-d", "^\\d{3}-\\d{1,2}-\\d{1,2}$");
        DatePropertyEditor.patterns3.put("yyy-M-d H:m", "^\\d{3}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns3.put("yyy-M-d H:m:s", "^\\d{3}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns4.put("yyyyMMdd", "^\\d{8}$");
        DatePropertyEditor.patterns4.put("yyyyMMddHHmmss", "^\\d{14}$");
        DatePropertyEditor.patterns4.put("yyyy/M/d", "^\\d{4}/\\d{1,2}/\\d{1,2}$");
        DatePropertyEditor.patterns4.put("yyyy/M/d H:m", "^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns4.put("yyyy/M/d H:m:s", "^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns4.put("yyyy-M-d", "^\\d{4}-\\d{1,2}-\\d{1,2}$");
        DatePropertyEditor.patterns4.put("yyyy-M-d H:m", "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$");
        DatePropertyEditor.patterns4.put("yyyy-M-d H:m:s", "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
    }

    @Override
    public String getAsText() {
        if (Objects.isNull(getValue())) {
            return "";
        } else if (getValue() instanceof Date date) {
            Instant instant = date.toInstant();
            if (minguo) {
                String val = MinguoDateTimeFormatter.getSlashInstance()
                        .format(instant.atZone(ZoneId.systemDefault()).toLocalDate());
                if (containTime) {
                    val = val + " " + DateTimeFormatter.ofPattern("HH:mm:ss").format(instant);
                }
                return val;
            } else {
                return DateTimeFormatter.ofPattern(containTime ? "yyyy/MM/dd HH:mm:ss" : "yyyy/MM/dd")
                        .format(instant);
            }
        } else {
            return getValue().toString();
        }
    }

    public String getColonTime() {
        String[] vals = getAsText().split("\\s+");
        if (vals.length > 1) {
            return vals[1];
        }
        return "";
    }

    public String getPlainTime() {
        String val = getColonTime();
        return val.replaceAll(":", "");
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (null == text || text.isBlank()) {
            setValue(null);
        } else {
            Object valObj = null;
            for (Map.Entry<String, String> entry : patterns3.entrySet()) {
                if (text.matches(entry.getValue())) {
                    try {
                        containTime = entry.getKey().contains("H");
                        minguo = true;
                        if (containTime) {
                            LocalDateTime ldt = LocalDateTime.parse(text, MinguoDateTimeFormatter.getInstance(entry.getKey()));
                            valObj = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                        } else {
                            LocalDate ld = LocalDate.parse(text, MinguoDateTimeFormatter.getInstance(entry.getKey()));
                            valObj = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        }
                    } catch (Exception ex) {
                        logger.error(String.format("無法轉換:%s[%s]:{}", text, entry.getKey()), ex.getMessage(), ex);
                    }
                    break;
                }
            }
            if (valObj == null) {
                SimpleDateFormat df = new SimpleDateFormat();
                for (Map.Entry<String, String> entry : patterns4.entrySet()) {
                    if (text.matches(entry.getValue())) {
                        containTime = entry.getKey().contains("H");
                        minguo = false;
                        df.applyPattern(entry.getKey());
                        try {
                            valObj = df.parse(text);
                        } catch (ParseException e) {
                            logger.error(String.format("Failed to convert:%s[%s]:{}", text, entry.getKey()), e.getMessage(), e);
                            return;
                        }
                        break;
                    }
                }
            }
            setValue(valObj);
        }
    }
}
