/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.properties;

import org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection;

import java.io.Serializable;

/**
 * DataPlatform defines an OCF connection object with endpoint and connectors as well as the external source ID for a collection of data assets.
 */
public class DataPlatform extends Source implements Serializable {
    private String externalSourceGuid = null;
    private String externalSourceName = null;
    private Connection connection = null;

    public String getExternalSourceGuid() {
        return externalSourceGuid;
    }

    public void setExternalSourceGuid(String externalSourceGuid) {
        this.externalSourceGuid = externalSourceGuid;
    }

    public String getExternalSourceName() {
        return externalSourceName;
    }

    public void setExternalSourceName(String externalSourceName) {
        this.externalSourceName = externalSourceName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "DataPlatform{" +
                "externalSourceGuid='" + externalSourceGuid + '\'' +
                ", externalSourceName='" + externalSourceName + '\'' +
                ", connection=" + connection +
                ", additionalProperties=" + additionalProperties +
                ", qualifiedName='" + qualifiedName + '\'' +
                ", guid='" + guid + '\'' +
                '}';
    }
}
