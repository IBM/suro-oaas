package com.ibm.au.optim.suro.docloud;

import com.ibm.au.optim.suro.docloud.util.LogFormatter;

import com.ibm.au.optim.suro.core.results.BasicResultManager;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.control.ResultManager;
import com.ibm.au.optim.suro.model.control.RunController;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.control.job.JobExecutor;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.optim.oaas.client.OperationException;
import com.ibm.optim.oaas.client.http.HttpClientFactory;
import com.ibm.optim.oaas.client.job.JobClient;
import com.ibm.optim.oaas.client.job.JobClientFactory;
import com.ibm.optim.oaas.client.job.JobNotFoundException;
import com.ibm.optim.oaas.client.job.model.Job;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

/**
 * Job Controller for DOCloud. It will hold a list of executors, which execute requests against DOCloud and monitor
 * their progress.
 *
 * @author Peter Ilfrich
 */
public class DOCloudJobController extends AbstractSuroService implements JobController {

    public static final String CFG_DOCLOUD_API_URL = "oaas.api.url";
    public static final String CFG_DOCLOUD_API_TOKEN = "oaas.authentication.token";
    public static final String CFG_DOCLOUD_MAX_CONCURRENT = "oaas.max.concurrent";
    
    /**./
     * A {@link String} instance representing the value of the path to the log file
     * that is used to store the log messages produced by the DOCloud libraries.
     */
    private static final String CFG_DOCLOUD_LOG_PATH = "logs/docloud-api.log";
    /**
     * A {@link String} instance representing the value of the package that defines the
     * classes that we're interested in capturing the log messages produced.
     */
    private static final String CFG_DOCLOUD_PACKAGE = "com.ibm.optim.oaas";


    /**
     * Default value for the maximum number of concurrent job executors
     */
    public static final int DEFAULT_DOCLOUD_MAX_CONCURRENT = 3;

    /**
     * Check every 2 seconds if all executors are busy
     */
    public static final int DEFAULT_DOCLOUD_EXECUTOR_CHECKINTERVAL = 5000;


    /**
     * The DOC job client used to submit the job request
     */
    private JobClient jobclient = null;
    /**
     * The current environment (used to load controllers and repositories)
     */
    private Environment environment = null;

    /**
     * The core of the system
     */
    private Core core = null;

    /**
     * Stores a result manager instance, which can transform and store results of optimization runs.
     */
    private ResultManager resultManager = null;

    /**
     * The list of DOCloudJobExecutors which process runs / jobs. Each executor can only process one job/run at a time.
     */
    private ArrayList<DOCloudJobExecutor> executors = null;

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DOCloudJobController.class);


    @Override
    public void completeJob(String runId) {
        JobExecutor exec = getJobExecutor(runId);
        if (exec != null) {
            LOGGER.debug("Processing run " + runId + " - Finished job " + core.getRunController().getRun(runId).getJobId());
            exec.finish();
        }
    }

    @Override
    public JobExecutor getJobExecutor(String runId) {
        JobExecutor exec = null;
        for (JobExecutor je : executors) {
            if (je.getCurrentRunId() != null && je.getCurrentRunId().equals(runId)) {
                exec = je;
            }
        }

        if (exec != null) {
            LOGGER.debug("Found job executor for run " + runId);
        } else {
            LOGGER.debug("Couldn't find job executor for run " + runId);
        }

        return exec;
    }

    @Override
    public boolean abortJob(String runId) {
        LOGGER.debug("Processing run " + runId + " - Abort run");
        // find the executor who holds the current job
        JobExecutor exec = getJobExecutor(runId);

        if (exec != null) {
            return exec.abort();
        } else {
            LOGGER.debug("The requested run holds a job ID that cannot be associated with any executor.");
            return true;
        }
    }

    @Override
    public void cleanupJobs() {
        try {
            // retrieve list of active jobs
            LOGGER.debug("Retrieving current job list.");
            List<? extends Job> jobList = jobclient.getAllJobs();
            LOGGER.debug("Cleaning up jobs in DOCloud. Found " + jobList.size() + " jobs.");
            if (jobList.size() > 0) {
                RunController controller = (RunController) this.environment.getAttribute(RunController.RUN_CONTROLLER_INSTANCE);

                // check each job in the job list for corresponding run object
                for (Job job : jobList) {
                    // retrieve run by job ID
                    Run run = controller.getRunByJobId(job.getId());
                    if (run == null) {
                        LOGGER.debug("There exists no run for job " + job.getId() + ". Attempting to abort and delete the job.");
                        try {
                            jobclient.abortJob(job.getId());
                        } catch (JobNotFoundException noJob) {
                            LOGGER.error("Trying to abort a job that doesn't seem to exist (might be a timing issue)", noJob);
                        }
                        if (!jobclient.deleteJob(job.getId())) {
                            LOGGER.error("Deleting job [" + job.getId() + "] did not succeed. Job might already be deleted");
                        }
                    }
                }

                LOGGER.debug("Job cleanup completed");
            }
        } catch (OperationException oe) {
            LOGGER.error("Operation error during job cleanup", oe);
        }
    }


    @Override
    public void createJobExecutor(int index) {
        DOCloudJobExecutor exec = new DOCloudJobExecutor(index, environment, this, DEFAULT_DOCLOUD_EXECUTOR_CHECKINTERVAL);
        Thread t = new Thread(exec);
        t.start();
        executors.add(exec);
    }


    @Override
    public List<JobExecutor> getExecutors() {
        List<JobExecutor> execs = new ArrayList<>();
        if (this.executors == null) {
            return null;
        }

        for (JobExecutor ex : this.executors) {
            execs.add(ex);
        }
        return execs;
    }

    @Override
    public void deleteJob(String jobId) {
        if (jobId == null) {
            LOGGER.error("Tried to delete job without providing a job ID.");
            return;
        }

        try {
            getJobClient().deleteJob(jobId);
        } catch (OperationException oe) {
            LOGGER.error("Error deleting job " + jobId + " in DOCloud.", oe);
        } catch (NullPointerException npe) {
            LOGGER.error("Error deleting job " + jobId + ". The job doesn't seem to exist in DOCloud", npe);
        }
    }

    @Override
    public ResultManager getResultManager() {
        return resultManager;
    }

    /*
     * ADDITIONAL METHODS WHICH ARE DOCLOUD SPECIFIC
     */

    /**
     * Returns the job client instance handling the DOCloud communication and holding the configured account key.
     * @return
     */
    public JobClient getJobClient() {
        return jobclient;
    }

    /**
     * This method initialises and redirects the logging infrastructure, internally used by DOCloud libraries, to utilise
     * a file that is located in the {@link DOCloudJobController#CFG_DOCLOUD_LOG_PATH} so that we can keep aside all the
     * logs related to the interaction with the remote service. The internal libraries use {@link java.util.logging.Logger}
     * and not the {@link org.slf4j.Logger}.
     */
    private void configureLogging() {

        try {
            java.util.logging.Logger doCloudLogger = java.util.logging.Logger.getLogger(DOCloudJobController.CFG_DOCLOUD_PACKAGE);
            File f = new File(DOCloudJobController.CFG_DOCLOUD_LOG_PATH);
            if (!f.getParentFile().exists() || !f.getParentFile().isDirectory()) {
                f.getParentFile().mkdirs();
            }
            Handler doCloudFileHandler = new FileHandler(DOCloudJobController.CFG_DOCLOUD_LOG_PATH);
            doCloudFileHandler.setFormatter(new LogFormatter());
            doCloudLogger.addHandler(doCloudFileHandler);
        } catch (IOException ioe) {
            LOGGER.error("Error initialising logging.", ioe);
        }
    }


    /*
     * RUNTIME SERVICE METHODS
     */


    @Override
    protected void doBind(Environment environment) {
    	
        this.configureLogging();
    	
        // store environment
        this.environment = environment;
        this.core = (Core) environment.getAttribute(Core.CORE_INSTANCE);
        this.resultManager = new BasicResultManager(environment);
        this.jobclient = JobClientFactory.createDefault(environment.getParameter(CFG_DOCLOUD_API_URL), environment.getParameter(CFG_DOCLOUD_API_TOKEN));

        // cleanup jobs in DOCloud (sync), requires the job client to be initialised.
        cleanupJobs();

        // create the job executor instances
        executors = new ArrayList<>();
        int maxConcurrent = DEFAULT_DOCLOUD_MAX_CONCURRENT;
        try {
            maxConcurrent = Integer.parseInt(environment.getParameter(CFG_DOCLOUD_MAX_CONCURRENT));
        } catch (NumberFormatException nfe) {
            // keep the default and log warning
            LOGGER.warn("Configuration for " + CFG_DOCLOUD_MAX_CONCURRENT + " could not be read. Falling back to default value: " + maxConcurrent);
        }
        for (int i = 1; i <= maxConcurrent; i++) {
            createJobExecutor(i);
        }

    }


    @Override
    protected void doRelease() {

        for (DOCloudJobExecutor exec : this.executors) {
            exec.quit();
        }

        this.resultManager = null;
        this.jobclient = null;
        this.environment = null;
        this.executors = null;
    }
}
