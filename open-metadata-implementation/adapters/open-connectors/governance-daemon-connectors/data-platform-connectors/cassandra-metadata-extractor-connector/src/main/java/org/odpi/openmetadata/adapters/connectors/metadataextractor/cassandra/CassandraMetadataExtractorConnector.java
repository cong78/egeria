/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.metadataextractor.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.metadata.schema.SchemaChangeListener;
import org.odpi.openmetadata.adapters.connectors.metadataextractor.cassandra.auditlog.CassandraMetadataExtractorAuditCode;
import org.odpi.openmetadata.dataplatformservices.api.DataPlatformMetadataExtractorBase;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.properties.ConnectionProperties;
import org.odpi.openmetadata.frameworks.connectors.properties.EndpointProperties;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * The Cassandra Metadata Extractor Connector is the connector for synchronizing data assets from Apache Cassandra Database.
 */
public abstract class CassandraMetadataExtractorConnector extends DataPlatformMetadataExtractorBase {

    private static final Logger log = LoggerFactory.getLogger(CassandraMetadataExtractorConnector.class);
    private OMRSAuditLog omrsAuditLog;
    private CassandraMetadataExtractorAuditCode auditLog;
    private CqlSession cqlSession;
    /**
     * Initialize the connector.
     *
     * @param connectorInstanceId  - unique id for the connector instance - useful for messages etc
     * @param connectionProperties - POJO for the configuration used to create the connector.
     */
    @Override
    public void initialize(String connectorInstanceId, ConnectionProperties connectionProperties) {

        final String actionDescription = "initialize Cassandra Metadata Extractor Connector";

        super.initialize(connectorInstanceId, connectionProperties);

        this.connectorInstanceId = connectorInstanceId;
        this.connectionProperties = connectionProperties;

        EndpointProperties endpoint = connectionProperties.getEndpoint();

        if (omrsAuditLog != null & endpoint!= null) {

            auditLog = CassandraMetadataExtractorAuditCode.CONNECTOR_INITIALIZING;
            omrsAuditLog.logRecord(
                    actionDescription,
                    auditLog.getLogMessageId(),
                    auditLog.getSeverity(),
                    auditLog.getFormattedLogMessage(),
                    null,
                    auditLog.getSystemAction(),
                    auditLog.getUserAction());

            CassandraMetadataListener cassandraMetadataListener = new CassandraMetadataListener(
                    this.getDataPlatformClient(), connectionProperties.getUserId());

            startCassandraConnection(cassandraMetadataListener, endpoint.getAddress(), endpoint.getProtocol());

        } else {
            log.debug("Errors in the Cassandra server configuration. The address of the server {} cannot be extracted.", endpoint);
            if (omrsAuditLog != null) {
                auditLog = CassandraMetadataExtractorAuditCode.CONNECTOR_SERVER_CONFIGURATION_ERROR;
                omrsAuditLog.logRecord(actionDescription,
                        auditLog.getLogMessageId(),
                        auditLog.getSeverity(),
                        auditLog.getFormattedLogMessage(),
                        null,
                        auditLog.getSystemAction(),
                        auditLog.getUserAction());
            }
        }

        if (omrsAuditLog != null) {
            auditLog = CassandraMetadataExtractorAuditCode.CONNECTOR_INITIALIZED;
            omrsAuditLog.logRecord(actionDescription,
                    auditLog.getLogMessageId(),
                    auditLog.getSeverity(),
                    auditLog.getFormattedLogMessage(),
                    null,
                    auditLog.getSystemAction(),
                    auditLog.getUserAction());
        }
    }


    private void startCassandraConnection(SchemaChangeListener schemaChangeListener,
                                          String serverAddresses,
                                          String port) {

        String actionDescription = "start Apache Cassandra Database Connection";

        try {
            CqlSessionBuilder builder = CqlSession.builder();
            builder.addContactPoint(new InetSocketAddress(serverAddresses, Integer.parseInt(port)));
            builder.withSchemaChangeListener(schemaChangeListener);
            this.cqlSession = builder.build();

        } catch (ExceptionInInitializerError e) {

            log.error("Error in connecting to Apache Cassandra: ", e);

            auditLog = CassandraMetadataExtractorAuditCode.CONNECTOR_SERVER_CONNECTION_ERROR;
            omrsAuditLog.logRecord(actionDescription,
                    auditLog.getLogMessageId(),
                    auditLog.getSeverity(),
                    auditLog.getFormattedLogMessage(),
                    null,
                    auditLog.getSystemAction(),
                    auditLog.getUserAction());
        }
        auditLog = CassandraMetadataExtractorAuditCode.CONNECTOR_INITIALIZED;
        omrsAuditLog.logRecord(actionDescription,
                auditLog.getLogMessageId(),
                auditLog.getSeverity(),
                auditLog.getFormattedLogMessage(),
                null,
                auditLog.getSystemAction(),
                auditLog.getUserAction());

    }


    /**
     * Provide Cassandra Session.
     *
     * @return Cassandra session.
     */
    public CqlSession getSession() {
        return this.cqlSession;
    }

    /**
     * Free up any resources held since the connector is no longer needed.
     *
     * @throws ConnectorCheckedException there is a problem within the connector.
     */
    @Override
    public void disconnect() throws ConnectorCheckedException {
        super.disconnect();

        String actionDescription = "Shut down the Cassandra connection.";
        this.cqlSession.close();

        auditLog = CassandraMetadataExtractorAuditCode.CONNECTOR_SHUTDOWN;
        omrsAuditLog.logRecord(actionDescription,
                auditLog.getLogMessageId(),
                auditLog.getSeverity(),
                auditLog.getFormattedLogMessage(),
                null,
                auditLog.getSystemAction(),
                auditLog.getUserAction());
    }

}
