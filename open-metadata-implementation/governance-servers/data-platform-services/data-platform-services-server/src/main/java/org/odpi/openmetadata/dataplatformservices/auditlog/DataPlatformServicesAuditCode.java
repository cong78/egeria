/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.auditlog;

import org.odpi.openmetadata.frameworks.auditlog.messagesets.AuditLogMessageDefinition;
import org.odpi.openmetadata.frameworks.auditlog.messagesets.AuditLogMessageSet;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;


public enum DataPlatformServicesAuditCode implements AuditLogMessageSet {


    SERVICE_INITIALIZING("DATA-PLATFORM-SERVICES-0001",
            OMRSAuditLogRecordSeverity.INFO,
            "The Data Platform Service is initializing a new server instance",
            "The local server has started up a new instance of Data Platform Service.",
            "No action is required.  This is part of the normal operation of the service."),
    SERVICE_INITIALIZED("DATA-PLATFORM-SERVICES-0002",
            OMRSAuditLogRecordSeverity.INFO,
            "The Data Platform Service has initialized a new instance for server {0}",
            "The local server has completed initialization of a new instance.",
            "No action is required.  This is part of the normal operation of the service."),
    SERVICE_SHUTDOWN("DATA-PLATFORM-SERVICES-0003",
            OMRSAuditLogRecordSeverity.INFO,
            "The Data Platform Service is shutting down its instance for server {0}",
            "The local server has requested shut down of a Data Platform Service instance.",
            "No action is required.  This is part of the normal operation of the service."),
    ERROR_INITIALIZING_DATA_PLATFORM_CONNECTION("DATA-PLATFORM-SERVICES-0004",
            OMRSAuditLogRecordSeverity.ERROR,
            "Unable to initialize the Data Platform connection",
            "The connection could not be initialized.",
            "Review the exception and resolve the configuration. "),
    DP_OMAS_IN_TOPIC_CONNECTION_INITIALIZED("DATA-PLATFORM-SERVICES-0005",
            OMRSAuditLogRecordSeverity.INFO,
            "The Data Platform Service has initialized an event bus connector for Data Platform In Topic",
            "The local server has completed initialization of a new event bus connector.",
            "No action is required.  This is part of the normal operation of the service."),
    ERROR_INITIALIZING_DP_OMAS_IN_TOPIC_CONNECTION("DATA-PLATFORM-SERVICES-0006",
            OMRSAuditLogRecordSeverity.ERROR,
            "Unable to initialize the Data Platform OMAS In Topic connection",
            "The connection could not be initialized.",
            "Review the exception and resolve the configuration. "),
    ERROR_SHUTDOWN("DATA-PLATFORM-SERVICES-0007",
            OMRSAuditLogRecordSeverity.ERROR,
            "The service is not shutdown properly.",
            "The connection could not be shutdown.",
            "Try again. "),
    PUBLISH_EVENT_EXCEPTION("DATA-PLATFORM-SERVICES-0008",
            OMRSAuditLogRecordSeverity.EXCEPTION,
            "Event {0} could not be published: {1}",
            "The system is unable to process the request.",
            "Verify the topic configuration."),
    ERROR_INVALID_CONNECTOR_TYPE("DATA-PLATFORM-SERVICES-0009",
            OMRSAuditLogRecordSeverity.ERROR,
            "Invalid Data Platform Service Connector type. It has not being define as Listener or Poller. ",
            "The Data Platform Service cannot initialize the connection from the provided configuration. ",
            "Verify the provided Data Platform Service configuration to make sure one of the connector type is set as true."),
    NO_CONFIG_DOC("DATA-PLATFORM-SERVICES-0010",
            OMRSAuditLogRecordSeverity.ERROR,
            "Data Platform Service {0} is not configured with a configuration document",
            "The server is not able to retrieve its configuration.  It fails to start.",
            "Add the configuration document for this Data Platform Service"),
    NO_OMAS_CONFIG("DATA-PLATFORM-SERVICES-0011",
            OMRSAuditLogRecordSeverity.ERROR,
            "Data Platform Service {0} is not configured with OMAS name or server URL parameters",
            "The server is not able to retrieve OMAS configuration details.  It fails to start.",
            "Add the OMAS name or URL configuration details for this Data Platform Service");


    private String logMessageId;
    private OMRSAuditLogRecordSeverity severity;
    private String logMessage;
    private String systemAction;
    private String userAction;


    /**
     * The constructor for OMRSAuditCode expects to be passed one of the enumeration rows defined in
     * OMRSAuditCode above.   For example:
     * <p>
     * OMRSAuditCode   auditCode = OMRSAuditCode.SERVER_NOT_AVAILABLE;
     * <p>
     * This will expand out to the 4 parameters shown below.
     *
     * @param messageId    - unique Id for the message
     * @param message      - text for the message
     * @param severity     - the severity of the message
     * @param systemAction - description of the action taken by the system when the condition happened
     * @param userAction   - instructions for resolving the situation, if any
     */
    DataPlatformServicesAuditCode(String messageId, OMRSAuditLogRecordSeverity severity,
                                  String message, String systemAction, String userAction) {
        this.logMessageId = messageId;
        this.severity = severity;
        this.logMessage = message;
        this.systemAction = systemAction;
        this.userAction = userAction;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLogMessageDefinition getMessageDefinition() {
        return new AuditLogMessageDefinition(logMessageId,
                severity,
                logMessage,
                systemAction,
                userAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLogMessageDefinition getMessageDefinition(String... params) {
        AuditLogMessageDefinition messageDefinition = new AuditLogMessageDefinition(logMessageId,
                severity,
                logMessage,
                systemAction,
                userAction);
        messageDefinition.setMessageParameters(params);
        return messageDefinition;
    }
}
