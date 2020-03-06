/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.processor;

import org.odpi.openmetadata.accessservices.dataplatform.client.DataPlatformClient;
import org.odpi.openmetadata.adminservices.configuration.properties.DataPlatformServicesConfig;
import org.odpi.openmetadata.dataplatformservices.api.listener.DataPlatformConnectorListenerBase;
import org.odpi.openmetadata.dataplatformservices.auditlog.DataPlatformServicesAuditCode;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to handle event-based mechanism to notify changes on data platforms by passing it to Data Platform OMAS
 * Client side implementation.
 */
public class DataPlatformServicesListener implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformServicesListener.class);
    private OMRSAuditLog auditLog;

    private DataPlatformServicesConfig dataPlatformServicesConfig;
    private DataPlatformClient dataPlatformClient;
    private DataPlatformConnectorListenerBase connector;

    public DataPlatformServicesListener(OMRSAuditLog auditLog, DataPlatformServicesConfig dataPlatformServicesConfig, DataPlatformClient dataPlatformClient, DataPlatformConnectorListenerBase connector) {

        String methodName = "DataPlatformServicesChangeListener";

        this.auditLog = auditLog;
        this.dataPlatformServicesConfig = dataPlatformServicesConfig;
        this.dataPlatformClient = dataPlatformClient;
        this.connector = connector;

        DataPlatformServicesAuditCode auditCode;

    }


    @Override
    public void run() {

    }
}
