/**
 * 
 */
package com.ibm.au.optim.suro.api.util.logging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * Class <b>RunEventEvaluator</b>. This class extends {@link EventEvaluatorBase} and provides
 * the capability of assessing whether a given logging event is related to a specific log 
 * message associated to a run. This is done by checking whether the event contains a marker
 * whose name complies with the regular expression defined by {@link RunEventEvaluator#RUN_MARKER_PATTERN}.
 * 
 * @author Christian Vecchiola
 *
 */
public class RunEventEvaluator extends EventEvaluatorBase<ILoggingEvent> {
	
	/**
	 * A {@link Pattern} that represents the regular expression used to identify all the logging
	 * events that are related to a specific run. This expression is used to match the name of 
	 * the marker (if any) attached to the logging event.
	 */
	public static final Pattern RUN_MARKER_PATTERN = Pattern.compile("^run-[a-f0-9]{32}$");

	/**
	 * Evaluates the given logging event to check whether it belongs to a specific run. The
	 * evaluator is only interested in capturing those messages that have a marker associated
	 * and whose name comply with the pattern {@link RunEventEvaluator#RUN_MARKER_PATTERN}.
	 * 
	 * @param event	an implementation of {@link ILoggingEvent} representing the logging message
	 * 				that is being evaluated.
	 * 
	 * @return 	{@literal true} if <i>event</i> has a non-null marker and its name complies with
	 * 			the given {@link RunEventEvaluator#RUN_MARKER_PATTERN}, {@literal false} otherwise.
	 * 
	 * @throws NullPointerException	if <i>event</i> is {@literal null}.
	 * @throws EvaluationException
	 */
	@Override
	public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
		
		Marker marker = event.getMarker();
				
		if (marker != null) {
			
			Matcher matcher = RunEventEvaluator.RUN_MARKER_PATTERN.matcher(marker.getName());
			return matcher.matches();
			
		}
		
		return false;
	}
	/**
	 * Extracts the unique identifier of the run instance that originated the log <i>event</i>. The
	 * method first checks whether <i>event</i> needs to be checked for compliance. In case it is
	 * required, this is done by invoking {@link RunEventEvaluator#evaluate(ILoggingEvent)}. The
	 * next step performed is then to extract the unique identifier of the run from the marker name
	 * if any.
	 * 
	 * @param event		an instance of {@link ILoggingEvent} representing the log event being processed.
	 * @param evaluate	a {@literal boolean} parameter representing the flag controlling the behaviour
	 * 					of the method. If {@literal true} <i>event</i> will be first checked for the
	 * 					presence of an appropriate marker before extracting the run identifier, if set
	 * 					to {@literal false} this check will be skipped.
	 * 
	 * @return	a {@literal String}	representing the unique identifier of the run instance if successful,
	 * 			or {@literal null} otherwise.
	 * 
	 * @throws NullPointerException	if <i>event</i> is {@literal null} or {@link ILoggingEvent#getMarker()}
	 * 								returns {@literal null} and <i>evaluate</i> is {@literal false}.
	 * @throws EvaluationException
	 */
	public String getRunId(ILoggingEvent event, boolean evaluate) throws NullPointerException, EvaluationException {
		
		if (evaluate == true) {
			
			boolean hasRun = this.evaluate(event);
			if (hasRun == false) {
				
				return null;
			}
		}
		
		String runMarker = event.getMarker().getName();
		return runMarker.substring(4);
		
	} 

}
