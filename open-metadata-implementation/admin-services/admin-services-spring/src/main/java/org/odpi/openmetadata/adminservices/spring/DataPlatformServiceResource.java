/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adminservices.spring;

import org.odpi.openmetadata.adminservices.OMAGServerDataPlatformService;
import org.odpi.openmetadata.adminservices.configuration.properties.DataPlatformServicesConfig;
import org.odpi.openmetadata.commonservices.ffdc.rest.VoidResponse;
import org.springframework.web.bind.annotation.*;


/**
 * DataPlatformServiceResource provides the API for configuring a data platform service in an OMAG
 * server.
 */
@RestController
@RequestMapping("/open-metadata/admin-services/users/{userId}/servers/{serverName}/data-platform-service")
public class DataPlatformServiceResource {


    private OMAGServerDataPlatformService adminAPI = new OMAGServerDataPlatformService();

    /**
     * Store the provided Data Platform Services configuration.
     *
     * @param userId                     the user id
     * @param serverName                 the server name
     * @param dataPlatformServicesConfig the data platform services config
     * @return the access services config
     */
    @PostMapping(path = "/configuration")
    public VoidResponse setDataPlatformServicesConfig(@PathVariable String userId,
                                                      @PathVariable String serverName,
                                                      @RequestBody DataPlatformServicesConfig dataPlatformServicesConfig) {
        return adminAPI.setDataPlatformServiceConfig(userId, serverName, dataPlatformServicesConfig);
    }

    /**
     * Remove this Data Platform Services from the server configuration.
     *
     * @param userId     user that is issuing the request
     * @param serverName local server name
     * @return void response
     */
    @DeleteMapping(path = "/configuration")
    public VoidResponse clearDataPlatformServices(@PathVariable String userId,
                                                  @PathVariable String serverName) {
        return adminAPI.clearDataPlatformService(userId, serverName);
    }
}
