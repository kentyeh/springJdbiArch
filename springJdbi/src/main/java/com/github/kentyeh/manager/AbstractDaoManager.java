package com.github.kentyeh.manager;

import java.beans.PropertyEditorSupport;
import org.jdbi.v3.core.JdbiException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 *
 * @author Kent Yeh
 * @param <K> Key Class
 * @param <E> Entity Class
 */
@Transactional(readOnly = true)
public abstract class AbstractDaoManager<K, E> extends PropertyEditorSupport implements ApplicationContextAware {

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public abstract E findByPrimaryKey(K key);

    private org.springframework.context.ApplicationContext context;

    public ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }

    /**
     * Convert String to key
     *
     * @param text primary key string.
     * @return primary key
     */
    public abstract K text2Key(String text);

    @Override
    public String getAsText() {
        return getValue() == null ? "" : getValue().toString();
    }

    /**
     *
     * @param text primary key string.
     * @throws IllegalArgumentException
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(StringUtils.hasText(text) ? findByPrimaryKey(text2Key(text)) : null);
        } catch (RuntimeException e) {
            setValue(null);
        }
    }

    protected Exception extractSQLException(Exception ex) {
        Throwable result = ex;
        boolean found = false;
        while (result != null) {
            if (result instanceof java.sql.SQLException) {
                found = true;
                break;
            } else if (result.getCause() == null) {
                break;
            } else {
                result = result.getCause();
            }
        }

        return found ? (java.sql.SQLException) result : ex;
    }

    protected JdbiException jdbiException(String msg) {
        return new JdbiExceptionImpl(msg);
    }

    protected JdbiException jdbiException(String msg, Throwable cause) {
        return new JdbiExceptionImpl(msg, cause);
    }

    protected JdbiException jdbiException(Throwable cause) {
        return new JdbiExceptionImpl(cause);
    }

    private static class JdbiExceptionImpl extends JdbiException {

        public JdbiExceptionImpl(String message, Throwable cause) {
            super(message, cause);
        }

        public JdbiExceptionImpl(String message) {
            super(message);
        }

        public JdbiExceptionImpl(Throwable cause) {
            super(cause);
        }
    }
}
