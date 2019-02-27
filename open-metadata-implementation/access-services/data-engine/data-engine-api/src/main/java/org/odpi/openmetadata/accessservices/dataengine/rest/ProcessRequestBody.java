/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataengine.rest;

import java.util.List;
import java.util.Objects;

public class ProcessRequestBody extends AssetRequestBody {
    private String displayName;
    private String parentProcessGuid;
    private List<String> ports;

    public String getDisplayName() {
        return displayName;
    }

    public String getParentProcessGuid() {
        return parentProcessGuid;
    }

    public List<String> getPorts() {
        return ports;
    }

    /**
     * JSON-like toString
     *
     * @return string containing the property names and values
     */
    @Override
    public String toString() {
        return "ProcessRequestBody{" +
                ", displayName='" + displayName + '\'' +
                ", parentProcessId='" + parentProcessGuid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProcessRequestBody that = (ProcessRequestBody) o;
        return Objects.equals(displayName, that.displayName) &&
                Objects.equals(parentProcessGuid, that.parentProcessGuid) &&
                Objects.equals(ports, that.ports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), displayName, parentProcessGuid, ports);
    }
}

