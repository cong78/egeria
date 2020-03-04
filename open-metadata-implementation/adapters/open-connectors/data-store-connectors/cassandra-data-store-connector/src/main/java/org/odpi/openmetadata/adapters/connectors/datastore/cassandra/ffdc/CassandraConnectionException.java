package org.odpi.openmetadata.adapters.connectors.datastore.cassandra.ffdc;

import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;


public class CassandraConnectionException extends ConnectorCheckedException {

    private String cassandraDbName;

    /**
     * This is the typical constructor used for creating an exception.
     *
     * @param httpCode          http response code to use if this exception flows over a rest call
     * @param className         name of class reporting error
     * @param actionDescription description of function it was performing when error detected
     * @param errorMessage      description of error
     * @param systemAction      actions of the system as a result of the error
     * @param userAction        instructions for correcting the error
     * @param cassandraDbName   name of the Cassandra database
     */
    public CassandraConnectionException(int httpCode,
                                        String className,
                                        String actionDescription,
                                        String errorMessage,
                                        String systemAction,
                                        String userAction,
                                        String cassandraDbName) {
        super(httpCode, className, actionDescription, errorMessage, systemAction, userAction);

        this.cassandraDbName = cassandraDbName;
    }


    /**
     * This is the constructor used for creating an exception that resulted from a previous error.
     *
     * @param httpCode          http response code to use if this exception flows over a rest call
     * @param className         name of class reporting error
     * @param actionDescription description of function it was performing when error detected
     * @param errorMessage      description of error
     * @param systemAction      actions of the system as a result of the error
     * @param userAction        instructions for correcting the error
     * @param caughtError       the error that resulted in this exception.
     * @param cassandraDbName   name of the Cassandra database
     */
    public CassandraConnectionException(int httpCode,
                                        String className,
                                        String actionDescription,
                                        String errorMessage,
                                        String systemAction,
                                        String userAction,
                                        Throwable caughtError,
                                        String cassandraDbName) {
        super(httpCode, className, actionDescription, errorMessage, systemAction, userAction, caughtError);

        this.cassandraDbName = cassandraDbName;
    }

    public String getCassandraDbName() {
        return cassandraDbName;
    }

    public void setCassandraDbName(String cassandraDbName) {
        this.cassandraDbName = cassandraDbName;
    }

    @Override
    public String toString() {
        return "CassandraConnectionException{" +
                "cassandraDbName='" + cassandraDbName + '\'' +
                "} " + super.toString();
    }
}
