/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.api.poller;

import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformDeployedDatabaseSchema;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformSoftwareServerCapability;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularColumn;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularSchema;

import java.util.Date;

public interface DataPlatformConnectorPoller {

    /**
     * Indicates whether the data platform requires polling (true) or is capable of notifying of changes on its own
     * (false).
     *
     * @return boolean
     */
    boolean requiresPolling();

    /**
     * Retrieve the date and time at which changes were last synchronized.
     *
     * @return Date
     */
    Date getChangesLastSynced();

    /**
     * Persist the date and time at which changes were last successfully synchronized.
     *
     * @param time the time to record for the last synchronization
     */
    void setChangesLastSynced(Date time);


    /**
     * Gets data platform software server capability.
     *
     * @return the data platform software server capability
     */
    DataPlatformSoftwareServerCapability getDataPlatformSoftwareServerCapability();

    /**
     * Gets data platform deployed database schema.
     *
     * @return the data platform deployed database schema
     */
    DataPlatformDeployedDatabaseSchema getDataPlatformDeployedDatabaseSchema();

    /**
     * Gets data platform tabular schema.
     *
     * @return the data platform tabular schema
     */
    DataPlatformTabularSchema getDataPlatformTabularSchema();

    /**
     * Gets data platform tabular column.
     *
     * @return the data platform tabular column
     */
    DataPlatformTabularColumn getDataPlatformTabularColumn();
}
