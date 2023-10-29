package com.github.kentyeh.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlunit.WebConsole;

/**
 *
 * @author kent
 */
public class WebConsolLoger implements WebConsole.Logger {

    private static final Logger logger = LogManager.getLogger(WebConsolLoger.class);

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void error(Object message) {
        logger.error("{}", message);
    }

    @Override
    public void warn(Object message) {
        logger.warn("{}", message);
    }

    @Override
    public void info(Object message) {
        logger.info("{}", message);
    }

    @Override
    public void debug(Object message) {
        logger.debug("{}", message);
    }

    @Override
    public void trace(Object message) {
        logger.trace("{}", message);
    }

}
