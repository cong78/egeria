/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.odpi.openmetadata.accessservices.dataplatform.beans.InformationViewAsset;
import org.odpi.openmetadata.accessservices.dataplatform.events.NewDeployedDatabaseSchemaEvent;
import org.odpi.openmetadata.accessservices.dataplatform.events.NewViewEvent;
import org.odpi.openmetadata.accessservices.dataplatform.ffdc.DataPlatformErrorCode;
import org.odpi.openmetadata.accessservices.dataplatform.handlers.DeployedDatabaseSchemaAssetHandler;
import org.odpi.openmetadata.accessservices.dataplatform.handlers.InformationViewAssetHandler;
import org.odpi.openmetadata.accessservices.dataplatform.server.DataPlatformInstanceHandler;
import org.odpi.openmetadata.accessservices.dataplatform.utils.Constants;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPlatformEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformEventProcessor.class);
    private static final String DEBUG_MESSAGE_METHOD = "Calling method: {}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final DataPlatformInstanceHandler dataPlatformInstanceHandler = new DataPlatformInstanceHandler();

    private final OMRSAuditLog auditLog;
    private final String serverName;


    public DataPlatformEventProcessor(OMRSAuditLog auditLog, String serverName) {
        this.auditLog = auditLog;
        this.serverName = serverName;
    }

    public void processInformationViewEvent(String dataPlatformEvent) {

        final String methodName = "processInformationViewEvent";

        log.debug(DEBUG_MESSAGE_METHOD, methodName);
        try {
            NewViewEvent newViewEvent = OBJECT_MAPPER.readValue(dataPlatformEvent, NewViewEvent.class);
            InformationViewAsset informationViewAsset = new InformationViewAsset();

            InformationViewAssetHandler handler = dataPlatformInstanceHandler.getInformationViewAssetHandler(Constants.USER_ID, serverName, methodName);
            handler.createInformationViewAsset(newViewEvent, informationViewAsset);


        } catch (JsonProcessingException | UserNotAuthorizedException | PropertyServerException | InvalidParameterException e) {
            logException(dataPlatformEvent, methodName, e);
        }

    }

    public void processDeployedDatabaseSchemaEvent(String dataPlatformEvent) {

        final String methodName = "processDeployedDatabaseSchemaEvent";

        log.debug(DEBUG_MESSAGE_METHOD, methodName);
        try {
            NewDeployedDatabaseSchemaEvent newDeployedDatabaseSchemaEvent = OBJECT_MAPPER.readValue(dataPlatformEvent, NewDeployedDatabaseSchemaEvent.class);

            DeployedDatabaseSchemaAssetHandler handler = dataPlatformInstanceHandler.getDeployedDatabaseSchemaAssetHandler(Constants.USER_ID, serverName, methodName);
            handler.createDeployedDatabaseSchemaAsset(newDeployedDatabaseSchemaEvent.getDeployedDatabaseSchema());

        } catch (JsonProcessingException | UserNotAuthorizedException | PropertyServerException | InvalidParameterException e) {
            logException(dataPlatformEvent, methodName, e);
        }

    }

    private void logException(String dataPlatformEvent, String methodName, Exception e) {
        log.debug("Exception in processing {} from in Data Platform In Topic: {}", methodName, e);

        DataPlatformErrorCode errorCode = DataPlatformErrorCode.PARSE_EVENT_EXCEPTION;
        auditLog.logException(methodName,
                errorCode.getErrorMessageId(),
                OMRSAuditLogRecordSeverity.EXCEPTION,
                errorCode.getFormattedErrorMessage(dataPlatformEvent, e.getMessage()), e.getMessage(), errorCode.getSystemAction(),
                errorCode.getUserAction(),
                e);
    }


}
