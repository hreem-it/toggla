package io.hreem.toggla.common.http;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.hreem.toggla.project.Service;
import io.vertx.core.http.HttpServerRequest;

@Provider
@Priority(1000)
public class AuthenticationInterceptor implements ContainerRequestFilter {
    private static final Logger LOG = Logger.getLogger(AuthenticationInterceptor.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Inject
    Service projectService;

    @Inject
    RequestContext requestContext;

    @Override
    public void filter(ContainerRequestContext context) {
        final String path = info.getPath();
        final var secretHeader = request.getHeader("X-api-secret");

        if (!path.contains("toggles")) {
            return;
        }

        // Validate API Secret here
        if (secretHeader == null || secretHeader.isEmpty()) {
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .header("reason", "Missing X-api-secret header in request.").build());
            return;
        }

        // Verify API is enabled and tied to a project
        final var projectKey = projectService.getProjectKeyFromApiKey(secretHeader);
        final var project = projectService.getProject(projectKey, false);
        final String environment = project.apiKeys().stream()
                .filter(key -> key.apiKey().toString().equals(secretHeader))
                .findFirst().get().env().toString();

        // Populate project key into request
        requestContext.setApiKey(secretHeader);
        requestContext.setProjectKey(projectKey);
        requestContext.setEnvironment(environment);
        LOG.infof("--> environment: %s", environment);
        LOG.infof("--> project-key: %s", projectKey);
    }
}
