/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.handlers;

import org.odpi.openmetadata.accessservices.dataplatform.properties.DeployedDatabaseSchema;
import org.odpi.openmetadata.accessservices.dataplatform.utils.Constants;
import org.odpi.openmetadata.accessservices.dataplatform.utils.EntityPropertiesBuilder;
import org.odpi.openmetadata.accessservices.dataplatform.utils.QualifiedNameUtils;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.ffdc.exceptions.InvalidParameterException;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

import static org.odpi.openmetadata.accessservices.dataplatform.utils.Constants.DATA_PLATFORM_USER_ID;

/**
 * The type DeployedDatabaseSchema asset handler.
 */
public class DeployedDatabaseSchemaAssetHandler {

    private String serviceName;
    private String serverName;
    private OMRSRepositoryHelper repositoryHelper;
    private RepositoryHandler repositoryHandler;
    private InvalidParameterHandler invalidParameterHandler;

    /**
     * Instantiates a new Deployed database schema asset handler.
     *
     * @param serviceName             the service name
     * @param serverName              the server name
     * @param repositoryHelper        the repository helper
     * @param repositoryHandler       the repository handler
     * @param invalidParameterHandler the invalid parameter handler
     */
    public DeployedDatabaseSchemaAssetHandler(String serviceName, String serverName, OMRSRepositoryHelper repositoryHelper, RepositoryHandler repositoryHandler, InvalidParameterHandler invalidParameterHandler) {
        this.serviceName = serviceName;
        this.serverName = serverName;
        this.repositoryHelper = repositoryHelper;
        this.repositoryHandler = repositoryHandler;
        this.invalidParameterHandler = invalidParameterHandler;
    }

    /**
     * Create deployed database schema asset
     *
     * @throws PropertyServerException    the property server exception
     * @throws UserNotAuthorizedException the user not authorized exception
     * @throws InvalidParameterException  the invalid parameter exception
     */
    public String createDeployedDatabaseSchemaAsset(DeployedDatabaseSchema deployedDatabaseSchema) throws
            PropertyServerException, UserNotAuthorizedException, InvalidParameterException {


        String methodName = "create Deployed Database Schema Asset";


        String qualifiedNameForSoftwareServer = QualifiedNameUtils.buildQualifiedName("", Constants.SOFTWARE_SERVER,
                deployedDatabaseSchema.getConnection().getEndpoint().getDisplayName() + deployedDatabaseSchema.getConnection().getEndpoint().getAddress().split(":")[0]);

        invalidParameterHandler.validateUserId(DATA_PLATFORM_USER_ID, methodName);
        invalidParameterHandler.validateName(qualifiedNameForSoftwareServer, Constants.QUALIFIED_NAME, methodName);

        InstanceProperties softwareServerProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForSoftwareServer)
                .withStringProperty(Constants.NAME, qualifiedNameForSoftwareServer)
                .build();

        String softwareServerEntityGuid = repositoryHandler.createExternalEntity(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.SOFTWARE_SERVER).getGUID(),
                Constants.SOFTWARE_SERVER,
                deployedDatabaseSchema.getExternalSourceGuid(),
                deployedDatabaseSchema.getExternalSourceName(),
                softwareServerProperties,
                methodName);


        String qualifiedNameForEndpoint = QualifiedNameUtils.buildQualifiedName("", Constants.ENDPOINT,
                deployedDatabaseSchema.getConnection().getEndpoint().getEncryptionMethod() + deployedDatabaseSchema.getConnection().getEndpoint().getAddress());

        InstanceProperties endpointProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForEndpoint)
                .withStringProperty(Constants.NAME, qualifiedNameForEndpoint)
                .withStringProperty(Constants.NETWORK_ADDRESS, deployedDatabaseSchema.getConnection().getEndpoint().getAddress())
                .withStringProperty(Constants.PROTOCOL, deployedDatabaseSchema.getConnection().getEndpoint().getProtocol())
                .build();

        invalidParameterHandler.validateName(qualifiedNameForEndpoint, Constants.QUALIFIED_NAME, methodName);
        String endpointEntityGuid = repositoryHandler.createExternalEntity(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.ENDPOINT).getGUID(),
                Constants.ENDPOINT,
                deployedDatabaseSchema.getExternalSourceGuid(),
                deployedDatabaseSchema.getExternalSourceName(),
                endpointProperties,
                methodName);


        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.SERVER_ENDPOINT).getGUID(),
                softwareServerEntityGuid,
                endpointEntityGuid,
                new InstanceProperties(),
                methodName);

        String qualifiedNameForConnection = QualifiedNameUtils.buildQualifiedName(qualifiedNameForEndpoint, Constants.CONNECTION,
                deployedDatabaseSchema.getConnection().getEndpoint().getDisplayName());

        InstanceProperties connectionProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForConnection)
                .withStringProperty(Constants.DESCRIPTION, "Connection to " + qualifiedNameForConnection)
                .build();

        invalidParameterHandler.validateName(qualifiedNameForConnection, Constants.QUALIFIED_NAME, methodName);
        String connectionEntityGuid = repositoryHandler.createExternalEntity(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.CONNECTION).getGUID(),
                Constants.CONNECTION,
                deployedDatabaseSchema.getExternalSourceGuid(),
                deployedDatabaseSchema.getExternalSourceName(),
                connectionProperties,
                methodName);


        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.CONNECTION_TO_ENDPOINT).getGUID(),
                endpointEntityGuid,
                connectionEntityGuid,
                new InstanceProperties(),
                methodName);


        String qualifiedNameForConnectorType = QualifiedNameUtils.buildQualifiedName("", Constants.CONNECTOR_TYPE,
                deployedDatabaseSchema.getConnection().getConnectorType().getConnectorProviderClassName());

        InstanceProperties connectorTypeProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForConnectorType)
                .withStringProperty(Constants.CONNECTOR_PROVIDER_CLASSNAME, deployedDatabaseSchema.getConnection().getConnectorType().getConnectorProviderClassName())
                .build();

        invalidParameterHandler.validateName(qualifiedNameForConnectorType, Constants.QUALIFIED_NAME, methodName);
        String connectorTypeEntityGuid = repositoryHandler.createExternalEntity(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.CONNECTOR_TYPE).getGUID(),
                Constants.CONNECTOR_TYPE,
                deployedDatabaseSchema.getExternalSourceGuid(),
                deployedDatabaseSchema.getExternalSourceName(),
                connectorTypeProperties,
                methodName);

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.CONNECTION_CONNECTOR_TYPE).getGUID(),
                connectionEntityGuid,
                connectorTypeEntityGuid,
                new InstanceProperties(),
                methodName);


        //TODO: fill in details in event payload about database server side info and change the qualified name
        String qualifiedNameForDatabase = QualifiedNameUtils.buildQualifiedName(qualifiedNameForSoftwareServer, Constants.DATABASE,
                "Apache Cassandra Data Store");
        InstanceProperties databaseProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForDatabase)
                .withStringProperty(Constants.NAME, "Apache Cassandra Data Store")
                .build();

        invalidParameterHandler.validateName(qualifiedNameForDatabase, Constants.QUALIFIED_NAME, methodName);
        String databaseEntityGuid = repositoryHandler.createExternalEntity(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.DATABASE).getGUID(),
                Constants.DATABASE,
                deployedDatabaseSchema.getExternalSourceGuid(),
                deployedDatabaseSchema.getExternalSourceName(),
                databaseProperties,
                methodName);
        String qualifiedNameForDeployedDatabaseSchema = QualifiedNameUtils.buildQualifiedName(qualifiedNameForDatabase, Constants.DEPLOYED_DATABASE_SCHEMA,
                deployedDatabaseSchema.getName());

        InstanceProperties deployedDbSchemaProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForDeployedDatabaseSchema)
                .withStringProperty(Constants.NAME, deployedDatabaseSchema.getName())
                //TODO: complete database source info from data platform service side
                .withStringProperty(Constants.OWNER, "Owner Info")
                .withStringProperty(Constants.DESCRIPTION, "Description")
                .build();

        invalidParameterHandler.validateName(qualifiedNameForDeployedDatabaseSchema, Constants.QUALIFIED_NAME, methodName);
        String deployedDbSchemaEntityGuid = repositoryHandler.createExternalEntity(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.DEPLOYED_DATABASE_SCHEMA).getGUID(),
                Constants.DEPLOYED_DATABASE_SCHEMA,
                deployedDatabaseSchema.getExternalSourceGuid(),
                deployedDatabaseSchema.getExternalSourceName(),
                deployedDbSchemaProperties,
                methodName);

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.DATA_CONTENT_FOR_DATASET).getGUID(),
                databaseEntityGuid,
                deployedDbSchemaEntityGuid,
                new InstanceProperties(),
                methodName);

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                repositoryHelper.getTypeDefByName(DATA_PLATFORM_USER_ID, Constants.CONNECTION_TO_ASSET).getGUID(),
                connectionEntityGuid,
                deployedDbSchemaEntityGuid,
                new InstanceProperties(),
                methodName);

        //TODO: Check whether the new Deployed DB also contains any schema types or schema attributes

        return deployedDbSchemaEntityGuid;
    }
}
