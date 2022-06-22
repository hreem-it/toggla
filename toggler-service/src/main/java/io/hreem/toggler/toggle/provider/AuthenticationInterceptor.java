package io.hreem.toggler.toggle.provider;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;

@Provider
@Priority(1000)
public class AuthenticationInterceptor implements ContainerRequestFilter {
    private static final Logger LOG = Logger.getLogger(AuthenticationInterceptor.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) {
        final String path = info.getPath();
        final var secretHeader = request.getHeader("api-secret");

        if (!path.contains("toggles")) {
            return;
        }

        // Validate API Secret here
        if (secretHeader == null || secretHeader.isEmpty()) {
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .header("reason", "Missing api-secret header in request.").build());
        }

        // Verify API Secret is enabled and tied to a project
        // TODO

        // Populate project key into request
        context.getHeaders().add("project-key", "compass");
        LOG.infof("--> project-key: %s", "compass");
    }
}
