package com.github.kentyeh.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Kent Yeh
 */
public class LocalDateTimePropertyEditor extends DatePropertyEditor {

    public LocalDateTimePropertyEditor() {
    }

    public LocalDateTimePropertyEditor(LocalDateTime vlaue) {
        super.setValue(vlaue);
    }

    public LocalDateTimePropertyEditor minguo() {
        this.minguo = true;
        return this;
    }

    public LocalDateTimePropertyEditor era() {
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
        } else if (getValue() instanceof LocalDateTime ldt) {
            if (minguo) {
                String val = MinguoDateTimeFormatter.getSlashInstance().format(ldt);
                if (containTime) {
                    return val + " " + DateTimeFormatter.ofPattern("HH:mm:ss").format(ldt);
                } else {
                    return val + " 00:00:00";
                }
            } else {
                return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(ldt);
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
            for (Map.Entry<String, String> entry : DatePropertyEditor.patterns3.entrySet()) {
                if (text.matches(entry.getValue())) {
                    containTime = entry.getKey().contains("H");
                    minguo = true;
                    setValue(LocalDateTime.parse(text, MinguoDateTimeFormatter.getInstance(entry.getKey())));
                    return;
                }
            }
            for (Map.Entry<String, String> entry : patterns4.entrySet()) {
                if (text.matches(entry.getValue())) {
                    containTime = entry.getKey().contains("H");
                    minguo = false;
                    setValue(LocalDateTime.parse(text, DateTimeFormatter.ofPattern(entry.getKey())));
                }
            }
        }
    }
}
