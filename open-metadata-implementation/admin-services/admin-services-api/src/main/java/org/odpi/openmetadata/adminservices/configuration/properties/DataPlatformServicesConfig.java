/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adminservices.configuration.properties;

import org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection;

import java.util.Map;


/**
 * The Data platform config class provides the configuration properties for the Data Platform Service.
 */
public class DataPlatformServicesConfig extends AdminServicesConfigHeader {
    private static final long serialVersionUID = 1L;

    /* Properties needed to call the access service REST APIs */
    private String dataPlatformServerURL;
    private String dataPlatformServerName;

    private Connection dataPlatformConnection;

    private boolean isListener;
    private boolean isPoller;

    /* Properties for a data platform  */
    private Map<String, Object> dataPlatformConfig;

    /**
     * Default Constructor
     */
    public DataPlatformServicesConfig() {
        super();
    }

    /**
     * Instantiates a new Data platform services config.
     *
     * @param dataPlatformServerURL  the data platform server url
     * @param dataPlatformServerName the data platform server name
     * @param dataPlatformConnection the data platform connection
     * @param isListener             the is listener
     * @param isPoller               the is poller
     * @param dataPlatformConfig     the data platform config
     */
    public DataPlatformServicesConfig(String dataPlatformServerURL, String dataPlatformServerName, Connection dataPlatformConnection, boolean isListener, boolean isPoller, Map<String, Object> dataPlatformConfig) {
        this.dataPlatformServerURL = dataPlatformServerURL;
        this.dataPlatformServerName = dataPlatformServerName;
        this.dataPlatformConnection = dataPlatformConnection;
        this.isListener = isListener;
        this.isPoller = isPoller;
        this.dataPlatformConfig = dataPlatformConfig;
    }

    /**
     * Instantiates a new Data platform services config.
     *
     * @param template               the template
     * @param dataPlatformServerURL  the data platform server url
     * @param dataPlatformServerName the data platform server name
     * @param dataPlatformConnection the data platform connection
     * @param isListener             the is listener
     * @param isPoller               the is poller
     * @param dataPlatformConfig     the data platform config
     */
    public DataPlatformServicesConfig(AdminServicesConfigHeader template, String dataPlatformServerURL, String dataPlatformServerName, Connection dataPlatformConnection, boolean isListener, boolean isPoller, Map<String, Object> dataPlatformConfig) {
        super(template);
        this.dataPlatformServerURL = dataPlatformServerURL;
        this.dataPlatformServerName = dataPlatformServerName;
        this.dataPlatformConnection = dataPlatformConnection;
        this.isListener = isListener;
        this.isPoller = isPoller;
        this.dataPlatformConfig = dataPlatformConfig;
    }

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Gets data platform server url.
     *
     * @return the data platform server url
     */
    public String getDataPlatformServerURL() {
        return dataPlatformServerURL;
    }

    /**
     * Sets data platform server url.
     *
     * @param dataPlatformServerURL the data platform server url
     */
    public void setDataPlatformServerURL(String dataPlatformServerURL) {
        this.dataPlatformServerURL = dataPlatformServerURL;
    }

    /**
     * Gets data platform server name.
     *
     * @return the data platform server name
     */
    public String getDataPlatformServerName() {
        return dataPlatformServerName;
    }

    /**
     * Sets data platform server name.
     *
     * @param dataPlatformServerName the data platform server name
     */
    public void setDataPlatformServerName(String dataPlatformServerName) {
        this.dataPlatformServerName = dataPlatformServerName;
    }

    /**
     * Gets data platform connection.
     *
     * @return the data platform connection
     */
    public Connection getDataPlatformConnection() {
        return dataPlatformConnection;
    }

    /**
     * Sets data platform connection.
     *
     * @param dataPlatformConnection the data platform connection
     */
    public void setDataPlatformConnection(Connection dataPlatformConnection) {
        this.dataPlatformConnection = dataPlatformConnection;
    }

    /**
     * Is listener boolean.
     *
     * @return the boolean
     */
    public boolean isListener() {
        return isListener;
    }

    /**
     * Sets listener.
     *
     * @param listener the listener
     */
    public void setListener(boolean listener) {
        isListener = listener;
    }

    /**
     * Is poller boolean.
     *
     * @return the boolean
     */
    public boolean isPoller() {
        return isPoller;
    }

    /**
     * Sets poller.
     *
     * @param poller the poller
     */
    public void setPoller(boolean poller) {
        isPoller = poller;
    }

    /**
     * Gets data platform config.
     *
     * @return the data platform config
     */
    public Map<String, Object> getDataPlatformConfig() {
        return dataPlatformConfig;
    }

    /**
     * Sets data platform config.
     *
     * @param dataPlatformConfig the data platform config
     */
    public void setDataPlatformConfig(Map<String, Object> dataPlatformConfig) {
        this.dataPlatformConfig = dataPlatformConfig;
    }

    /**
     * Convert all properties to a string.
     *
     * @return string description of object
     */
    @Override
    public String toString() {
        return "DataPlatformServicesConfig{" +
                "dataPlatformServerURL='" + dataPlatformServerURL + '\'' +
                ", dataPlatformServerName='" + dataPlatformServerName + '\'' +
                ", dataPlatformConnection=" + dataPlatformConnection +
                ", isListener=" + isListener +
                ", isPoller=" + isPoller +
                ", dataPlatformConfig=" + dataPlatformConfig +
                "} " + super.toString();
    }
}