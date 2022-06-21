package io.hreem.toggler.toggle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.hreem.toggler.toggle.model.dto.NewToggleRequest;

@Path("toggles")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Resource {

    @Inject
    Logger log;

    @Inject
    Service toggleService;

    @GET
    public Response getToggles(@HeaderParam("project-key") String projectKey) {
        return Response.ok().build();
    }

    @GET
    @Path("/{key}")
    public Response getToggleData(@HeaderParam("project-key") String projectKey, @PathParam("key") String key)
            throws JsonMappingException, JsonProcessingException {
        final var response = toggleService.getToggle(projectKey, key);
        return Response.ok(response).build();
    }

    @POST
    public Response createToggle(@HeaderParam("project-key") String projectKey, @Valid NewToggleRequest request)
            throws JsonProcessingException {
        log.infof("Creating a new toggle with key %s for project with key: %s", request.key(), projectKey);
        toggleService.createNewToggle(request, projectKey);
        return Response.ok().build();
    }

}
