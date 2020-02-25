/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataplatform.handlers;

import org.odpi.openmetadata.accessservices.dataplatform.events.NewViewEvent;
import org.odpi.openmetadata.accessservices.dataplatform.properties.BusinessTerm;
import org.odpi.openmetadata.accessservices.dataplatform.properties.DerivedColumn;
import org.odpi.openmetadata.accessservices.dataplatform.properties.TableSource;
import org.odpi.openmetadata.accessservices.dataplatform.utils.Constants;
import org.odpi.openmetadata.accessservices.dataplatform.utils.EntityPropertiesBuilder;
import org.odpi.openmetadata.accessservices.dataplatform.utils.QualifiedNameUtils;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.*;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.odpi.openmetadata.accessservices.dataplatform.mappers.InformationViewAssetMapper.*;
import static org.odpi.openmetadata.accessservices.dataplatform.utils.Constants.*;

/**
 * The Information view asset handler creates entities and relationships from NewViewEvents.
 */
public class InformationViewAssetHandler {

    private static final Logger log = LoggerFactory.getLogger(InformationViewAssetHandler.class);
    private final OMRSAuditLog auditLog;

    private String serviceName;
    private OMRSRepositoryHelper repositoryHelper;
    private RepositoryHandler repositoryHandler;
    private InvalidParameterHandler invalidParameterHandler;

    public InformationViewAssetHandler(String serviceName, OMRSAuditLog auditLog, OMRSRepositoryHelper repositoryHelper, RepositoryHandler repositoryHandler, InvalidParameterHandler invalidParameterHandler) {
        this.serviceName = serviceName;
        this.auditLog = auditLog;
        this.repositoryHelper = repositoryHelper;
        this.repositoryHandler = repositoryHandler;
        this.invalidParameterHandler = invalidParameterHandler;
    }

    public void createInformationViewAsset(NewViewEvent event) throws UserNotAuthorizedException, PropertyServerException, InvalidParameterException {

        String actionDescription = "createInformationViewAsset";

        if (event.getDerivedColumns().isEmpty() || event.getDerivedColumns() == null) {
            log.debug("Delete existing view as event received has no derived columns");

           /* DataPlatformErrorCode auditCode = DataPlatformErrorCode.;
            auditLog.logRecord(actionDescription,
                    auditCode.getLogMessageId(),
                    auditCode.getSeverity(),
                    auditCode.getFormattedLogMessage("Empty or null derived column value in new view event."),
                    null,
                    auditCode.getSystemAction(),
                    auditCode.getUserAction());*/
        } else {
            createInformationViewAsset(event.getTableSource());
        }

    }

    private void createInformationView(List<DerivedColumn> derivedColumns, TableSource tableSource, TableSource originalTableSource)
            throws UserNotAuthorizedException, PropertyServerException {

        String methodName = "createDerivedColumn";

        String qualifiedNameForInformationView = QualifiedNameUtils.buildQualifiedNameForInformationView(tableSource.getDatabaseSource().getEndpointSource().getNetworkAddress().split(":")[0], tableSource.getDatabaseSource().getName(), tableSource.getSchemaName());
        String qualifiedNameForTableType = QualifiedNameUtils.buildQualifiedName(qualifiedNameForInformationView, Constants.RELATIONAL_TABLE_TYPE, tableSource.getName() + Constants.TYPE_SUFFIX);
        InstanceProperties tableTypeProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForTableType)
                .withStringProperty(Constants.DISPLAY_NAME, tableSource.getName() + Constants.TYPE_SUFFIX)
                .withStringProperty(Constants.AUTHOR, "")
                .withStringProperty(Constants.USAGE, "")
                .withStringProperty(Constants.ENCODING_STANDARD, "")
                .build();

        repositoryHandler.createEntity(DATA_PLATFORM_USER_ID,
                RELATIONAL_TABLE_TYPE_GUID,
                RELATIONAL_TABLE_TYPE_NAME,
                tableTypeProperties,
                methodName);

        EntityDetail entityRelationalTableType = repositoryHandler.getUniqueEntityByName(
                DATA_PLATFORM_USER_ID,
                qualifiedNameForTableType,
                QUALIFIED_NAME,
                null,
                RELATIONAL_TABLE_TYPE_NAME,
                RELATIONAL_TABLE_TYPE_GUID,
                methodName);

        String relationalTableGuid = entityRelationalTableType.getGUID();
        boolean updateFlag = false;

        if (entityRelationalTableType.getGUID() != null) {
            repositoryHandler.updateEntity(DATA_PLATFORM_USER_ID,
                    entityRelationalTableType.getGUID(),
                    RELATIONAL_TABLE_TYPE_GUID,
                    RELATIONAL_TABLE_TYPE_NAME,
                    tableTypeProperties,
                    methodName);
            updateFlag = true;
        } else {
            relationalTableGuid = repositoryHandler.createEntity(DATA_PLATFORM_USER_ID,
                    RELATIONAL_TABLE_TYPE_GUID,
                    RELATIONAL_TABLE_TYPE_NAME,
                    tableTypeProperties,
                    methodName);
        }

        String qualifiedNameForTable = QualifiedNameUtils.buildQualifiedName(qualifiedNameForInformationView, Constants.RELATIONAL_TABLE, tableSource.getName());
        InstanceProperties tableProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForTable)
                .withStringProperty(Constants.ATTRIBUTE_NAME, tableSource.getName())
                .build();

        if (originalTableSource != null) {
            HashMap<String, String> prop = new HashMap<>();
            prop.put(Constants.DISPLAY_NAME, originalTableSource.getName());
            tableProperties = repositoryHelper.addStringMapPropertyToInstance(Constants.DATA_PLATFORM_OMAS_NAME, tableProperties, Constants.ADDITIONAL_PROPERTIES, prop, "ViewHandler.call");
        }

        String tableGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                RELATIONAL_TABLE_NAME,
                RELATIONAL_TABLE_GUID,
                tableProperties,
                methodName
        );

        repositoryHandler.createRelationship(DATA_PLATFORM_USER_ID,
                SCHEMA_ATTRIBUTE_TYPE_TYPE_GUID,
                tableGuid,
                relationalTableGuid,
                null,
                methodName
        );

        List<Relationship> columnsToBeDeleted = null;

        if (!updateFlag) {
            columnsToBeDeleted = repositoryHandler.getRelationshipsByType(
                    DATA_PLATFORM_USER_ID,
                    relationalTableGuid,
                    RELATIONAL_TABLE_GUID,
                    ATTRIBUTE_FOR_SCHEMA_TYPE_GUID,
                    ATTRIBUTE_FOR_SCHEMA_TYPE_NAME,
                    methodName
            );
        }

        if (derivedColumns != null && !derivedColumns.isEmpty()) {
            String finalRelationalTableGuid = relationalTableGuid;
            List<String> newDerivedColumnsGuids = derivedColumns.parallelStream().map(c -> addDerivedColumn(finalRelationalTableGuid, qualifiedNameForTable, c)).collect(Collectors.toList());
            if (columnsToBeDeleted != null && !columnsToBeDeleted.isEmpty()) {
                columnsToBeDeleted = columnsToBeDeleted.stream().filter(c -> !newDerivedColumnsGuids.contains(c.getEntityTwoProxy().getGUID())).collect(Collectors.toList());
            }
        }
        deleteColumns(columnsToBeDeleted);
    }

    private void createInformationViewAsset(TableSource tableSource)
            throws UserNotAuthorizedException, PropertyServerException, InvalidParameterException {
        /*
        Step 1: Create Software Server Entity
        */
        final String createSoftwareServerEntity = "createSoftwareServerEntity";

        String qualifiedNameForSoftwareServer = QualifiedNameUtils.buildQualifiedName("", SOFTWARE_SERVER_TYPE_NAME, tableSource.getDatabaseSource().getEndpointSource().getNetworkAddress().split(":")[0]);

        InstanceProperties softwareServerProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForSoftwareServer)
                .withStringProperty(Constants.NAME, qualifiedNameForSoftwareServer)
                .build();

        invalidParameterHandler.validateUserId(DATA_PLATFORM_USER_ID, createSoftwareServerEntity);


        String softwareServerEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                SOFTWARE_SERVER_TYPE_GUID,
                SOFTWARE_SERVER_TYPE_NAME,
                softwareServerProperties,
                createSoftwareServerEntity);

        /*
        Step 2: Create Endpoint Entity
        */
        final String createEndpointEntity = "createEndpointEntity";

        String qualifiedNameForEndpoint = QualifiedNameUtils.buildQualifiedName("", ENDPOINT_TYPE_NAME, tableSource.getDatabaseSource().getEndpointSource().getProtocol() + tableSource.getDatabaseSource().getEndpointSource().getNetworkAddress());

        InstanceProperties endpointProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForEndpoint)
                .withStringProperty(Constants.NAME, qualifiedNameForEndpoint)
                .withStringProperty(Constants.NETWORK_ADDRESS, tableSource.getDatabaseSource().getEndpointSource().getNetworkAddress())
                .withStringProperty(Constants.PROTOCOL, tableSource.getDatabaseSource().getEndpointSource().getProtocol())
                .build();

        String endpointEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                ENDPOINT_TYPE_GUID,
                ENDPOINT_TYPE_NAME,
                endpointProperties,
                createEndpointEntity);

        /*
        Step 3: Create Relationship between Software Server Entity and Endpoint Entity
        */

        final String createServerEndpointRelationship = "createServerEndpointRelationship";

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                SERVER_ENDPOINT_TYPE_GUID,
                softwareServerEntityGuid,
                endpointEntityGuid,
                new InstanceProperties(),
                createServerEndpointRelationship);


        /*
        Step 4: Create Connection Entity
        */

        final String createConnectionEntity = "createConnectionEntity";

        String qualifiedNameForConnection = QualifiedNameUtils.buildQualifiedName(qualifiedNameForEndpoint, CONNECTION_TYPE_NAME, tableSource.getDatabaseSource().getEndpointSource().getUser());
        InstanceProperties connectionProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForConnection)
                .withStringProperty(Constants.DESCRIPTION, "Connection to " + qualifiedNameForConnection)
                .build();

        String connectionEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                CONNECTION_TYPE_GUID,
                CONNECTION_TYPE_NAME,
                connectionProperties,
                createConnectionEntity);

        /*
        Step 5: Create Relationship between Software Server Entity and Endpoint Entity
        */

        final String createConnectionEndpointRelationship = "createConnectionEndpointRelationship";

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                CONNECTION_ENDPOINT_TYPE_GUID,
                connectionEntityGuid,
                endpointEntityGuid,
                new InstanceProperties(),
                createConnectionEndpointRelationship);

        /*
        Step 6: Create Connector Type Entity
        */

        final String createConnectorTypeEntity = "createConnectorTypeEntity";

        String qualifiedNameForConnectorType = QualifiedNameUtils.buildQualifiedName("", CONNECTION_TYPE_NAME, tableSource.getDatabaseSource().getEndpointSource().getConnectorProviderName());
        InstanceProperties connectorTypeProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForConnectorType)
                .withStringProperty(Constants.CONNECTOR_PROVIDER_CLASSNAME, tableSource.getDatabaseSource().getEndpointSource().getConnectorProviderName())
                .build();

        String connectorTypeEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                CONNECTOR_TYPE_TYPE_GUID,
                CONNECTOR_TYPE_TYPE_NAME,
                connectorTypeProperties,
                createConnectorTypeEntity);


        /*
        Step 7: create Connection ConnectorType Relationship
        */

        final String createConnectionConnectorTypeRelationship = "createConnectionConnectorTypeRelationship";

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                CONNECTION_CONNECTOR_TYPE_TYPE_GUID,
                connectionEntityGuid,
                connectorTypeEntityGuid,
                new InstanceProperties(),
                createConnectionConnectorTypeRelationship);

        /*
        Step 8: create Database Entity
        */
        final String createDatabaseEntity = "createDatabaseEntity";

        String qualifiedNameForDatabase = QualifiedNameUtils.buildQualifiedName(qualifiedNameForSoftwareServer, DATABASE_TYPE_NAME, tableSource.getDatabaseSource().getName());
        InstanceProperties databaseProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForDatabase)
                .withStringProperty(Constants.NAME, tableSource.getDatabaseSource().getName())
                .build();

        String databaseEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                DATABASE_TYPE_GUID,
                DATABASE_TYPE_NAME,
                databaseProperties,
                createDatabaseEntity);

        /*
        Step 9: create Connection To Asset Relationship
        */

        final String createConnectionToAssetTypeRelationship = "createConnectionToAssetTypeRelationship";

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                CONNECTION_TO_ASSET_TYPE_GUID,
                connectionEntityGuid,
                databaseEntityGuid,
                new InstanceProperties(),
                createConnectionToAssetTypeRelationship);

        /*
        Step 10: create Information View Entity
        */

        final String createInformationViewEntity = "createInformationViewEntity";

        String qualifiedNameForInformationView = QualifiedNameUtils.buildQualifiedName(qualifiedNameForDatabase, INFORMATION_VIEW_TYPE_NAME, tableSource.getSchemaName());
        InstanceProperties informationViewProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForInformationView)
                .withStringProperty(Constants.NAME, tableSource.getSchemaName())
                .withStringProperty(Constants.OWNER, "")
                .withStringProperty(Constants.DESCRIPTION, "This asset is an " + "information " + "view")
                .build();

        String informationViewEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                INFORMATION_VIEW_TYPE_GUID,
                INFORMATION_VIEW_TYPE_NAME,
                informationViewProperties,
                createInformationViewEntity);


        /*
        Step 9: create Connection To Asset Relationship
        */

        final String createDataContentForDataSetRelationship = "createDataContentForDataSetRelationship";

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                DATA_CONTENT_FOR_DATASET_TYPE_GUID,
                databaseEntityGuid,
                informationViewEntityGuid,
                new InstanceProperties(),
                createDataContentForDataSetRelationship);


         /*
        Step 10: create Relational DB Schema Type Entity
        */

        final String createRelationalDBSchemaTypeEntity = "createRelationalDBSchemaTypeEntity";

        String qualifiedNameForDbSchemaType = QualifiedNameUtils.buildQualifiedName(qualifiedNameForDatabase, RELATIONAL_DB_SCHEMA_TYPE_TYPE_NAME, tableSource.getSchemaName() + Constants.TYPE_SUFFIX);
        InstanceProperties dbSchemaTypeProperties = new EntityPropertiesBuilder()
                .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForDbSchemaType)
                .withStringProperty(Constants.DISPLAY_NAME, tableSource.getSchemaName() + Constants.TYPE_SUFFIX)
                .withStringProperty(Constants.AUTHOR, "")
                .withStringProperty(Constants.USAGE, "")
                .withStringProperty(Constants.ENCODING_STANDARD, "").build();

        String relationalDBSchemaTypeEntityGuid = repositoryHandler.createEntity(
                DATA_PLATFORM_USER_ID,
                RELATIONAL_DB_SCHEMA_TYPE_TYPE_GUID,
                RELATIONAL_DB_SCHEMA_TYPE_TYPE_NAME,
                dbSchemaTypeProperties,
                createRelationalDBSchemaTypeEntity);

        /*
        Step 11: create Asset To Schema Type Relationship
        */

        final String createAssetToSchemaTypeRelationship = "createAssetToSchemaTypeRelationship";

        repositoryHandler.createRelationship(
                DATA_PLATFORM_USER_ID,
                ASSET_SCHEMA_TYPE_TYPE_GUID,
                informationViewEntityGuid,
                relationalDBSchemaTypeEntityGuid,
                new InstanceProperties(),
                createAssetToSchemaTypeRelationship);
    }

    private void deleteView(NewViewEvent event) throws UserNotAuthorizedException, PropertyServerException, InvalidParameterException {

        String methodName = "deleteView";
        String qualifiedNameForInformationView = QualifiedNameUtils.buildQualifiedNameForInformationView(event.getTableSource().getDatabaseSource().getEndpointSource().getNetworkAddress().split(":")[0], event.getTableSource().getName(), event.getTableSource().getSchemaName());
        String qualifiedNameForTableType = QualifiedNameUtils.buildQualifiedName(qualifiedNameForInformationView, RELATIONAL_TABLE_TYPE_NAME, event.getTableSource().getName() + Constants.TYPE_SUFFIX);


        // EntityDetail tableTypeEntity = omEntityDao.getEntity(RELATIONAL_TABLE_TYPE_NAME, qualifiedNameForTableType, false);

        EntityDetail tableTypeEntity = repositoryHandler.getUniqueEntityByName(DATA_PLATFORM_USER_ID,
                qualifiedNameForTableType,
                QUALIFIED_NAME,
                null,
                RELATIONAL_TABLE_TYPE_NAME,
                RELATIONAL_TABLE_TYPE_GUID,
                methodName);


        if (tableTypeEntity != null) {
            //List<Relationship> derivedColumns = omEntityDao.getRelationships(Constants.ATTRIBUTE_FOR_SCHEMA, tableTypeEntity.getGUID());

            String methodGetRelationshipDerivedColumns = "methodGetRelationshipDerivedColumns";
            List<Relationship> derivedColumns = repositoryHandler.getRelationshipsByType(DATA_PLATFORM_USER_ID,
                    tableTypeEntity.getGUID(),
                    RELATIONAL_TABLE_TYPE,
                    ATTRIBUTE_FOR_SCHEMA_TYPE_NAME,
                    ATTRIBUTE_FOR_SCHEMA_TYPE_GUID,
                    methodGetRelationshipDerivedColumns);

            deleteColumns(derivedColumns);
            repositoryHandler.removeEntity(DATA_PLATFORM_USER_ID,
                    tableTypeEntity.getGUID(),
                    RELATIONAL_TABLE_TYPE,
                    RELATIONAL_TABLE_TYPE_GUID,
                    null,
                    null,
                    "removeRelationalTableType");
        }

        String qualifiedNameForTable = QualifiedNameUtils.buildQualifiedName(qualifiedNameForInformationView, RELATIONAL_TABLE_TYPE_NAME, event.getTableSource().getName());
        EntityDetail tableEntity = repositoryHandler.getUniqueEntityByName(DATA_PLATFORM_USER_ID,
                qualifiedNameForTable,
                QUALIFIED_NAME,
                null,
                RELATIONAL_TABLE_NAME,
                RELATIONAL_TABLE_GUID,
                "getRelationalTableTypeByQualifiedName");

        if (tableEntity != null) {
            repositoryHandler.removeEntity(DATA_PLATFORM_USER_ID,
                    tableEntity.getGUID(),
                    tableEntity.getType().getTypeDefName(),
                    tableEntity.getType().getTypeDefGUID(),
                    null,
                    null,
                    "removeRelationalTableType");
        }
    }

    private void deleteColumns(List<Relationship> columns) {

        if (columns != null && !columns.isEmpty()) {
            columns.parallelStream().forEach(c -> {
                try {
                    deleteEntity(c.getEntityTwoProxy());
                } catch (UserNotAuthorizedException e) {
                    log.debug("UserNotAuthorizedException", e);
                } catch (PropertyServerException e) {
                    log.debug("PropertyServerException", e);
                } catch (InvalidParameterException e) {
                    log.debug("InvalidParameterException", e);
                }
            });
        }


    }

    private void deleteEntity(EntityProxy entityProxy) throws UserNotAuthorizedException, PropertyServerException, InvalidParameterException {

        String methodName = "deleteEntity";

        repositoryHandler.removeEntity(DATA_PLATFORM_USER_ID,
                entityProxy.getGUID(),
                entityProxy.getType().getTypeDefName(),
                entityProxy.getType().getTypeDefGUID(),
                null,
                null,
                methodName);
    }

    private String addDerivedColumn(String tableTypeEntityGuid, String qualifiedNameForTable, DerivedColumn derivedColumn) {

        String methodName = "addDerivedColumn";
        try {
            String qualifiedNameColumnType = QualifiedNameUtils.buildQualifiedName(qualifiedNameForTable, Constants.RELATIONAL_COLUMN_TYPE, derivedColumn.getName() + Constants.TYPE_SUFFIX);

            InstanceProperties columnTypeProperties = new EntityPropertiesBuilder()
                    .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameColumnType)
                    .withStringProperty(Constants.DISPLAY_NAME, derivedColumn.getName() + Constants.TYPE_SUFFIX)
                    .withStringProperty(Constants.AUTHOR, "")
                    .withStringProperty(Constants.USAGE, "")
                    .withStringProperty(Constants.ENCODING_STANDARD, "")
                    .withStringProperty(Constants.DATA_TYPE, derivedColumn.getType())
                    .build();

            String relationalColumnTypeEntityGuid = repositoryHandler.createEntity(DATA_PLATFORM_USER_ID,
                    RELATIONAL_COLUMN_TYPE_GUID,
                    RELATIONAL_COLUMN_TYPE_NAME,
                    columnTypeProperties,
                    methodName);

            String qualifiedNameForColumn = QualifiedNameUtils.buildQualifiedName(qualifiedNameForTable, Constants.DERIVED_RELATIONAL_COLUMN, derivedColumn.getName());
            InstanceProperties columnProperties = new EntityPropertiesBuilder()
                    .withStringProperty(Constants.QUALIFIED_NAME, qualifiedNameForColumn)
                    .withStringProperty(Constants.ATTRIBUTE_NAME, derivedColumn.getName())
                    .withStringProperty(Constants.FORMULA, "")
                    .withIntegerProperty(Constants.ELEMENT_POSITION_NAME, derivedColumn.getPosition())
                    .build();

            String derivedColumnEntityGuid = repositoryHandler.createEntity(DATA_PLATFORM_USER_ID,
                    DERIVED_RELATIONAL_COLUMN_TYPE_GUID,
                    DERIVED_RELATIONAL_COLUMN_TYPE_NAME,
                    columnProperties,
                    methodName);

            derivedColumn.setGuid(derivedColumnEntityGuid);

            repositoryHandler.createRelationship(
                    DATA_PLATFORM_USER_ID,
                    ATTRIBUTE_FOR_SCHEMA_TYPE_GUID,
                    tableTypeEntityGuid,
                    derivedColumnEntityGuid,
                    null,
                    methodName
            );

            repositoryHandler.createRelationship(
                    DATA_PLATFORM_USER_ID,
                    SCHEMA_QUERY_IMPLEMENTATION_TYPE_GUID,
                    derivedColumnEntityGuid,
                    derivedColumn.getSourceColumn().getGuid(),
                    null,
                    methodName
            );

            repositoryHandler.createRelationship(
                    DATA_PLATFORM_USER_ID,
                    SCHEMA_ATTRIBUTE_TYPE_TYPE_GUID,
                    derivedColumnEntityGuid,
                    relationalColumnTypeEntityGuid,
                    null,
                    methodName
            );

            for (BusinessTerm businessTerm : derivedColumn.getSourceColumn().getBusinessTerms()) {
                repositoryHandler.createRelationship(
                        DATA_PLATFORM_USER_ID,
                        SEMANTIC_ASSIGNMENT_TYPE_GUID,
                        derivedColumnEntityGuid,
                        businessTerm.getGuid(),
                        null,
                        methodName
                );
            }

            return derivedColumnEntityGuid;
        } catch (UserNotAuthorizedException e) {
            log.debug("UserNotAuthorizedException", e);
        } catch (PropertyServerException e) {
            log.debug("PropertyServerException", e);
        }
        return null;
    }

}