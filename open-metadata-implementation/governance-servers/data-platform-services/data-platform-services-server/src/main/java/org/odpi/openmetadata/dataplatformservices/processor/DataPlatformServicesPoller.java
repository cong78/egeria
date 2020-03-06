/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.processor;

import org.odpi.openmetadata.accessservices.dataplatform.client.DataPlatformClient;
import org.odpi.openmetadata.adminservices.configuration.properties.DataPlatformServicesConfig;
import org.odpi.openmetadata.dataplatformservices.api.poller.DataPlatformConnectorPollerBase;
import org.odpi.openmetadata.dataplatformservices.auditlog.DataPlatformServicesAuditCode;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPlatformServicesPoller {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformServicesPoller.class);
    private OMRSAuditLog auditLog;

    private DataPlatformServicesConfig dataPlatformServicesConfig;
    private DataPlatformClient dataPlatformClient;
    private DataPlatformConnectorPollerBase connector;

    public DataPlatformServicesPoller(OMRSAuditLog auditLog, DataPlatformServicesConfig dataPlatformServicesConfig, DataPlatformClient dataPlatformClient, DataPlatformConnectorPollerBase connector) {

        String methodName = "DataPlatformServicesChangeListener";

        this.auditLog = auditLog;
        this.dataPlatformServicesConfig = dataPlatformServicesConfig;
        this.dataPlatformClient = dataPlatformClient;
        this.connector = connector;

        DataPlatformServicesAuditCode auditCode;


    }
}
