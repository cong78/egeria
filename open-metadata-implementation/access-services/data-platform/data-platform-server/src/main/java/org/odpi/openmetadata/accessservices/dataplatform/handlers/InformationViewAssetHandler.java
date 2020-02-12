/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.handlers;

import org.odpi.openmetadata.accessservices.dataplatform.events.NewViewEvent;
import org.odpi.openmetadata.accessservices.dataplatform.service.DataPlatformAssetService;
import org.odpi.openmetadata.accessservices.dataplatform.utils.Constants;
import org.odpi.openmetadata.accessservices.dataplatform.utils.EntityPropertiesBuilder;
import org.odpi.openmetadata.accessservices.dataplatform.utils.QualifiedNameUtils;
import org.odpi.openmetadata.accessservices.dataplatform.beans.InformationViewAsset;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

import static org.odpi.openmetadata.accessservices.dataplatform.mappers.InformationViewAssetMapper.*;

/**
 * The Information view asset handler creates entities and relationships from NewViewEvents.
 */
public class InformationViewAssetHandler {

    private String serviceName;
    private OMRSRepositoryHelper repositoryHelper;
    private RepositoryHandler repositoryHandler;
    private InvalidParameterHandler invalidParameterHandler;

    public InformationViewAssetHandler(String serviceName, OMRSRepositoryHelper repositoryHelper, RepositoryHandler repositoryHandler, InvalidParameterHandler invalidParameterHandler) {
        this.serviceName = serviceName;
        this.repositoryHelper = repositoryHelper;
        this.repositoryHandler = repositoryHandler;
        this.invalidParameterHandler = invalidParameterHandler;
    }

    public void createInformationViewAsset(NewViewEvent event)
            throws UserNotAuthorizedException, PropertyServerException, InvalidParameterException {

        /*
        Step 1: Create Software Server Entity
        */
        final String createSoftwareServerEntity = "createSoftwareServerEntity";

        String qualifiedNameForSoftwareServer = QualifiedNameUtils.buildQualifiedName("", SOFTWARE_SERVER_TYPE_NAME, event.getTableSource().getDatabaseSource().getEndpointSource().getNetworkAddress().split(":")[0]);

        InstanceProperties softwareServerProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForSoftwareServer)
                .withStringProperty(Constants.NAME, qualifiedNameForSoftwareServer)
                .build();

        invalidParameterHandler.validateUserId(Constants.DATA_PLATFORM_USER_ID, createSoftwareServerEntity);


        String softwareServerEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                SOFTWARE_SERVER_TYPE_GUID,
                SOFTWARE_SERVER_TYPE_NAME,
                softwareServerProperties,
                createSoftwareServerEntity);

        /*
        Step 2: Create Endpoint Entity
        */
        final String createEndpointEntity = "createEndpointEntity";

        String qualifiedNameForEndpoint = QualifiedNameUtils.buildQualifiedName("", ENDPOINT_TYPE_NAME, event.getTableSource().getDatabaseSource().getEndpointSource().getProtocol() + event.getTableSource().getDatabaseSource().getEndpointSource().getNetworkAddress());

        InstanceProperties endpointProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForEndpoint)
                .withStringProperty(Constants.NAME, qualifiedNameForEndpoint)
                .withStringProperty(Constants.NETWORK_ADDRESS, event.getTableSource().getDatabaseSource().getEndpointSource().getNetworkAddress())
                .withStringProperty(Constants.PROTOCOL, event.getTableSource().getDatabaseSource().getEndpointSource().getProtocol())
                .build();

        String endpointEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                ENDPOINT_TYPE_GUID,
                ENDPOINT_TYPE_NAME,
                endpointProperties,
                createEndpointEntity);

        /*
        Step 3: Create Relationship between Software Server Entity and Endpoint Entity
        */

        final String createServerEndpointRelationship = "createServerEndpointRelationship";

        repositoryHandler.createRelationship(
                Constants.DATA_PLATFORM_USER_ID,
                SERVER_ENDPOINT_TYPE_GUID,
                softwareServerEntityGuid,
                endpointEntityGuid,
                new InstanceProperties(),
                createServerEndpointRelationship);


        /*
        Step 4: Create Connection Entity
        */

        final String createConnectionEntity = "createConnectionEntity";

        String qualifiedNameForConnection = QualifiedNameUtils.buildQualifiedName(qualifiedNameForEndpoint, CONNECTION_TYPE_NAME, event.getTableSource().getDatabaseSource().getEndpointSource().getUser());
        InstanceProperties connectionProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForConnection)
                .withStringProperty(Constants.DESCRIPTION, "Connection to " + qualifiedNameForConnection)
                .build();

        String connectionEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                CONNECTION_TYPE_GUID,
                CONNECTION_TYPE_NAME,
                connectionProperties,
                createConnectionEntity);

        /*
        Step 5: Create Relationship between Software Server Entity and Endpoint Entity
        */

        final String createConnectionEndpointRelationship = "createConnectionEndpointRelationship";

        repositoryHandler.createRelationship(
                Constants.DATA_PLATFORM_USER_ID,
                CONNECTION_ENDPOINT_TYPE_GUID,
                connectionEntityGuid,
                endpointEntityGuid,
                new InstanceProperties(),
                createConnectionEndpointRelationship);

        /*
        Step 6: Create Connector Type Entity
        */

        final String createConnectorTypeEntity = "createConnectorTypeEntity";

        String qualifiedNameForConnectorType = QualifiedNameUtils.buildQualifiedName("", CONNECTION_TYPE_NAME, event.getTableSource().getDatabaseSource().getEndpointSource().getConnectorProviderName());
        InstanceProperties connectorTypeProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForConnectorType)
                .withStringProperty(Constants.CONNECTOR_PROVIDER_CLASSNAME, event.getTableSource().getDatabaseSource().getEndpointSource().getConnectorProviderName())
                .build();

        String connectorTypeEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                CONNECTOR_TYPE_TYPE_GUID,
                CONNECTOR_TYPE_TYPE_NAME,
                connectorTypeProperties,
                createConnectorTypeEntity);


        /*
        Step 7: create Connection ConnectorType Relationship
        */

        final String createConnectionConnectorTypeRelationship = "createConnectionConnectorTypeRelationship";

        repositoryHandler.createRelationship(
                Constants.DATA_PLATFORM_USER_ID,
                CONNECTION_CONNECTOR_TYPE_TYPE_GUID,
                connectionEntityGuid,
                connectorTypeEntityGuid,
                new InstanceProperties(),
                createConnectionConnectorTypeRelationship);

        /*
        Step 8: create Database Entity
        */
        final String createDatabaseEntity = "createDatabaseEntity";

        String qualifiedNameForDatabase = QualifiedNameUtils.buildQualifiedName(qualifiedNameForSoftwareServer, DATABASE_TYPE_NAME, event.getTableSource().getDatabaseSource().getName());
        InstanceProperties databaseProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForDatabase)
                .withStringProperty(Constants.NAME, event.getTableSource().getDatabaseSource().getName())
                .build();

        String databaseEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                DATABASE_TYPE_GUID,
                DATABASE_TYPE_NAME,
                databaseProperties,
                createDatabaseEntity);

        /*
        Step 9: create Connection To Asset Relationship
        */

        final String createConnectionToAssetTypeRelationship = "createConnectionToAssetTypeRelationship";

        repositoryHandler.createRelationship(
                Constants.DATA_PLATFORM_USER_ID,
                CONNECTION_TO_ASSET_TYPE_GUID,
                connectionEntityGuid,
                databaseEntityGuid,
                new InstanceProperties(),
                createConnectionToAssetTypeRelationship);

        /*
        Step 10: create Information View Entity
        */

        final String createInformationViewEntity = "createInformationViewEntity";

        String qualifiedNameForInformationView = QualifiedNameUtils.buildQualifiedName(qualifiedNameForDatabase, INFORMATION_VIEW_TYPE_NAME, event.getTableSource().getSchemaName());
        InstanceProperties informationViewProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForInformationView)
                .withStringProperty(Constants.NAME, event.getTableSource().getSchemaName())
                .withStringProperty(Constants.OWNER, "")
                .withStringProperty(Constants.DESCRIPTION, "This asset is an " + "information " + "view")
                .build();

        String informationViewEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                INFORMATION_VIEW_TYPE_GUID,
                INFORMATION_VIEW_TYPE_NAME,
                informationViewProperties,
                createInformationViewEntity);


        /*
        Step 9: create Connection To Asset Relationship
        */

        final String createDataContentForDataSetRelationship = "createDataContentForDataSetRelationship";

        repositoryHandler.createRelationship(
                Constants.DATA_PLATFORM_USER_ID,
                DATA_CONTENT_FOR_DATASET_TYPE_GUID,
                databaseEntityGuid,
                informationViewEntityGuid,
                new InstanceProperties(),
                createDataContentForDataSetRelationship);


         /*
        Step 10: create Relational DB Schema Type Entity
        */

        final String createRelationalDBSchemaTypeEntity = "createRelationalDBSchemaTypeEntity";

        String qualifiedNameForDbSchemaType = QualifiedNameUtils.buildQualifiedName(qualifiedNameForDatabase, RELATIONAL_DB_SCHEMA_TYPE_TYPE_NAME, event.getTableSource().getSchemaName() + Constants.TYPE_SUFFIX);
        InstanceProperties dbSchemaTypeProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForDbSchemaType)
                .withStringProperty(Constants.DISPLAY_NAME, event.getTableSource().getSchemaName() + Constants.TYPE_SUFFIX)
                .withStringProperty(Constants.AUTHOR, "")
                .withStringProperty(Constants.USAGE, "")
                .withStringProperty(Constants.ENCODING_STANDARD, "").build();

        String relationalDBSchemaTypeEntityGuid = repositoryHandler.createEntity(
                Constants.DATA_PLATFORM_USER_ID,
                RELATIONAL_DB_SCHEMA_TYPE_TYPE_GUID,
                RELATIONAL_DB_SCHEMA_TYPE_TYPE_NAME,
                dbSchemaTypeProperties,
                createRelationalDBSchemaTypeEntity);

        /*
        Step 11: create Asset To Schema Type Relationship
        */

        final String createAssetToSchemaTypeRelationship = "createAssetToSchemaTypeRelationship";

        repositoryHandler.createRelationship(
                Constants.DATA_PLATFORM_USER_ID,
                ASSET_SCHEMA_TYPE_TYPE_GUID,
                informationViewEntityGuid,
                relationalDBSchemaTypeEntityGuid,
                new InstanceProperties(),
                createAssetToSchemaTypeRelationship);
    }
}