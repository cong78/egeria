/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.dataplatformservices.api.listener;

import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformDeployedDatabaseSchema;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformSoftwareServerCapability;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularColumn;
import org.odpi.openmetadata.dataplatformservices.api.model.DataPlatformTabularSchema;

/**
 * The interface of Data Platform Metadata Extractor.
 */
public interface DataPlatformConnectorListener {

    /**
     * Method to pass an event received on topic.
     *
     * @param event inbound event
     */
    void processEvent(String event);

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
