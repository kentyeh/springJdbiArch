package com.github.kentyeh.context;

import org.apache.logging.log4j.LogManager;
import org.skife.jdbi.v2.logging.FormattedLog;

/**
 *
 * @author Kent Yeh
 */
public class JdbiLog extends FormattedLog {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(JdbiLog.class);

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected void log(String string) {
        logger.debug(string);
    }
}
