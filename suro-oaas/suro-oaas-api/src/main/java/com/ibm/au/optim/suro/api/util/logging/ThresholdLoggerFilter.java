package com.ibm.au.optim.suro.api.util.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Threshold logger filter. Can be used to filter messages for different loggers above a certain level.
 * Inside an appender definition add the following filter:
 *
 * <filter class="com.ibm.au.optim.suro.api.util.ThresholdLoggerFilter">
 *   <logger>com.ibm.au.optim</logger>
 *   <level>DEBUG</level>
 * </filter>
 *
 * This will filter all messages above DEBUG of loggers with the prefix com.ibm.au.optim into this appender.
 *
 * @author Peter Ilfrich
 */
public class ThresholdLoggerFilter extends Filter<ILoggingEvent> {

    protected Level level;
    protected String logger;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (!event.getLoggerName().startsWith(logger))
            return FilterReply.DENY;

        if (event.getLevel().isGreaterOrEqual(level)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }

    /**
     * Setter for the level
     * @param level - the new log level for this filter
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Setter for the logger
     * @param logger - the new logger for this filter
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     * Activates the filter.
     */
    public void start() {
        if (this.level != null && this.logger != null) {
            super.start();
        }
    }
}
