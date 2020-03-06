/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adminservices;

import org.odpi.openmetadata.adminservices.configuration.properties.*;
import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceDescription;
import org.odpi.openmetadata.adminservices.configuration.registration.CommonServicesDescription;
import org.odpi.openmetadata.adminservices.configuration.registration.GovernanceServicesDescription;
import org.odpi.openmetadata.adminservices.ffdc.exception.OMAGInvalidParameterException;
import org.odpi.openmetadata.adminservices.ffdc.exception.OMAGNotAuthorizedException;
import org.odpi.openmetadata.commonservices.ffdc.RESTCallLogger;
import org.odpi.openmetadata.commonservices.ffdc.rest.VoidResponse;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OMAGServerDataPlatformService supports the configuration requests for Data Platform Services.
 */
public class OMAGServerDataPlatformService {

    private static final String serviceName = GovernanceServicesDescription.DATA_PLATFORM_SERVICES.getServiceName();
    private static final String accessService = AccessServiceDescription.DATA_PLATFORM_OMAS.getAccessServiceName();

    private static RESTCallLogger restCallLogger = new RESTCallLogger(LoggerFactory.getLogger(OMAGServerConfigDiscoveryEngineServices.class),
            CommonServicesDescription.ADMIN_OPERATIONAL_SERVICES.getServiceName());

    private OMAGServerAdminStoreServices configStore = new OMAGServerAdminStoreServices();
    private OMAGServerErrorHandler errorHandler = new OMAGServerErrorHandler();
    private OMAGServerExceptionHandler exceptionHandler = new OMAGServerExceptionHandler();


    /**
     * Sets data platform service configuration to OMGA server.
     *
     * @param userId                     the user id
     * @param serverName                 the server name
     * @param dataPlatformServicesConfig the data platform services config
     * @return the data platform service config
     */
    public VoidResponse setDataPlatformServiceConfig(String userId, String serverName, DataPlatformServicesConfig dataPlatformServicesConfig) {
        String methodName = "setDataPlatformServiceConfig";
        VoidResponse response = new VoidResponse();

        try {
            errorHandler.validateServerName(serverName, methodName);
            errorHandler.validateUserId(userId, serverName, methodName);

            OMAGServerConfig serverConfig = configStore.getServerConfig(userId, serverName, methodName);

            List<String> configAuditTrail = serverConfig.getAuditTrail();

            if (configAuditTrail == null) {
                configAuditTrail = new ArrayList<>();
            }

            if (dataPlatformServicesConfig == null) {
                configAuditTrail.add(new Date().toString() + " " + userId + " empty configuration for " + serviceName);
            } else {
                configAuditTrail.add(new Date().toString() + " " + userId + " new configuration for " + serviceName);
                serverConfig.setAuditTrail(configAuditTrail);
                serverConfig.setDataPlatformServicesConfig(dataPlatformServicesConfig);
                configStore.saveServerConfig(serverName, methodName, serverConfig);
            }

        } catch (OMAGInvalidParameterException error) {
            exceptionHandler.captureInvalidParameterException(response, error);
        } catch (OMAGNotAuthorizedException error) {
            exceptionHandler.captureNotAuthorizedException(response, error);
        } catch (Throwable error) {
            exceptionHandler.capturePlatformRuntimeException(serverName, methodName, response, error);
        }

        return response;
    }

    /**
     * Clear data platform service configuration from OMAG server.
     *
     * @param userId     the user id
     * @param serverName the server name
     * @return the void response
     */
    public VoidResponse clearDataPlatformService(String userId, String serverName) {

        final String methodName = "clearDataPlatformService";
        VoidResponse response = new VoidResponse();

        try {
            errorHandler.validateServerName(serverName, methodName);
            errorHandler.validateUserId(userId, serverName, methodName);

            OMAGServerConfig serverConfig = configStore.getServerConfig(userId, serverName, methodName);
            List<String> configAuditTrail = serverConfig.getAuditTrail();
            if (configAuditTrail == null) {
                configAuditTrail = new ArrayList<>();
            }
            configAuditTrail.add(new Date().toString() + " " + userId + " removed configuration for " + serviceName);
            serverConfig.setAuditTrail(configAuditTrail);
            serverConfig.setDataPlatformServicesConfig(null);
            configStore.saveServerConfig(serverName, methodName, serverConfig);

        } catch (OMAGInvalidParameterException error) {
            exceptionHandler.captureInvalidParameterException(response, error);
        } catch (OMAGNotAuthorizedException error) {
            exceptionHandler.captureNotAuthorizedException(response, error);
        } catch (Throwable error) {
            exceptionHandler.capturePlatformRuntimeException(serverName, methodName, response, error);
        }

        return response;
    }
}
