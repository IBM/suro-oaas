package com.ibm.au.optim.suro.model.store;

import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * General interface for database preparers. A database preparers responsibility is to ensure that the database
 * structure and content is compatible with the application logic. Each major version of the application will have
 * various database preparers, which make sure the database content of a running system is properly migrated to the
 * desired target version.
 *
 * E.g. you could have a database with version 0.0.2 (related to 0.0.2 of the SURO core logic),
 * but the currently deployed version is 0.0.4. In this case the data migration would execute all preparers of 0.0.2
 * (current), 0.0.3 (interim) and 0.0.4 (target version) and at the end update the database version to 0.0.4. On the
 * next system startup it would only re-check/execute preparers for 0.0.4.
 *
 * @author Peter Ilfrich
 */
public interface DatabasePreparer {

    /**
     * The check method is executed for each applicable preparer on server startup. If all the data is already migrated
     * a preparer should return false. Only if the execute() method needs to be executed the result should be true.
     *
     * @param env - the current Environment
     * @return - true if the preparer needs to be executed
     * @throws Exception
     */
    public boolean check(Environment env) throws Exception;

    /**
     * Executes the data migration. This method is only executed if the check() returned true, which means the execution
     * is necessary. The execute method will perform the necessary actions to execute database operations in order to
     * migrate the data.
     *
     * @param env - the current Environment
     * @throws Exception
     */
    public void execute(Environment env) throws Exception;

    /**
     * This method is executed after the execute() method has finished. The purpose is to validate if the migration has
     * fixed the data into a state that is requested. In most cases this method will probably return the result of
     * !check() - which basically means the validation is successful, if check returns false (no migration necessary).
     *
     * @param env - the current Environment
     * @return - true if the validation is successful; false if the validation failed.
     * @throws Exception
     */
    public boolean validate(Environment env) throws Exception;
}
