/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.adapters.connectors.datastore.cassandra;

import org.odpi.openmetadata.adapters.connectors.datastore.cassandra.ffdc.CassandraConnectionException;
import org.odpi.openmetadata.adapters.connectors.datastore.cassandra.ffdc.CassandraDataStoreErrorCode;
import org.odpi.openmetadata.frameworks.connectors.ConnectorBase;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectionCheckedException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.properties.ConnectionProperties;
import org.odpi.openmetadata.frameworks.connectors.properties.EndpointProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Cassandra data store connector provides the OCF connection objects for Apache Cassandra Database.
 */
public class CassandraDataStoreConnector extends ConnectorBase {

    private static final Logger log = LoggerFactory.getLogger(CassandraDataStoreConnector.class);

    /* Qualified Name of the Cassandra Connection */
    private String cassandraConnectionName = null;

    /* Clear password set up */
    private String username = null;
    private String password = null;

    /* Server Connection Details */
    private String serverAddresses = null;
    private String port = null;

    /**
     * Typical Constructor: Connectors should always have a constructor requiring no parameters and perform
     * initialization in the initialize method.
     */
    public CassandraDataStoreConnector() {
        super();
    }

    /**
     * Call made by the ConnectorProvider to initialize the Connector with the base services.
     *
     * @param connectorInstanceId  unique id for the connector instance   useful for messages etc
     * @param connectionProperties POJO for the configuration used to create the connector.
     */
    @Override
    public void initialize(String connectorInstanceId, ConnectionProperties connectionProperties) {

        final String methodName = "initialize Cassandra Data Store Connector";

        super.initialize(connectorInstanceId, connectionProperties);

        EndpointProperties endpoint = connectionProperties.getEndpoint();

        try {
            if (endpoint.getAddress().isEmpty()) {
                this.throwException(CassandraDataStoreErrorCode.CASSANDRA_SERVER_NOT_SPECIFIED, methodName, null, null);
            } else {

                this.cassandraConnectionName = connectionProperties.getQualifiedName();
                this.serverAddresses = endpoint.getAddress();
                this.port = endpoint.getAdditionalProperties().getProperty("port");
                this.username = connectionProperties.getUserId();
                this.password = connectionProperties.getClearPassword();


            }
        } catch (CassandraConnectionException e) {
            log.debug("Unexpected exception " + e.getClass().getSimpleName() + " with message " + e.getMessage());
        }
    }

    @Override
    public void disconnect() throws ConnectorCheckedException {

        String actionDescription = "Shut down the Cassandra Data Store connection.";

        super.disconnect();

        if (log.isDebugEnabled()) {
            log.info(actionDescription);
        }
    }

    /**
     * Throw a standard exception based on the supplied error code.
     *
     * @param errorCode               error code describing the problem
     * @param methodName              calling method
     * @param cassandraConnectionName name of the Cassandra Database
     *                                CassandraDataStoreErrorCode exception that is generated
     */
    private void throwException(CassandraDataStoreErrorCode errorCode,
                                String methodName,
                                String cassandraConnectionName,
                                Throwable caughtException) throws CassandraConnectionException {
        String errorMessage;

        if (cassandraConnectionName == null) {
            errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(super.connectionBean.getQualifiedName());
        } else {
            errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(cassandraConnectionName,
                    super.connectionBean.getQualifiedName());
        }

        if (caughtException == null) {
            throw new CassandraConnectionException(errorCode.getHTTPErrorCode(),
                    this.getClass().getName(),
                    methodName,
                    errorMessage,
                    errorCode.getSystemAction(),
                    errorCode.getUserAction(),
                    cassandraConnectionName);
        } else {
            throw new CassandraConnectionException(errorCode.getHTTPErrorCode(),
                    this.getClass().getName(),
                    methodName,
                    errorMessage,
                    errorCode.getSystemAction(),
                    errorCode.getUserAction(),
                    caughtException,
                    cassandraConnectionName);
        }
    }

}
