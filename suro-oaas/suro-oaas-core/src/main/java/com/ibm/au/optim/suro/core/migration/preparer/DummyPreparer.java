package com.ibm.au.optim.suro.core.migration.preparer;

import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * This preparer is used for testing and will not perform anything. It can be configured to return exceptions in each
 * step and also configure which result to return for the check and validate method.
 *
 * @author Peter Ilfrich
 */
public class DummyPreparer implements DatabasePreparer {


    /**
     * The environment key used to store the configuration for the check result
     */
    public static final String KEY_CHECK_RESULT = "dummypreparer:check:result";

    /**
     * The environment key used to store the boolean flag determining if an exception is thrown in the check method.
     */
    public static final String KEY_CHECK_EXCEPTION = "dummypreparer:check:exception";

    /**
     * The environment key used to store the boolean flag determining if an exception is thrown in the execute method.
     */
    public static final String KEY_EXECUTE_EXCEPTION = "dummypreparer:execute:exception";

    /**
     * The environment key used to store the configuration for the validation result
     */
    public static final String KEY_VALIDATE_RESULT = "dummypreparer:validate:result";

    /**
     * The environment key used to store the boolean flag determining if an exception is thrown in the validate method.
     */
    public static final String KEY_VALIDATE_EXCEPTION = "dummypreparer:validate:exception";

    /**
     * The environment key used to store an instance of this preparer in the environment.
     */
    public static final String DUMMYPREPARER_INSTANCE = "dummypreparer:instance";


    // flags determining if certain methods have been executed.
    private boolean checkExecuted = false;
    private boolean executeExecuted = false;
    private boolean validateExecuted = false;


    @Override
    public boolean check(Environment env) throws Exception {
        // store preparer in environment
        env.setAttribute(DUMMYPREPARER_INSTANCE, this);

        // set check method as executed
        this.checkExecuted = true;

        // check if we need to throw an exception
        if ((boolean) env.getAttribute(KEY_CHECK_EXCEPTION) == true) {
            throw new RuntimeException("Check Exception");
        }
        // returns the result of the check
        return (boolean) env.getAttribute(KEY_CHECK_RESULT);
    }

    @Override
    public void execute(Environment env) throws Exception {
        // set execute method as executed
        this.executeExecuted = true;

        if ((boolean) env.getAttribute(KEY_EXECUTE_EXCEPTION) == true) {
            throw new RuntimeException("Execute Exception");
        }
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        // set validate method as executed
        this.validateExecuted = true;

        if ((boolean) env.getAttribute(KEY_VALIDATE_EXCEPTION) == true) {
            throw new RuntimeException("Check Exception");
        }
        return (boolean) env.getAttribute(KEY_VALIDATE_RESULT);
    }

    /**
     * Getter for the check flag.
     * @return - true if the check method has been executed.
     */
    public boolean isCheckExecuted() {
        return checkExecuted;
    }

    /**
     * Getter for the execute flag.
     * @return - true if the execute method has been executed.
     */
    public boolean isExecuteExecuted() {
        return executeExecuted;
    }

    /**
     * Getter for the validate flag
     * @return - true if the validate method has been executed.
     */
    public boolean isValidateExecuted() {
        return validateExecuted;
    }
}
