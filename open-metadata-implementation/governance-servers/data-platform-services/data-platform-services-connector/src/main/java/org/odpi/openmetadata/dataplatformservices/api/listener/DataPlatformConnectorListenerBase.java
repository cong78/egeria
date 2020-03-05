/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.api.listener;

import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformDeployedDatabaseSchema;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformSoftwareServerCapability;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularColumn;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularSchema;
import org.odpi.openmetadata.frameworks.connectors.ConnectorBase;


/**
 * Base implementation of a Data Platform Metadata Connector Listener, implementing all of the required methods
 * from Data Platform Connector Listener for passively receiving metadata changes.
 * It is an abstract class as on its own it does absolutely nothing, and therefore a Data Platform
 * Connector needs to extend it and override at least one of the methods to actually do something.
 */
public class DataPlatformConnectorListenerBase extends ConnectorBase implements DataPlatformConnectorListener {

    /**
     * Instantiates a new Data platform metadata extractor base.
     */
    public DataPlatformConnectorListenerBase() {
        super();
    }

    public DataPlatformSoftwareServerCapability getDataPlatformSoftwareServerCapability() {
        return null;
    }

    public DataPlatformDeployedDatabaseSchema getDataPlatformDeployedDatabaseSchema() {
        return null;
    }

    public DataPlatformTabularSchema getDataPlatformTabularSchema() {
        return null;
    }

    public DataPlatformTabularColumn getDataPlatformTabularColumn() {
        return null;
    }

    @Override
    public void processEvent(String event) {

    }
}
