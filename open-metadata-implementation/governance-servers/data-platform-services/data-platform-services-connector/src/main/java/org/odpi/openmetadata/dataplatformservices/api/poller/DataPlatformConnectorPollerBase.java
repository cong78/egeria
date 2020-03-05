/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.api.poller;

import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformDeployedDatabaseSchema;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformSoftwareServerCapability;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularColumn;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularSchema;
import org.odpi.openmetadata.frameworks.connectors.ConnectorBase;

import java.util.Date;

/**
 * Base implementation of a Data Platform Metadata Poller Connector, implementing all of the required methods
 * from Data Platform Connector Poller for actively listening metadata changes.
 * It is an abstract class as on its own it does absolutely nothing, and therefore a Data Platform
 * Connector needs to extend it and override at least one of the methods to actually do something.
 */
public class DataPlatformConnectorPollerBase extends ConnectorBase implements DataPlatformConnectorPoller {

    @Override
    public boolean requiresPolling() {
        return false;
    }

    @Override
    public Date getChangesLastSynced() {
        return null;
    }

    @Override
    public void setChangesLastSynced(Date time) {

    }

    @Override
    public DataPlatformSoftwareServerCapability getDataPlatformSoftwareServerCapability() {
        return null;
    }

    @Override
    public DataPlatformDeployedDatabaseSchema getDataPlatformDeployedDatabaseSchema() {
        return null;
    }

    @Override
    public DataPlatformTabularSchema getDataPlatformTabularSchema() {
        return null;
    }

    @Override
    public DataPlatformTabularColumn getDataPlatformTabularColumn() {
        return null;
    }
}
