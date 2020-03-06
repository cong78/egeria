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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class to handle periodically polling mechanism to capture changes on data platforms by passing it to Data Platform OMAS
 * Client side implementation.
 */
public class DataPlatformServicesPoller implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformServicesPoller.class);
    private OMRSAuditLog auditLog;

    private DataPlatformServicesConfig dataPlatformServicesConfig;
    private DataPlatformClient dataPlatformClient;
    private DataPlatformConnectorPollerBase connector;

    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Instantiates a new Data platform services poller.
     *
     * @param auditLog                   the audit log
     * @param dataPlatformServicesConfig the data platform services config
     * @param dataPlatformClient         the data platform client
     * @param connector                  the connector
     */
    public DataPlatformServicesPoller(OMRSAuditLog auditLog, DataPlatformServicesConfig dataPlatformServicesConfig, DataPlatformClient dataPlatformClient, DataPlatformConnectorPollerBase connector) {

        String methodName = "DataPlatformServicesChangeListener";

        this.auditLog = auditLog;
        this.dataPlatformServicesConfig = dataPlatformServicesConfig;
        this.dataPlatformClient = dataPlatformClient;
        this.connector = connector;

        DataPlatformServicesAuditCode auditCode;


    }

    /**
     * Start.
     */
    public void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    /**
     * Stop.
     */
    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
    }
}
