/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.falcon.resource;

import org.apache.falcon.FalconWebException;
import org.apache.falcon.monitors.Dimension;
import org.apache.falcon.monitors.Monitored;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("entities")
public class SchedulableEntityManager extends AbstractSchedulableEntityManager {

    @GET
    @Path("status/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Monitored(event = "status")
    @Override
    public APIResult getStatus(@Dimension("entityType") @PathParam("type") String type,
                               @Dimension("entityName") @PathParam("entity") String entity,
                               @Dimension("colo") @QueryParam("colo") final String colo)
            throws FalconWebException {
        return super.getStatus(type, entity, colo);
    }

    @GET
    @Path("dependencies/{type}/{entity}")
    @Produces(MediaType.TEXT_XML)
    @Monitored(event = "dependencies")
    @Override
    public EntityList getDependencies(@Dimension("entityType") @PathParam("type") String type,
                                      @Dimension("entityName") @PathParam("entity") String entity) {
        return super.getDependencies(type, entity);
    }

    @GET
    @Path("list/{type}")
    @Produces(MediaType.TEXT_XML)
    @Monitored(event = "dependencies")
    @Override
    public EntityList getDependencies(@Dimension("type") @PathParam("type") String type) {
        return super.getDependencies(type);
    }

    @GET
    @Path("definition/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Monitored(event = "definition")
    @Override
    public String getEntityDefinition(@Dimension("type") @PathParam("type") String type,
                                      @Dimension("entity") @PathParam("entity") String entityName) {
        return super.getEntityDefinition(type, entityName);
    }

    @POST
    @Path("schedule/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Monitored(event = "schedule")
    @Override
    public APIResult schedule(@Context HttpServletRequest request,
                              @Dimension("entityType") @PathParam("type") String type,
                              @Dimension("entityName") @PathParam("entity") String entity,
                              @Dimension("colo") @QueryParam("colo") String colo) {
        return super.schedule(request, type, entity, colo);
    }

    @POST
    @Path("suspend/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Monitored(event = "suspend")
    @Override
    public APIResult suspend(@Context HttpServletRequest request,
                             @Dimension("entityType") @PathParam("type") String type,
                             @Dimension("entityName") @PathParam("entity") String entity,
                             @Dimension("colo") @QueryParam("colo") String colo) {
        return super.suspend(request, type, entity, colo);
    }

    @POST
    @Path("resume/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Monitored(event = "resume")
    @Override
    public APIResult resume(@Context HttpServletRequest request,
                            @Dimension("entityType") @PathParam("type") String type,
                            @Dimension("entityName") @PathParam("entity") String entity,
                            @Dimension("colo") @QueryParam("colo") String colo) {
        return super.resume(request, type, entity, colo);
    }
}
