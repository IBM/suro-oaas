package com.ibm.au.optim.suro.core.migration;

import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.jaws.data.utils.ReflectionUtils;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This is the data migration service. On bind the data migration will be executed. Once the migration is completed,
 * the system will be put into "ready" state by updating the Core.
 *
 * The data migration requires all repositories, providers, controllers and the core to be initialized.
 *
 * @author Peter Ilfrich
 */
public class DataMigration extends AbstractSuroService {

    /**
     * The name of the environment attribute holding the data migration instance.
     */
    public static final String DATA_MIGRATION_INSTANCE = "migration:instance";

    /**
     * The source.properties key for the version list
     */
    public static final String CFG_VERSION_LIST = "versions";
    /**
     * The source.properties key for the migration preparer prefix (e.g. PREFIX.0.0.3=.. PREFIX.0.0.4=..)
     */
    public static final String CFG_PREFIX_MIGRATION = "migration.";


    /**
     * Version of data migration that is always executed.
     */
    private final String ALWAYS_VERSION = "0.0.0";

    private Environment environment;

    /**
     * The Logger for the data migration.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigration.class);

    @Override
    protected void doBind(Environment environment) {
        this.environment = environment;
        LOGGER.debug("Starting data migration");
        LOGGER.debug("=======================");

        try {
            String versionString = environment.getParameter(CFG_VERSION_LIST);

            // init the version list (needs to be sorted alphabetically)
            SortedSet<String> versionList = new TreeSet<>();

            if ("".equals(versionString.trim())) {
                throw new FileNotFoundException("'versions' property is set to empty string: ''");
            }

            // load the version list from the environment
            String[] versionSplit = versionString.split(" ");
            for (int i = 0; i < versionSplit.length; i++) {
                versionList.add(versionSplit[i]);
            }

            // determine the current version of the database
            Core core = (Core) environment.getAttribute(Core.CORE_INSTANCE);
            String currentVersion = core.getCurrentDatabaseVersion();
            String lastSuccessfulVersion = core.getCurrentDatabaseVersion();

            // check each version in the list
            for (String version : versionList) {
                // only if the current version is smaller or equal than the version to check
                if (version.compareTo(currentVersion) >= 0 || ALWAYS_VERSION.equals(version)) {
                    // load the list of preparers
                    String[] preparerList = getPreparerList(version);

                    for (String preparerClass : preparerList) {
                        // create the preparer
                        DatabasePreparer preparer = getPreparer(preparerClass);
                        processPreparer(preparer, version);
                    }
                }

                lastSuccessfulVersion = version;
            }

            core.setDatabaseVersion(lastSuccessfulVersion);
        } catch (Exception versionsListDoesntExist) {
            LOGGER.error("source.properties doesn't contain a list of versions.", versionsListDoesntExist);
        }

        LOGGER.debug("Data migration completed. Setting the Core to READY");
        LOGGER.debug("---------------------------------------------------");
        Core core = (Core) environment.getAttribute(Core.CORE_INSTANCE);
        core.setReady(true);
    }

    @Override
    protected void doRelease() {
        this.environment = null;
    }

    protected void processPreparer(DatabasePreparer preparer, String version) {
        String preparerClass = preparer.getClass().getName();

        boolean checkResult = false;
        try {
            LOGGER.debug("Checking if " + preparerClass + " needs to be executed.");
            checkResult = preparer.check(this.environment);
        } catch (Exception e) {
            LOGGER.error("Check threw exception: " + version + ": " + preparerClass, e);
        }

        if (checkResult) {
            try {
                // execute the preparer
                LOGGER.debug("Executing preparer " + preparerClass + " (" + version + ")");
                preparer.execute(this.environment);
            } catch (Exception e) {
                // handle exceptions thrown by execution
                LOGGER.error("Execution threw exception: " + version + ": " + preparerClass, e);
                return;
            }

            boolean validationResult = false;

            try {
                // validate the execution
                validationResult = preparer.validate(this.environment);
            } catch (Exception e) {
                // handle exceptions thrown by validation
                LOGGER.error("Validation threw exception: " + version + ": " + preparerClass, e);
            }

            if (!validationResult) {
                LOGGER.error("Validation failed:  " + version + ": " + preparerClass);
            }
        }
    }

    protected String[] getPreparerList(String version) {
        String preparerString = this.environment.getParameter(CFG_PREFIX_MIGRATION + version, null);
        if (preparerString == null) {
            LOGGER.error("source.properties specifies version " + version + ", but doesn't specify a preparer list (even an empty one)");
            return new String[] {};
        }


        if (preparerString.trim().equals("")) {
            LOGGER.debug("Version " + version + " has no preparers to execute.");
            return new String[] {};
        }
        // split up preparer class list
        return preparerString.trim().split(" ");
    }

    protected DatabasePreparer getPreparer(String preparerClass) {
        try {
            DatabasePreparer preparer = ReflectionUtils.createInstance(preparerClass);
            return preparer;
        } catch (Exception e) {
            LOGGER.error("Preparer " + preparerClass + " is not a valid database preparer (needs to implement " + DatabasePreparer.class.getName() + ")", e);
            // skip this entry, it's not a valid preparer
            return null;
        }
    }
}
