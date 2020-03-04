/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
/**
 * This is the interface for the generic operations on data cassandra clusters
 */
package org.odpi.openmetadata.adapters.connectors.metadataextractor.cassandra.auditlog;

import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;
import java.text.MessageFormat;

/**
 * The enum Cassandra connector audit code.
 */
public enum CassandraMetadataExtractorErrorCode {

    CASSANDRA_SERVER_NOT_SPECIFIED(400, "CASSANDRA-METADATA-EXTRACTOR-CONNECTOR-400-001 ",
            "The Cassandra database server address is null in the Connection object {0}",
            "The connector is unable to set up connection with Cassandra database because the server address is not passed in the Connection object.",
            "The Cassandra server address should be set up in the address property of the connection's Endpoint object."),
    CONNECTION_FAILURE(500, "CASSANDRA-METADATA-EXTRACTOR-CONNECTOR-500-001",
            "Unable to initialize Cassandra database connectivity to: {0}",
            "The system was unable to initialize connectivity to Cassandra database on the provided address.",
            "Check the inter-host network resolution, credentials and system logs to diagnose or report the problem."),
    ;

    private int httpErrorCode;
    private String errorMessageId;
    private String errorMessage;
    private String systemAction;
    private String userAction;

    /**
     * The constructor for LocalAtlasOMRSErrorCode expects to be passed one of the enumeration rows defined in
     * LocalAtlasOMRSErrorCode above.   For example:
     * <p>
     * LocalAtlasOMRSErrorCode   errorCode = LocalAtlasOMRSErrorCode.NULL_INSTANCE;
     * <p>
     * This will expand out to the 5 parameters shown below.
     *
     * @param newHTTPErrorCode  - error code to use over REST calls
     * @param newErrorMessageId - unique Id for the message
     * @param newErrorMessage   - text for the message
     * @param newSystemAction   - description of the action taken by the system when the error condition happened
     * @param newUserAction     - instructions for resolving the error
     */
    CassandraMetadataExtractorErrorCode(int newHTTPErrorCode, String newErrorMessageId, String newErrorMessage, String newSystemAction, String newUserAction) {
        this.httpErrorCode = newHTTPErrorCode;
        this.errorMessageId = newErrorMessageId;
        this.errorMessage = newErrorMessage;
        this.systemAction = newSystemAction;
        this.userAction = newUserAction;
    }


    /**
     * Return the numeric code that can be used in REST responses.
     *
     * @return int
     */
    public int getHTTPErrorCode() {
        return httpErrorCode;
    }


    /**
     * Returns the unique identifier for the error message.
     *
     * @return errorMessageId
     */
    public String getErrorMessageId() {
        return errorMessageId;
    }


    /**
     * Returns the error message with placeholders for specific details.
     *
     * @return errorMessage (unformatted)
     */
    public String getUnformattedErrorMessage() {
        return errorMessage;
    }


    /**
     * Returns the error message with the placeholders filled out with the supplied parameters.
     *
     * @param params - strings that plug into the placeholders in the errorMessage
     * @return errorMessage (formatted with supplied parameters)
     */
    public String getFormattedErrorMessage(String... params) {
        MessageFormat mf = new MessageFormat(errorMessage);
        return mf.format(params);
    }


    /**
     * Returns a description of the action taken by the system when the condition that caused this exception was
     * detected.
     *
     * @return systemAction
     */
    public String getSystemAction() {
        return systemAction;
    }


    /**
     * Returns instructions of how to resolve the issue reported in this exception.
     *
     * @return userAction
     */
    public String getUserAction() {
        return userAction;
    }

}
