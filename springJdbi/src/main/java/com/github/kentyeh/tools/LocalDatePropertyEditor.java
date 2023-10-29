package com.github.kentyeh.tools;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author kent
 */
public class LocalDatePropertyEditor extends PropertyEditorSupport {

    private static final Map<String, String> patterns3 = new HashMap<>();
    private static final Map<String, String> patterns4 = new HashMap<>();
    private boolean minguo = false;

    static {
        patterns3.put("yyyMMdd", "^\\d{7}$");
        patterns3.put("yyy/M/d", "^\\d{3}/\\d{1,2}/\\d{1,2}$");
        patterns3.put("yyy-M-d", "^\\d{3}-\\d{1,2}-\\d{1,2}$");
        patterns4.put("yyyyMMdd", "^\\d{8}$");
        patterns4.put("yyyy/M/d", "^\\d{4}/\\d{1,2}/\\d{1,2}$");
        patterns4.put("yyyy-M-d", "^\\d{4}-\\d{1,2}-\\d{1,2}$");
    }

    public LocalDatePropertyEditor() {
    }

    public LocalDatePropertyEditor(LocalDate value) {
        super.setValue(value);
    }

    public boolean isMinguo() {
        return minguo;
    }

    public void setMinguo(boolean minguo) {
        this.minguo = minguo;
    }

    public LocalDatePropertyEditor minguo() {
        this.minguo = true;
        return this;
    }

    public LocalDatePropertyEditor era() {
        this.minguo = false;
        return this;
    }

    public String getFormatedDate() {
        String[] val = getAsText().split("\\s+");
        return val.length > 0 ? val[0] : "";
    }

    public String getPlainDate() {
        return getFormatedDate().replaceAll("/", "");
    }

    @Override
    public String getAsText() {
        if (Objects.isNull(getValue())) {
            return "";
        } else if (getValue() instanceof LocalDate ld) {
            if (minguo) {
                return MinguoDateTimeFormatter.getSlashInstance().format(ld);
            } else {
                return DateTimeFormatter.ofPattern("yyyy/MM/dd").format(ld);
            }
        } else {
            return getValue().toString();
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (null == text || text.isBlank()) {
            setValue(null);
        } else {
            for (Map.Entry<String, String> entry : patterns3.entrySet()) {
                if (text.matches(entry.getValue())) {
                    minguo = true;
                    setValue(LocalDate.parse(text, MinguoDateTimeFormatter.getInstance(entry.getKey())));
                    return;
                }
            }
            for (Map.Entry<String, String> entry : patterns4.entrySet()) {
                if (text.matches(entry.getValue())) {
                    minguo = false;
                    setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern(entry.getKey())));
                }
            }
        }
    }
}
