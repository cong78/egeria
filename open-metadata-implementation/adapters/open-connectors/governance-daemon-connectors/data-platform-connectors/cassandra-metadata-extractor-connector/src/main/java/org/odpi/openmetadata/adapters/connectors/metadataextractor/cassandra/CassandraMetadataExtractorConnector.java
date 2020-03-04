/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.metadataextractor.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.metadata.schema.SchemaChangeListener;
import org.odpi.openmetadata.accessservices.dataplatform.properties.SoftwareServerCapability;
import org.odpi.openmetadata.adapters.connectors.metadataextractor.cassandra.auditlog.CassandraMetadataExtractorErrorCode;
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
    private CassandraMetadataExtractorErrorCode errorCode;
    private CqlSession cqlSession;
    private SoftwareServerCapability dataPlatform;

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

        EndpointProperties endpoint = connectionProperties.getEndpoint();

        try {
            if (endpoint.getAddress() == null || endpoint.getAddress().equals("")) {
                throwException(CassandraMetadataExtractorErrorCode.CONNECTION_FAILURE, actionDescription, endpoint.getAddress());
            } else {
                CassandraMetadataListener cassandraMetadataListener = new CassandraMetadataListener(
                        this.getDataPlatformClient(), connectionProperties.getUserId());
                startCassandraConnection(cassandraMetadataListener, endpoint.getAddress(), endpoint.getProtocol());
            }
        } catch (ConnectorCheckedException e) {
            if (log.isDebugEnabled()) {
                log.error("Exception on initializing Cassandra Metadata Extractor Connector: ", e);
            }
        }
    }


    private void startCassandraConnection(SchemaChangeListener schemaChangeListener,
                                          String serverAddresses,
                                          String port) throws ConnectorCheckedException {

        String actionDescription = "start Apache Cassandra Database Connection";

        try {
            CqlSessionBuilder builder = CqlSession.builder();
            builder.addContactPoint(new InetSocketAddress(serverAddresses, Integer.parseInt(port)));
            builder.withSchemaChangeListener(schemaChangeListener);
            this.cqlSession = builder.build();

        } catch (ExceptionInInitializerError e) {
            if (log.isDebugEnabled()) {
                throwException(CassandraMetadataExtractorErrorCode.CONNECTION_FAILURE, actionDescription, "");
                log.error("Error in connecting to Apache Cassandra: ", e);
            }
        }
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

    }

    private void throwException(CassandraMetadataExtractorErrorCode errorCode,
                                String methodName,
                                String... params) throws ConnectorCheckedException {
        String errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(params);
        throw new ConnectorCheckedException(
                errorCode.getHTTPErrorCode(),
                this.getClass().getName(),
                methodName,
                errorMessage,
                errorCode.getSystemAction(),
                errorCode.getUserAction());
    }
}
