/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.service;

import org.odpi.openmetadata.accessservices.dataplatform.beans.InformationViewAsset;
import org.odpi.openmetadata.accessservices.dataplatform.events.NewViewEvent;
import org.odpi.openmetadata.accessservices.dataplatform.properties.DeployedDatabaseSchema;
import org.odpi.openmetadata.accessservices.dataplatform.properties.SoftwareServerCapability;
import org.odpi.openmetadata.accessservices.dataplatform.server.DataPlatformInstanceHandler;
import org.odpi.openmetadata.commonservices.ffdc.RESTExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPlatformAssetService {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformAssetService.class);
    private static final String DEBUG_MESSAGE_METHOD = "Calling method: {}";
    private static final String DEBUG_MESSAGE_METHOD_RETURN = "Returning from method: {} with response: {}";
    private static final String DEBUG_METHOD_RETURN_VOID_RESPONSE = "Returning from method: {} with void response";

    private final RESTExceptionHandler restExceptionHandler = new RESTExceptionHandler();
    private final DataPlatformInstanceHandler instanceHandler = new DataPlatformInstanceHandler();


    public void createExternalDataPlatforms(SoftwareServerCapability softwareServerCapability
    ) {

    }

    public void createInformationViewAsset(NewViewEvent newViewEvent) {

    }

    public void createDeployedDatabaseSchemaAsset(DeployedDatabaseSchema deployedDatabaseSchema) {

    }


}
