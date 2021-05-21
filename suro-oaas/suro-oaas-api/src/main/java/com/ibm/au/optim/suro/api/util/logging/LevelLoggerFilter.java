package com.ibm.au.optim.suro.api.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Threshold logger filter. Can be used to filter messages for different loggers matching a certain level.
 * Inside an appender definition add the following filter:
 *
 * <filter class="com.ibm.au.optim.suro.api.util.ThresholdLoggerFilter">
 *   <logger>com.ibm.au.optim</logger>
 *   <level>DEBUG</level>
 * </filter>
 *
 * This will filter all messages of type DEBUG of loggers with the prefix com.ibm.au.optim into this appender.
 *
 * @author Peter Ilfrich
 */
public class LevelLoggerFilter extends ThresholdLoggerFilter {


    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (!event.getLoggerName().startsWith(logger))
            return FilterReply.DENY;

        if (event.getLevel().equals(level)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
}
