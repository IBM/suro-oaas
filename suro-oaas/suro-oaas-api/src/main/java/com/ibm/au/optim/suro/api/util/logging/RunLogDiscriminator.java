package com.ibm.au.optim.suro.api.util.logging;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;

/**
 * Class <b>RunLogDiscriminator</b>. This discriminator extends {@link AbstractDiscriminator} and
 * it is used within a {@link ch.qos.logback.classic.sift.SiftingAppender} instance to dispatch 
 * logging events related to different runs to different appenders. It relies on the services of 
 * the {@link RunEventEvaluator} class to first assess whether the log message represents a log 
 * message associated to a run or not, and if so it extracts the run identifier as discriminating
 * value for the property {@link RunLogDiscriminator#getKey()}.
 * 
 * @author Christian Vecchiola
 *
 */
public class RunLogDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

	/**
	 * A {@link String} constant representing the name of the property that is
	 * used by the discriminator.
	 */
	public final static String KEY_RUN_ID = "runId";

	/**
	 * A {@link RunEventEvaluator} instance that is used to extract the information
	 * about the run that generated the {@link ILoggingEvent} instance representing 
	 * the log message being processed. 
	 */
	private static final RunEventEvaluator RUN_EVENT = new RunEventEvaluator();
	
	/**
	 * A {@literal boolean} flag used to instruct the discriminator on whether the
	 * marker needs to be checked before extracting the information about the run
	 * identifier (i.e. {@literal true}) or not (i.e. {@literal false}). The default
	 * value is set to {@literal false}.
	 */
	private boolean check = false;

	/**
	 * <p>
	 * Gets the actual value of the property used for sifting through the logging
	 * messages and dispatching them to the different appenders.
	 * </p>
	 * <p>
	 * The method uses {@link RunEventEvaluator} to extract the information about
	 * the run identifier. If the operation is unsuccessful the method will return
	 * {@literal null}.
	 * </p>
	 * 
	 * @param	event	an {@link ILoggingEvent} implementation that embeds the 
	 * 					information about the log message and its context.
	 * 
	 * @return 	the value of the identifier of the run that the log message refers to
	 * 			if any, otherwise {@literal null}.
	 */
	@Override
	public String getDiscriminatingValue(ILoggingEvent event) {

		String value = null;

		try {

			value = RunLogDiscriminator.RUN_EVENT.getRunId(event, this.check);

		} catch (Exception ex) {

			// [CV] NOTE: we would love to log this but we can't do that
			//			  since we could generate a loop.
		}
		
		return value;
	}
	/**
	 * Gets the name of the property that is being used for sifting through
	 * the log messages.
	 * 
	 * @return a {@link String} containing the value of {@link RunLogDiscriminator#KEY_RUN_ID}.
	 */
	@Override
	public String getKey() {

		return RunLogDiscriminator.KEY_RUN_ID;
	}
	/**
	 * Instructs the discriminator on whether the logging event needs to be
	 * first checked for the existence of the marker containing the information
	 * about the run that originated the message or not.
	 * 
	 * @param check	a {@literal boolean} value indicating the behaviour of the
	 * 				discrimnator. If {@literal true} the logging event will first
	 * 				be checked to see whether there is a marker associated to the
	 * 				message and that marker is recognised as conveying the information
	 * 				about the run. If {@literal false} the the value of the run will
	 * 				be automatically extracted. The default behaviour is {@literal false}.
	 */
	public void setCheckMarker(boolean check) {
		this.check = check;
	}
	
	/**
	 * Gets the configured behaviour for checking the logging event. This flag controls
	 * whether the discriminator should check first that the logging event as a marker
	 * associated and the name of the marker complies with the guidelines that identify
	 * a logging event associated to a run.
	 * 
	 * @return	if {@literal false} no check is performed and unique identifier of the 
	 * 			run is extracted by the marker name. If there is no marker the value
	 * 			returned will be null. if {@literal true} an check is performed via the
	 * 			{@link RunEventEvaluator#evaluate(ILoggingEvent)} and then the value of
	 * 			the run will be extracted. In case of errors {@literal null} is returned.
	 * 			The default value is {@literal false}.
	 * 
	 */
	public boolean getCheckMarker() {
		
		return this.check;
	}

}
