/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.server;

import org.odpi.openmetadata.accessservices.dataplatform.ffdc.DataPlatformErrorCode;
import org.odpi.openmetadata.accessservices.dataplatform.handlers.DeployedDatabaseSchemaAssetHandler;
import org.odpi.openmetadata.accessservices.dataplatform.handlers.DataPlatformRegistrationHandler;
import org.odpi.openmetadata.accessservices.dataplatform.handlers.InformationViewAssetHandler;
import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceDescription;
import org.odpi.openmetadata.commonservices.multitenant.OCFOMASServiceInstance;
import org.odpi.openmetadata.commonservices.multitenant.ffdc.exceptions.NewInstanceException;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryConnector;

import java.util.List;

/**
 * DataPlatformServicesInstance caches references to objects for a specific server.
 * It is also responsible for registering itself in the instance map.
 */
public class DataPlatformServicesInstance extends OCFOMASServiceInstance {

    private static AccessServiceDescription myDescription = AccessServiceDescription.DATA_PLATFORM_OMAS;

    private DataPlatformRegistrationHandler dataPlatformRegistrationHandler;
    private DeployedDatabaseSchemaAssetHandler deployedDatabaseSchemaAssetHandler;
    private InformationViewAssetHandler informationViewAssetHandler;


    /**
     * Set up the handler for Data Platform OMAS server.
     *
     * @param repositoryConnector link to the repository responsible for servicing the REST calls.
     * @param supportedZones      list of zones that DiscoveryEngine is allowed to serve Assets from.
     * @param auditLog            logging destination
     * @throws NewInstanceException a problem occurred during initialization
     */
    public DataPlatformServicesInstance(OMRSRepositoryConnector repositoryConnector,
                                        List<String> supportedZones,
                                        List<String> defaultZones,
                                        OMRSAuditLog auditLog,
                                        String localServerUserId,
                                        int maxPageSize) throws NewInstanceException {

        super(myDescription.getAccessServiceFullName(),
                repositoryConnector,
                supportedZones,
                defaultZones,
                auditLog,
                localServerUserId,
                maxPageSize);

        final String methodName = "new ServiceInstance";

        if (repositoryHandler != null) {
            dataPlatformRegistrationHandler = new DataPlatformRegistrationHandler(
                    serverName,
                    repositoryHelper,
                    repositoryHandler,
                    invalidParameterHandler);
            deployedDatabaseSchemaAssetHandler = new DeployedDatabaseSchemaAssetHandler(
                    serviceName,
                    serverName,
                    repositoryHelper,
                    repositoryHandler,
                    invalidParameterHandler);
            informationViewAssetHandler = new InformationViewAssetHandler(
                    serverName,
                    repositoryHelper,
                    repositoryHandler,
                    invalidParameterHandler
            );
        } else {

            DataPlatformErrorCode errorCode = DataPlatformErrorCode.OMRS_NOT_INITIALIZED;
            String errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(methodName);

            throw new NewInstanceException(errorCode.getHTTPErrorCode(),
                    this.getClass().getName(),
                    methodName,
                    errorMessage,
                    errorCode.getSystemAction(),
                    errorCode.getUserAction());
        }
    }

    /**
     * Gets my description.
     *
     * @return the my description
     */
    public static AccessServiceDescription getMyDescription() {
        return myDescription;
    }

    /**
     * Gets registration handler.
     *
     * @return the registration handler
     */
    public DataPlatformRegistrationHandler getDataPlatformRegistrationHandler() {
        return dataPlatformRegistrationHandler;
    }


    public DeployedDatabaseSchemaAssetHandler getDeployedDatabaseSchemaAssetHandler() {
        return deployedDatabaseSchemaAssetHandler;
    }

    public InformationViewAssetHandler getInformationViewAssetHandler() {
        return informationViewAssetHandler;
    }
}
