package io.hreem.toggler.toggle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.hreem.toggler.toggle.model.dto.AddToggleVariationRequest;
import io.hreem.toggler.toggle.model.dto.NewToggleRequest;
import io.smallrye.mutiny.Uni;

@Path("toggles")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Feature Toggling")
public class Resource {

    @Context
    UriInfo uriInfo;

    @Inject
    Logger log;

    @Inject
    Service toggleService;

    @GET
    @Operation(summary = "Get all toggles for a project key")
    public Response getToggles(@HeaderParam("project-key") String projectKey) {
        final var response = toggleService.getAllToggles(projectKey);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{key}")
    @Operation(summary = "Get detailed information about a toggle")
    public Response getToggleData(@HeaderParam("project-key") String projectKey, @PathParam("key") String key)
            throws JsonMappingException, JsonProcessingException {
        final var response = toggleService.getToggle(projectKey, key);
        if (response == null)
            throw new NotFoundException();

        return Response.ok(response).build();
    }

    @GET
    @Path("/{key}/status")
    @Operation(summary = "Recommended: Get the status of a toggle")
    public Boolean getToggleStatus(@HeaderParam("project-key") String projectKey,
            @PathParam("key") String key)
            throws JsonMappingException, JsonProcessingException {
        return getToggleStatusForVariation(projectKey, key, "default");
    }

    @GET
    @Path("/{key}/{variationKey}/status")
    @Operation(summary = "Recommended: Get the status of a toggle and a variation")
    public Boolean getToggleStatusForVariation(@HeaderParam("project-key") String projectKey,
            @PathParam("key") String key, @PathParam("variationKey") String variationKey)
            throws JsonMappingException, JsonProcessingException {
        return toggleService.getToggleStatus(projectKey, key, variationKey);
    }

    @PUT
    @Path("/{key}")
    @Operation(summary = "Enable/Disable a toggle")
    public Response toggle(@HeaderParam("project-key") String projectKey, @PathParam("key") String key,
            @PathParam("variationKey") String variationKey)
            throws JsonMappingException, JsonProcessingException {
        return toggleVariation(projectKey, key, "default");
    }

    @PUT
    @Path("/{key}/{variationKey}")
    @Operation(summary = "Enable/Disable a toggle and a variation")
    public Response toggleVariation(@HeaderParam("project-key") String projectKey, @PathParam("key") String key,
            @PathParam("variationKey") String variationKey)
            throws JsonMappingException, JsonProcessingException {
        log.infof("Toggling feature-toggle with key %s and variation: %s", key, variationKey);
        toggleService.toggle(projectKey, key, variationKey);
        return Response.ok().build();
    }

    @POST
    @Operation(summary = "Create a new toggle for a project")
    public Response createToggle(@HeaderParam("project-key") String projectKey, @Valid NewToggleRequest request)
            throws JsonProcessingException {
        log.infof("Creating a new toggle with key %s", request.key());
        toggleService.createNewToggle(request, projectKey);

        final var uri = uriInfo.getAbsolutePathBuilder().path(request.key()).build();
        return Response.created(uri).build();
    }

    @POST
    @Path("/{key}")
    @Operation(summary = "Create a new toggle variation for a project")
    public Response createToggleVariation(@HeaderParam("project-key") String projectKey, @PathParam("key") String key,
            @Valid AddToggleVariationRequest request)
            throws JsonProcessingException {
        log.infof("Creating a new toggle variation for key: %s with variationkey: %s", key,
                request.variationKey());
        toggleService.addToggleVariation(request, projectKey, key);

        final var uri = uriInfo.getAbsolutePathBuilder().path(request.variationKey()).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/{key}")
    @Operation(summary = "Remove a new toggle for a project")
    public Response removeToggle(@HeaderParam("project-key") String projectKey, @PathParam("key") String key)
            throws JsonMappingException, JsonProcessingException {
        log.infof("Removing feature-toggle with key %s", key);
        toggleService.removeToggle(projectKey, key);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{key}/{variationKey}")
    @Operation(summary = "Remove a new toggle variation for a project")
    public Response removeToggleVariation(@HeaderParam("project-key") String projectKey, @PathParam("key") String key,
            @PathParam("variationKey") String variationKey)
            throws JsonProcessingException {
        log.infof("Removing toggle variation for key: %s with variationkey: %s", key,
                variationKey);
        toggleService.removeToggleVariation(projectKey, key, variationKey);

        return Response.ok().build();
    }

}
