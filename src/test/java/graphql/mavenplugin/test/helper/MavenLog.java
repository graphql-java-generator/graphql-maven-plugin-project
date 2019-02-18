/**
 * 
 */
package graphql.mavenplugin.test.helper;

import org.apache.logging.log4j.Logger;

/**
 * Simulate a
 * 
 * @author Etienne
 */
public class MavenLog implements org.apache.maven.plugin.logging.Log {

    /** The logger, to which all log calls will be delegated */
    Logger logger = null;

    public MavenLog(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(CharSequence arg0) {
        logger.debug(arg0);
    }

    @Override
    public void debug(Throwable arg0) {
        logger.debug(arg0);
    }

    @Override
    public void debug(CharSequence arg0, Throwable arg1) {
        logger.debug(arg0);
    }

    @Override
    public void error(CharSequence arg0) {
        logger.error(arg0);
    }

    @Override
    public void error(Throwable arg0) {
        logger.error(arg0);
    }

    @Override
    public void error(CharSequence arg0, Throwable arg1) {
        logger.error(arg0, arg1);
    }

    @Override
    public void info(CharSequence arg0) {
        logger.info(arg0);
    }

    @Override
    public void info(Throwable arg0) {
        logger.info(arg0);
    }

    @Override
    public void info(CharSequence arg0, Throwable arg1) {
        logger.info(arg0);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(CharSequence arg0) {
        logger.warn(arg0);
    }

    @Override
    public void warn(Throwable arg0) {
        logger.warn(arg0);
    }

    @Override
    public void warn(CharSequence arg0, Throwable arg1) {
        logger.warn(arg0, arg1);
    }
}
