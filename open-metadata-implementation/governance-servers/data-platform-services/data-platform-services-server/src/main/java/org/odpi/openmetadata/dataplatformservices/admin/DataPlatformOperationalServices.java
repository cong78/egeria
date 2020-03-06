/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.admin;

import org.odpi.openmetadata.accessservices.dataplatform.client.DataPlatformClient;
import org.odpi.openmetadata.adminservices.configuration.properties.DataPlatformServicesConfig;
import org.odpi.openmetadata.adminservices.ffdc.exception.OMAGConfigurationErrorException;
import org.odpi.openmetadata.dataplatformservices.api.listener.DataPlatformConnectorListenerBase;
import org.odpi.openmetadata.dataplatformservices.api.poller.DataPlatformConnectorPoller;
import org.odpi.openmetadata.dataplatformservices.api.poller.DataPlatformConnectorPollerBase;
import org.odpi.openmetadata.dataplatformservices.auditlog.DataPlatformServicesAuditCode;
import org.odpi.openmetadata.dataplatformservices.processor.DataPlatformServicesListener;
import org.odpi.openmetadata.dataplatformservices.processor.DataPlatformServicesPoller;
import org.odpi.openmetadata.frameworks.connectors.ConnectorBroker;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.connectors.openmetadatatopic.OpenMetadataTopicConnector;
import org.odpi.openmetadata.repositoryservices.ffdc.OMRSErrorCode;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.OMRSConfigErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DataPlatformOperationalServices is responsible for initializing different Data Platform Metadata Extractor
 * Connectors. It is implemented as the bridge between Data Platforms and Egeria Data Platform OMAS by REST APIs from
 * Data Platform OMAS Client or Data Platform OMAS InTopic asynchronous events.
 */
public class DataPlatformOperationalServices {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformOperationalServices.class);
    private OMRSAuditLog auditLog;

    private String localServerName;
    private String localServerUserId;
    private String localServerPassword;

    private DataPlatformConnectorListenerBase dataPlatformConnectorListenerConnector;
    private DataPlatformConnectorPollerBase dataPlatformConnectorPollerConnector;

    private DataPlatformServicesListener dataPlatformServicesListener;
    private DataPlatformServicesPoller dataPlatformServicesPoller;

    /**
     * Constructor used at server startup.
     *
     * @param localServerName   the local server name
     * @param localServerUserId the local server user id
     */
    public DataPlatformOperationalServices(String localServerName, String localServerUserId, String localServerPassword) {
        this.localServerName = localServerName;
        this.localServerUserId = localServerUserId;
        this.localServerPassword = localServerPassword;
    }

    /**
     * Initialize the Data Platform Services with provided configuration
     *
     * @param dataPlatformServicesConfig the data platform config
     * @param auditLog                   the audit log
     * @throws OMAGConfigurationErrorException the omag configuration error exception
     */
    public void initialize(DataPlatformServicesConfig dataPlatformServicesConfig, OMRSAuditLog auditLog) throws OMAGConfigurationErrorException {

        final String methodName = "initialize";

        if (dataPlatformServicesConfig == null) {
            auditLog.logMessage(methodName, DataPlatformServicesAuditCode.NO_CONFIG_DOC.getMessageDefinition(localServerName));
        } else if (dataPlatformServicesConfig.getAccessServiceRootURL() == null || dataPlatformServicesConfig.getAccessServiceServerName() == null) {
            auditLog.logMessage(methodName, DataPlatformServicesAuditCode.NO_OMAS_CONFIG.getMessageDefinition(localServerName));
        } else {
            auditLog.logMessage(methodName, DataPlatformServicesAuditCode.SERVICE_INITIALIZING.getMessageDefinition(localServerName));

            /*
             * Configuring the Data Platform OMAS client
             */
            DataPlatformClient dataPlatformClient;
            try {
                dataPlatformClient = new DataPlatformClient(
                        dataPlatformServicesConfig.getAccessServiceServerName(),
                        dataPlatformServicesConfig.getAccessServiceRootURL(),
                        localServerUserId,
                        localServerPassword
                );
                if (log.isDebugEnabled()) {
                    log.debug("Configuring the Data Platform OMAS Client: {}", dataPlatformClient);
                }
            } catch (InvalidParameterException error) {
                throw new OMAGConfigurationErrorException(error.getReportedHTTPCode(),
                        this.getClass().getName(),
                        methodName,
                        error.getErrorMessage(),
                        error.getReportedSystemAction(),
                        error.getReportedUserAction(),
                        error);
            }

            /*
             * Configuring the Data Platform Connector to Listener or Poller
             */
            Connection dataPlatformConnection = dataPlatformServicesConfig.getDataPlatformConnection();

            if (dataPlatformConnection != null) {
                try {
                    ConnectorBroker connectorBroker = new ConnectorBroker();
                    if (dataPlatformServicesConfig.isListener() && !dataPlatformServicesConfig.isPoller()) {
                        dataPlatformConnectorListenerConnector = (DataPlatformConnectorListenerBase) connectorBroker.getConnector(dataPlatformConnection);
                        dataPlatformServicesListener = new DataPlatformServicesListener(auditLog,
                                dataPlatformServicesConfig,
                                dataPlatformClient,
                                dataPlatformConnectorListenerConnector);
                        dataPlatformServicesListener.run();
                    } else if (!dataPlatformServicesConfig.isListener() && dataPlatformServicesConfig.isPoller()) {
                        dataPlatformConnectorPollerConnector = (DataPlatformConnectorPollerBase) connectorBroker.getConnector(dataPlatformConnection);
                        dataPlatformServicesPoller = new DataPlatformServicesPoller(auditLog,
                                dataPlatformServicesConfig,
                                dataPlatformClient,
                                dataPlatformConnectorPollerConnector);
                        dataPlatformServicesPoller.run();
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Exception in creating the valid Data Platform Connector type to be listener or poller.");
                        }
                        auditLog.logMessage(methodName, DataPlatformServicesAuditCode.ERROR_INVALID_CONNECTOR_TYPE.getMessageDefinition());
                    }
                } catch (Exception e) {
                    log.error("Exception in creating the Data Platform Connector: ", e);
                    auditLog.logMessage(methodName, DataPlatformServicesAuditCode.ERROR_INITIALIZING_DATA_PLATFORM_CONNECTION.getMessageDefinition());
                }
            }

            auditLog.logMessage(methodName, DataPlatformServicesAuditCode.SERVICE_INITIALIZED.getMessageDefinition(localServerName));

        }

    }


    /**
     * Shutdown the Data Platform Services.
     *
     * @param permanent boolean flag indicating whether this server permanently shutting down or not
     * @return boolean indicated whether the disconnect was successful.
     */
    public boolean disconnect(boolean permanent) {

        String methodName = "disconnect";
        DataPlatformServicesAuditCode auditCode;

        try {
            // Disconnect the data platform connector
            dataPlatformConnectorListenerConnector.disconnect();
            dataPlatformConnectorListenerConnector.disconnect();

            auditLog.logMessage(methodName, DataPlatformServicesAuditCode.SERVICE_SHUTDOWN.getMessageDefinition(localServerName));

            return true;
        } catch (Exception e) {
            auditLog.logMessage(methodName, DataPlatformServicesAuditCode.ERROR_SHUTDOWN.getMessageDefinition(localServerName));
            return false;
        }
    }


    /**
     * Returns the connector created from topic connection properties
     *
     * @param topicConnection properties of the topic connection
     * @return the connector created based on the topic connection properties
     */
    private OpenMetadataTopicConnector getTopicConnector(Connection topicConnection, OMRSAuditLog auditLog) {
        try {
            ConnectorBroker connectorBroker = new ConnectorBroker();

            OpenMetadataTopicConnector topicConnector = (OpenMetadataTopicConnector) connectorBroker.getConnector(topicConnection);
            topicConnector.setAuditLog(auditLog);

            return topicConnector;
        } catch (Exception error) {
            String methodName = "getTopicConnector";

            OMRSErrorCode errorCode = OMRSErrorCode.NULL_TOPIC_CONNECTOR;
            String errorMessage = errorCode.getErrorMessageId()
                    + errorCode.getFormattedErrorMessage("getTopicConnector");

            throw new OMRSConfigErrorException(errorCode.getHTTPErrorCode(),
                    this.getClass().getName(),
                    methodName,
                    errorMessage,
                    errorCode.getSystemAction(),
                    errorCode.getUserAction(),
                    error);

        }
    }
}