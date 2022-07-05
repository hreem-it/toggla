package io.hreem.toggler.project;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.hreem.toggler.project.model.dto.CreateApiKeyRequest;
import io.hreem.toggler.project.model.dto.CreateProjectRequest;

@Path("projects")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Project Management")
public class Resource {

    @Context
    UriInfo uriInfo;

    @Inject
    Service projectService;

    @GET
    public Response getAllProjects() {
        final var projects = projectService.getProjects();
        return Response.ok(projects).build();
    }

    @GET
    @Path("/{projectKey}")
    public Response getProject(@PathParam("projectKey") String projectKey) {
        final var project = projectService.getProject(projectKey, true);
        return Response.ok(project).build();
    }

    @GET
    @Path("/verifykey")
    public Response getProjectKeyByApiKey(@HeaderParam("api-secret") String apiKey) {
        final var projectKey = projectService.getProjectKeyFromApiKey(apiKey);
        return Response.ok(projectKey).build();
    }

    @GET
    @Path("/{projectKey}/environment")
    public Response getProjectEnvironment(@PathParam("projectKey") String projectKey,
            @HeaderParam("api-secret") String apiKey) {
        final var environment = projectService.getProjectEnvironment(projectKey, apiKey);
        return Response.ok(environment).build();
    }

    @POST
    @Operation(summary = "Create a new toggler project")
    public Response createProject(@Valid CreateProjectRequest request) {
        projectService.createProject(request);

        final var uri = uriInfo.getAbsolutePathBuilder().path(request.projectKey()).build();
        return Response.created(uri).build();
    }

    @POST
    @Path("{project-key}/apikey")
    @Operation(summary = "Generate an API key for a project")
    public Response createApiKey(@PathParam("project-key") String projectKey, @Valid CreateApiKeyRequest request) {
        final var secretKey = projectService.createAPIKey(projectKey, request);
        return Response.ok(secretKey).build();
    }

}
