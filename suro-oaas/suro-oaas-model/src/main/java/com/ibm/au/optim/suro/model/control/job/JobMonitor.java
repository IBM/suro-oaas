package com.ibm.au.optim.suro.model.control.job;

import com.ibm.au.optim.suro.model.entities.Run;

/**
 * Main interface used to identify a job monitor. Implementing classes can also implement other interfaces (such as
 * JobCallBack (DOCloud))
 *
 * @author Peter Ilfrich
 */
public interface JobMonitor {

    /**
     * Returns the job executor which created the job monitor.
     * @return
     */
    JobExecutor getJobExecutor();

    /**
     * Returns the run associated with this monitor.
     * @return
     */
    Run getRun();


    /**
     * Completely stops the current job monitor (any thread observing streams of data or polling updates in an interval)
     */
    void cancel();
}
