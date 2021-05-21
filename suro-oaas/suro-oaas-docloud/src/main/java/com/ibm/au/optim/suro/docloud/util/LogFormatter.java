package com.ibm.au.optim.suro.docloud.util;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;

/**
 * Class <b>LogFormatter</b>. This class extends the {@link Formatter} class
 * to specialise the way in which {@link LogRecord} instances are formatted
 * into a string.
 * 
 * @author Peter Ilfrich
 */
public class LogFormatter extends Formatter {

	/**
	 * Formats the {@link LogRecord} instance that is passed as argument and
	 * writes the formatted message into a {@link String}.
	 * 
	 * @param record	a {@link LogRecord} instance representing the log
	 * 					information collected by the logging framework.
	 * 
	 * @return	a {@link String} representing the formatted message composed
	 * 			out of <i>record</i>.
	 */
    @Override
    public String format(LogRecord record) {
    	
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        return sdf.format(new Date(record.getMillis())) + " [" + record.getLevel() + "] - " + record.getMessage() + "\n";
    }


}
