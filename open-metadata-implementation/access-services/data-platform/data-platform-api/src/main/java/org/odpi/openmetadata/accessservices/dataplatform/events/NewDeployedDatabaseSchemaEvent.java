/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.odpi.openmetadata.accessservices.dataplatform.properties.DataPlatform;
import org.odpi.openmetadata.accessservices.dataplatform.properties.DeployedDatabaseSchema;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;


/**
 * The deployed database schema event will create a new asset of DeployedDatabaseSchema from a data platform as an external source. .
 */
@JsonAutoDetect(getterVisibility = PUBLIC_ONLY, setterVisibility = PUBLIC_ONLY, fieldVisibility = NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "class")
public class NewDeployedDatabaseSchemaEvent extends DataPlatformEventHeader {

    private DeployedDatabaseSchema deployedDatabaseSchema;

    /**
     * Gets deployed database schema.
     *
     * @return the deployed database schema
     */
    public DeployedDatabaseSchema getDeployedDatabaseSchema() {
        return deployedDatabaseSchema;
    }

    /**
     * Sets deployed database schema.
     *
     * @param deployedDatabaseSchema the deployed database schema
     */
    public void setDeployedDatabaseSchema(DeployedDatabaseSchema deployedDatabaseSchema) {
        this.deployedDatabaseSchema = deployedDatabaseSchema;
    }

    @Override
    public String toString() {
        return "NewDeployedDatabaseSchemaEvent{" +
                "deployedDatabaseSchema=" + deployedDatabaseSchema +
                '}';
    }
}
