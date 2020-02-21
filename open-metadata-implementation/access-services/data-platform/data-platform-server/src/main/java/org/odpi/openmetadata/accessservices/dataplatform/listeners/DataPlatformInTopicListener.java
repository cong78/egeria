/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.odpi.openmetadata.accessservices.dataplatform.events.DataPlatformEventHeader;
import org.odpi.openmetadata.accessservices.dataplatform.ffdc.DataPlatformErrorCode;
import org.odpi.openmetadata.accessservices.dataplatform.processor.DataPlatformEventProcessor;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;
import org.odpi.openmetadata.repositoryservices.connectors.openmetadatatopic.OpenMetadataTopicListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Data Platform In Topic Listener is listening events from external data platforms about
 * metadata changes. It will handle different types of events defined in Data Platform OMAS API module.
 */
public class DataPlatformInTopicListener implements OpenMetadataTopicListener {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformInTopicListener.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final OMRSAuditLog auditLog;
    private DataPlatformEventProcessor dataPlatformEventProcessor;

    public DataPlatformInTopicListener(OMRSAuditLog auditLog, DataPlatformEventProcessor dataPlatformEventProcessor) {
        this.auditLog = auditLog;
        this.dataPlatformEventProcessor = dataPlatformEventProcessor;
    }

    /**
     * @param dataPlatformEvent contains all the information needed to create or update new assets from data platforms.
     */
    @Override
    public void processEvent(String dataPlatformEvent) {

        if (dataPlatformEvent == null) {
            log.debug("Null instance event - ignoring event");
        } else {

            try {
                DataPlatformEventHeader dataPlatformEventHeader = OBJECT_MAPPER.readValue(dataPlatformEvent, DataPlatformEventHeader.class);

                if ((dataPlatformEventHeader != null)) {
                    switch (dataPlatformEventHeader.getEventType()) {
                        case NEW_VIEW_EVENT:
                            dataPlatformEventProcessor.processInformationViewEvent(dataPlatformEvent);
                            break;
                        case NEW_DEPLOYED_DB_SCHEMA_EVENT:
                            dataPlatformEventProcessor.processDeployedDatabaseSchemaEvent(dataPlatformEvent);
                        default:
                            log.debug("Ignored instance event - unknown event type");
                            break;
                    }
                }
            } catch (Exception e) {
                log.debug("Exception processing event from Data Platform OMAS in topic", e);
                DataPlatformErrorCode errorCode = DataPlatformErrorCode.PROCESS_EVENT_EXCEPTION;
                auditLog.logException("processEvent",
                        errorCode.getErrorMessageId(),
                        OMRSAuditLogRecordSeverity.EXCEPTION,
                        errorCode.getFormattedErrorMessage(dataPlatformEvent, e.getMessage()),
                        e.getMessage(),
                        errorCode.getSystemAction(),
                        errorCode.getUserAction(),
                        e);
            }
        }
    }
}